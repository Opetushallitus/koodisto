package fi.vm.sade.koodisto.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

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

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.dao.KoodistoVersioDAO;
import fi.vm.sade.koodisto.dao.KoodistonSuhdeDAO;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class KoodistonSuhdeDAOTest {

    @Autowired
    private KoodistonSuhdeDAO suhdeDAO;

    @Autowired
    private KoodistoVersioDAO versionDAO;

    @Test
    public void copiesRelationsFromOldKoodistonVersioToNewOne() {
        KoodistoVersio original = versionDAO.read(Long.valueOf(1));
        KoodistoVersio newVersion = givenNewKoodistoVersio(original);
        suhdeDAO.copyRelations(original, newVersion);
        assertRelations(original, newVersion);
    }

    @Test
    public void copiedRelationsAreActuallyStoredInDb() {
        KoodistoVersio original = versionDAO.read(Long.valueOf(1));
        KoodistoVersio newVersion = givenNewKoodistoVersio(original);
        suhdeDAO.copyRelations(original, newVersion);
        versionDAO.detach(newVersion);
        newVersion = versionDAO.read(newVersion.getId());
        assertRelations(original, newVersion);
    }

    @Test
    public void deleRelations() {
        KoodistonSuhde toBeDeleted = suhdeDAO.read(Long.valueOf(5));
        KoodistoUriAndVersioType yla = getKoodistoUriAndVersioType(versionDAO.read(toBeDeleted.getYlakoodistoVersio().getId()));
        KoodistoUriAndVersioType ala = getKoodistoUriAndVersioType(versionDAO.read(toBeDeleted.getAlakoodistoVersio().getId()));
        suhdeDAO.deleteRelations(yla, Arrays.asList(ala), toBeDeleted.getSuhteenTyyppi());
        assertNull(suhdeDAO.read(toBeDeleted.getId()));
    }

    private KoodistoUriAndVersioType getKoodistoUriAndVersioType(KoodistoVersio versio) {
        KoodistoUriAndVersioType type = new KoodistoUriAndVersioType();
        type.setKoodistoUri(versio.getKoodisto().getKoodistoUri());
        type.setVersio(versio.getVersio());
        return type;
    }

    private void assertRelations(KoodistoVersio original, KoodistoVersio newVersion) {
        assertEquals(original.getYlakoodistos().size(), newVersion.getYlakoodistos().size());
        assertEquals(original.getAlakoodistos().size(), newVersion.getAlakoodistos().size());
    }

    private KoodistoVersio givenNewKoodistoVersio(KoodistoVersio original) {
        KoodistoVersio newVersion = new KoodistoVersio();
        newVersion.setKoodisto(original.getKoodisto());
        newVersion.setTila(Tila.HYVAKSYTTY);
        newVersion.setVoimassaAlkuPvm(original.getVoimassaAlkuPvm());
        newVersion.setVersio(2);
        for ( KoodistoMetadata data : original.getMetadatas()) {
            newVersion.addMetadata(data);
        }
        return versionDAO.insert(newVersion);
    }

}
