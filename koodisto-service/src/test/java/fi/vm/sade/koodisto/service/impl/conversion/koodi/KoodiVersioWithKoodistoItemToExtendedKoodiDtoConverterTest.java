package fi.vm.sade.koodisto.service.impl.conversion.koodi;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.Koodi;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.business.util.KoodistoItem;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodiVersioWithKoodistoItemToExtendedKoodiDtoConverterTest {
	
	@Autowired()
    private SadeConversionService conversionService;
	
	@Test
	public void convertsKoodinSuhdeToRelationCodeElement() {
		KoodiVersioWithKoodistoItem kv = givenKoodiVersioWithKoodistoItem();
		ExtendedKoodiDto dto = conversionService.convert(kv, ExtendedKoodiDto.class);
		assertEquals(1, dto.getIncludesCodeElements().size());
		assertEquals(2, dto.getLevelsWithCodeElements().size());
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
		kv.addMetadata(givenKoodiMetadata());
		kv.setVersio(1);
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

	private KoodiVersio givenKoodiVersio() {
		Koodi koodi = new Koodi();
		koodi.setKoodiUri("testikoodi");
		KoodiVersio kv = new KoodiVersio();
		kv.setKoodi(koodi);
		kv.setKoodiarvo("testi koodin arvo");
		kv.setVoimassaAlkuPvm(Calendar.getInstance().getTime());		
		return kv;
	}
	
}
