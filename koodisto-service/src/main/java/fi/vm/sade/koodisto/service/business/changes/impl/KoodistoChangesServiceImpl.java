package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.koodisto.dto.KoodistoChangesDto;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
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
        KoodistoVersio latest = koodistoService.getLatestKoodistoVersio(uri);
        return constructChangesDto(koodistoVersio, latest);
    }

    @Override
    public KoodistoChangesDto getChangesDto(String uri, Date date, boolean compareToLatestAccepted) {
        // TODO Auto-generated method stub
        return null;
    }

    private KoodistoChangesDto constructChangesDto(KoodistoVersio koodistoVersio, KoodistoVersio latest) {
        List<SimpleMetadataDto> changedMetas = changedMetadatas(koodistoVersio.getMetadatas(), latest.getMetadatas());
        MuutosTila changesState = anyChanges(koodistoVersio.getVersio(), latest.getVersio(), changedMetas);
        return new KoodistoChangesDto(changesState, latest.getVersio(), changedMetas, null, latest.getPaivitysPvm(), null, null, null, null, null, null, null, null, null, null);
    }
    
    private MuutosTila anyChanges(Integer versio, Integer latestVersio, List<SimpleMetadataDto> changedMetas) {
        if (versio.equals(latestVersio)) {
            return MuutosTila.EI_MUUTOKSIA;
        }
        return changedMetas.size() > 0 ? MuutosTila.MUUTOKSIA : MuutosTila.EI_MUUTOKSIA;
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
