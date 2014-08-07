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
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

public class CodesValidatorTest {
    
    private static CodesValidator validator = new CodesValidator();
    
    public static class ValidatingInsert {
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowNullKoodistoDtoWhenCreatingCodes() {
            validator.validateCreateNew(null);
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodesWithoutCodesGroupUri() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("");
            validator.validateCreateNew(dto); 
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodesWithoutTila() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("group");
            dto.setKoodistoUri("koodistoUri");
            validator.validateCreateNew(dto); 
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodesWithoutOrganization() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("group");
            dto.setKoodistoUri("koodistoUri");
            dto.setTila(Tila.LUONNOS);
            dto.setOrganisaatioOid("");
            validator.validateCreateNew(dto);
        }
        
        @Test(expected = MetadataEmptyException.class)
        public void doesNotAllowCreatingCodesWithoutMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            validator.validateCreateNew(dto); 
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodesWithoutLanguageDefinedForMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            dto.setMetadata(Arrays.asList(data));
            validator.validateCreateNew(dto); 
        }
        
        @Test(expected = KoodistoNimiEmptyException.class)
        public void doesNotAllowCreatingCodesWithoutName() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            data.setNimi("    ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateCreateNew(dto); 
        }
        
        @Test(expected = KoodistoKuvausEmptyException.class)
        public void doesNotAllowCreatingCodesWithoutDescription() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            KoodistoMetadata data = new KoodistoMetadata();
            data.setNimi("name");
            data.setKuvaus(" ");
            data.setKieli(Kieli.FI);
            dto.setMetadata(Arrays.asList(data));
            validator.validateCreateNew(dto); 
        }
        
        @Test
        public void passessWithAllDataGiven() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            dto.setMetadata(givenCorrectMetaData());
            validator.validateCreateNew(dto); 
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
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowNullKoodistoDtoWhenDeleting() {
            validator.validateDelete(null);
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
