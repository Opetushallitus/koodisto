package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodiMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Tila;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CodeElementValidatorTest {

    private static CodeElementValidator validator = new CodeElementValidator();

    public static class ValidatingInsert {

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowNullKoodiDtoWhenCreatingCodeElement() {
            validator.validateInsert(null);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutMetadata() {
            KoodiDto dto = new KoodiDto();
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutLanguageDefinedForMetadata() {
            KoodiDto dto = new KoodiDto();
            KoodiMetadataDto data = new KoodiMetadataDto();
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutName() {
            KoodiDto dto = new KoodiDto();
            KoodiMetadataDto data = new KoodiMetadataDto();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutValue() {
            KoodiDto dto = givenCorrectKoodiDto();
            dto.setKoodiArvo("");
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutBeginDate() {
            KoodiDto dto = givenCorrectKoodiDto();
            dto.setVoimassaAlkuPvm(null);
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithInvalidEndDate() {
            KoodiDto dto = givenCorrectKoodiDto();
            dto.setVoimassaLoppuPvm(new Date(0L));
            validator.validateInsert(dto);
        }

        @Test
        public void passessWithAllDataGiven() {
            validator.validateInsert(givenCorrectKoodiDto());
        }

    }

    public static class ValidatingUpdate {

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowNullKoodiDtoWhenUpdatingCodeElement() {
            validator.validateUpdate(null);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodeElementWithoutUri() {
            validator.validateUpdate(new KoodiDto());
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodeElementWithoutLanguageDefinedForMetadata() {
            KoodiDto dto = new KoodiDto();
            dto.setKoodiUri("uri");
            KoodiMetadataDto data = new KoodiMetadataDto();
            dto.setMetadata(Arrays.asList(data));
            validator.validateUpdate(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodeElementWithoutName() {
            KoodiDto dto = new KoodiDto();
            dto.setKoodiUri("uri");
            KoodiMetadataDto data = new KoodiMetadataDto();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateUpdate(dto);
        }

        @Test
        public void passessWithAllDataGiven() {
            validator.validateUpdate(givenCorrectKoodiDto());
        }
    }

    private static KoodiDto givenCorrectKoodiDto() {
        KoodiDto dto = new KoodiDto();
        dto.setMetadata(givenCorrectMetaData());
        dto.setKoodiUri("uri");
        dto.setKoodiArvo("arvo");
        dto.setTila(Tila.LUONNOS);
        dto.setVoimassaAlkuPvm(new Date());
        return dto;
    }

    private static List<KoodiMetadataDto> givenCorrectMetaData() {
        KoodiMetadataDto data = new KoodiMetadataDto();
        data.setNimi("name");
        data.setKuvaus("description");
        data.setLyhytNimi("n");
        data.setKieli(Kieli.FI);
        return Arrays.asList(data);
    }
}