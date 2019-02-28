package fi.vm.sade.koodisto.service.business.impl;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.types.common.*;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodistoConversionServiceTest {
    private static final int DAYS_IN_WEEK = 7;

    private Calendar now;
    private Calendar weekLater;

    @Before
    public void setUp() {
        now = Calendar.getInstance();
        weekLater = Calendar.getInstance();
        weekLater.add(Calendar.DATE, DAYS_IN_WEEK);
    }

    @Autowired()
    private SadeConversionService conversionService;

    @Test
    public void testKoodiTypeToKoodiVersioConverter() {
        KoodiType dto = createKoodiType();
        KoodiVersio versio = conversionService.convert(dto, KoodiVersio.class);
        assertNotNull(versio);
        checkConvertedFields(versio, KoodiVersio.class, "id", "version", "luotu", "paivittajaOid");
    }

    @Test
    public void testKoodiMetadataTypeToKoodiMetadataConverter() {
        KoodiMetadataType dto = createKoodiMetatadataDTO();
        KoodiMetadata metadata = conversionService.convert(dto, KoodiMetadata.class);
        assertNotNull(metadata);
        checkConvertedFields(metadata, KoodiMetadata.class, "version", "id");
    }

    @Test
    public void testKoodiMetadataToKoodimetadataDTOConverter() {
        KoodiMetadata meta = createKoodiMetatadata();
        KoodiMetadataType dto = conversionService.convert(meta, KoodiMetadataType.class);
        assertNotNull(dto);
        checkConvertedFields(dto, KoodiMetadataType.class);
    }

    @Test
    public void testKoodistoTypeToKoodistoVersioConverter() {
        KoodistoType dto = createKoodistoType();
        KoodistoVersio versio = conversionService.convert(dto, KoodistoVersio.class);
        assertNotNull(versio);
        checkConvertedFields(versio, KoodistoVersio.class, "id", "version", "luotu", "paivittajaOid");
    }

    @Test
    public void testKoodistoMetadataTypeToKoodistoMetadataConverter() {
        KoodistoMetadataType dto = createKoodistoMetadataType();
        KoodistoMetadata metadata = conversionService.convert(dto, KoodistoMetadata.class);
        assertNotNull(metadata);
        checkConvertedFields(metadata, KoodistoMetadata.class, "version", "id");
    }

    @Test
    public void testKoodistoMetadataToKoodistoMetadataTypeConverter() {
        KoodistoMetadata metadata = createKoodistoMetadata();
        KoodistoMetadataType dto = conversionService.convert(metadata, KoodistoMetadataType.class);
        assertNotNull(dto);
        checkConvertedFields(dto, KoodistoMetadataType.class);
    }

    @Test
    public void testKoodistoVersioToKoodistoTypeConverter() {
        KoodistoVersio versio = createKoodistoVersio(createKoodisto());
        KoodistoType dto = conversionService.convert(versio, KoodistoType.class);
        assertNotNull(dto);
        checkConvertedFields(dto, KoodistoType.class);
    }
    
    @Test
    public void testKoodistoVersioToKoodistoDtoConverter() {
        KoodistoVersio versio = createKoodistoVersio(createKoodisto());
        KoodistoDto dto = conversionService.convert(versio, KoodistoDto.class);
        assertNotNull(dto);
        checkConvertedFields(dto, KoodistoDto.class);
    }
    
    @Test
    public void testKoodistoVersioToKoodistoDtoConverterWithRelations() {
        KoodistoVersio versio = createKoodistoVersioWithKoodinSuhdes(createKoodisto());
        KoodistoDto dto = conversionService.convert(versio, KoodistoDto.class);
        assertNotNull(dto);
        assertEquals(1, dto.getIncludesCodes().size());
        assertEquals(2, dto.getLevelsWithCodes().size());
        assertEquals(1, dto.getWithinCodes().size());
        checkConvertedFields(dto, KoodistoDto.class);
    }

    private <T> void checkConvertedFields(T instance, Class<T> clazz, String... ignoredFields) {
        try {
            methods: for (Method m : clazz.getMethods()) {

                // Skip getClass method and methods whose name does not start
                // with "get". Also skip methods with parameters and methods
                // that return a collection.
                if (!m.getName().startsWith("get") || m.getName().equals("getClass")
                        || BaseEntity.class.isAssignableFrom(m.getReturnType())
                        || Collection.class.isAssignableFrom(m.getReturnType()) || m.getParameterTypes().length > 0) {
                    continue;
                }

                // Skip ignored fields
                if (ignoredFields != null && ignoredFields.length > 0) {
                    for (String f : ignoredFields) {
                        if (f == null || "".equals(f)) {
                            continue;
                        }

                        if (m.getName().equals("get" + f.substring(0, 1).toUpperCase() + f.substring(1))) {
                            continue methods;
                        }
                    }
                }

                // Call the method and check it does not return null value
                assertNotNull(m.invoke(instance, new Object[] {}));
            }
        } catch (Exception e) {
            Assert.fail("Conversion failed with error message: " + e.getMessage());
        }
    }

    private KoodiType createKoodiType() {
        KoodiType dto = new KoodiType();
        dto.setVersio(1);
        dto.setKoodiArvo("arvo");
        dto.setKoodiUri("http://foo.fi");
        dto.setPaivitysPvm(DateHelper.DateToXmlCal(new Date()));
        dto.setTila(TilaType.HYVAKSYTTY);
        dto.setVersio(1);
        dto.setVoimassaAlkuPvm(DateHelper.DateToXmlCal(new Date()));
        dto.setVoimassaLoppuPvm(DateHelper.DateToXmlCal(new Date()));

        return dto;
    }

    private KoodiMetadataType createKoodiMetatadataDTO() {
        KoodiMetadataType dto = new KoodiMetadataType();
        dto.setEiSisallaMerkitysta("Ei sisällä merkitystä");
        dto.setHuomioitavaKoodi("Huomioitava koodi");
        dto.setKasite("Käsite");
        dto.setKayttoohje("Käyttöohje");
        dto.setKieli(KieliType.FI);
        dto.setKuvaus("Kuvaus");
        dto.setLyhytNimi("Lyhyt nimi");
        dto.setNimi("Nimi");
        dto.setSisaltaaKoodiston("Sisältää koodiston");
        dto.setSisaltaaMerkityksen("Sisältää merkityksen");
        return dto;
    }

    private KoodiMetadata createKoodiMetatadata() {
        KoodiMetadata meta = new KoodiMetadata();
        meta.setId(1L);
        meta.setEiSisallaMerkitysta("Ei sisällä merkitystä");
        meta.setHuomioitavaKoodi("Huomioitava koodi");
        meta.setKasite("Käsite");
        meta.setKayttoohje("Käyttöohje");
        meta.setKieli(fi.vm.sade.koodisto.model.Kieli.FI);
        meta.setKuvaus("Kuvaus");
        meta.setLyhytNimi("Lyhyt nimi");
        meta.setNimi("Nimi");
        meta.setSisaltaaKoodiston("Sisältää koodiston");
        meta.setSisaltaaMerkityksen("Sisältää merkityksen");
        return meta;
    }

    private KoodistoType createKoodistoType() {
        KoodistoType dto = new KoodistoType();
        dto.setKoodistoUri("http://koodistoUri");
        dto.setLukittu(false);
        dto.setOmistaja("Omistaja");
        dto.setOrganisaatioOid("organisaatioOid");
        dto.setPaivitysPvm(DateHelper.DateToXmlCal(now.getTime()));
        dto.setTila(TilaType.HYVAKSYTTY);
        dto.setVersio(1);
        dto.setVoimassaAlkuPvm(DateHelper.DateToXmlCal(now.getTime()));
        dto.setVoimassaLoppuPvm(DateHelper.DateToXmlCal(weekLater.getTime()));
        dto.setPaivittajaOid("oid");

        return dto;
    }

    private Koodisto createKoodisto() {
        Koodisto koodisto = new Koodisto();
        koodisto.setId(1L);
        koodisto.setKoodistoUri("http://koodistouri.fi");
        koodisto.setLukittu(false);
        koodisto.setOmistaja("Omistaja");
        koodisto.setOrganisaatioOid("organisaatiooid");
        koodisto.setVersion(1L);

        return koodisto;
    }

    private KoodistoVersio createKoodistoVersio(Koodisto koodisto) {
        KoodistoVersio versio = new KoodistoVersio();
        versio.setId(1L);
        versio.setKoodisto(koodisto);
        versio.setPaivitysPvm(now.getTime());
        versio.setTila(Tila.HYVAKSYTTY);
        versio.setVersio(1);
        versio.setVersion(1L);
        versio.setVoimassaAlkuPvm(now.getTime());
        versio.setVoimassaLoppuPvm(weekLater.getTime());
        versio.setPaivittajaOid("oid");
        return versio;
    }
    
    private KoodistoVersio createKoodistoVersioWithKoodinSuhdes(Koodisto koodisto) {        
        KoodistoVersio versio = createKoodistoVersio(koodisto);
        //Note that relations created here are very atypical and actually invalid, but useful for quick conversion testing purposes. 
        Set<KoodistonSuhde> alaKoodistos = new HashSet<KoodistonSuhde>();
        alaKoodistos.add(createKoodistonSuhde(versio, versio, SuhteenTyyppi.SISALTYY));
        alaKoodistos.add(createKoodistonSuhde(versio, versio, SuhteenTyyppi.RINNASTEINEN));
        versio.setAlakoodistos(alaKoodistos);
        Set<KoodistonSuhde> ylaKoodistos = new HashSet<KoodistonSuhde>();
        ylaKoodistos.add(createKoodistonSuhde(versio, versio, SuhteenTyyppi.SISALTYY));
        ylaKoodistos.add(createKoodistonSuhde(versio, versio, SuhteenTyyppi.RINNASTEINEN));
        versio.setYlakoodistos(ylaKoodistos);
        return versio;
    }
    
    private KoodistonSuhde createKoodistonSuhde(KoodistoVersio yla, KoodistoVersio ala, SuhteenTyyppi tyyppi) {
        KoodistonSuhde suhde = new KoodistonSuhde();
        suhde.setAlakoodistoVersio(ala);
        suhde.setSuhteenTyyppi(tyyppi);
        suhde.setYlakoodistoVersio(yla);        
        return suhde;
    }

    private KoodistoMetadataType createKoodistoMetadataType() {
        KoodistoMetadataType meta = new KoodistoMetadataType();
        meta.setHuomioitavaKoodisto("Huomioitava koodisto");
        meta.setKasite("Käsite");
        meta.setKayttoohje("Käyttöohje");
        meta.setKieli(KieliType.FI);
        meta.setKohdealue("Kohdealue");
        meta.setKohdealueenOsaAlue("Kohdealueen osa-alue");
        meta.setKoodistonLahde("Koodiston lähde");
        meta.setKuvaus("Kuvaus");
        meta.setNimi("Nimi");
        meta.setSitovuustaso("Sitovuustaso");
        meta.setTarkentaaKoodistoa("Tarkentaa koodistoa");
        meta.setToimintaymparisto("Toimintaympäristö");
        return meta;
    }

    private KoodistoMetadata createKoodistoMetadata() {
        KoodistoMetadata meta = new KoodistoMetadata();
        meta.setId(1L);
        meta.setHuomioitavaKoodisto("Huomioitava koodisto");
        meta.setKasite("Käsite");
        meta.setKayttoohje("Käyttöohje");
        meta.setKieli(Kieli.FI);
        meta.setKohdealue("Kohdealue");
        meta.setKohdealueenOsaAlue("Kohdealueen osa-alue");
        meta.setKoodistonLahde("Koodiston lähde");
        meta.setKuvaus("Kuvaus");
        meta.setNimi("Nimi");
        meta.setSitovuustaso("Sitovuustaso");
        meta.setTarkentaaKoodistoa("Tarkentaa koodistoa");
        meta.setToimintaymparisto("Toimintaympäristö");
        return meta;
    }
}
