package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.KoodiMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Tila;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class ExtendedCodeElementValidatorTest {

    private static final ExtendedCodeElementValidator validator = new ExtendedCodeElementValidator();

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
            KoodiMetadataDto data = new KoodiMetadataDto();
            dto.setMetadata(List.of(data));
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutValue() {
            ExtendedKoodiDto dto = givenCorrectExtendedKoodiDto();
            dto.setKoodiArvo("");
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutName() {
            ExtendedKoodiDto dto = new ExtendedKoodiDto();
            KoodiMetadataDto data = new KoodiMetadataDto();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(List.of(data));
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

    private static List<KoodiMetadataDto> givenCorrectMetaData() {
        KoodiMetadataDto data = new KoodiMetadataDto();
        data.setNimi("name");
        data.setKuvaus("description");
        data.setLyhytNimi("n");
        data.setKieli(Kieli.FI);
        return List.of(data);
    }
}
