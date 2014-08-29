package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;


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
            KoodiMetadata data = new KoodiMetadata();
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto); 
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodeElementWithoutName() {
            KoodiDto dto = new KoodiDto();
            KoodiMetadata data = new KoodiMetadata();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
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
            KoodiMetadata data = new KoodiMetadata();
            dto.setMetadata(Arrays.asList(data));
            validator.validateUpdate(dto); 
        }

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodeElementWithoutName() {
            KoodiDto dto = new KoodiDto();
            dto.setKoodiUri("uri");
            KoodiMetadata data = new KoodiMetadata();
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