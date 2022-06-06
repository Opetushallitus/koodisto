package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CodesValidatorTest {
    
    private static CodesValidator validator = new CodesValidator();
    
    public static class ValidatingInsert {
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowNullKoodistoDtoWhenCreatingCodes() {
            validator.validateInsert(null);
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodesWithoutCodesGroupUri() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("");
            validator.validateInsert(dto); 
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodesWithoutTila() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("group");
            dto.setKoodistoUri("koodistoUri");
            validator.validateInsert(dto); 
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodesWithoutOrganization() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("group");
            dto.setKoodistoUri("koodistoUri");
            dto.setTila(Tila.LUONNOS);
            dto.setOrganisaatioOid("");
            validator.validateInsert(dto);
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodesWithoutMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            validator.validateInsert(dto); 
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodesWithoutLanguageDefinedForMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto); 
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowCreatingCodesWithoutName() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto); 
        }
        
       
        @Test
        public void passessWithAllDataGiven() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            dto.setMetadata(givenCorrectMetaData());
            validator.validateInsert(dto); 
        }

        

    }
    
    public static class ValidatingUpdate {
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowNullKoodistoDtoWhenUpdating() {
            validator.validateUpdate(null);
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodesWithoutCodesUri() {
            KoodistoDto dto = new KoodistoDto();
            validator.validateUpdate(dto); 
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodesWithoutMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            validator.validateUpdate(dto); 
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodesWithoutLanguageDefinedForMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            dto.setMetadata(Arrays.asList(data));
            validator.validateUpdate(dto); 
        }
        
        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodesWithoutName() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateUpdate(dto); 
        }
        
        @Test
        public void passessWithAllDataGiven() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            dto.setMetadata(givenCorrectMetaData());
            validator.validateUpdate(dto);
        }
    }
       
    private static KoodistoDto givenKoodistoDtoWithBasicFields() {
        KoodistoDto dto = new KoodistoDto();
        dto.setVoimassaAlkuPvm(new Date());
        dto.setCodesGroupUri("group");
        dto.setKoodistoUri("koodistoUri");
        dto.setTila(Tila.LUONNOS);
        dto.setOrganisaatioOid("1.2.3413");
        return dto;
    }
    
    private static List<KoodistoMetadata> givenCorrectMetaData() {
        KoodistoMetadata data = new KoodistoMetadata();
        data.setNimi("name");
        data.setKuvaus("description");
        data.setKieli(Kieli.FI);
        return Arrays.asList(data);
    }
    
}
