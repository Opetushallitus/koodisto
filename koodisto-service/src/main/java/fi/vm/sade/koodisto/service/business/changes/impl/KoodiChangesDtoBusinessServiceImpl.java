package fi.vm.sade.koodisto.service.business.changes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.dto.KoodiChangesDto.MuutosTila;
import fi.vm.sade.koodisto.dto.SimpleKoodiMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.KoodiChangesDtoBusinessService;

@Service
public class KoodiChangesDtoBusinessServiceImpl implements KoodiChangesDtoBusinessService {
    
    @Autowired
    private KoodiBusinessService service;

    @Override
    public KoodiChangesDto getChangesDto(String uri, Integer versio) {
        KoodiVersio koodiVersio = service.getKoodiVersio(uri, versio);
        KoodiVersio latestKoodiVersio = service.getLatestKoodiVersio(uri);
        List<SimpleKoodiMetadataDto> changedMetas = changedMetadatas(koodiVersio.getMetadatas(), latestKoodiVersio.getMetadatas());
        List<SimpleKoodiMetadataDto> removedMetas = removedMetadatas(koodiVersio.getMetadatas(), latestKoodiVersio.getMetadatas());
        MuutosTila muutosTila = anyChanges(versio, latestKoodiVersio.getVersio(), changedMetas, removedMetas);
        return new KoodiChangesDto(muutosTila, latestKoodiVersio.getVersio(), changedMetas, removedMetas, null, null, null, null, null, null);
    }

    private List<SimpleKoodiMetadataDto> removedMetadatas(Set<KoodiMetadata> compareToMetas, final Set<KoodiMetadata> latestMetas) {
        Collection<SimpleKoodiMetadataDto> removedMetas = Collections2.transform(Collections2.filter(compareToMetas, new Predicate<KoodiMetadata>() {

            @Override
            public boolean apply(KoodiMetadata input) {
                return !latestMetas.contains(input);
            }
            
        }), new Function<KoodiMetadata, SimpleKoodiMetadataDto>() {

            @Override
            public SimpleKoodiMetadataDto apply(KoodiMetadata input) {
                return new SimpleKoodiMetadataDto(input.getNimi(), input.getKieli(), input.getKuvaus(), input.getLyhytNimi());
            }
        });
        
        return new ArrayList<>(removedMetas);
    }

    private MuutosTila anyChanges(Integer versio, Integer latestVersio, List<SimpleKoodiMetadataDto> changedMetas, List<SimpleKoodiMetadataDto> removedMetas) {
        if (versio.equals(latestVersio)) {
            return MuutosTila.EI_MUUTOKSIA;
        }
        if (removedMetas.size() > 0) {
            return MuutosTila.MUUTOKSIA;
        }
        return changedMetas.size() > 0 ? MuutosTila.MUUTOKSIA : MuutosTila.EI_MUUTOKSIA;
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

}
