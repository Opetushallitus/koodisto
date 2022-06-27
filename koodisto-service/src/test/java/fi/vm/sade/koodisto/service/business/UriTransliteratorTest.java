package fi.vm.sade.koodisto.service.business;

import fi.vm.sade.koodisto.repository.KoodiRepository;
import fi.vm.sade.koodisto.repository.KoodistoRepository;
import fi.vm.sade.koodisto.service.business.exception.MetadataEmptyException;
import fi.vm.sade.koodisto.service.business.impl.UriTransliteratorImpl;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: kwuoti Date: 22.3.2013 Time: 6.41
 */
public class UriTransliteratorTest {

    private KoodiRepository koodiRepository;
    private KoodistoRepository koodistoRepository;

    private UriTransliterator uriTransliterator;

    @Before
    public void setUp() {
        koodiRepository = mock(KoodiRepository.class);
        koodistoRepository= mock(KoodistoRepository.class);

        uriTransliterator = new UriTransliteratorImpl();
        ReflectionTestUtils.setField(uriTransliterator, "koodiRepository", koodiRepository);
        ReflectionTestUtils.setField(uriTransliterator, "koodistoRepository", koodistoRepository);
    }

    private static Collection<KoodistoMetadataType> generateMetadataByNimi(Map<KieliType, String> nimiByKieli) {
        List<KoodistoMetadataType> metadatas = new ArrayList<>();

        for (Map.Entry<KieliType, String> entry : nimiByKieli.entrySet()) {
            KoodistoMetadataType metadata = createMetadata(entry.getKey(), entry.getValue());
            metadatas.add(metadata);
        }

        return metadatas;
    }

    private static KoodistoMetadataType createMetadata(KieliType kieli, String nimi) {
        KoodistoMetadataType meta = new KoodistoMetadataType();
        meta.setKieli(kieli);
        meta.setNimi(nimi);
        return meta;
    }

    @Test
    public void testGenerateKoodistoUri() {
        final String nimi = "!#€%&&/%öösåyNÄväFi123!#€%&/()=";
        final String expectedUri = "oosoynavafi123";

        when(koodistoRepository.existsByKoodistoUri(anyString())).thenReturn(false);
        Map<KieliType, String> nimiByKieli = new HashMap<KieliType, String>();
        nimiByKieli.put(KieliType.FI, nimi);

        String generatedUri = uriTransliterator.generateKoodistoUriByMetadata(generateMetadataByNimi(nimiByKieli));
        verify(koodistoRepository, times(1)).existsByKoodistoUri(anyString());
        assertEquals(expectedUri, generatedUri);
    }

    @Test
    public void testGenerateNonUniqueKoodistoUri() {
        final String nimi = "!#€%&&/%säyNÄväFi123!#€%&/()=";
        final String expectedUri = "saynavafi123-2";
        when(koodistoRepository.existsByKoodistoUri(anyString())).thenReturn(true, true, false);
        Map<KieliType, String> nimiByKieli = new HashMap<KieliType, String>();
        nimiByKieli.put(KieliType.FI, nimi);

        String generatedUri = uriTransliterator.generateKoodistoUriByMetadata(generateMetadataByNimi(nimiByKieli));
        verify(koodistoRepository, times(3)).existsByKoodistoUri(anyString());
        assertEquals(expectedUri, generatedUri);
    }

    @Test
    public void testGenerateKoodistoUriPreferredLanguageOrder() {
        final String nimiFI = "!#€%&&/%säyNÄväFI123!#€%&/()=";
        final String nimiSV = "!#€%&&/%säyNÄväSV123!#€%&/()=";
        final String nimiEN = "!#€%&&/%säyNÄväEN123!#€%&/()=";

        final String expectedUriFI = "saynavafi123";
        final String expectedUriSV = "saynavasv123";
        final String expectedUriEN = "saynavaen123";

        when(koodistoRepository.existsByKoodistoUri(anyString())).thenReturn(false, false, false);
        Map<KieliType, String> nimiByKieli = new HashMap<KieliType, String>();
        nimiByKieli.put(KieliType.FI, nimiFI);
        nimiByKieli.put(KieliType.SV, nimiSV);
        nimiByKieli.put(KieliType.EN, nimiEN);

        String generatedUriFI = uriTransliterator.generateKoodistoUriByMetadata(generateMetadataByNimi(nimiByKieli));
        assertEquals(expectedUriFI, generatedUriFI);
        nimiByKieli.remove(KieliType.FI);

        String generatedUriSV = uriTransliterator.generateKoodistoUriByMetadata(generateMetadataByNimi(nimiByKieli));
        assertEquals(expectedUriSV, generatedUriSV);
        nimiByKieli.remove(KieliType.SV);

        String generatedUriEN = uriTransliterator.generateKoodistoUriByMetadata(generateMetadataByNimi(nimiByKieli));
        assertEquals(expectedUriEN, generatedUriEN);

        verify(koodistoRepository, times(3)).existsByKoodistoUri(anyString());
    }

    @Test(expected = MetadataEmptyException.class)
    public void testGenerateKoodistoUriEmptyMetadataList() {
        uriTransliterator.generateKoodistoUriByMetadata(new ArrayList<>());
    }

    @Test
    public void testGenerateKoodistoUriWithNameConsistingOfOnlySpecialCharacters() {
        final String nimi = "!#€%&€&()=%(#?^_--*'";
        final String expectedUri = "-";

        when(koodistoRepository.existsByKoodistoUri(anyString())).thenReturn(false);
        String generatedUri = uriTransliterator.generateKoodistoUriByMetadata(
                Arrays.asList(createMetadata(KieliType.FI, nimi)));

        verify(koodistoRepository, times(1)).existsByKoodistoUri(anyString());
        assertEquals(expectedUri, generatedUri);
    }

    @Test
    public void testGenerateKoodiUri() {
        final String koodistoUri = "koodistouri";
        final String arvo = "/€%&)#öösoyNÄväFi123#€#%€%*'";
        final String expectedUri = koodistoUri + "_" + "oosoynavafi123";

        when(koodiRepository.existsByKoodiUri(anyString())).thenReturn(false);

        String generatedUri = uriTransliterator.generateKoodiUriByKoodistoUriAndKoodiArvo(koodistoUri, arvo);

        verify(koodiRepository, times(1)).existsByKoodiUri(anyString());
        assertEquals(expectedUri, generatedUri);
    }

    @Test
    public void testGenerateNonUniqueKoodiUri() {
        final String koodistoUri = "koodistouri";
        final String arvo = "/€%&)#öösoyNÄväFi123#€#%€%*'";
        final String expectedUri = koodistoUri + "_" + "oosoynavafi123-2";

        when(koodiRepository.existsByKoodiUri(anyString())).thenReturn(true, true, false);

        String generatedUri = uriTransliterator.generateKoodiUriByKoodistoUriAndKoodiArvo(koodistoUri, arvo);

        verify(koodiRepository, times(3)).existsByKoodiUri(anyString());
        assertEquals(expectedUri, generatedUri);
    }

    @Test
    public void testGenerateKoodiUriWithKoodiArvoConsistingOfOnlySpecialCharacters() {
        final String koodistoUri = "koodistouri";
        final String arvo = "/€%&)!\"#%€&€%*'";
        final String expectedUri = koodistoUri + "_" + "-";

        when(koodiRepository.existsByKoodiUri(anyString())).thenReturn(false);

        String generatedUri = uriTransliterator.generateKoodiUriByKoodistoUriAndKoodiArvo(koodistoUri, arvo);

        verify(koodiRepository, times(1)).existsByKoodiUri(anyString());
        assertEquals(expectedUri, generatedUri);
    }


}
