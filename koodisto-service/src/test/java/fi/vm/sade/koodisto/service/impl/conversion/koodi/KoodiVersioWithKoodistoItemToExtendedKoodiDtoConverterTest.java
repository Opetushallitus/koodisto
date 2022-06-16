package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto.RelationCodeElement;
import fi.vm.sade.koodisto.dto.SimpleMetadataDto;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.repository.KoodiVersioRepository;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.business.util.KoodistoItem;
import fi.vm.sade.koodisto.service.conversion.impl.koodi.KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter;
import fi.vm.sade.koodisto.test.support.DtoFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverterTest {

    @Mock
    private KoodiVersioRepository koodiVersioRepository;

    @Autowired
    KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverter koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter;

    private Integer koodiVersio = 1;

    @Test
    public void convertsKoodinSuhdeToRelationCodeElement() {
        KoodiVersioWithKoodistoItem kv = givenKoodiVersioWithKoodistoItem();
        ExtendedKoodiDto dto = koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter.convert(kv);
        assertEquals(1, dto.getIncludesCodeElements().size());
        assertEquals(2, dto.getLevelsWithCodeElements().size());
        assertEquals(1, dto.getWithinCodeElements().size());
    }

    @Test
    public void storesCodeElementNameLanguageDescriptionAndValueToRelationCodeElement() {
        KoodiVersioWithKoodistoItem kv = givenKoodiVersioWithKoodistoItem();
        ExtendedKoodiDto dto = koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter.convert(kv);
        RelationCodeElement rel = dto.getIncludesCodeElements().get(0);
        SimpleMetadataDto data = rel.getRelationMetadata().get(0);
        KoodiMetadata givenKoodiMetadata = givenKoodiMetadata();
        assertEquals(givenKoodiMetadata.getKieli(), data.kieli);
        assertEquals(givenKoodiMetadata.getNimi(), data.nimi);
        assertEquals(givenKoodiMetadata.getKuvaus(), data.kuvaus);
        assertEquals(givenKoodiVersio().getKoodiarvo(), rel.getCodeElementValue());
    }

    @Test
    public void storesParentCodesMetadataToRelationCodeElement() {
        KoodiVersioWithKoodistoItem kv = givenKoodiVersioWithKoodistoItem();
        ExtendedKoodiDto dto = koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter.convert(kv);
        assertEquals(Kieli.EN, dto.getIncludesCodeElements().get(0).getParentMetadata().get(0).kieli);
    }

    @Test
    public void converterProvidesRelationCodeElementForLatestCodeVersionWhenRelationRelatesToLatestCodeVersion() {
        KoodiVersio parent = DtoFactory.createKoodiVersioWithUriAndVersio("penaali", 1).build();
        KoodiVersio child = DtoFactory.createKoodiVersioWithUriAndVersioAndRelation("kynä", 1, parent, SuhteenTyyppi.SISALTYY);
        Map<String, Integer> dummyResponse = new HashMap<String, Integer>();
        dummyResponse.put("penaali", 1);
        when(koodiVersioRepository.getLatestVersionNumbersForUris("penaali")).thenReturn(dummyResponse);
        when(koodiVersioRepository.isLatestKoodiVersio("kynä", 1)).thenReturn(true);
        ExtendedKoodiDto dto = koodiVersioWithKoodistoItemToExtendedKoodiDtoConverter.convert(new KoodiVersioWithKoodistoItem(child, new KoodistoItem()));
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
