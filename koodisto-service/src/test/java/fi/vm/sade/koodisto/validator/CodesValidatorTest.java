package fi.vm.sade.koodisto.validator;

import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CodesValidatorTest {

    private static final CodesValidator validator = new CodesValidator();

    private static KoodistoDto givenKoodistoDtoWithBasicFields() {
        KoodistoDto dto = new KoodistoDto();
        dto.setVoimassaAlkuPvm(new Date());
        dto.setCodesGroupUri("group");
        dto.setKoodistoUri("koodistoUri");
        dto.setTila(Tila.LUONNOS);
        dto.setOrganisaatioOid("1.2.3413");
        return dto;
    }

    private static List<KoodistoMetadataType> givenCorrectMetaData() {
        KoodistoMetadataType data = new KoodistoMetadataType();
        data.setNimi("name");
        data.setKuvaus("description");
        data.setKieli(KieliType.FI);
        return Arrays.asList(data);
    }

    public static class ValidatingInsert {

        @Test
        public void passessWithAllDataGiven() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            dto.setMetadata(givenCorrectMetaData());
            validator.validateInsert(dto);
        }
    }

    public static class ValidatingUpdate {

        @Test(expected = KoodistoValidationException.class)
        public void doesNotAllowUpdatingCodesWithoutCodesUri() {
            KoodistoDto dto = new KoodistoDto();
            validator.validateUpdate(dto);
        }

        @Test
        public void passessWithAllDataGiven() {
            KoodistoDto dto = givenKoodistoDtoWithBasicFields();
            dto.setMetadata(givenCorrectMetaData());
            validator.validateUpdate(dto);
        }
    }

}
