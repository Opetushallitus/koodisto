package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.dao.KoodistoRyhmaMetadataDAO;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.repository.KoodistoRyhmaRepository;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.exception.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Transactional
@Service("koodistoRyhmaBusinessService")
public class KoodistoRyhmaBusinessServiceImpl implements KoodistoRyhmaBusinessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KoodistoRyhmaBusinessServiceImpl.class);

    @Autowired
    private KoodistoRyhmaRepository koodistoRyhmaRepository;
    @Autowired
    private KoodistoRyhmaMetadataDAO koodistoRyhmaMetadataDAO;

    @Override
    public KoodistoRyhma createKoodistoRyhma(final KoodistoRyhmaDto koodistoRyhmaDto) {
        if (koodistoRyhmaDto == null || StringUtils.isBlank(koodistoRyhmaDto.getKoodistoRyhmaUri())) {
            throw new KoodistoRyhmaUriEmptyException();
        }
        if (koodistoRyhmaDto.getId() != null) {
            throw new IllegalArgumentException("Trying to use createKoodistoRyhma() to update KoodistoRyhma, id: " +
                    koodistoRyhmaDto.getId());
        }
        if (koodistoRyhmaRepository.existsKoodistoRyhmaByKoodistoRyhmaUri(koodistoRyhmaDto.getKoodistoRyhmaUri())) {
            throw new IllegalArgumentException("Trying to use createKoodistoRyhma() to update KoodistoRyhma, uri: " +
                    koodistoRyhmaDto.getKoodistoRyhmaUri());
        }
        checkMetadatas(koodistoRyhmaDto.getKoodistoRyhmaMetadatas());

        KoodistoRyhma koodistoRyhma = new KoodistoRyhma();
        koodistoRyhma.setKoodistoRyhmaUri(koodistoRyhmaDto.getKoodistoRyhmaUri());

        for (KoodistoRyhmaMetadata koodistoRyhmaMetadata : koodistoRyhmaDto.getKoodistoRyhmaMetadatas()) {
            koodistoRyhma.addKoodistoRyhmaMetadata(koodistoRyhmaMetadata);
        }
        koodistoRyhma = koodistoRyhmaRepository.save(koodistoRyhma);
        return koodistoRyhma;
    }

    private void checkRequiredMetadataFields(Set<KoodistoRyhmaMetadata> metadatas) {
        for (KoodistoRyhmaMetadata md : metadatas) {
            if (StringUtils.isBlank(md.getNimi())) {
                LOGGER.error("No koodistoryhmä nimi defined for language " + md.getKieli().name());
                throw new KoodistoRyhmaNimiEmptyException();
            }
        }
    }

    private void checkMetadatas(Set<KoodistoRyhmaMetadata> metadatas) {
        if (metadatas == null || metadatas.isEmpty()) {
            throw new MetadataEmptyException();
        } else {
            checkRequiredMetadataFields(metadatas);
        }
    }

    @Override
    public KoodistoRyhma updateKoodistoRyhma(final KoodistoRyhmaDto koodistoRyhmaDto) {
        if (koodistoRyhmaDto == null || StringUtils.isBlank(koodistoRyhmaDto.getKoodistoRyhmaUri())) {
            throw new KoodistoRyhmaUriEmptyException();
        }

        KoodistoRyhma koodistoRyhma = getKoodistoRyhmaById(koodistoRyhmaDto.getId());
        Set<KoodistoRyhmaMetadata> koodistoRyhmaDtoMetadatas = koodistoRyhmaDto.getKoodistoRyhmaMetadatas();
        Set<KoodistoRyhmaMetadata> koodistoRyhmaMetadatas = koodistoRyhma.getKoodistoJoukkoMetadatas();
        Set<KoodistoRyhmaMetadata> removeKoodistoRyhmaMetadatas = new HashSet<>();
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
            koodistoRyhmaMetadataDAO.remove(metadata);
        }
        return koodistoRyhma;
    }

    @Override
    public KoodistoRyhma getKoodistoRyhmaById(final Long id) {
        return koodistoRyhmaRepository.getById(id).orElseThrow(KoodistoRyhmaNotFoundException::new);
    }

    @Override
    public void delete(final Long id) {
        KoodistoRyhma koodistoRyhma = koodistoRyhmaRepository.getById(id)
                .orElseThrow(KoodistoRyhmaNotFoundException::new);
        if (koodistoRyhma.getKoodistos().isEmpty()) {
            koodistoRyhmaRepository.delete(koodistoRyhma);
        } else {
            throw new KoodistoRyhmaNotEmptyException();
        }
    }
}
