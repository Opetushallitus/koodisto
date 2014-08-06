package fi.vm.sade.koodisto.service.koodisto.rest.validator;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.exception.KoodistoKuvausEmptyException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNimiEmptyException;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;

public class CodesValidatorTest {
    
    private static CodesValidator validator = new CodesValidator();
    
    @RunWith(BlockJUnit4ClassRunner.class)
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
        public void doesNotAllowCreatingCodesWithoutCodesUri() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("group");
            validator.validateCreateNew(dto); 
        }
        
        @Test(expected = IllegalArgumentException.class)
        public void doesNotAllowCreatingCodesWithoutTila() {
            KoodistoDto dto = new KoodistoDto();
            dto.setCodesGroupUri("group");
            dto.setKoodistoUri("koodistoUri");
            validator.validateCreateNew(dto); 
        }
        
        @Test(expected = MetadataEmptyException.class)
        public void doesNotAllowCreatingCodesWithoutMetadata() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
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

    }
   
    private static KoodistoDto givenKoodistoDtoWithBasicFields() {
        KoodistoDto dto = new KoodistoDto();
        dto.setCodesGroupUri("group");
        dto.setKoodistoUri("koodistoUri");
        dto.setTila(Tila.LUONNOS);
        return dto;
    }
    
}
