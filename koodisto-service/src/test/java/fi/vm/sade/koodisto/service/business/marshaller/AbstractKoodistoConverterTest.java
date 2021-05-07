package fi.vm.sade.koodisto.service.business.marshaller;

import fi.jhs_suositukset.skeemat.oph._2012._05._03.KoodiListaus;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.ByteArrayDataSource;
import fi.vm.sade.koodisto.util.DateHelper;
import fi.vm.sade.koodisto.util.KoodistoHelper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import javax.activation.DataHandler;

import java.io.*;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 13.50
 */
public abstract class AbstractKoodistoConverterTest {
    protected abstract KoodistoConverter getConverter();
    protected abstract String getTestFile();

    @Test
    public void testUnmarshall() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(getTestFile());
//        DataHandler handler = new DataHandler(new ByteArrayDataSource(convertStreamToString(inputStream).getBytes()));
        DataHandler handler = new DataHandler(new ByteArrayDataSource(IOUtils.toByteArray(inputStream)));

        KoodiListaus listaus = getConverter().unmarshal(handler, "UTF-8");
        List<KoodiType> koodis = listaus.getKoodi();
        assertEquals(1, koodis.size());

        KoodiType koodi = koodis.get(0);
        assertTrue(StringUtils.isNotBlank(koodi.getKoodiArvo()));
        assertTrue(StringUtils.isBlank(koodi.getKoodiUri()));
        assertNotNull(koodi.getTila());
        assertTrue(koodi.getVersio() != 0);
        assertNotNull(koodi.getVoimassaLoppuPvm());
        assertNotNull(koodi.getVoimassaAlkuPvm());

        assertEquals(KieliType.values().length, koodi.getMetadata().size());

        for (KieliType k : KieliType.values()) {
            KoodiMetadataType meta = KoodistoHelper.getKoodiMetadataForLanguage(koodi, k);
            assertEquals(k, meta.getKieli());

            assertTrue(StringUtils.isNotBlank(meta.getEiSisallaMerkitysta()));
            assertTrue(StringUtils.isNotBlank(meta.getHuomioitavaKoodi()));
            assertTrue(StringUtils.isNotBlank(meta.getKasite()));
            assertTrue(StringUtils.isNotBlank(meta.getKayttoohje()));
            assertTrue(StringUtils.isNotBlank(meta.getKuvaus()));
            assertTrue(StringUtils.isNotBlank(meta.getLyhytNimi()));
            assertTrue(StringUtils.isNotBlank(meta.getNimi()));
            assertTrue(StringUtils.isNotBlank(meta.getSisaltaaKoodiston()));
            assertTrue(StringUtils.isNotBlank(meta.getSisaltaaMerkityksen()));
        }
    }

    @Test
    public void testMarshallAndUnmarshall() throws IOException {
        final String encoding = "UTF-8";

        KoodiType koodi = new KoodiType();

        koodi.setKoodiArvo("arvo");
        koodi.setKoodiUri("koodiuri");
        koodi.setPaivitysPvm(DateHelper.dateToXmlCal(new Date()));
        koodi.setTila(TilaType.HYVAKSYTTY);
        koodi.setVersio(1);
        koodi.setVoimassaAlkuPvm(DateHelper.dateToXmlCal(new Date()));
        koodi.setVoimassaLoppuPvm(DateHelper.dateToXmlCal(new Date()));

        for (KieliType k : KieliType.values()) {
            KoodiMetadataType meta = new KoodiMetadataType();
            meta.setEiSisallaMerkitysta("Ei sisalla merkitysta - " + k.name());
            meta.setHuomioitavaKoodi("Huomioitava koodi - " + k.name());
            meta.setKasite("Kasite - " + k.name());
            meta.setKayttoohje("Kayttoohje - " + k.name());
            meta.setKieli(k);
            meta.setKuvaus("Kuvaus - " + k.name());
            meta.setLyhytNimi("Lyhyt nimi - " + k.name());
            meta.setNimi("Nimi - " + k.name());
            meta.setSisaltaaKoodiston("Sisaltaa koodiston - " + k.name());
            meta.setSisaltaaMerkityksen("Sisaltaa merkityksen - " + k.name());
            koodi.getMetadata().add(meta);
        }

        KoodiListaus toMarshalling = new KoodiListaus();
        toMarshalling.getKoodi().add(koodi);

        DataHandler handler = getConverter().marshal(toMarshalling, encoding);

        List<KoodiType> koodis = getConverter().unmarshal(handler, encoding).getKoodi();
        assertEquals(1, koodis.size());

        KoodiType unmarshalled = koodis.get(0);
        assertEquals(koodi.getKoodiArvo(), unmarshalled.getKoodiArvo());
        assertEquals(koodi.getKoodiUri(), unmarshalled.getKoodiUri());
        assertEquals(koodi.getPaivitysPvm(), unmarshalled.getPaivitysPvm());
        assertEquals(koodi.getTila(), unmarshalled.getTila());
        assertEquals(koodi.getVersio(), unmarshalled.getVersio());
        assertEquals(koodi.getVoimassaAlkuPvm(), unmarshalled.getVoimassaAlkuPvm());
        assertEquals(koodi.getVoimassaLoppuPvm(), unmarshalled.getVoimassaLoppuPvm());

        for (KieliType k : KieliType.values()) {
            KoodiMetadataType meta = KoodistoHelper.getKoodiMetadataForLanguage(koodi, k);
            KoodiMetadataType unmarshalledMeta = KoodistoHelper.getKoodiMetadataForLanguage(unmarshalled, k);

            assertEquals(meta.getEiSisallaMerkitysta(), unmarshalledMeta.getEiSisallaMerkitysta());
            assertEquals(meta.getHuomioitavaKoodi(), unmarshalledMeta.getHuomioitavaKoodi());
            assertEquals(meta.getKasite(), unmarshalledMeta.getKasite());
            assertEquals(meta.getKayttoohje(), unmarshalledMeta.getKayttoohje());
            assertEquals(meta.getKieli(), unmarshalledMeta.getKieli());
            assertEquals(meta.getKuvaus(), unmarshalledMeta.getKuvaus());
            assertEquals(meta.getLyhytNimi(), unmarshalledMeta.getLyhytNimi());
            assertEquals(meta.getNimi(), unmarshalledMeta.getNimi());
            assertEquals(meta.getSisaltaaKoodiston(), unmarshalledMeta.getSisaltaaKoodiston());
            assertEquals(meta.getSisaltaaMerkityksen(), unmarshalledMeta.getSisaltaaMerkityksen());
        }
    }

    public static final String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
