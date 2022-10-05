package fi.vm.sade.koodisto.service.business.changes.impl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.dto.KoodiChangesDto.SimpleCodeElementRelation;
import fi.vm.sade.koodisto.dto.KoodistoChangesDto;
import fi.vm.sade.koodisto.dto.KoodistoChangesDto.SimpleCodesRelation;
import fi.vm.sade.koodisto.dto.SimpleKoodiMetadataDto;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.changes.*;
import fi.vm.sade.koodisto.service.conversion.impl.MetadataToSimpleMetadataConverter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
@Service
public class KoodistoChangesServiceImpl implements KoodistoChangesService {
    
    @Autowired
    private KoodistoBusinessService koodistoService;
    
    @Autowired
    private KoodiBusinessService koodiBusinessService;
    
    @Autowired
    private KoodiChangesService koodiChangesService;

    @Override
    public KoodistoChangesDto getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted) {
        KoodistoVersio koodistoVersio = koodistoService.getKoodistoVersio(uri, versio);
        KoodistoVersio latest = fetchLatestDesiredCodesVersion(uri, compareToLatestAccepted);
        return constructChangesDto(koodistoVersio, latest);
    }

    @Override
    public KoodistoChangesDto getChangesDto(String uri, DateTime date, boolean compareToLatestAccepted) {
        KoodistoVersio koodistoVersio = determineCodeVersionThatMatchesDate(uri, date);
        KoodistoVersio latest = fetchLatestDesiredCodesVersion(uri, compareToLatestAccepted);
        return constructChangesDto(koodistoVersio, latest);
    }
    
    private KoodistoVersio determineCodeVersionThatMatchesDate(String uri, DateTime date) {
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
        String koodistoUri = latest.getKoodisto().getKoodistoUri();
        if (koodistoVersio.getVersio().equals(latest.getVersio()) || koodistoVersio.getVersio() > latest.getVersio()) {
            return new KoodistoChangesDto(koodistoUri, MuutosTila.EI_MUUTOKSIA, latest.getVersio(), latest.getPaivitysPvm());
        }
        List<SimpleMetadataDto> changedMetas = changedMetadatas(koodistoVersio.getMetadatas(), latest.getMetadatas());
        List<SimpleMetadataDto> removedMetas = removedMetadatas(koodistoVersio.getMetadatas(), latest.getMetadatas());
        DatesChangedHandler dateHandler = DatesChangedHandler.setDatesHaveChanged(koodistoVersio.getVoimassaAlkuPvm(), koodistoVersio.getVoimassaLoppuPvm(),
                latest.getVoimassaAlkuPvm(), latest.getVoimassaLoppuPvm());
        Tila changedTila = koodistoVersio.getTila().equals(latest.getTila()) ? null : latest.getTila();
        List<SimpleCodesRelation> addedRelations = addedRelations(koodistoVersio, latest);
        List<SimpleCodesRelation> removedRelations = removedRelations(koodistoVersio, latest);
        List<SimpleCodesRelation> passiveRelations = passiveRelations(koodistoVersio, latest);
        List<KoodiChangesDto> addedCodeElements = addedCodeElements(koodistoVersio, latest);
        List<KoodiChangesDto> changedCodeElements = changedCodeElements(koodistoVersio, latest);
        List<KoodiChangesDto> removedCodeElements = removedCodeElements(koodistoVersio, latest);
        MuutosTila changesState = anyChanges(changedMetas, removedMetas, dateHandler.anyChanges(), changedTila, 
                addedRelations, removedRelations, passiveRelations, addedCodeElements, removedCodeElements, changedCodeElements);
        return new KoodistoChangesDto(koodistoUri, changesState, latest.getVersio(), changedMetas, removedMetas, 
                latest.getPaivitysPvm(), dateHandler.startDateChanged, dateHandler.endDateChanged, dateHandler.endDateRemoved, changedTila, addedRelations, removedRelations, 
                passiveRelations, addedCodeElements, changedCodeElements, removedCodeElements);
    }


    private MuutosTila anyChanges(List<SimpleMetadataDto> changedMetas, List<SimpleMetadataDto> removedMetas, boolean validDateHasChanged, Tila changedTila, 
            List<SimpleCodesRelation> addedRelations, List<SimpleCodesRelation> removedRelations, List<SimpleCodesRelation> passiveRelations, 
            List<KoodiChangesDto> addedCodeElements, List<KoodiChangesDto> removedCodeElements, List<KoodiChangesDto> changedCodeElements) {
        boolean noChanges = true;
        noChanges = noChanges && changedMetas.isEmpty() && removedMetas.isEmpty();
        noChanges = noChanges && !validDateHasChanged && changedTila == null;
        noChanges = noChanges && addedRelations.isEmpty() && removedRelations.isEmpty() && passiveRelations.isEmpty();
        noChanges = noChanges && addedCodeElements.isEmpty() && removedCodeElements.isEmpty() && changedCodeElements.isEmpty();
        return noChanges ? MuutosTila.EI_MUUTOKSIA : MuutosTila.MUUTOKSIA;
    }
    
    private List<KoodiChangesDto> addedCodeElements(KoodistoVersio koodistoVersio, KoodistoVersio latest) {
        final Set<KoodistoVersioKoodiVersio> koodiVersios = koodistoVersio.getKoodiVersios();
        Collection<KoodiChangesDto> addedCodeElements = Collections2.transform(Collections2.filter(latest.getKoodiVersios(), new KoodiVersioNotFound(koodiVersios)), new Function<KoodistoVersioKoodiVersio, KoodiChangesDto>() {

            @Override
            public KoodiChangesDto apply(KoodistoVersioKoodiVersio input) {
                KoodiVersio koodiVersio = input.getKoodiVersio();
                String koodiUri = koodiVersio.getKoodi().getKoodiUri();
                Set<KoodiMetadata> metadatas = koodiVersio.getMetadatas();
                List<SimpleKoodiMetadataDto> simpleMetadata = MetadataToSimpleMetadataConverter.convertToSimpleKoodiMetadata(metadatas.toArray(new KoodiMetadata[metadatas.size()]));
                List<SimpleCodeElementRelation> relations = Lists.transform(getRelationsFromKoodiVersio(koodiVersio), new KoodinSuhdeToSimpleCodeElementRelation(koodiUri));
                return new KoodiChangesDto(koodiUri, null, koodiVersio.getVersio(), simpleMetadata, null, relations, null, passiveRelations(koodiVersio), koodiVersio.getPaivitysPvm(), koodiVersio.getVoimassaAlkuPvm(), koodiVersio.getVoimassaLoppuPvm(), null, koodiVersio.getTila());
            }
            
            private List<SimpleCodeElementRelation> passiveRelations(KoodiVersio koodiVersio) {
                Collection<KoodinSuhde> passiveRelations = Collections2.filter(getRelationsFromKoodiVersio(koodiVersio), new Predicate<KoodinSuhde>() {
                    
                    @Override
                    public boolean apply(KoodinSuhde input) {
                        return input.isPassive();
                    }
                });
                return new ArrayList<>(Collections2.transform(passiveRelations, new KoodinSuhdeToSimpleCodeElementRelation(koodiVersio.getKoodi().getKoodiUri())));
            }
            
            private List<KoodinSuhde> getRelationsFromKoodiVersio(KoodiVersio koodiVersio) {
                List<KoodinSuhde> allRelations = new ArrayList<>(koodiVersio.getAlakoodis());
                allRelations.addAll(koodiVersio.getYlakoodis());
                return allRelations;
            }
            
        });
        return new ArrayList<>(addedCodeElements);
    }
    
    private List<KoodiChangesDto> removedCodeElements(KoodistoVersio koodistoVersio, KoodistoVersio latest) {
        final Set<KoodistoVersioKoodiVersio> koodiVersios = latest.getKoodiVersios();
        Collection<KoodiChangesDto> removedCodeElements = Collections2.transform(Collections2.filter(koodistoVersio.getKoodiVersios(), new KoodiVersioNotFound(koodiVersios)), new Function<KoodistoVersioKoodiVersio, KoodiChangesDto>() {

            @Override
            public KoodiChangesDto apply(KoodistoVersioKoodiVersio input) {
                KoodiVersio koodiVersio = input.getKoodiVersio();
                String koodiUri = koodiVersio.getKoodi().getKoodiUri();
                Integer versio = koodiBusinessService.getLatestKoodiVersio(koodiUri).getVersio();
                return new KoodiChangesDto(koodiUri, null, versio, null);
            }
            
        });
        return new ArrayList<KoodiChangesDto>(removedCodeElements);
    }
    
    private List<KoodiChangesDto> changedCodeElements(KoodistoVersio koodistoVersio, KoodistoVersio latest) {
        List<KoodiChangesDto> changedCodeElements = new ArrayList<>();
        for (KoodistoVersioKoodiVersio kvkv : koodistoVersio.getKoodiVersios()) {
            KoodiVersio kv = kvkv.getKoodiVersio();
            for (KoodistoVersioKoodiVersio latestKvkv : latest.getKoodiVersios()) {
                KoodiVersio latestKv = latestKvkv.getKoodiVersio();
                if(latestKv.getKoodi().getKoodiUri().equals(kv.getKoodi().getKoodiUri()) && !latestKv.getVersio().equals(kv.getVersio())) {
                    changedCodeElements.add(koodiChangesService.constructChangesDto(kv, latestKv));
                }
            }
        }
        return changedCodeElements;
    }

    private List<SimpleMetadataDto> removedMetadatas(Set<KoodistoMetadata> compareToMetas, final Set<KoodistoMetadata> latestMetas) {
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
    
    private List<SimpleMetadataDto> changedMetadatas(Set<KoodistoMetadata> compareToMetadatas, Set<KoodistoMetadata> latestMetadatas) {
        List<SimpleMetadataDto> changedMetadatas = new ArrayList<>();
        for (KoodistoMetadata latestData : latestMetadatas) {
            KoodistoMetadata metaWithMatchingKieli = getMetadataWithMatchingLanguage(compareToMetadatas, latestData.getKieli());
            SimpleMetadataDto changedMetadata = getChangesForMetadata(latestData, metaWithMatchingKieli);
            if (changedMetadata != null) {
                changedMetadatas.add(changedMetadata);
            }
        }
        
        return changedMetadatas;
    }

    private SimpleMetadataDto getChangesForMetadata(KoodistoMetadata latestData, KoodistoMetadata metaWithMatchingKieli) {
        if (metaWithMatchingKieli == null) {
            return new SimpleMetadataDto(latestData.getNimi(), latestData.getKieli(), latestData.getKuvaus());
        }
        String changedName = latestData.getNimi().equals(metaWithMatchingKieli.getNimi()) ? null : latestData.getNimi();
        String changedDescription = getChangeForMetadataField(latestData.getKuvaus(), metaWithMatchingKieli.getKuvaus());
        return changedName == null && changedDescription == null? null : new SimpleMetadataDto(changedName, latestData.getKieli(), changedDescription);
    }
    
    private String getChangeForMetadataField(String latestData, String matchingData) {
        if (latestData == null && matchingData != null) {
            return ChangesService.REMOVED_METADATA_FIELD;
        }
        return latestData != null && latestData.equals(matchingData) ? null : latestData;
    }

    private KoodistoMetadata getMetadataWithMatchingLanguage(Set<KoodistoMetadata> compareToMetadatas, Kieli kieli) {
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
        Collection<SimpleCodesRelation> removedRelations = Collections2.transform(Collections2.filter(getRelationsFromKoodistoVersio(kv), new KoodistonSuhdeNotFound(relationsFromLatest)), new KoodistonSuhdeToSimpleCodesRelation(koodistoUri));
        return new ArrayList<SimpleCodesRelation>(removedRelations);
    }

    private List<SimpleCodesRelation> addedRelations(KoodistoVersio kv, KoodistoVersio latestKoodistoVersio) {
        final String koodistoUri = latestKoodistoVersio.getKoodisto().getKoodistoUri();
        final List<KoodistonSuhde> relationsFromVersio = getRelationsFromKoodistoVersio(kv);
        Collection<SimpleCodesRelation> relationsAdded = Collections2.transform(Collections2.filter(getRelationsFromKoodistoVersio(latestKoodistoVersio), new KoodistonSuhdeNotFound(relationsFromVersio)), new KoodistonSuhdeToSimpleCodesRelation(koodistoUri));
        return new ArrayList<SimpleCodesRelation>(relationsAdded);
    }

    private List<KoodistonSuhde> getRelationsFromKoodistoVersio(KoodistoVersio kv) {
        List<KoodistonSuhde> allRelations = new ArrayList<>(kv.getAlakoodistos());
        allRelations.addAll(kv.getYlakoodistos());
        return allRelations;
    }
    
    private final class KoodiVersioNotFound implements Predicate<KoodistoVersioKoodiVersio> {
        private final Set<KoodistoVersioKoodiVersio> koodiVersiosToCompare;

        private KoodiVersioNotFound(Set<KoodistoVersioKoodiVersio> koodiVersios) {
            this.koodiVersiosToCompare = koodiVersios;
        }

        @Override
        public boolean apply(KoodistoVersioKoodiVersio input) {
            String koodiUri = input.getKoodiVersio().getKoodi().getKoodiUri();
            for (KoodistoVersioKoodiVersio kvkv : koodiVersiosToCompare) {
                if (kvkv.getKoodiVersio().getKoodi().getKoodiUri().equals(koodiUri)) {
                    return false;
                }
            }
            return true;
        }
    }

    private final class KoodistonSuhdeNotFound implements Predicate<KoodistonSuhde> {
        private final List<KoodistonSuhde> relationsToCompare;

        private KoodistonSuhdeNotFound(List<KoodistonSuhde> relationsToCompare) {
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
            boolean isChild = koodistoUri.equals(input.getYlakoodistoVersio().getKoodisto().getKoodistoUri());
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
