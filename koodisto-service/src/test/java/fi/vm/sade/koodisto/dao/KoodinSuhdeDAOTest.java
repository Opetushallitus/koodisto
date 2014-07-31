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

    @Test
    public void testGetAndDeleteMultipleRelationsIncludes() {

        KoodiUriAndVersioType koodi1 = new KoodiUriAndVersioType();
        koodi1.setKoodiUri("463");
        koodi1.setVersio(1);
        KoodiUriAndVersioType koodi2 = new KoodiUriAndVersioType();
        koodi2.setKoodiUri("464");
        koodi2.setVersio(1);
        KoodiUriAndVersioType koodi3 = new KoodiUriAndVersioType();
        koodi3.setKoodiUri("465");
        koodi3.setVersio(1);

        List<KoodiUriAndVersioType> alakoodis = new ArrayList<KoodiUriAndVersioType>();
        alakoodis.add(koodi2);
        alakoodis.add(koodi3);

        SuhteenTyyppi st = SuhteenTyyppi.SISALTYY;

        List<KoodinSuhde> relations = koodinSuhdeDAO.getRelations(koodi1, alakoodis, st);
        assertEquals(2, relations.size());

        koodinSuhdeDAO.massRemove(relations);

        List<KoodinSuhde> relationsAfter = koodinSuhdeDAO.getRelations(koodi1, alakoodis, st);
        assertEquals(0, relationsAfter.size());
    }
    
    @Test
    public void testGetAndDeleteMultipleRelationsLevelsWith() {

        KoodiUriAndVersioType koodi1 = new KoodiUriAndVersioType();
        koodi1.setKoodiUri("3");
        koodi1.setVersio(1);
        KoodiUriAndVersioType koodi2 = new KoodiUriAndVersioType();
        koodi2.setKoodiUri("5");
        koodi2.setVersio(1);
        KoodiUriAndVersioType koodi3 = new KoodiUriAndVersioType();
        koodi3.setKoodiUri("7");
        koodi3.setVersio(1);

        List<KoodiUriAndVersioType> alakoodis1 = new ArrayList<KoodiUriAndVersioType>();
        alakoodis1.add(koodi2);
        alakoodis1.add(koodi3);
        List<KoodiUriAndVersioType> alakoodis2 = new ArrayList<KoodiUriAndVersioType>();
        alakoodis2.add(koodi1);
        alakoodis2.add(koodi3);
        List<KoodiUriAndVersioType> alakoodis3 = new ArrayList<KoodiUriAndVersioType>();
        alakoodis3.add(koodi1);
        alakoodis3.add(koodi2);

        
        SuhteenTyyppi st = SuhteenTyyppi.RINNASTEINEN;

        List<KoodinSuhde> relations1 = koodinSuhdeDAO.getRelations(koodi1, alakoodis1, st);
        List<KoodinSuhde> relations2 = koodinSuhdeDAO.getRelations(koodi2, alakoodis2, st);
        List<KoodinSuhde> relations3 = koodinSuhdeDAO.getRelations(koodi3, alakoodis3, st);
        assertEquals(2, relations1.size());
        assertEquals(2, relations2.size());
        assertEquals(2, relations3.size());

        koodinSuhdeDAO.massRemove(relations1);
        
        List<KoodinSuhde> relationsAfter1 = koodinSuhdeDAO.getRelations(koodi1, alakoodis1, st);
        List<KoodinSuhde> relationsAfter2 = koodinSuhdeDAO.getRelations(koodi2, alakoodis2, st);
        List<KoodinSuhde> relationsAfter3 = koodinSuhdeDAO.getRelations(koodi3, alakoodis3, st);
        assertEquals(0, relationsAfter1.size());
        assertEquals(1, relationsAfter2.size());
        assertEquals(1, relationsAfter3.size());
    }

}
