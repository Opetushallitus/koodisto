package fi.vm.sade.koodisto.ui.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;

public abstract class KoodistoValidator {

    public static List<String> validate(final KoodiMetadataType koodiMetadataFi, final KoodiMetadataType koodiMetadataSv,
            final KoodiMetadataType koodiMetadataEn) {
        List<String> validationErrors = new ArrayList<String>();

        if (!ValidatorUtil.atLeastOneStringIsNotBlank(koodiMetadataFi.getNimi(), koodiMetadataEn.getNimi(), koodiMetadataSv.getNimi())) {
            validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodiMetadata.nimi"));
        }

        if (!ValidatorUtil.atLeastOneStringIsNotBlank(koodiMetadataFi.getKuvaus(), koodiMetadataEn.getKuvaus(), koodiMetadataSv.getKuvaus())) {
            validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodiMetadata.kuvaus"));
        }

        if (!ValidatorUtil.atLeastOneStringIsNotBlank(koodiMetadataFi.getLyhytNimi(), koodiMetadataEn.getLyhytNimi(), koodiMetadataSv.getLyhytNimi())) {
            validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodiMetadata.lyhytNimi"));
        }

        if (!validateRequiredFieldsAreAllBlankOrAllAreNotBlank(koodiMetadataFi) || !validateRequiredFieldsAreAllBlankOrAllAreNotBlank(koodiMetadataSv)
                || !validateRequiredFieldsAreAllBlankOrAllAreNotBlank(koodiMetadataEn)) {
            validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodiMetadata.nimiAndLyhytNimiAndKuvaus"));
        }

        try {
            if (!validateOtherFieldsAreBlankIfRequiredFieldsAreBlank(koodiMetadataFi)) {
                validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodimetadata.requiredFieldsMissingFi"));
            }

            if (!validateOtherFieldsAreBlankIfRequiredFieldsAreBlank(koodiMetadataSv)) {
                validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodimetadata.requiredFieldsMissingSv"));
            }

            if (!validateOtherFieldsAreBlankIfRequiredFieldsAreBlank(koodiMetadataEn)) {
                validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodimetadata.requiredFieldsMissingEn"));
            }
        } catch (Exception e) {
            // Should not happen
            throw new RuntimeException(e);
        }

        return validationErrors;
    }

    private static boolean validateRequiredFieldsAreAllBlankOrAllAreNotBlank(final KoodiMetadataType metadata) {
        return ValidatorUtil.allStringsAreBlankOrAllStringsAreNotBlank(metadata.getNimi(), metadata.getLyhytNimi(), metadata.getKuvaus());
    }

    private static boolean validateOtherFieldsAreBlankIfRequiredFieldsAreBlank(final KoodiMetadataType metadata) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        if (ValidatorUtil.allStringsAreBlank(metadata.getNimi(), metadata.getLyhytNimi(), metadata.getKuvaus())) {
            return UtilityMethods.allFieldsAreNullOrEmpty(KoodiMetadataType.class, metadata, "nimi", "lyhytNimi", "kuvaus");
        } else {
            return true;
        }
    }

    public static List<String> validate(final KoodistoMetadataType koodistoMetadataFi, final KoodistoMetadataType koodistoMetadataSv,
            final KoodistoMetadataType koodistoMetadataEn) {
        List<String> validationErrors = new ArrayList<String>();

        if (!ValidatorUtil.atLeastOneStringIsNotBlank(koodistoMetadataFi.getNimi(), koodistoMetadataSv.getNimi(), koodistoMetadataEn.getNimi())) {
            validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodistometadata.nimi"));
        }

        if (!ValidatorUtil.atLeastOneStringIsNotBlank(koodistoMetadataFi.getKuvaus(), koodistoMetadataSv.getKuvaus(), koodistoMetadataEn.getKuvaus())) {
            validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodistometadata.kuvaus"));
        }

        if (!validateRequiredFieldsAreAllBlankOrAllAreNotBlank(koodistoMetadataFi) || !validateRequiredFieldsAreAllBlankOrAllAreNotBlank(koodistoMetadataSv)
                || !validateRequiredFieldsAreAllBlankOrAllAreNotBlank(koodistoMetadataEn)) {
            validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodistometadata.nimiAndKuvaus"));
        }

        try {
            if (!validateOtherFieldsAreBlankIfRequiredFieldsAreBlank(koodistoMetadataFi)) {
                validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodistometadata.requiredFieldsMissingFi"));
            }

            if (!validateOtherFieldsAreBlankIfRequiredFieldsAreBlank(koodistoMetadataSv)) {
                validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodistometadata.requiredFieldsMissingSv"));
            }

            if (!validateOtherFieldsAreBlankIfRequiredFieldsAreBlank(koodistoMetadataEn)) {
                validationErrors.add(I18N.getMessage("koodistoValidator.validate.koodistometadata.requiredFieldsMissingEn"));
            }

        } catch (Exception e) {
            // Should not happen
            throw new RuntimeException(e);
        }

        return validationErrors;
    }

    private static boolean validateRequiredFieldsAreAllBlankOrAllAreNotBlank(final KoodistoMetadataType metadata) {
        return ValidatorUtil.allStringsAreBlankOrAllStringsAreNotBlank(metadata.getNimi(), metadata.getKuvaus());
    }

    private static boolean validateOtherFieldsAreBlankIfRequiredFieldsAreBlank(final KoodistoMetadataType metadata) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        if (ValidatorUtil.allStringsAreBlank(metadata.getNimi(), metadata.getKuvaus())) {
            return UtilityMethods.allFieldsAreNullOrEmpty(KoodistoMetadataType.class, metadata, "nimi", "kuvaus");
        } else {
            return true;
        }
    }
}
