package fi.vm.sade.koodisto.service.impl.conversion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodistoMetadata;


public class MetadataToSimpleMetadataConverterTest {
    
    private final static String CODES_NAME = "codes", CODES_DESCRIPTION = "desc";
    private final static String CODE_NAME = "koodi", CODE_DESCRIPTION = "kuvaus";
    
    @Test
    public void convertsKoodistoMetadataToSimpleMetadata() {
        SimpleMetadataDto data = MetadataToSimpleMetadataConverter.convert(givenKoodistoMetadata());
        assertEquals(CODES_NAME, data.nimi);
        assertEquals(CODES_DESCRIPTION, data.kuvaus);
        assertEquals(Kieli.EN, data.kieli);
    }

    @Test
    public void convertsKoodiMetadataToSimpleMetadata() {
        SimpleMetadataDto data = MetadataToSimpleMetadataConverter.convert(givenKoodiMetadata());
        assertEquals(CODE_NAME, data.nimi);
        assertEquals(CODE_DESCRIPTION, data.kuvaus);
        assertEquals(Kieli.FI, data.kieli);
    }
    
    private KoodistoMetadata givenKoodistoMetadata() {
        KoodistoMetadata data = new KoodistoMetadata();
        data.setKieli(Kieli.EN);
        data.setNimi(CODES_NAME);
        data.setKuvaus(CODES_DESCRIPTION);
        return data;
    }
    
    private KoodiMetadata givenKoodiMetadata() {
        KoodiMetadata data = new KoodiMetadata();
        data.setKieli(Kieli.FI);
        data.setNimi(CODE_NAME);
        data.setKuvaus(CODE_DESCRIPTION);
        return data;
    }
    
}
