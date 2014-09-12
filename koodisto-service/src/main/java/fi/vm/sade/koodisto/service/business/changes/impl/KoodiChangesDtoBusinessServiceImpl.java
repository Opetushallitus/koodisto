package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.dto.KoodiChangesDto.MuutosTila;
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
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesDtoBusinessService;

@Transactional(readOnly = true)
@Service
public class KoodiChangesDtoBusinessServiceImpl implements KoodiChangesDtoBusinessService {
    
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
    public KoodiChangesDto getChangesDto(String uri, Date date, boolean compareToLatestAccepted) {
        KoodiVersio koodiVersio = determineCodeVersionThatMatchesDate(uri, date);
        KoodiVersio latestKoodiVersio = fetchLatestDesiredCodeVersion(uri, compareToLatestAccepted);
        return constructChangesDto(koodiVersio, latestKoodiVersio, compareToLatestAccepted);
    }
    
    private KoodiVersio determineCodeVersionThatMatchesDate(String uri, Date date) {
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
            return new KoodiChangesDto(MuutosTila.POISTETTU, null, null, null, null, null, null, null, null, null, null, null);
        }
        List<SimpleKoodiMetadataDto> changedMetas = changedMetadatas(koodiVersio.getMetadatas(), latestKoodiVersio.getMetadatas());
        List<SimpleKoodiMetadataDto> removedMetas = removedMetadatas(koodiVersio.getMetadatas(), latestKoodiVersio.getMetadatas());
        DatesChangedHandler dateHandler = DatesChangedHandler.setDatesHaveChanged(koodiVersio.getVoimassaAlkuPvm(), koodiVersio.getVoimassaLoppuPvm(),
                latestKoodiVersio.getVoimassaAlkuPvm(), latestKoodiVersio.getVoimassaLoppuPvm());
        Tila tilaHasChanged = latestKoodiVersio.getTila().equals(koodiVersio.getTila()) ? null : latestKoodiVersio.getTila();
        List<SimpleCodeElementRelation> addedRelations = addedRelations(koodiVersio, latestKoodiVersio);
        List<SimpleCodeElementRelation> removedRelations = removedRelations(koodiVersio, latestKoodiVersio);
        List<SimpleCodeElementRelation> passiveRelations = passiveRelations(koodiVersio, latestKoodiVersio);
        MuutosTila muutosTila = anyChanges(koodiVersio.getVersio(), latestKoodiVersio.getVersio(), changedMetas, removedMetas, dateHandler.anyChanges(), tilaHasChanged, 
                addedRelations, removedRelations, passiveRelations);
        return new KoodiChangesDto(muutosTila, latestKoodiVersio.getVersio(), changedMetas, removedMetas, addedRelations, removedRelations, passiveRelations, 
                latestKoodiVersio.getPaivitysPvm(), dateHandler.startDateChanged, dateHandler.endDateChanged, dateHandler.endDateRemoved, tilaHasChanged);
    }
    
    private boolean removedFromLatestCodes(KoodiVersio koodiVersio, boolean compareToLatestAccepted) {
        Koodisto koodisto = koodistoService.getKoodistoByKoodistoUri(koodiVersio.getKoodi().getKoodisto().getKoodistoUri());
        KoodistoVersio koodistoVersio = getMatchingKoodistoVersio(koodisto.getKoodistoVersios(), compareToLatestAccepted);
        return !containsCodeElementVersion(koodiVersio, koodistoVersio);
    }

    private boolean containsCodeElementVersion(KoodiVersio koodiVersio, KoodistoVersio koodistoVersio) {
        Collection<KoodiVersio> koodiVersios = Collections2.transform(koodistoVersio.getKoodiVersios(), new Function<KoodistoVersioKoodiVersio, KoodiVersio>() {

            @Override
            public KoodiVersio apply(KoodistoVersioKoodiVersio input) {
                return input.getKoodiVersio();
            }
            
        });
        return koodiVersios.contains(koodiVersio);
    }

    private KoodistoVersio getMatchingKoodistoVersio(Set<KoodistoVersio> koodistoVersios, boolean compareToLatestAccepted) {
        KoodistoVersio latestMatching = null;
        for (KoodistoVersio kv : koodistoVersios) {
            latestMatching = latestMatching == null || (latestMatching.getVersio() < kv.getVersio() && (!compareToLatestAccepted || Tila.HYVAKSYTTY.equals(kv.getTila()))) ? 
                    kv : latestMatching;
        }
        return latestMatching;
    }

    private List<SimpleCodeElementRelation> passiveRelations(KoodiVersio kv, KoodiVersio latestKoodiVersio) {
        Collection<KoodinSuhde> passiveRelations = Collections2.filter(getRelationsFromKoodiVersio(latestKoodiVersio), new Predicate<KoodinSuhde>() {
            
            @Override
            public boolean apply(KoodinSuhde input) {
                return input.isPassive();
            }
        });
        return new ArrayList<SimpleCodeElementRelation>(Collections2.transform(passiveRelations, new KoodinSuhdeToSimpleCodeElementRelation(kv.getKoodi().getKoodiUri())));
    }

    private List<SimpleCodeElementRelation> removedRelations(KoodiVersio koodiVersio, KoodiVersio latestKoodiVersio) {
        final String koodiUri = latestKoodiVersio.getKoodi().getKoodiUri();
        final List<KoodinSuhde> relationsFromLatest = getRelationsFromKoodiVersio(latestKoodiVersio);
        Collection<SimpleCodeElementRelation> removedRelations = Collections2.transform(Collections2.filter(getRelationsFromKoodiVersio(koodiVersio), new KoodinSuhdeFoundNotFound(relationsFromLatest)), new KoodinSuhdeToSimpleCodeElementRelation(koodiUri));
        return new ArrayList<SimpleCodeElementRelation>(removedRelations);
    }

    private List<SimpleCodeElementRelation> addedRelations(KoodiVersio koodiVersio, KoodiVersio latestKoodiVersio) {
        final String koodiUri = latestKoodiVersio.getKoodi().getKoodiUri();
        final List<KoodinSuhde> relationsFromVersio = getRelationsFromKoodiVersio(koodiVersio);
        Collection<SimpleCodeElementRelation> relationsAdded = Collections2.transform(Collections2.filter(getRelationsFromKoodiVersio(latestKoodiVersio), new KoodinSuhdeFoundNotFound(relationsFromVersio)), new KoodinSuhdeToSimpleCodeElementRelation(koodiUri));
        return new ArrayList<SimpleCodeElementRelation>(relationsAdded);
    }

    private List<KoodinSuhde> getRelationsFromKoodiVersio(KoodiVersio koodiVersio) {
        List<KoodinSuhde> allRelations = new ArrayList<>(koodiVersio.getAlakoodis());
        allRelations.addAll(koodiVersio.getYlakoodis());
        return allRelations;
    }

    private MuutosTila anyChanges(Integer versio, Integer latestVersio, List<SimpleKoodiMetadataDto> changedMetas, List<SimpleKoodiMetadataDto> removedMetas, boolean anyChangesInValidThruDates, Tila tilaHasChanged, List<SimpleCodeElementRelation> addedRelations, List<SimpleCodeElementRelation> removedRelations, List<SimpleCodeElementRelation> passiveRelations) {
        if (versio.equals(latestVersio)) {
            return MuutosTila.EI_MUUTOKSIA;
        }
        if (removedMetas.size() > 0) {
            return MuutosTila.MUUTOKSIA;
        }
        if (anyChangesInValidThruDates) {
            return MuutosTila.MUUTOKSIA;
        }
        if (tilaHasChanged != null) {
            return MuutosTila.MUUTOKSIA;
        }
        if (addedRelations.size() > 0 || removedRelations.size() > 0 || passiveRelations.size() > 0) {
            return MuutosTila.MUUTOKSIA;
        }
        return changedMetas.size() > 0 ? MuutosTila.MUUTOKSIA : MuutosTila.EI_MUUTOKSIA;
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
            public SimpleKoodiMetadataDto apply(KoodiMetadata input) {
                return new SimpleKoodiMetadataDto(input.getNimi(), input.getKieli(), input.getKuvaus(), input.getLyhytNimi());
            }
        });
        
        return new ArrayList<>(removedMetas);
    }

    private List<SimpleKoodiMetadataDto> changedMetadatas(Set<KoodiMetadata> compareToMetadatas, Set<KoodiMetadata> latestMetadatas) {
        List<SimpleKoodiMetadataDto> changedMetadatas = new ArrayList<>();
        for (KoodiMetadata latestData : latestMetadatas) {
            if (!compareToMetadatas.contains(latestData)) {
                KoodiMetadata metaWithMatchingKieli = getMetadataWithMatchingLanguage(compareToMetadatas, latestData.getKieli());
                changedMetadatas.add(getChangesForMetadata(latestData, metaWithMatchingKieli));
            }
        }
        
        return changedMetadatas;
    }

    private SimpleKoodiMetadataDto getChangesForMetadata(KoodiMetadata latestData, KoodiMetadata metaWithMatchingKieli) {
        if (metaWithMatchingKieli == null) {
            return new SimpleKoodiMetadataDto(latestData.getNimi(), latestData.getKieli(), latestData.getKuvaus(), latestData.getLyhytNimi());
        }
        String changedName = latestData.getNimi().equals(metaWithMatchingKieli.getNimi()) ? null : latestData.getNimi();
        String changedShortName = latestData.getLyhytNimi().equals(metaWithMatchingKieli.getLyhytNimi()) ? null : latestData.getLyhytNimi();
        String changedDescription = latestData.getKuvaus().equals(metaWithMatchingKieli.getKuvaus()) ? null : latestData.getKuvaus();
        return new SimpleKoodiMetadataDto(changedName, latestData.getKieli(), changedDescription, changedShortName);
    }

    private KoodiMetadata getMetadataWithMatchingLanguage(Set<KoodiMetadata> compareToMetadatas, Kieli kieli) {
        for(KoodiMetadata data : compareToMetadatas) {
            if (data.getKieli().equals(kieli)) { 
                return data;
            }
        }
        return null;
    }
    
    private final class KoodinSuhdeFoundNotFound implements Predicate<KoodinSuhde> {
        private final List<KoodinSuhde> relationsToCompare;

        private KoodinSuhdeFoundNotFound(List<KoodinSuhde> relationsToCompare) {
            this.relationsToCompare = relationsToCompare;
        }

        @Override
        public boolean apply(KoodinSuhde input) {
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

    private class KoodinSuhdeToSimpleCodeElementRelation implements Function<KoodinSuhde, SimpleCodeElementRelation> {
        private final String koodiUri;

        private KoodinSuhdeToSimpleCodeElementRelation(String koodiUri) {
            this.koodiUri = koodiUri;
        }

        @Override
        public SimpleCodeElementRelation apply(KoodinSuhde input) {
            boolean isChild = koodiUri.equals(input.getYlakoodiVersio().getKoodi().getKoodiUri()) ? true : false;
            String uri = isChild ? input.getAlakoodiVersio().getKoodi().getKoodiUri() : input.getYlakoodiVersio().getKoodi().getKoodiUri();
            Integer versio = isChild ? input.getAlakoodiVersio().getVersio() : input.getYlakoodiVersio().getVersio();
            return new SimpleCodeElementRelation(uri, versio, input.getSuhteenTyyppi(), isChild);
        }
    }

    private static class DatesChangedHandler {
        
        private final Date startDateChanged;
        
        private final Date endDateChanged;
        
        private final Boolean endDateRemoved;
        
        public DatesChangedHandler(Date startDateChanged, Date endDateChanged, Boolean endDateRemoved) {
            this.startDateChanged = startDateChanged;
            this.endDateChanged = endDateChanged;
            this.endDateRemoved = endDateRemoved;
        }
        
        private static DatesChangedHandler setDatesHaveChanged(Date relateToStartDate, Date relateToEndDate, Date latestStartDate, Date latestEndDate) {
            Date startDateChanged = relateToStartDate.equals(latestStartDate) ? null : latestStartDate;
            Date endDateChanged = latestEndDate == null || latestEndDate.equals(relateToEndDate) ? null : latestEndDate;
            Boolean endDateRemoved = relateToEndDate != null && latestEndDate == null ? true : null;
            return new DatesChangedHandler(startDateChanged, endDateChanged, endDateRemoved);
        }
        
        private boolean anyChanges() {
            return startDateChanged != null || endDateChanged != null || endDateRemoved != null;
        }
        
    }
    
    private class KoodiChangesDateComparator extends ChangesDateComparator<KoodiVersio> {

        @Override
        protected DateTime getDateFromEntity(KoodiVersio entity) {
            return new DateTime(entity.getLuotu());
        }
        
    }

}
