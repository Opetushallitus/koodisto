package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaMetadataDto;
import fi.vm.sade.koodisto.dto.internal.InternalInsertKoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.repository.KoodistoRyhmaMetadataRepository;
import fi.vm.sade.koodisto.repository.KoodistoRyhmaRepository;
import fi.vm.sade.koodisto.service.business.KoodistoRyhmaBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaExistsException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service("koodistoRyhmaBusinessService")
public class KoodistoRyhmaBusinessServiceImpl implements KoodistoRyhmaBusinessService {

    @Autowired
    private Converter<KoodistoRyhmaMetadataDto, KoodistoRyhmaMetadata> koodistoRyhmaMetadataDtoToKoodistoRyhmaMetadataConverter;
    @Autowired
    private KoodistoRyhmaRepository koodistoRyhmaRepository;
    @Autowired
    private KoodistoRyhmaMetadataRepository koodistoRyhmaMetadataRepository;

    @Override
    @Transactional
    public KoodistoRyhma createKoodistoRyhma(final KoodistoRyhmaDto koodistoRyhmaDto) {
        final KoodistoRyhma koodistoRyhma = new KoodistoRyhma();
        koodistoRyhma.setKoodistoRyhmaUri(koodistoRyhmaDto.getKoodistoRyhmaUri());
        koodistoRyhma.setKoodistoRyhmaMetadatas(convertMetadata(koodistoRyhma, koodistoRyhmaDto));
        return koodistoRyhmaRepository.save(koodistoRyhma);
    }

    private Set<KoodistoRyhmaMetadata> convertMetadata(final KoodistoRyhma koodistoRyhma, KoodistoRyhmaDto dto) {
        return dto.getKoodistoRyhmaMetadatas().stream()
                .map(koodistoRyhmaMetadataDtoToKoodistoRyhmaMetadataConverter::convert)
                .map(metadata -> {
                    metadata.setKoodistoRyhma(koodistoRyhma);
                    return metadata;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public KoodistoRyhma createKoodistoRyhma(InternalInsertKoodistoRyhmaDto insertKoodistoRyhma) {
        String koodistoRyhmaUri = insertKoodistoRyhma.getNimi().getFi();
        if (koodistoRyhmaRepository.existsByKoodistoRyhmaUri(koodistoRyhmaUri)) {
            throw new KoodistoRyhmaExistsException();
        }
        KoodistoRyhma koodistoRyhma = new KoodistoRyhma();
        koodistoRyhma.setKoodistoRyhmaUri(koodistoRyhmaUri);
        koodistoRyhma.setKoodistoRyhmaMetadatas(Set.of(
                KoodistoRyhmaMetadata.builder().kieli(Kieli.FI).nimi(insertKoodistoRyhma.getNimi().getFi()).koodistoRyhma(koodistoRyhma).build(),
                KoodistoRyhmaMetadata.builder().kieli(Kieli.SV).nimi(insertKoodistoRyhma.getNimi().getSv()).koodistoRyhma(koodistoRyhma).build(),
                KoodistoRyhmaMetadata.builder().kieli(Kieli.EN).nimi(insertKoodistoRyhma.getNimi().getEn()).koodistoRyhma(koodistoRyhma).build()
        ));
        return koodistoRyhmaRepository.save(koodistoRyhma);
    }

    @Override
    public KoodistoRyhma updateKoodistoRyhma(final KoodistoRyhmaDto koodistoRyhmaDto) {
        KoodistoRyhma koodistoRyhma = getKoodistoRyhmaById(koodistoRyhmaDto.getId());
        Set<KoodistoRyhmaMetadata> koodistoRyhmaDtoMetadatas = convertMetadata(koodistoRyhma, koodistoRyhmaDto);
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
    public KoodistoRyhma updateKoodistoRyhma(String koodistoRyhmaUri, InternalInsertKoodistoRyhmaDto updateKoodistoRyhma) {
        KoodistoRyhma koodistoRyhma = koodistoRyhmaRepository.findByKoodistoRyhmaUri(koodistoRyhmaUri).orElseThrow(KoodistoRyhmaNotFoundException::new);
        koodistoRyhma.getKoodistoRyhmaMetadatas().stream().filter(a -> a.getKieli().equals(Kieli.EN)).findFirst().orElseGet(() -> getKoodistoRyhmaMetadata(koodistoRyhma, Kieli.EN)).setNimi(updateKoodistoRyhma.getNimi().getEn());
        koodistoRyhma.getKoodistoRyhmaMetadatas().stream().filter(a -> a.getKieli().equals(Kieli.SV)).findFirst().orElseGet(() -> getKoodistoRyhmaMetadata(koodistoRyhma, Kieli.SV)).setNimi(updateKoodistoRyhma.getNimi().getSv());
        koodistoRyhma.getKoodistoRyhmaMetadatas().stream().filter(a -> a.getKieli().equals(Kieli.FI)).findFirst().orElseGet(() -> getKoodistoRyhmaMetadata(koodistoRyhma, Kieli.FI)).setNimi(updateKoodistoRyhma.getNimi().getFi());
        return koodistoRyhma;
    }

    private KoodistoRyhmaMetadata getKoodistoRyhmaMetadata(KoodistoRyhma koodistoRyhma, Kieli kieli) {
        KoodistoRyhmaMetadata a = KoodistoRyhmaMetadata.builder().kieli(kieli).build();
        koodistoRyhma.addKoodistoRyhmaMetadata(a);
        return a;
    }

    @Override
    public KoodistoRyhma getKoodistoRyhmaById(final Long id) {
        if (id == null) {
            throw new KoodistoRyhmaNotFoundException();
        }
        return koodistoRyhmaRepository.findById(id).orElseThrow(KoodistoRyhmaNotFoundException::new);
    }

    @Override
    public KoodistoRyhma getKoodistoRyhmaByUri(String koodistoRyhmaUri) {
        return koodistoRyhmaRepository.findByKoodistoRyhmaUri(koodistoRyhmaUri).orElseThrow(KoodistoRyhmaNotFoundException::new);
    }

    @Override
    public List<KoodistoRyhma> getEmptyKoodistoRyhma() {
        return koodistoRyhmaRepository.findEmptyKoodistoRyhma();
    }

    @Override
    public List<KoodistoRyhma> getKoodistoRyhma() {
        return koodistoRyhmaRepository.findAll();
    }

    @Override
    public void delete(final Long id) {
        delete(koodistoRyhmaRepository::findById, id);
    }

    @Override
    public void delete(String uri) {

        delete(koodistoRyhmaRepository::findByKoodistoRyhmaUri, uri);
    }

    private <T> void delete(KoodistoRyhmaFinder<T> finder, T a) {
        try {
            KoodistoRyhma koodistoRyhma = finder.find(a).orElseThrow(KoodistoRyhmaNotFoundException::new);
            if (koodistoRyhma.getKoodistos().isEmpty()) {
                koodistoRyhmaRepository.delete(koodistoRyhma);
            } else {
                throw new KoodistoRyhmaNotEmptyException();
            }
        } catch (NoSuchElementException e) {
            throw new KoodistoRyhmaNotFoundException();
        }
    }

    private interface KoodistoRyhmaFinder<T> {
        Optional<KoodistoRyhma> find(T a);
    }
}


