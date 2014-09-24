package fi.vm.sade.koodisto.service.impl.conversion;

import java.util.List;

import org.junit.Test;

import fi.vm.sade.koodisto.dto.SimpleKoodiMetadataDto;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import static org.junit.Assert.assertEquals;


public class MetadataToSimpleMetadataConverterTest {
    
    private final static String CODES_NAME = "codes", CODES_DESCRIPTION = "desc";
    private final static String CODE_NAME = "koodi", CODE_DESCRIPTION = "kuvaus", CODE_SHORT_NAME = "ko";
    
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
    
    @Test
    public void convertsKoodiMetadataToSimpleKoodiMetadata() {
        assertSimpleKoodiMetadata(MetadataToSimpleMetadataConverter.convertToSimpleKoodiMetadata(givenKoodiMetadata()));
    }
    
    @Test
    public void convertsMultipleKoodiMetadataToSimpleKoodiMetadata() {
        List<SimpleKoodiMetadataDto> data = MetadataToSimpleMetadataConverter.convertToSimpleKoodiMetadata(givenKoodiMetadata(), givenKoodiMetadata());
        assertEquals(2, data.size());
        for (SimpleKoodiMetadataDto simpleKoodiMetadataDto : data) {
            assertSimpleKoodiMetadata(simpleKoodiMetadataDto);
        }
    }

    private void assertSimpleKoodiMetadata(SimpleKoodiMetadataDto data) {
        assertEquals(CODE_NAME, data.nimi);
        assertEquals(CODE_DESCRIPTION, data.kuvaus);
        assertEquals(Kieli.FI, data.kieli);
        assertEquals(CODE_SHORT_NAME, data.lyhytNimi);
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
        data.setLyhytNimi(CODE_SHORT_NAME);
        return data;
    }
    
}
