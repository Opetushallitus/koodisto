package fi.vm.sade.koodisto.util;

import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.*;

import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public abstract class KoodistoHelper {
    public static KoodiMetadataType getKoodiMetadataForLanguage(Collection<KoodiMetadataType> metadatas, KieliType kieli) {
        KoodiMetadataType found = null;

        for (KoodiMetadataType m : metadatas) {
            if (kieli.equals(m.getKieli())) {
                found = m;
                break;
            }
        }

        return found;
    }

    public static KoodistoMetadataType getKoodistoMetadataForLanguage(Collection<KoodistoMetadataType> metadatas, KieliType kieli) {
        KoodistoMetadataType found = null;

        for (KoodistoMetadataType m : metadatas) {
            if (kieli.equals(m.getKieli())) {
                found = m;
                break;
            }
        }

        return found;
    }

    public static KoodiMetadataType getKoodiMetadataForLanguage(KoodiType koodi, KieliType kieli) {
        if (koodi != null) {
            for (KoodiMetadataType m : koodi.getMetadata()) {
                if (m.getKieli().equals(kieli)) {
                    return m;
                }
            }
        }
        return null;
    }

    public static KoodiMetadataType getKoodiMetadataForAnyLanguage(KoodiType koodi) {
        if (koodi != null) {
            KoodiMetadataType mSv = null;
            KoodiMetadataType mEn = null;
            for (KoodiMetadataType m : koodi.getMetadata()) {
                KieliType k = m.getKieli();
                if (k == KieliType.FI) {
                    return m;
                } else if (k == KieliType.SV) {
                    mSv = m;
                } else if (k == KieliType.EN) {
                    mEn = m;
                }
            }
            if (mSv != null)
                return mSv;
            return mEn;
        }
        return null;
    }

    public static KoodistoMetadataType getKoodistoMetadataForLanguage(KoodistoType koodisto, KieliType kieli) {
        if (koodisto != null) {
            for (KoodistoMetadataType m : koodisto.getMetadataList()) {
                if (m.getKieli().equals(kieli)) {
                    return m;
                }

            }
        }

        return null;
    }

    public static KoodistoMetadataType getKoodistoMetadataForAnyLanguage(KoodistoType koodisto) {
        if (koodisto != null) {
            KoodistoMetadataType mSv = null;
            KoodistoMetadataType mEn = null;
            for (KoodistoMetadataType m : koodisto.getMetadataList()) {
                KieliType k = m.getKieli();
                if (k == KieliType.FI) {
                    return m;
                } else if (k == KieliType.SV) {
                    mSv = m;
                } else if (k == KieliType.EN) {
                    mEn = m;
                }
            }
            if (mSv != null)
                return mSv;
            return mEn;
        }
        return null;
    }

    public static KoodistoMetadataType getKoodistoMetadataForLanguage(KoodistoVersioListType koodisto, KieliType kieli) {
        if (koodisto != null) {
            for (KoodistoMetadataType m : koodisto.getMetadataList()) {
                if (m.getKieli().equals(kieli)) {
                    return m;
                }

            }
        }

        return null;
    }

    public static KoodistoMetadataType getKoodistoMetadataWithAvailableName(KoodistoVersioListType koodisto) {
        if (koodisto != null) {
            for (KoodistoMetadataType m : koodisto.getMetadataList()) {
                if (StringUtils.isNotBlank(m.getNimi())) {
                    return m;
                }

            }
        }

        return null;
    }

    public static KoodistoRyhmaMetadataType getKoodistoRyhmaMetadataForLanguage(KoodistoRyhmaListType ryhma, KieliType kieli) {
        if (ryhma != null) {
            for (KoodistoRyhmaMetadataType m : ryhma.getKoodistoRyhmaMetadatas()) {
                if (m.getKieli().equals(kieli)) {
                    return m;
                }

            }
        }
        return null;
    }

    public static KieliType getKieliForLocale(Locale locale) {
        return KieliType.valueOf(locale.getLanguage().toUpperCase());
    }

    public static String createNameForKoodiVersio(KoodiType koodi, KieliType kieli) {
        KoodiMetadataType koodiMetadata = getKoodiMetadataForLanguage(koodi, kieli);
        String caption = "N/A v. " + koodi.getVersio();
        if (koodiMetadata != null && koodiMetadata.getNimi() != null && !koodiMetadata.getNimi().isEmpty()) {
            caption = koodiMetadata.getNimi() + " v. " + koodi.getVersio();
        }
        return caption;
    }

    public static void copyFields(KoodistoType from, CreateKoodistoDataType to) {
        to.setLukittu(from.isLukittu());
        to.setOmistaja(from.getOmistaja());
        to.setOrganisaatioOid(from.getOrganisaatioOid());
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());
        to.getMetadataList().clear();
        to.getMetadataList().addAll(from.getMetadataList());
    }

    public static void copyFields(KoodistoType from, UpdateKoodistoDataType to) {
        to.setKoodistoUri(from.getKoodistoUri());
        to.setLukittu(from.isLukittu());
        to.setOmistaja(from.getOmistaja());
        to.setOrganisaatioOid(from.getOrganisaatioOid());
        to.setTila(from.getTila());
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());
        to.getMetadataList().clear();
        to.getMetadataList().addAll(from.getMetadataList());
        to.setVersio(from.getVersio());
        to.setLockingVersion(from.getLockingVersion());
    }

    public static void copyFields(KoodiType from, CreateKoodiDataType to) {
        to.setKoodiArvo(from.getKoodiArvo());
        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());
        to.getMetadata().clear();
        to.getMetadata().addAll(from.getMetadata());
    }

    public static void copyFields(KoodiType from, UpdateKoodiDataType to) {
        to.setKoodiArvo(from.getKoodiArvo());
        to.setKoodiUri(from.getKoodiUri());
        if (!TilaType.HYVAKSYTTY.equals(from.getTila())) {
            to.setTila(UpdateKoodiTilaType.valueOf(from.getTila().name()));
        }

        to.setVoimassaAlkuPvm(from.getVoimassaAlkuPvm());
        to.setVoimassaLoppuPvm(from.getVoimassaLoppuPvm());
        to.getMetadata().clear();
        to.getMetadata().addAll(from.getMetadata());
        to.setVersio(from.getVersio());
        to.setLockingVersion(from.getLockingVersion());
    }
}
