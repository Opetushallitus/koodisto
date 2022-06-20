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

    private static final CodeElementValidator validator = new CodeElementValidator();

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

    public static class ValidatingInsert {

        @Test
        public void passessWithAllDataGiven() {
            validator.validateInsert(givenCorrectKoodiDto());
        }
    }

    public static class ValidatingUpdate {

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodeElementWithoutUri() {
            validator.validateUpdate(new KoodiDto());
        }

        @Test
        public void passessWithAllDataGiven() {
            validator.validateUpdate(givenCorrectKoodiDto());
        }
    }
}
