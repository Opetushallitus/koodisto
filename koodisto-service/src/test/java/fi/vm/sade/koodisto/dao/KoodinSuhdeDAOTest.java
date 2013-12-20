package fi.vm.sade.koodisto.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.model.KoodinSuhde;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class KoodinSuhdeDAOTest {

    @Autowired
    private KoodinSuhdeDAO koodinSuhdeDAO;

    @Test
    public void testGetRelations() {

        List<KoodiUriAndVersioType> list = new ArrayList<KoodiUriAndVersioType>();
        KoodiUriAndVersioType kv1 = new KoodiUriAndVersioType();
        kv1.setKoodiUri("3");
        kv1.setVersio(1);

        KoodiUriAndVersioType kv2 = new KoodiUriAndVersioType();
        kv2.setKoodiUri("5");
        kv2.setVersio(1);

        list.add(kv1);
        list.add(kv2);

        KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
        kv.setKoodiUri("7");
        kv.setVersio(1);

        List<KoodinSuhde> l = koodinSuhdeDAO.getRelations(kv, list, SuhteenTyyppi.RINNASTEINEN);
        Assert.assertEquals(2, l.size());
    }

    @Test
    public void testGetRelations1() {
        List<KoodiUriAndVersioType> list = new ArrayList<KoodiUriAndVersioType>();
        KoodiUriAndVersioType kv1 = new KoodiUriAndVersioType();
        kv1.setKoodiUri("9");
        kv1.setVersio(1);
        list.add(kv1);

        KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
        kv.setKoodiUri("3");
        kv.setVersio(1);

        List<KoodinSuhde> l = koodinSuhdeDAO.getRelations(kv, list, SuhteenTyyppi.SISALTYY);
        assertEquals(1, l.size());
        assertEquals(l.get(0).getSuhteenTyyppi(), SuhteenTyyppi.SISALTYY);
    }
}
