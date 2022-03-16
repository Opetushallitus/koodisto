package fi.vm.sade.koodisto.service.business.impl;

import com.google.common.base.Strings;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.repository.KoodistoRyhmaMetadataRepository;
import fi.vm.sade.koodisto.repository.KoodistoRyhmaRepository;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service("koodistoRyhmaBusinessService")
public class KoodistoRyhmaBusinessServiceImpl implements KoodistoRyhmaBusinessService {
    @Autowired
    private KoodistoRyhmaRepository koodistoRyhmaRepository;
    @Autowired
    private KoodistoRyhmaMetadataRepository koodistoRyhmaMetadataRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public KoodistoRyhma createKoodistoRyhma(final KoodistoRyhmaDto koodistoRyhmaDto) {
        if (koodistoRyhmaDto == null || Strings.isNullOrEmpty(koodistoRyhmaDto.getKoodistoRyhmaUri())) {
            throw new KoodistoRyhmaUriEmptyException();
        }
        checkMetadatas(new ArrayList<>(koodistoRyhmaDto.getKoodistoRyhmaMetadatas()));

        KoodistoRyhma koodistoRyhma = new KoodistoRyhma();
        koodistoRyhma.setKoodistoRyhmaUri(koodistoRyhmaDto.getKoodistoRyhmaUri());

        for (KoodistoRyhmaMetadata koodistoRyhmaMetadata : koodistoRyhmaDto.getKoodistoRyhmaMetadatas()) {
            koodistoRyhma.addKoodistoRyhmaMetadata(koodistoRyhmaMetadata);
        }
        koodistoRyhma = koodistoRyhmaRepository.save(koodistoRyhma);
        return koodistoRyhma;
    }

    private void checkRequiredMetadataFields(List<KoodistoRyhmaMetadata> metadatas) {
        for (KoodistoRyhmaMetadata md : metadatas) {
            if (md.getNimi().isBlank()) {
                logger.error("No koodistoryhmä nimi defined for language " + md.getKieli().name());
                throw new KoodistoRyhmaNimiEmptyException();
            }
        }
    }

    private void checkMetadatas(List<KoodistoRyhmaMetadata> metadatas) {
        if (metadatas == null || metadatas.isEmpty()) {
            throw new MetadataEmptyException();
        } else {
            checkRequiredMetadataFields(metadatas);
        }
    }

    @Override
    public KoodistoRyhma updateKoodistoRyhma(final KoodistoRyhmaDto koodistoRyhmaDto) {
        if (koodistoRyhmaDto == null || koodistoRyhmaDto.getKoodistoRyhmaUri().isBlank()) {
            throw new KoodistoRyhmaUriEmptyException();
        }

        KoodistoRyhma koodistoRyhma = getKoodistoRyhmaById(koodistoRyhmaDto.getId());
        Set<KoodistoRyhmaMetadata> koodistoRyhmaDtoMetadatas = koodistoRyhmaDto.getKoodistoRyhmaMetadatas();
        Set<KoodistoRyhmaMetadata> koodistoRyhmaMetadatas = koodistoRyhma.getKoodistoJoukkoMetadatas();
        Set<KoodistoRyhmaMetadata> removeKoodistoRyhmaMetadatas = new HashSet<KoodistoRyhmaMetadata>();
        koodistoRyhma.setKoodistoRyhmaUri(koodistoRyhmaDto.getKoodistoRyhmaUri());
        for (KoodistoRyhmaMetadata updatedMetadata : koodistoRyhmaDtoMetadatas) {
            boolean found = false;
            for (KoodistoRyhmaMetadata existingMetadata : koodistoRyhmaMetadatas) {
                if (updatedMetadata.getKieli().equals(existingMetadata.getKieli())) {
                    if (updatedMetadata.getNimi().isEmpty()) {
                        removeKoodistoRyhmaMetadatas.add(existingMetadata);
                    } else {
                        existingMetadata.setNimi(updatedMetadata.getNimi());
                    }
                    found = true;
                }
            }
            if (!found && !updatedMetadata.getNimi().isEmpty()) {
                koodistoRyhma.addKoodistoRyhmaMetadata(updatedMetadata);
            }
        }
        for (KoodistoRyhmaMetadata metadata : removeKoodistoRyhmaMetadatas) {
            koodistoRyhma.removeKoodistoRyhmaMetadata(metadata);
            koodistoRyhmaMetadataRepository.delete(metadata);
        }
        return koodistoRyhma;
    }

    @Override
    public KoodistoRyhma getKoodistoRyhmaById(final Long id) {
        if (id == null) {
            throw new KoodistoRyhmaNotFoundException();
        }
        return koodistoRyhmaRepository.findById(id).orElseThrow(KoodistoRyhmaNotFoundException::new);
    }

    @Override
    public void delete(final Long id) {
        try {
            KoodistoRyhma koodistoRyhma = koodistoRyhmaRepository.findById(id).orElseThrow();
            if (koodistoRyhma.getKoodistos().isEmpty()) {
                koodistoRyhmaRepository.delete(koodistoRyhma);
            } else {
                throw new KoodistoRyhmaNotEmptyException();
            }
        } catch (NoSuchElementException e) {
            throw new KoodistoRyhmaNotFoundException();
        }
    }
}
