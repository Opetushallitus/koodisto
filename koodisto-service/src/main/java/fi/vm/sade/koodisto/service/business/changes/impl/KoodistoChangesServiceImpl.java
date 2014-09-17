package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import fi.vm.sade.koodisto.dto.KoodistoChangesDto;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodisto;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
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
        // TODO Auto-generated method stub
        return null;
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
        MuutosTila changesState = anyChanges(koodistoVersio.getVersio(), latest.getVersio(), changedMetas, removedMetas, dateHandler.anyChanges(), changedTila);
        return new KoodistoChangesDto(changesState, latest.getVersio(), changedMetas, removedMetas, latest.getPaivitysPvm(), 
                dateHandler.startDateChanged, dateHandler.endDateChanged, dateHandler.endDateRemoved, changedTila, null, null, null, null, null, null);
    }
    
    private MuutosTila anyChanges(Integer versio, Integer latestVersio, List<SimpleMetadataDto> changedMetas, List<SimpleMetadataDto> removedMetas, boolean validDateHasChanged, Tila changedTila) {
        if (versio.equals(latestVersio)) {
            return MuutosTila.EI_MUUTOKSIA;
        }
        return changedMetas.size() > 0 || removedMetas.size() > 0 || validDateHasChanged || changedTila != null ? MuutosTila.MUUTOKSIA : MuutosTila.EI_MUUTOKSIA;
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
}
