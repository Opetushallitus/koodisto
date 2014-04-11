package fi.vm.sade.koodisto.service.business.it;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class KoodiBusinessServiceRelationsTest {

    private Logger logger = LoggerFactory.getLogger(KoodiBusinessServiceRelationsTest.class);

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Test
    public void testListByRelation() {
        KoodiUriAndVersioType kv = givenKoodiUriAndVersioType("3", 1);

        List<KoodiVersioWithKoodistoItem> rinnastuvat = koodiBusinessService.listByRelation(kv,
                SuhteenTyyppi.RINNASTEINEN, false);
        assertEquals(2, rinnastuvat.size());
        for (KoodiVersioWithKoodistoItem k : rinnastuvat) {
            logger.info("Koodi: " + k.getKoodiVersio().getKoodi().getKoodiUri() + " - versio "
                    + k.getKoodiVersio().getVersio());
        }

        List<KoodiVersioWithKoodistoItem> children = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.SISALTYY,
                false);
        assertEquals(2, children.size());
        List<KoodiVersioWithKoodistoItem> parents = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.SISALTYY,
                true);
        assertEquals(1, parents.size());

    }

    @Test
    public void testAddRelation1() {
        KoodiUriAndVersioType ylakoodi = givenKoodiUriAndVersioType("3", 1);
        KoodiUriAndVersioType alakoodi = givenKoodiUriAndVersioType("21", 1);

        List<KoodiVersioWithKoodistoItem> rinnastuvat = koodiBusinessService.listByRelation(ylakoodi,
                SuhteenTyyppi.RINNASTEINEN, false);
        assertEquals(2, rinnastuvat.size());

        koodiBusinessService.addRelation(ylakoodi.getKoodiUri(), alakoodi.getKoodiUri(), SuhteenTyyppi.RINNASTEINEN);

        rinnastuvat = koodiBusinessService.listByRelation(ylakoodi, SuhteenTyyppi.RINNASTEINEN, false);
        assertEquals(3, rinnastuvat.size());
    }

    @Test
    public void testAddRelation2() {
        KoodiUriAndVersioType kv = givenKoodiUriAndVersioType("369", 1);

        List<KoodiVersioWithKoodistoItem> result = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.RINNASTEINEN,
                false);
        Assert.assertEquals(0L, result.size());

        List<String> list = new ArrayList<String>();
        list.add("371");
        list.add("373");
        list.add("375");

        koodiBusinessService.addRelation(kv.getKoodiUri(), list, SuhteenTyyppi.RINNASTEINEN);
        result = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.RINNASTEINEN, false);
        Assert.assertEquals(3L, result.size());
    }

    @Test
    public void testRemoveRelation() {
        KoodiUriAndVersioType kv = givenKoodiUriAndVersioType("7", 1);

        List<KoodiVersioWithKoodistoItem> result = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.RINNASTEINEN,
                true);
        Assert.assertEquals(2L, result.size());

        List<String> list = new ArrayList<String>();
        list.add("5");
        list.add("3");

        koodiBusinessService.removeRelation(kv.getKoodiUri(), list, SuhteenTyyppi.RINNASTEINEN);
        result = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.RINNASTEINEN, true);
        Assert.assertEquals(0L, result.size());
    }
    
    @Test
    public void updatingKoodiVersioDoesNotRemoveOldRelations() {
    	KoodiUriAndVersioType kv = givenKoodiUriAndVersioType("7", 1);
    	koodiBusinessService.createNewVersion(kv.getKoodiUri());
     	List<KoodiVersioWithKoodistoItem> newItems = koodiBusinessService.listByRelation(givenKoodiUriAndVersioType("7", 2), SuhteenTyyppi.RINNASTEINEN, true);
    	assertEquals(2, newItems.size());
    	assertEquals(2, koodiBusinessService.listByRelation(kv, SuhteenTyyppi.RINNASTEINEN, true).size());
    }

	private KoodiUriAndVersioType givenKoodiUriAndVersioType(String koodiUri, int versio) {
		KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
    	kv.setKoodiUri(koodiUri);
    	kv.setVersio(versio);
		return kv;
	}

}
