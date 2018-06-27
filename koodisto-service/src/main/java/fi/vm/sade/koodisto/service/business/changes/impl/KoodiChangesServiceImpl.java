package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import fi.vm.sade.generic.model.BaseEntity;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.dto.KoodiChangesDto.SimpleCodeElementRelation;
import fi.vm.sade.koodisto.dto.SimpleKoodiMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.changes.ChangesDateComparator;
import fi.vm.sade.koodisto.service.business.changes.ChangesService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;

@Transactional(readOnly = true)
@Service
public class KoodiChangesServiceImpl implements KoodiChangesService {
    
    @Autowired
    private KoodiBusinessService service;
    
    @Autowired
    private KoodistoBusinessService koodistoService;

    @Override
    public KoodiChangesDto getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted) {
        KoodiVersio koodiVersio = service.getKoodiVersio(uri, versio);
        KoodiVersio latestKoodiVersio = fetchLatestDesiredCodeVersion(uri, compareToLatestAccepted);
        return constructChangesDto(koodiVersio, latestKoodiVersio, compareToLatestAccepted);
    }
    
    @Override
    public KoodiChangesDto getChangesDto(String uri, DateTime date, boolean compareToLatestAccepted) {
        KoodiVersio koodiVersio = determineCodeVersionThatMatchesDate(uri, date);
        KoodiVersio latestKoodiVersio = fetchLatestDesiredCodeVersion(uri, compareToLatestAccepted);
        return constructChangesDto(koodiVersio, latestKoodiVersio, compareToLatestAccepted);
    }
    
    private KoodiVersio determineCodeVersionThatMatchesDate(String uri, DateTime date) {
        return new KoodiChangesDateComparator().getClosestMatchingEntity(date, service.getKoodi(uri).getKoodiVersios());
    }

    private KoodiVersio fetchLatestDesiredCodeVersion(String uri, boolean compareToLatestAccepted) {
        if (compareToLatestAccepted) {
            Koodi koodi = service.getKoodi(uri);
            KoodiVersio latestAccepted = null;
            for (KoodiVersio kv : koodi.getKoodiVersios()) {
                if ((latestAccepted == null || latestAccepted.getVersio() < kv.getVersio()) && Tila.HYVAKSYTTY.equals(kv.getTila())) {
                    latestAccepted = kv;
                }
            }
            return latestAccepted;
        }
        return service.getLatestKoodiVersio(uri);
    }

    private KoodiChangesDto constructChangesDto(KoodiVersio koodiVersio, KoodiVersio latestKoodiVersio, boolean compareToLatestAccepted) {
        if (removedFromLatestCodes(latestKoodiVersio, compareToLatestAccepted)) {
            return new KoodiChangesDto(koodiVersio.getKoodi().getKoodiUri(), MuutosTila.POISTETTU, null, null);
        }
        return constructChangesDto(koodiVersio, latestKoodiVersio);
    }

    @Override
    public KoodiChangesDto constructChangesDto(KoodiVersio koodiVersio, KoodiVersio latestKoodiVersio) {
        String koodiUri = koodiVersio.getKoodi().getKoodiUri();
        if (koodiVersio.getVersio().equals(latestKoodiVersio.getVersio()) || koodiVersio.getVersio() > latestKoodiVersio.getVersio()) {
            return new KoodiChangesDto(koodiUri, MuutosTila.EI_MUUTOKSIA, latestKoodiVersio.getVersio(), latestKoodiVersio.getPaivitysPvm());
        }
        List<SimpleKoodiMetadataDto> changedMetas = changedMetadatas(koodiVersio.getMetadatas(), latestKoodiVersio.getMetadatas());
        List<SimpleKoodiMetadataDto> removedMetas = removedMetadatas(koodiVersio.getMetadatas(), latestKoodiVersio.getMetadatas());
        DatesChangedHandler dateHandler = DatesChangedHandler.setDatesHaveChanged(koodiVersio.getVoimassaAlkuPvm(), koodiVersio.getVoimassaLoppuPvm(),
                latestKoodiVersio.getVoimassaAlkuPvm(), latestKoodiVersio.getVoimassaLoppuPvm());
        Tila tilaHasChanged = latestKoodiVersio.getTila().equals(koodiVersio.getTila()) ? null : latestKoodiVersio.getTila();
        List<SimpleCodeElementRelation> addedRelations = addedRelations(koodiVersio, latestKoodiVersio);
        List<SimpleCodeElementRelation> removedRelations = removedRelations(koodiVersio, latestKoodiVersio);
        List<SimpleCodeElementRelation> passiveRelations = passiveRelations(latestKoodiVersio);
        MuutosTila muutosTila = anyChanges(changedMetas, removedMetas, dateHandler.anyChanges(), tilaHasChanged, addedRelations, removedRelations, passiveRelations);
        return new KoodiChangesDto(koodiUri, muutosTila, latestKoodiVersio.getVersio(), changedMetas, removedMetas, addedRelations, removedRelations, 
                passiveRelations, latestKoodiVersio.getPaivitysPvm(), dateHandler.startDateChanged, dateHandler.endDateChanged, dateHandler.endDateRemoved, tilaHasChanged);
    }
    
    private boolean removedFromLatestCodes(KoodiVersio koodiVersio, boolean compareToLatestAccepted) {
        Koodisto koodisto = koodistoService.getKoodistoByKoodistoUri(koodiVersio.getKoodi().getKoodisto().getKoodistoUri());
        KoodistoVersio koodistoVersio = getMatchingKoodistoVersio(new ArrayList<>(koodisto.getKoodistoVersios()), compareToLatestAccepted);
        return !containsCodeElementVersion(koodiVersio, koodistoVersio);
    }

    private boolean containsCodeElementVersion(KoodiVersio koodiVersio, KoodistoVersio koodistoVersio) {
        Collection<KoodiVersio> koodiVersios = Collections2.transform(koodistoVersio.getKoodiVersios(), KoodistoVersioKoodiVersio::getKoodiVersio);
        return koodiVersios.contains(koodiVersio);
    }

    private KoodistoVersio getMatchingKoodistoVersio(List<KoodistoVersio> koodistoVersios, boolean compareToLatestAccepted) {
        KoodistoVersio latestMatching = null;
        koodistoVersios.sort(Comparator.comparingLong(KoodistoVersio::getVersio));
        for (KoodistoVersio kv : koodistoVersios) {
            latestMatching = latestMatching == null
                    || (latestMatching.getVersio() < kv.getVersio() && (!compareToLatestAccepted || Tila.HYVAKSYTTY.equals(kv.getTila())))
                    ? kv : latestMatching;
        }
        return latestMatching;
    }

    private List<SimpleCodeElementRelation> passiveRelations(KoodiVersio latestKoodiVersio) {
        Collection<KoodinSuhde> passiveRelations = Collections2.filter(getRelationsFromKoodiVersio(latestKoodiVersio), new Predicate<KoodinSuhde>() {
            
            @Override
            public boolean apply(@Nonnull KoodinSuhde input) {
                return input.isPassive();
            }
        });
        return new ArrayList<>(Collections2.transform(passiveRelations, new KoodinSuhdeToSimpleCodeElementRelation(latestKoodiVersio.getKoodi().getKoodiUri())));
    }

    private List<SimpleCodeElementRelation> removedRelations(KoodiVersio koodiVersio, KoodiVersio latestKoodiVersio) {
        final String koodiUri = latestKoodiVersio.getKoodi().getKoodiUri();
        final List<KoodinSuhde> relationsFromLatest = getRelationsFromKoodiVersio(latestKoodiVersio);
        Collection<SimpleCodeElementRelation> removedRelations = Collections2.transform(Collections2.filter(getRelationsFromKoodiVersio(koodiVersio), new KoodinSuhdeFoundNotFound(relationsFromLatest)), new KoodinSuhdeToSimpleCodeElementRelation(koodiUri));
        return new ArrayList<>(removedRelations);
    }

    private List<SimpleCodeElementRelation> addedRelations(KoodiVersio koodiVersio, KoodiVersio latestKoodiVersio) {
        final String koodiUri = latestKoodiVersio.getKoodi().getKoodiUri();
        final List<KoodinSuhde> relationsFromVersio = getRelationsFromKoodiVersio(koodiVersio);
        Collection<SimpleCodeElementRelation> relationsAdded = Collections2.transform(Collections2.filter(getRelationsFromKoodiVersio(latestKoodiVersio), new KoodinSuhdeFoundNotFound(relationsFromVersio)), new KoodinSuhdeToSimpleCodeElementRelation(koodiUri));
        return new ArrayList<>(relationsAdded);
    }

    private List<KoodinSuhde> getRelationsFromKoodiVersio(KoodiVersio koodiVersio) {
        List<KoodinSuhde> allRelations = new ArrayList<>(koodiVersio.getAlakoodis());
        allRelations.addAll(koodiVersio.getYlakoodis());
        return allRelations;
    }

    private MuutosTila anyChanges(List<SimpleKoodiMetadataDto> changedMetas, List<SimpleKoodiMetadataDto> removedMetas, boolean anyChangesInValidThruDates, Tila tilaHasChanged, List<SimpleCodeElementRelation> addedRelations, List<SimpleCodeElementRelation> removedRelations, List<SimpleCodeElementRelation> passiveRelations) {
        boolean noChanges = removedMetas.isEmpty() && changedMetas.isEmpty();
        noChanges = noChanges && !anyChangesInValidThruDates && tilaHasChanged == null;
        noChanges = noChanges && addedRelations.isEmpty() && removedRelations.isEmpty() && passiveRelations.isEmpty();
        return noChanges ? MuutosTila.EI_MUUTOKSIA : MuutosTila.MUUTOKSIA;
    }
    
    private List<SimpleKoodiMetadataDto> removedMetadatas(Set<KoodiMetadata> compareToMetas, final Set<KoodiMetadata> latestMetas) {
        Collection<SimpleKoodiMetadataDto> removedMetas = Collections2.transform(Collections2.filter(compareToMetas, new Predicate<KoodiMetadata>() {

            @Override
            public boolean apply(KoodiMetadata input) {
                for (KoodiMetadata data : latestMetas) {
                    if(data.getKieli().equals(input.getKieli())) {
                        return false;
                    }
                }
                return true;
            }
            
        }), new Function<KoodiMetadata, SimpleKoodiMetadataDto>() {

            @Override
            public SimpleKoodiMetadataDto apply(@Nonnull KoodiMetadata input) {
                return new SimpleKoodiMetadataDto(input.getNimi(), input.getKieli(), input.getKuvaus(), input.getLyhytNimi());
            }
        });
        
        return new ArrayList<>(removedMetas);
    }

    private List<SimpleKoodiMetadataDto> changedMetadatas(Set<KoodiMetadata> compareToMetadatas, Set<KoodiMetadata> latestMetadatas) {
        List<SimpleKoodiMetadataDto> changedMetadatas = new ArrayList<>();
        for (KoodiMetadata latestData : latestMetadatas) {
            if (!containsMetadata(compareToMetadatas, latestData)) {
                KoodiMetadata metaWithMatchingKieli = getMetadataWithMatchingLanguage(compareToMetadatas, latestData.getKieli());
                changedMetadatas.add(getChangesForMetadata(latestData, metaWithMatchingKieli));
            }
        }
        
        return changedMetadatas;
    }

    private boolean containsMetadata(Set<KoodiMetadata> compareToMetadatas, KoodiMetadata compareAgainst) {
        for (KoodiMetadata compare : compareToMetadatas) {
            if (compare.getKieli().equals(compareAgainst.getKieli()) && compare.getNimi().equals(compareAgainst.getNimi())
                    && ((compare.getKuvaus() == null && compareAgainst.getKuvaus() == null) || (compare.getKuvaus() != null && compare.getKuvaus().equals(compareAgainst.getKuvaus())))
                    && ((compare.getLyhytNimi() == null && compareAgainst.getLyhytNimi() == null) || (compare.getLyhytNimi() != null && compare.getLyhytNimi().equals(compareAgainst.getLyhytNimi())))) {
                return true;
            }
        }
        return false;
    }

    private SimpleKoodiMetadataDto getChangesForMetadata(KoodiMetadata latestData, KoodiMetadata metaWithMatchingKieli) {
        if (metaWithMatchingKieli == null) {
            return new SimpleKoodiMetadataDto(latestData.getNimi(), latestData.getKieli(), latestData.getKuvaus(), latestData.getLyhytNimi());
        }
        String changedName = latestData.getNimi().equals(metaWithMatchingKieli.getNimi()) ? null : latestData.getNimi();
        String changedShortName = getChangeForMetadataField(latestData.getLyhytNimi(), metaWithMatchingKieli.getLyhytNimi());
        String changedDescription = getChangeForMetadataField(latestData.getKuvaus(), metaWithMatchingKieli.getKuvaus());
        return new SimpleKoodiMetadataDto(changedName, latestData.getKieli(), changedDescription, changedShortName);
    }

    private String getChangeForMetadataField(String latestData, String matchingData) {
        if (latestData == null && matchingData != null) {
            return ChangesService.REMOVED_METADATA_FIELD;
        }
        return latestData != null && latestData.equals(matchingData) ? null : latestData;
    }

    private KoodiMetadata getMetadataWithMatchingLanguage(Set<KoodiMetadata> compareToMetadatas, Kieli kieli) {
        for(KoodiMetadata data : compareToMetadatas) {
            if (data.getKieli().equals(kieli)) { 
                return data;
            }
        }
        return null;
    }
    
    private static final class KoodinSuhdeFoundNotFound implements Predicate<KoodinSuhde> {
        private final List<KoodinSuhde> relationsToCompare;

        private KoodinSuhdeFoundNotFound(List<KoodinSuhde> relationsToCompare) {
            this.relationsToCompare = relationsToCompare;
        }

        @Override
        public boolean apply(@Nonnull KoodinSuhde input) {
            boolean missing = true;
            String upperCodeUri = input.getYlakoodiVersio().getKoodi().getKoodiUri();
            String lowerCodeUri = input.getAlakoodiVersio().getKoodi().getKoodiUri();
            for (KoodinSuhde ks : relationsToCompare) {
                if (lowerCodeUri.equals(ks.getAlakoodiVersio().getKoodi().getKoodiUri()) && upperCodeUri.equals(ks.getYlakoodiVersio().getKoodi().getKoodiUri()) 
                        && ks.getSuhteenTyyppi().equals(input.getSuhteenTyyppi())) {
                    missing = false;
                }
            }
            return missing;
        }
    }

    private static class KoodiChangesDateComparator extends ChangesDateComparator<KoodiVersio> {

        @Override
        protected DateTime getDateFromEntity(KoodiVersio entity) {
            return new DateTime(entity.getLuotu());
        }
        
    }

}
