package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.validator.ExtendedCodeElementValidator;
import fi.vm.sade.koodisto.validator.KoodistoValidationException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ExtendedCodeElementValidatorTest {

    private static ExtendedCodeElementValidator validator = new ExtendedCodeElementValidator();

    public static class ValidatingInsert {

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowNullKoodiDtoWhenCreatingCodeElement() {
            validator.validateInsert(null);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutMetadata() {
            ExtendedKoodiDto dto = new ExtendedKoodiDto();
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutLanguageDefinedForMetadata() {
            ExtendedKoodiDto dto = new ExtendedKoodiDto();
            KoodiMetadata data = new KoodiMetadata();
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutValue() {
            ExtendedKoodiDto dto = givenCorrectExtendedKoodiDto();
            dto.setKoodiArvo("");
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutBeginDate() {
            ExtendedKoodiDto dto = givenCorrectExtendedKoodiDto();
            dto.setVoimassaAlkuPvm(null);
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithInvalidEndDate() {
            ExtendedKoodiDto dto = givenCorrectExtendedKoodiDto();
            dto.setVoimassaLoppuPvm(new Date(0L));
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutName() {
            ExtendedKoodiDto dto = new ExtendedKoodiDto();
            KoodiMetadata data = new KoodiMetadata();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto);
        }

        @Test
        public void passessWithAllDataGiven() {
            validator.validateInsert(givenCorrectExtendedKoodiDto());
        }

    }

    private static ExtendedKoodiDto givenCorrectExtendedKoodiDto() {
        ExtendedKoodiDto dto = new ExtendedKoodiDto();
        dto.setMetadata(givenCorrectMetaData());
        dto.setKoodiUri("uri");
        dto.setKoodiArvo("arvo");
        dto.setTila(Tila.LUONNOS);
        dto.setVoimassaAlkuPvm(new Date());
        return dto;
    }

    private static List<KoodiMetadata> givenCorrectMetaData() {
        KoodiMetadata data = new KoodiMetadata();
        data.setNimi("name");
        data.setKuvaus("description");
        data.setLyhytNimi("n");
        data.setKieli(Kieli.FI);
        return Arrays.asList(data);
    }
}