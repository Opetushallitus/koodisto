package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.dao.KoodistoRyhmaDAO;
import fi.vm.sade.koodisto.dao.KoodistoRyhmaMetadataDAO;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.exception.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public KoodistoRyhma createKoodistoRyhma(final KoodistoRyhmaDto koodistoRyhmaDto) {
        if (koodistoRyhmaDto == null || StringUtils.isBlank(koodistoRyhmaDto.getKoodistoRyhmaUri())) {
            throw new KoodistoRyhmaUriEmptyException("Koodistoryhmä URI is empty");
        }
        List<KoodistoRyhmaMetadata> metadatas = new ArrayList();
        metadatas.addAll(koodistoRyhmaDto.getKoodistoRyhmaMetadatas());
        checkMetadatas(metadatas);

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
                throw new KoodistoRyhmaNimiEmptyException("No koodistoryhmä nimi defined for language " + md.getKieli().name());
            }
        }
    }

    private void checkMetadatas(List<KoodistoRyhmaMetadata> metadatas) {
        if (metadatas == null || metadatas.isEmpty()) {
            throw new MetadataEmptyException("Metadata list is empty");
        } else {
            checkRequiredMetadataFields(metadatas);
        }
    }

    @Override
    public KoodistoRyhma updateKoodistoRyhma(final KoodistoRyhmaDto koodistoRyhmaDto) {
        if (koodistoRyhmaDto == null || StringUtils.isBlank(koodistoRyhmaDto.getKoodistoRyhmaUri())) {
            throw new KoodistoRyhmaUriEmptyException("Koodistoryhmä URI is empty");
        }
        List<KoodistoRyhmaMetadata> metadatas = new ArrayList();
        metadatas.addAll(koodistoRyhmaDto.getKoodistoRyhmaMetadatas());


        KoodistoRyhma koodistoRyhma = getKoodistoRyhmaById(koodistoRyhmaDto.getId());
        Set<KoodistoRyhmaMetadata> koodistoRyhmaDtoMetadatas = koodistoRyhmaDto.getKoodistoRyhmaMetadatas();
        Set<KoodistoRyhmaMetadata> koodistoRyhmaMetadatas = koodistoRyhma.getKoodistoJoukkoMetadatas();
        Set<KoodistoRyhmaMetadata> removeKoodistoRyhmaMetadatas = new HashSet();
        koodistoRyhma.setKoodistoRyhmaUri(koodistoRyhmaDto.getKoodistoRyhmaUri());
        for (KoodistoRyhmaMetadata metadata : koodistoRyhmaDtoMetadatas) {
            boolean found = false;
            for (KoodistoRyhmaMetadata koodistoRyhmaMetadata : koodistoRyhmaMetadatas) {
                if (metadata.getKieli().equals(koodistoRyhmaMetadata.getKieli())) {
                    if (metadata.getNimi().isEmpty()) {
                        removeKoodistoRyhmaMetadatas.add(koodistoRyhmaMetadata);
                    } else {
                        koodistoRyhmaMetadata.setNimi(metadata.getNimi());
                    }
                    found = true;
                }
            }
            if (!found) {
                koodistoRyhma.addKoodistoRyhmaMetadata(metadata);
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
        KoodistoRyhma koodistoRyhma = koodistoRyhmaDAO.findById(id);
        return koodistoRyhma;
    }
}
