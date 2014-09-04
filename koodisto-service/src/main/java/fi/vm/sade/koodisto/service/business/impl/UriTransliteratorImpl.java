package fi.vm.sade.koodisto.service.business.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.koodisto.dao.KoodiDAO;
import fi.vm.sade.koodisto.dao.KoodistoDAO;
import fi.vm.sade.koodisto.dao.KoodistoRyhmaDAO;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.UriTransliterator;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.util.KoodistoHelper;

/**
 * User: kwuoti Date: 21.3.2013 Time: 15.20
 */
@Component
public class UriTransliteratorImpl implements UriTransliterator {

    @Autowired
    private KoodiDAO koodiDAO;

    @Autowired
    private KoodistoDAO koodistoDAO;

    @Autowired
    private KoodistoRyhmaDAO koodistoRyhmaDAO;

    private static final Map<Character, Character> TRANSLITERATION = new HashMap<Character, Character>();

    static {
        Character[][] specialCharacters = new Character[][] { { 'å', 'o' }, { 'ä', 'a' }, { 'ö', 'o' } };

        Character[] allowedCharacters = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

        for (Character c : allowedCharacters) {
            TRANSLITERATION.put(c, c);
        }

        for (Character[] c : specialCharacters) {
            TRANSLITERATION.put(c[0], c[1]);
        }
    }

    private static final KieliType[] PREFERRED_ORDER = { KieliType.FI, KieliType.SV, KieliType.EN };

    private static String transliterate(String value) {
        StringBuilder b = new StringBuilder();

        value = value.toLowerCase();
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);

            if (TRANSLITERATION.containsKey(c)) {
                b.append(TRANSLITERATION.get(c));
            }
        }

        return b.toString();
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
        if (StringUtils.isBlank(baseKoodistoUri)) {
            baseKoodistoUri = "-";
        }

        String koodistoUri = baseKoodistoUri;

        int i = 1;
        while (koodistoDAO.koodistoUriExists(koodistoUri)) {
            koodistoUri = baseKoodistoUri + "-" + i;
            ++i;
        }

        return koodistoUri;
    }

    private static KoodistoRyhmaMetadata getKoodistoRyhmaMetadataForLanguage(Collection<KoodistoRyhmaMetadata> metadatas, KieliType kieli) {
        KoodistoRyhmaMetadata found = null;

        for (KoodistoRyhmaMetadata m : metadatas) {
            if (kieli.value().equals(m.getKieli().name())) {
                found = m;
                break;
            }
        }
        return found;
    }

    @Override
    public String generateKoodistoGroupUriByMetadata(Collection<KoodistoRyhmaMetadata> metadatas) {

        KoodistoRyhmaMetadata meta = null;
        for (KieliType k : PREFERRED_ORDER) {
            meta = getKoodistoRyhmaMetadataForLanguage(metadatas, k);
            if (meta != null) {
                break;
            }
        }

        if (meta == null) {
            throw new MetadataEmptyException();
        }

        String baseKoodistoRyhmaUri = transliterate(meta.getNimi());
        if (StringUtils.isBlank(baseKoodistoRyhmaUri)) {
            baseKoodistoRyhmaUri = "-";
        }
        if (!StringUtils.containsIgnoreCase(baseKoodistoRyhmaUri, "http://")) {
            baseKoodistoRyhmaUri = "http://" + baseKoodistoRyhmaUri;
        }
        String koodistoRyhmaUri = baseKoodistoRyhmaUri;

        int i = 1;
        while (koodistoRyhmaDAO.koodistoRyhmaUriExists(koodistoRyhmaUri)) {
            koodistoRyhmaUri = baseKoodistoRyhmaUri + "-" + i;
            ++i;
        }

        return koodistoRyhmaUri;
    }



    @Override
    public String generateKoodiUriByKoodistoUriAndKoodiArvo(String koodistoUri, String koodiArvo) {
        String arvoTransliterated = transliterate(koodiArvo);
        if (StringUtils.isBlank(arvoTransliterated)) {
            arvoTransliterated = "-";
        }

        String baseKoodiUri = koodistoUri + "_" + arvoTransliterated;
        String koodiUri = baseKoodiUri;

        int i = 1;
        while (koodiDAO.koodiUriExists(koodiUri)) {
            koodiUri = baseKoodiUri + "-" + i;
            ++i;
        }

        return koodiUri;
    }
}
