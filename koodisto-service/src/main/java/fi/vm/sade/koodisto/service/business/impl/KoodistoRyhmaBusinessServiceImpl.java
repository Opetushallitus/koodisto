package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.dao.KoodistoRyhmaDAO;
import fi.vm.sade.koodisto.dao.KoodistoRyhmaMetadataDAO;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.exception.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service("koodistoRyhmaBusinessService")
public class KoodistoRyhmaBusinessServiceImpl implements KoodistoRyhmaBusinessService {
    @Autowired
    private KoodistoRyhmaDAO koodistoRyhmaDAO;
    @Autowired
    private KoodistoRyhmaMetadataDAO koodistoRyhmaMetadataDAO;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public KoodistoRyhma createKoodistoRyhma(final KoodistoRyhmaDto koodistoRyhmaDto) {
        if (koodistoRyhmaDto == null || StringUtils.isBlank(koodistoRyhmaDto.getKoodistoRyhmaUri())) {
            throw new KoodistoRyhmaUriEmptyException();
        }
        checkMetadatas(new ArrayList<>(koodistoRyhmaDto.getKoodistoRyhmaMetadatas()));

        KoodistoRyhma koodistoRyhma = new KoodistoRyhma();
        koodistoRyhma.setKoodistoRyhmaUri(koodistoRyhmaDto.getKoodistoRyhmaUri());

        for (KoodistoRyhmaMetadata koodistoRyhmaMetadata : koodistoRyhmaDto.getKoodistoRyhmaMetadatas()) {
            koodistoRyhma.addKoodistoRyhmaMetadata(koodistoRyhmaMetadata);
        }
        koodistoRyhma = koodistoRyhmaDAO.insert(koodistoRyhma);
        return koodistoRyhma;
    }

    private void checkRequiredMetadataFields(List<KoodistoRyhmaMetadata> metadatas) {
        for (KoodistoRyhmaMetadata md : metadatas) {
            if (StringUtils.isBlank(md.getNimi())) {
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
        if (koodistoRyhmaDto == null || StringUtils.isBlank(koodistoRyhmaDto.getKoodistoRyhmaUri())) {
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
            koodistoRyhmaMetadataDAO.remove(metadata);
        }
        return koodistoRyhma;
    }

    @Override
    public KoodistoRyhma getKoodistoRyhmaById(final Long id) {
        try {
            return koodistoRyhmaDAO.findById(id);
        } catch (NoResultException e) {
            throw new KoodistoRyhmaNotFoundException();
        }
    }

    @Override
    public void delete(final Long id) {
        try {
            KoodistoRyhma koodistoRyhma = koodistoRyhmaDAO.findById(id);
            if (koodistoRyhma.getKoodistos().isEmpty()) {
                koodistoRyhmaDAO.remove(koodistoRyhma);
            } else {
                throw new KoodistoRyhmaNotEmptyException();
            }
        } catch (NoResultException e) {
            throw new KoodistoRyhmaNotFoundException();
        }
    }
}
