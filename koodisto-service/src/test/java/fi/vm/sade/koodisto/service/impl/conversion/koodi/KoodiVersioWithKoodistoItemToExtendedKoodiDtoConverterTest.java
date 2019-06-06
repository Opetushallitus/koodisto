package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.koodisto.service.conversion.SadeConversionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.koodisto.dao.KoodiVersioDAO;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto.RelationCodeElement;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.util.HostAwareKoodistoConfiguration;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.business.util.KoodistoItem;
import fi.vm.sade.koodisto.test.support.DtoFactory;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.when;

@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverterTest {

    @ReplaceWithMock
    @Autowired
    private HostAwareKoodistoConfiguration koodistoConfiguration;

    @ReplaceWithMock
    @Autowired
    private KoodiVersioDAO koodiVersioDao;
    
    @Autowired
    private SadeConversionService conversionService;

    private Integer koodiVersio = 1;

    @Test
    public void convertsKoodinSuhdeToRelationCodeElement() {
        KoodiVersioWithKoodistoItem kv = givenKoodiVersioWithKoodistoItem();
        ExtendedKoodiDto dto = conversionService.convert(kv, ExtendedKoodiDto.class);
        assertEquals(1, dto.getIncludesCodeElements().size());
        assertEquals(2, dto.getLevelsWithCodeElements().size());
        assertEquals(1, dto.getWithinCodeElements().size());
    }

    @Test
    public void storesCodeElementNameLanguageDescriptionAndValueToRelationCodeElement() {
        KoodiVersioWithKoodistoItem kv = givenKoodiVersioWithKoodistoItem();
        ExtendedKoodiDto dto = conversionService.convert(kv, ExtendedKoodiDto.class);
        RelationCodeElement rel = dto.getIncludesCodeElements().get(0);
        SimpleMetadataDto data = rel.relationMetadata.get(0);
        KoodiMetadata givenKoodiMetadata = givenKoodiMetadata();
        assertEquals(givenKoodiMetadata.getKieli(), data.kieli);
        assertEquals(givenKoodiMetadata.getNimi(), data.nimi);
        assertEquals(givenKoodiMetadata.getKuvaus(), data.kuvaus);
        assertEquals(givenKoodiVersio().getKoodiarvo(), rel.codeElementValue);
    }

    @Test
    public void storesParentCodesMetadataToRelationCodeElement() {
        KoodiVersioWithKoodistoItem kv = givenKoodiVersioWithKoodistoItem();
        ExtendedKoodiDto dto = conversionService.convert(kv, ExtendedKoodiDto.class);
        assertEquals(Kieli.EN, dto.getIncludesCodeElements().get(0).parentMetadata.get(0).kieli);
    }

    @Test
    public void converterProvidesRelationCodeElementForLatestCodeVersionWhenRelationRelatesToLatestCodeVersion() {
        KoodiVersio parent = DtoFactory.createKoodiVersioWithUriAndVersio("penaali", 1).build();
        KoodiVersio child = DtoFactory.createKoodiVersioWithUriAndVersioAndRelation("kynä", 1, parent, SuhteenTyyppi.SISALTYY);
        Map<String, Integer> dummyResponse = new HashMap<String, Integer>();
        dummyResponse.put("penaali", 1);
        when(koodiVersioDao.getLatestVersionNumbersForUris("penaali")).thenReturn(dummyResponse);
        when(koodiVersioDao.isLatestKoodiVersio("kynä", 1)).thenReturn(true);
        ExtendedKoodiDto dto = conversionService.convert(new KoodiVersioWithKoodistoItem(child, new KoodistoItem()), ExtendedKoodiDto.class);
        assertEquals(1, dto.getWithinCodeElements().size());
    }

    private KoodiVersioWithKoodistoItem givenKoodiVersioWithKoodistoItem() {
        KoodiVersioWithKoodistoItem item = new KoodiVersioWithKoodistoItem();
        item.setKoodistoItem(givenKoodistoItem());
        KoodiVersio kv = givenKoodiVersio();
        kv.addAlakoodi(givenKoodinSuhde(kv, givenKoodiVersio(), SuhteenTyyppi.SISALTYY));
        kv.addAlakoodi(givenKoodinSuhde(kv, givenKoodiVersio(), SuhteenTyyppi.RINNASTEINEN));
        kv.addYlakoodi(givenKoodinSuhde(givenKoodiVersio(), kv, SuhteenTyyppi.SISALTYY));
        kv.addYlakoodi(givenKoodinSuhde(givenKoodiVersio(), kv, SuhteenTyyppi.RINNASTEINEN));
        item.setKoodiVersio(kv);
        return item;
    }

    private KoodinSuhde givenKoodinSuhde(KoodiVersio ylaKoodiVersio, KoodiVersio alaKoodiVersio, SuhteenTyyppi tyyppi) {
        KoodinSuhde suhde = new KoodinSuhde();
        suhde.setAlakoodiVersio(alaKoodiVersio);
        suhde.setYlakoodiVersio(ylaKoodiVersio);
        suhde.setSuhteenTyyppi(tyyppi);
        return suhde;
    }

    private KoodiMetadata givenKoodiMetadata() {
        KoodiMetadata data = new KoodiMetadata();
        data.setId(1l);
        data.setKieli(Kieli.FI);
        data.setNimi("Name");
        data.setKuvaus("Kuvaus");
        data.setLyhytNimi("n");
        return data;
    }

    private KoodistoItem givenKoodistoItem() {
        KoodistoItem item = new KoodistoItem();
        item.setKoodistoUri("koodistouri");
        item.setOrganisaatioOid("1.9.2.3.405");
        return item;
    }

    private KoodistoVersioKoodiVersio givenKoodistoVersio(KoodiVersio koodiv) {
        KoodistoVersio kv = new KoodistoVersio();
        KoodistoMetadata data = new KoodistoMetadata();
        data.setKieli(Kieli.EN);
        kv.addMetadata(data);
        KoodistoVersioKoodiVersio kvkv = new KoodistoVersioKoodiVersio();
        kvkv.setKoodistoVersio(kv);
        kvkv.setKoodiVersio(koodiv);
        return kvkv;
    }

    private KoodiVersio givenKoodiVersio() {
        Koodi koodi = new Koodi();
        koodi.setKoodiUri("testikoodi");
        KoodiVersio kv = new KoodiVersio();
        kv.setKoodi(koodi);
        kv.setKoodiarvo("koodi elementin arvo");
        kv.setVoimassaAlkuPvm(Calendar.getInstance().getTime());
        kv.addMetadata(givenKoodiMetadata());
        kv.addKoodistoVersio(givenKoodistoVersio(kv));
        kv.setVersio(koodiVersio++);
        return kv;
    }

}
