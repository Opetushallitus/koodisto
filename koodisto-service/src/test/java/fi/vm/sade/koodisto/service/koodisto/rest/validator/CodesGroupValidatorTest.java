package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import fi.vm.sade.koodisto.dto.KoodistoRyhmaDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoRyhmaMetadata;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoRyhmaUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;


public class CodesGroupValidatorTest {

    private static RestValidator<KoodistoRyhmaDto> validator = new CodesGroupValidator();
    
    public static class ValidateInsert {
        
        @Test(expected = IllegalArgumentException.class)
        public void throwsExceptionIfCOdesGroupIsNull() {
            validator.validateInsert(null);
        }
        
        @Test(expected = MetadataEmptyException.class)
        public void throwsExceptionIfMissingMetadata() {
            validator.validateInsert(new KoodistoRyhmaDto());;
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void throwsExceptionIfLanguageIsNotGiven() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            KoodistoRyhmaMetadata data = new KoodistoRyhmaMetadata();
            data.setNimi("name");
            dto.setKoodistoRyhmaMetadatas(new HashSet<KoodistoRyhmaMetadata>(Arrays.asList(data)));
            validator.validateInsert(dto);
        }
        
        @Test(expected = KoodistoRyhmaNimiEmptyException.class)
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
        
        @Test(expected = IllegalArgumentException.class)
        public void throwsExceptionIfCOdesGroupIsNull() {
            validator.validateUpdate(null);
        }
        
        @Test(expected = KoodistoRyhmaUriEmptyException.class)
        public void throwsExceptionIfCodesGroupUriIsBlank() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            dto.setKoodistoRyhmaUri(" ");
            validator.validateUpdate(dto);
        }
        
        @Test
        public void passes() {
            KoodistoRyhmaDto dto = new KoodistoRyhmaDto();
            dto.setKoodistoRyhmaUri("group");
            validator.validateUpdate(dto);
        }
    }
    
    public static class ValidateDelete {
        
        @Test(expected = IllegalArgumentException.class)
        public void throwsExceptionIfIdIsNull() {
            validator.validateDelete(null, null);
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void throwsExceptionIfIdIsBelowOne() {
            validator.validateDelete(null, -2);
        }
        
        @Test
        public void passes() {
            validator.validateDelete(null, 1);
            validator.validateDelete(null, 2);
            validator.validateDelete(null, 100);
        }
    }
    
    public static class ValidateGet {
        @Test(expected = IllegalArgumentException.class)
        public void throwsExceptionIfIdIsNull() {
            validator.validateGet(null);
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void throwsExceptionIfIdIsBelowOne() {
            validator.validateGet("-3");
        }
        
        @Test
        public void passes() {
            validator.validateGet("1");
            validator.validateGet("12");
            validator.validateGet("101");
        }
    }
}
