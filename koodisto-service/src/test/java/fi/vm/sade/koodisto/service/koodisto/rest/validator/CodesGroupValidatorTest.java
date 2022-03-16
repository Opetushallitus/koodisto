package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.validator.CodesGroupValidator;
import fi.vm.sade.koodisto.validator.KoodistoValidationException;
import fi.vm.sade.koodisto.validator.RestValidator;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class CodesGroupValidatorTest {

    private static RestValidator<KoodistoRyhmaDto> validator = new CodesGroupValidator();

    public static class ValidateInsert {

        @Test(expected = KoodistoValidationException.class)
        public void throwsExceptionIfCOdesGroupIsNull() {
            validator.validateInsert(null);
        }

        @Test(expected = KoodistoValidationException.class)
        public void throwsExceptionIfMissingMetadata() {
            validator.validateInsert(new KoodistoRyhmaDto());
        }

        @Test(expected = KoodistoValidationException.class)
        public void throwsExceptionIfLanguageIsNotGiven() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            KoodistoRyhmaMetadata data = new KoodistoRyhmaMetadata();
            data.setNimi("name");
            dto.setKoodistoRyhmaMetadatas(new HashSet<KoodistoRyhmaMetadata>(Arrays.asList(data)));
            validator.validateInsert(dto);
        }

        @Test(expected = KoodistoValidationException.class)
        public void throwsExceptionIfNameIsNotGiven() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            KoodistoRyhmaMetadata data = new KoodistoRyhmaMetadata();
            data.setNimi(" ");
            data.setKieli(Kieli.EN);
            dto.setKoodistoRyhmaMetadatas(new HashSet<KoodistoRyhmaMetadata>(Arrays.asList(data)));
            validator.validateInsert(dto);
        }

        @Test
        public void passes() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            KoodistoRyhmaMetadata data = new KoodistoRyhmaMetadata();
            data.setNimi("name");
            data.setKieli(Kieli.EN);
            dto.setKoodistoRyhmaMetadatas(new HashSet<KoodistoRyhmaMetadata>(Arrays.asList(data)));
            validator.validateInsert(dto);
        }
    }

    public static class ValidateUpdate {

        @Test(expected = KoodistoValidationException.class)
        public void throwsExceptionIfCodesGroupIsNull() {
            validator.validateUpdate(null);
        }

        @Test(expected = KoodistoValidationException.class)
        public void throwsExceptionIfCodesGroupUriIsBlank() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            dto.setKoodistoRyhmaUri(" ");
            validator.validateUpdate(dto);
        }

        @Test
        public void passes() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            dto.setKoodistoRyhmaUri("group");
            KoodistoRyhmaMetadata data = new KoodistoRyhmaMetadata();
            data.setNimi("name");
            data.setKieli(Kieli.EN);
            dto.setKoodistoRyhmaMetadatas(new HashSet<KoodistoRyhmaMetadata>(Arrays.asList(data)));
            validator.validateUpdate(dto);
        }
    }
}
