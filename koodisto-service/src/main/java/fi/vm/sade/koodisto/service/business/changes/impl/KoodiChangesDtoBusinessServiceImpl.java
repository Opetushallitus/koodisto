package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.dto.KoodiChangesDto.MuutosTila;
import fi.vm.sade.koodisto.dto.SimpleKoodiMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.ChangesDateComparator;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesDtoBusinessService;

@Service
public class KoodiChangesDtoBusinessServiceImpl implements KoodiChangesDtoBusinessService {
    
    @Autowired
    private KoodiBusinessService service;

    @Override
    public KoodiChangesDto getChangesDto(String uri, Integer versio, boolean compareToLatestAccepted) {
        KoodiVersio koodiVersio = service.getKoodiVersio(uri, versio);
        KoodiVersio latestKoodiVersio = fetchLatestDesiredCodeVersion(uri, compareToLatestAccepted);
        return constructChangesDto(koodiVersio, latestKoodiVersio);
    }
    
    @Override
    public KoodiChangesDto getChangesDto(String uri, Date date, boolean compareToLatestAccepted) {
        KoodiVersio koodiVersio = determineCodeVersionThatMatchesDate(uri, date);
        KoodiVersio latestKoodiVersio = fetchLatestDesiredCodeVersion(uri, compareToLatestAccepted);
        return constructChangesDto(koodiVersio, latestKoodiVersio);
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

    private KoodiChangesDto constructChangesDto(KoodiVersio koodiVersio, KoodiVersio latestKoodiVersio) {
        List<SimpleKoodiMetadataDto> changedMetas = changedMetadatas(koodiVersio.getMetadatas(), latestKoodiVersio.getMetadatas());
        List<SimpleKoodiMetadataDto> removedMetas = removedMetadatas(koodiVersio.getMetadatas(), latestKoodiVersio.getMetadatas());
        DatesChangedHandler dateHandler = DatesChangedHandler.setDatesHaveChanged(koodiVersio.getVoimassaAlkuPvm(), koodiVersio.getVoimassaLoppuPvm(),
                latestKoodiVersio.getVoimassaAlkuPvm(), latestKoodiVersio.getVoimassaLoppuPvm());
        Tila tilaHasChanged = latestKoodiVersio.getTila().equals(koodiVersio.getTila()) ? null : latestKoodiVersio.getTila();
        MuutosTila muutosTila = anyChanges(koodiVersio.getVersio(), latestKoodiVersio.getVersio(), changedMetas, removedMetas, dateHandler.anyChanges(), tilaHasChanged);
        return new KoodiChangesDto(muutosTila, latestKoodiVersio.getVersio(), changedMetas, removedMetas, null, null, latestKoodiVersio.getPaivitysPvm(), 
                dateHandler.startDateChanged, dateHandler.endDateChanged, dateHandler.endDateRemoved, tilaHasChanged);
    }

    private MuutosTila anyChanges(Integer versio, Integer latestVersio, List<SimpleKoodiMetadataDto> changedMetas, List<SimpleKoodiMetadataDto> removedMetas, boolean anyChangesInValidThruDates, Tila tilaHasChanged) {
        if (versio.equals(latestVersio)) {
            return MuutosTila.EI_MUUTOKSIA;
        }
        if (removedMetas.size() > 0) {
            return MuutosTila.MUUTOKSIA;
        }
        if(anyChangesInValidThruDates) {
            return MuutosTila.MUUTOKSIA;
        }
        if(tilaHasChanged != null) {
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
            return new DateTime(entity.getPaivitysPvm());
        }
        
    }

}
