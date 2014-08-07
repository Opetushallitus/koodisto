package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodiKuvausEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiLyhytNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodiNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;


public class CodeElementValidatorTest {

    private static CodeElementValidator validator = new CodeElementValidator();

    public static class ValidatingInsert {

        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowNullKoodiDtoWhenCreatingCodes() {
            validator.validateInsert(null);
        }

        @Test(expected = MetadataEmptyException.class)
        public void doesNotAllowCreatingCodeElementWithoutMetadata() {
            KoodiDto dto = new KoodiDto();
            validator.validateInsert(dto); 
        }

        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodeElementWithoutLanguageDefinedForMetadata() {
            KoodiDto dto = new KoodiDto();
            KoodiMetadata data = new KoodiMetadata();
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto); 
        }

        @Test(expected = KoodiNimiEmptyException.class)
        public void doesNotAllowCreatingCodeElementWithoutName() {
            KoodiDto dto = new KoodiDto();
            KoodiMetadata data = new KoodiMetadata();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto); 
        }

        @Test(expected = KoodiKuvausEmptyException.class)
        public void doesNotAllowCreatingCodeElementWithoutDescription() {
            KoodiDto dto = new KoodiDto();
            KoodiMetadata data = new KoodiMetadata();
            data.setNimi("name");
            data.setKuvaus(" ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto); 
        }
        
        @Test(expected = KoodiLyhytNimiEmptyException.class)
        public void doesNotAllowCreatingCodeElementWithoutShortName() {
            KoodiDto dto = new KoodiDto();
            KoodiMetadata data = new KoodiMetadata();
            data.setNimi("name");
            data.setKuvaus("description");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto); 
        }

        @Test
        public void passessWithAllDataGiven() {
            KoodiDto dto = new KoodiDto();
            dto.setMetadata(givenCorrectMetaData());
            validator.validateInsert(dto); 
        }

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