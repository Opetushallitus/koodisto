package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.exception.KoodistoKuvausEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoUriEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoVersionNumberEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

public class CodesValidatorTest {
    
    private static CodesValidator validator = new CodesValidator();
    
    public static class ValidatingInsert {
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowNullKoodistoDtoWhenCreatingCodes() {
            validator.validateInsert(null);
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodesWithoutCodesGroupUri() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("");
            validator.validateInsert(dto); 
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodesWithoutTila() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("group");
            dto.setKoodistoUri("koodistoUri");
            validator.validateInsert(dto); 
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodesWithoutOrganization() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("group");
            dto.setKoodistoUri("koodistoUri");
            dto.setTila(Tila.LUONNOS);
            dto.setOrganisaatioOid("");
            validator.validateInsert(dto);
        }
        
        @Test(expected = MetadataEmptyException.class)
        public void doesNotAllowCreatingCodesWithoutMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            validator.validateInsert(dto); 
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodesWithoutLanguageDefinedForMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto); 
        }
        
        @Test(expected = KoodistoNimiEmptyException.class)
        public void doesNotAllowCreatingCodesWithoutName() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateInsert(dto); 
        }
        
        @Test(expected = KoodistoKuvausEmptyException.class)
        public void doesNotAllowCreatingCodesWithoutDescription() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            data.setNimi("name");
            data.setKuvaus(" ");
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
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowNullKoodistoDtoWhenUpdating() {
            validator.validateUpdate(null);
        }
        
        @Test(expected = KoodistoUriEmptyException.class)
        public void doesNotAllowUpdatingCodesWithoutCodesUri() {
            KoodistoDto dto = new KoodistoDto();
            validator.validateUpdate(dto); 
        }
        
        @Test(expected = MetadataEmptyException.class)
        public void doesNotAllowUpdatingCodesWithoutMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            validator.validateUpdate(dto); 
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowUpdatingCodesWithoutLanguageDefinedForMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            dto.setMetadata(Arrays.asList(data));
            validator.validateUpdate(dto); 
        }
        
        @Test(expected = KoodistoNimiEmptyException.class)
        public void doesNotAllowUpdatingCodesWithoutName() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateUpdate(dto); 
        }
        
        @Test(expected = KoodistoKuvausEmptyException.class)
        public void doesNotAllowUpdatingCodesWithoutDescription() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            data.setNimi("name");
            data.setKuvaus(" ");
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
    
    public static class ValidatingDelete {
        
        @Test(expected = KoodistoUriEmptyException.class)
        public void doesNotAllowEmptyCodesUriWhenDeleting() {
            validator.validateDelete("", 1);
        }
        
        @Test(expected = KoodistoVersionNumberEmptyException.class)
        public void doesNotAllowNullVersionWhenDeleting() {
            validator.validateDelete("uri", null);
        }
        
        @Test
        public void passesWithAllDataGiven() {
            validator.validateDelete("uri", 1);
        }
    }
    
    public static class ValidatingGet {
        
        @Test(expected = KoodistoUriEmptyException.class)
        public void doesNotAllowEmptyCodesUriWhenFetching() {
            validator.validateGet("");
        }
        
        @Test
        public void passesWithAllDataGiven() {
            validator.validateGet("uri");
        }
    }
   
    private static KoodistoDto givenKoodistoDtoWithBasicFields() {
        KoodistoDto dto = new KoodistoDto();
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
