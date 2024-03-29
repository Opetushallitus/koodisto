package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaMetadataDto;
import fi.vm.sade.koodisto.repository.KoodiRepository;
import fi.vm.sade.koodisto.repository.KoodistoRepository;
import fi.vm.sade.koodisto.repository.KoodistoRyhmaRepository;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UriTransliteratorImpl implements UriTransliterator {

    private static final Map<String, String> TRANSLITERATION = new HashMap<>();
    private static final KieliType[] PREFERRED_ORDER = {KieliType.FI, KieliType.SV, KieliType.EN};

    static {
        String[][] specialCharacters = new String[][]{{"å", "o"}, {"ä", "a"}, {"ö", "o"}};

        String[] allowedCharacters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        for (String c : allowedCharacters) {
            TRANSLITERATION.put(c, c);
        }

        for (String[] c : specialCharacters) {
            TRANSLITERATION.put(c[0], c[1]);
        }
    }

    @Autowired
    @Lazy
    private KoodiRepository koodiRepository;
    @Autowired
    private KoodistoRepository koodistoRepository;
    @Autowired
    private KoodistoRyhmaRepository koodistoRyhmaRepository;

    private static String transliterate(String value) {
        StringBuilder b = new StringBuilder();

        value = value.toLowerCase();
        for (int i = 0; i < value.length(); ++i) {
            String c = value.substring(i, i + 1);

            if (TRANSLITERATION.containsKey(c)) {
                b.append(TRANSLITERATION.get(c));
            }
        }

        return b.toString();
    }

    private static KoodistoRyhmaMetadataDto getKoodistoRyhmaMetadataForLanguage(Collection<KoodistoRyhmaMetadataDto> metadatas, KieliType kieli) {
        KoodistoRyhmaMetadataDto found = null;

        for (KoodistoRyhmaMetadataDto m : metadatas) {
            if (kieli.value().equals(m.getKieli().name())) {
                found = m;
                break;
            }
        }
        return found;
    }

    @Override
    public String generateKoodistoUriByMetadata(Collection<KoodistoMetadataType> metadatas) {

        KoodistoMetadataType meta = null;
        for (KieliType k : PREFERRED_ORDER) {
            meta = KoodistoHelper.getKoodistoMetadataForLanguage(metadatas, k);
            if (meta != null) {
                break;
            }
        }

        if (meta == null) {
            throw new MetadataEmptyException();
        }

        String baseKoodistoUri = transliterate(meta.getNimi());
        if (baseKoodistoUri.isBlank()) {
            baseKoodistoUri = "-";
        }

        String koodistoUri = baseKoodistoUri;

        int i = 1;
        while (koodistoRepository.existsByKoodistoUri(koodistoUri)) {
            koodistoUri = baseKoodistoUri + "-" + i;
            ++i;
        }

        return koodistoUri;
    }

    @Override
    public String generateKoodistoGroupUriByMetadata(Collection<KoodistoRyhmaMetadataDto> metadatas) {

        KoodistoRyhmaMetadataDto meta = null;
        for (KieliType k : PREFERRED_ORDER) {
            meta = getKoodistoRyhmaMetadataForLanguage(metadatas, k);
            if (meta != null && !meta.getNimi().isBlank()) {
                break;
            }
        }

        if (meta == null) {
            throw new MetadataEmptyException();
        }

        String baseKoodistoRyhmaUri = transliterate(meta.getNimi());
        if (baseKoodistoRyhmaUri.isBlank()) {
            baseKoodistoRyhmaUri = "-";
        }
        String koodistoRyhmaUri = baseKoodistoRyhmaUri;

        int i = 1;
        while (koodistoRyhmaRepository.existsByKoodistoRyhmaUri(koodistoRyhmaUri)) {
            koodistoRyhmaUri = baseKoodistoRyhmaUri + "-" + i;
            ++i;
        }

        return koodistoRyhmaUri;
    }


    @Override
    public String generateKoodiUriByKoodistoUriAndKoodiArvo(String koodistoUri, String koodiArvo) {
        String arvoTransliterated = transliterate(koodiArvo);
        if (arvoTransliterated.isBlank()) {
            arvoTransliterated = "-";
        }

        String baseKoodiUri = koodistoUri + "_" + arvoTransliterated;
        String koodiUri = baseKoodiUri;

        int i = 1;
        while (koodiRepository.existsByKoodiUri(koodiUri)) {
            koodiUri = baseKoodiUri + "-" + i;
            ++i;
        }

        return koodiUri;
    }
}
