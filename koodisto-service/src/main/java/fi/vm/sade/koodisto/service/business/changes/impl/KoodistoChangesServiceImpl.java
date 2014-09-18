package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import fi.vm.sade.koodisto.dto.KoodistoChangesDto;
import fi.vm.sade.koodisto.dto.KoodistoChangesDto.SimpleCodesRelation;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.changes.ChangesDateComparator;
import fi.vm.sade.koodisto.service.business.changes.KoodistoChangesService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;

@Transactional(readOnly = true)
@Service
public class KoodistoChangesServiceImpl implements KoodistoChangesService {
    
    @Autowired
    private KoodistoBusinessService koodistoService;

    @Override
    public KoodistoChangesDto getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted) {
        KoodistoVersio koodistoVersio = koodistoService.getKoodistoVersio(uri, versio);
        KoodistoVersio latest = fetchLatestDesiredCodesVersion(uri, compareToLatestAccepted);
        return constructChangesDto(koodistoVersio, latest);
    }

    @Override
    public KoodistoChangesDto getChangesDto(String uri, Date date, boolean compareToLatestAccepted) {
        KoodistoVersio koodistoVersio = determineCodeVersionThatMatchesDate(uri, date);
        KoodistoVersio latest = fetchLatestDesiredCodesVersion(uri, compareToLatestAccepted);
        return constructChangesDto(koodistoVersio, latest);
    }
    
    private KoodistoVersio determineCodeVersionThatMatchesDate(String uri, Date date) {
        return new KoodistoChangesDateComparator().getClosestMatchingEntity(date, koodistoService.getKoodistoByKoodistoUri(uri).getKoodistoVersios());
    }
    
    private KoodistoVersio fetchLatestDesiredCodesVersion(String uri, boolean compareToLatestAccepted) {
        if (compareToLatestAccepted) {
            Koodisto koodisto = koodistoService.getKoodistoByKoodistoUri(uri);
            KoodistoVersio latestAccepted = null;
            for (KoodistoVersio kv : koodisto.getKoodistoVersios()) {
                if ((latestAccepted == null || latestAccepted.getVersio() < kv.getVersio()) && Tila.HYVAKSYTTY.equals(kv.getTila())) {
                    latestAccepted = kv;
                }
            }
            return latestAccepted;
        }
        return koodistoService.getLatestKoodistoVersio(uri);
    }

    private KoodistoChangesDto constructChangesDto(KoodistoVersio koodistoVersio, KoodistoVersio latest) {
        List<SimpleMetadataDto> changedMetas = changedMetadatas(koodistoVersio.getMetadatas(), latest.getMetadatas());
        List<SimpleMetadataDto> removedMetas = removedMetadatas(koodistoVersio.getMetadatas(), latest.getMetadatas());
        DatesChangedHandler dateHandler = DatesChangedHandler.setDatesHaveChanged(koodistoVersio.getVoimassaAlkuPvm(), koodistoVersio.getVoimassaLoppuPvm(),
                latest.getVoimassaAlkuPvm(), latest.getVoimassaLoppuPvm());
        Tila changedTila = koodistoVersio.getTila().equals(latest.getTila()) ? null : latest.getTila();
        List<SimpleCodesRelation> addedRelations = addedRelations(koodistoVersio, latest);
        List<SimpleCodesRelation> removedRelations = removedRelations(koodistoVersio, latest);
        List<SimpleCodesRelation> passiveRelations = passiveRelations(koodistoVersio, latest);
        MuutosTila changesState = anyChanges(koodistoVersio.getVersio(), latest.getVersio(), changedMetas, removedMetas, dateHandler.anyChanges(), changedTila, 
                addedRelations, removedRelations, passiveRelations);
        return new KoodistoChangesDto(changesState, latest.getVersio(), changedMetas, removedMetas, latest.getPaivitysPvm(), 
                dateHandler.startDateChanged, dateHandler.endDateChanged, dateHandler.endDateRemoved, changedTila, addedRelations, removedRelations, passiveRelations, 
                null, null, null);
    }
    
    private MuutosTila anyChanges(Integer versio, Integer latestVersio, List<SimpleMetadataDto> changedMetas, List<SimpleMetadataDto> removedMetas, boolean validDateHasChanged, Tila changedTila, 
            List<SimpleCodesRelation> addedRelations, List<SimpleCodesRelation> removedRelations, List<SimpleCodesRelation> passiveRelations) {
        if (versio.equals(latestVersio)) {
            return MuutosTila.EI_MUUTOKSIA;
        }
        boolean noChanges = true;
        noChanges = noChanges && changedMetas.size() < 1 && removedMetas.size() < 1;
        noChanges = noChanges && !validDateHasChanged && changedTila == null;
        noChanges = noChanges && addedRelations.size() < 1 && removedRelations.size() < 1 && passiveRelations.size() < 1;
        return  noChanges ? MuutosTila.EI_MUUTOKSIA : MuutosTila.MUUTOKSIA;
    }
    
    private List<SimpleMetadataDto> removedMetadatas(List<KoodistoMetadata> compareToMetas, final List<KoodistoMetadata> latestMetas) {
        Collection<SimpleMetadataDto> removedMetas = Collections2.transform(Collections2.filter(compareToMetas, new Predicate<KoodistoMetadata>() {

            @Override
            public boolean apply(KoodistoMetadata input) {
                for (KoodistoMetadata data : latestMetas) {
                    if(data.getKieli().equals(input.getKieli())) {
                        return false;
                    }
                }
                return true;
            }
            
        }), new Function<KoodistoMetadata, SimpleMetadataDto>() {

            @Override
            public SimpleMetadataDto apply(KoodistoMetadata input) {
                return new SimpleMetadataDto(input.getNimi(), input.getKieli(), input.getKuvaus());
            }
        });
        
        return new ArrayList<>(removedMetas);
    }
    
    private List<SimpleMetadataDto> changedMetadatas(List<KoodistoMetadata> compareToMetadatas, List<KoodistoMetadata> latestMetadatas) {
        List<SimpleMetadataDto> changedMetadatas = new ArrayList<>();
        for (KoodistoMetadata latestData : latestMetadatas) {
            if (!compareToMetadatas.contains(latestData)) {
                KoodistoMetadata metaWithMatchingKieli = getMetadataWithMatchingLanguage(compareToMetadatas, latestData.getKieli());
                SimpleMetadataDto changedMetadata = getChangesForMetadata(latestData, metaWithMatchingKieli);
                if (changedMetadata != null) {
                    changedMetadatas.add(changedMetadata);
                }
            }
        }
        
        return changedMetadatas;
    }

    private SimpleMetadataDto getChangesForMetadata(KoodistoMetadata latestData, KoodistoMetadata metaWithMatchingKieli) {
        if (metaWithMatchingKieli == null) {
            return new SimpleMetadataDto(latestData.getNimi(), latestData.getKieli(), latestData.getKuvaus());
        }
        String changedName = latestData.getNimi().equals(metaWithMatchingKieli.getNimi()) ? null : latestData.getNimi();
        String changedDescription = latestData.getKuvaus().equals(metaWithMatchingKieli.getKuvaus()) ? null : latestData.getKuvaus();
        return changedName == null && changedDescription == null? null : new SimpleMetadataDto(changedName, latestData.getKieli(), changedDescription);
    }

    private KoodistoMetadata getMetadataWithMatchingLanguage(List<KoodistoMetadata> compareToMetadatas, Kieli kieli) {
        for(KoodistoMetadata data : compareToMetadatas) {
            if (data.getKieli().equals(kieli)) { 
                return data;
            }
        }
        return null;
    }
    
    private List<SimpleCodesRelation> passiveRelations(KoodistoVersio kv, KoodistoVersio latestKoodistoVersio) {
        Collection<KoodistonSuhde> passiveRelations = Collections2.filter(getRelationsFromKoodistoVersio(latestKoodistoVersio), new Predicate<KoodistonSuhde>() {
            
            @Override
            public boolean apply(KoodistonSuhde input) {
                return input.isPassive();
            }
        });
        return new ArrayList<SimpleCodesRelation>(Collections2.transform(passiveRelations, new KoodistonSuhdeToSimpleCodesRelation(kv.getKoodisto().getKoodistoUri())));
    }

    private List<SimpleCodesRelation> removedRelations(KoodistoVersio kv, KoodistoVersio latestKoodistoVersio) {
        final String koodistoUri = latestKoodistoVersio.getKoodisto().getKoodistoUri();
        final List<KoodistonSuhde> relationsFromLatest = getRelationsFromKoodistoVersio(latestKoodistoVersio);
        Collection<SimpleCodesRelation> removedRelations = Collections2.transform(Collections2.filter(getRelationsFromKoodistoVersio(kv), new KoodistonSuhdeFoundNotFound(relationsFromLatest)), new KoodistonSuhdeToSimpleCodesRelation(koodistoUri));
        return new ArrayList<SimpleCodesRelation>(removedRelations);
    }

    private List<SimpleCodesRelation> addedRelations(KoodistoVersio kv, KoodistoVersio latestKoodistoVersio) {
        final String koodistoUri = latestKoodistoVersio.getKoodisto().getKoodistoUri();
        final List<KoodistonSuhde> relationsFromVersio = getRelationsFromKoodistoVersio(kv);
        Collection<SimpleCodesRelation> relationsAdded = Collections2.transform(Collections2.filter(getRelationsFromKoodistoVersio(latestKoodistoVersio), new KoodistonSuhdeFoundNotFound(relationsFromVersio)), new KoodistonSuhdeToSimpleCodesRelation(koodistoUri));
        return new ArrayList<SimpleCodesRelation>(relationsAdded);
    }

    private List<KoodistonSuhde> getRelationsFromKoodistoVersio(KoodistoVersio kv) {
        List<KoodistonSuhde> allRelations = new ArrayList<>(kv.getAlakoodistos());
        allRelations.addAll(kv.getYlakoodistos());
        return allRelations;
    }
    
    private final class KoodistonSuhdeFoundNotFound implements Predicate<KoodistonSuhde> {
        private final List<KoodistonSuhde> relationsToCompare;

        private KoodistonSuhdeFoundNotFound(List<KoodistonSuhde> relationsToCompare) {
            this.relationsToCompare = relationsToCompare;
        }

        @Override
        public boolean apply(KoodistonSuhde input) {
            boolean missing = true;
            String upperCodeUri = input.getYlakoodistoVersio().getKoodisto().getKoodistoUri();
            String lowerCodeUri = input.getAlakoodistoVersio().getKoodisto().getKoodistoUri();
            for (KoodistonSuhde ks : relationsToCompare) {
                if (lowerCodeUri.equals(ks.getAlakoodistoVersio().getKoodisto().getKoodistoUri()) && upperCodeUri.equals(ks.getYlakoodistoVersio().getKoodisto().getKoodistoUri()) 
                        && ks.getSuhteenTyyppi().equals(input.getSuhteenTyyppi())) {
                    missing = false;
                }
            }
            return missing;
        }
    }
    
    private class KoodistonSuhdeToSimpleCodesRelation implements Function<KoodistonSuhde, SimpleCodesRelation> {
        private final String koodistoUri;

        private KoodistonSuhdeToSimpleCodesRelation(String koodistoUri) {
            this.koodistoUri = koodistoUri;
        }

        @Override
        public SimpleCodesRelation apply(KoodistonSuhde input) {
            boolean isChild = koodistoUri.equals(input.getYlakoodistoVersio().getKoodisto().getKoodistoUri()) ? true : false;
            String uri = isChild ? input.getAlakoodistoVersio().getKoodisto().getKoodistoUri() : input.getYlakoodistoVersio().getKoodisto().getKoodistoUri();
            Integer versio = isChild ? input.getAlakoodistoVersio().getVersio() : input.getYlakoodistoVersio().getVersio();
            return new SimpleCodesRelation(uri, versio, input.getSuhteenTyyppi(), isChild);
        }
    }
    
    private class KoodistoChangesDateComparator extends ChangesDateComparator<KoodistoVersio> {

        @Override
        protected DateTime getDateFromEntity(KoodistoVersio entity) {
            return new DateTime(entity.getLuotu());
        }
        
    }
}
