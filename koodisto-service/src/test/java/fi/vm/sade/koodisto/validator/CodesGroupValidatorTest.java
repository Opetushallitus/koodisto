package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class CodesGroupValidatorTest {

    private static RestValidator<KoodistoRyhmaDto> validator = new CodesGroupValidator();

    public static class ValidateInsert {

        @Test
        public void passes() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            KoodistoRyhmaMetadata data = new KoodistoRyhmaMetadata();
            data.setNimi("name");
            data.setKieli(Kieli.EN);
            dto.setKoodistoRyhmaMetadatas(new HashSet<>(Arrays.asList(data)));
            validator.validateInsert(dto);
        }
    }

    public static class ValidateUpdate {

        @Test
        public void passes() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            dto.setKoodistoRyhmaUri("group");
            KoodistoRyhmaMetadata data = new KoodistoRyhmaMetadata();
            data.setNimi("name");
            data.setKieli(Kieli.EN);
            dto.setKoodistoRyhmaMetadatas(new HashSet<>(Arrays.asList(data)));
            validator.validateUpdate(dto);
        }
    }
}
