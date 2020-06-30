package fi.vm.sade.koodisto.dao.impl;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.dao.KoodistoVersioDAO;
import fi.vm.sade.koodisto.dao.KoodistonSuhdeDAO;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistonSuhde;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.types.common.KoodistoUriAndVersioType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@DatabaseSetup("classpath:test-data.xml")
@Transactional
@WithMockUser("1.2.3.4.5")
public class KoodistonSuhdeDAOImplIT {

    @Autowired
    private KoodistonSuhdeDAO suhdeDAO;

    @Autowired
    private KoodistoVersioDAO versionDAO;

    @Test
    public void copiesRelationsFromOldKoodistonVersioToNewOne() {
        KoodistoVersio original = versionDAO.read(-1L);
        KoodistoVersio newVersion = givenNewKoodistoVersio(original);
        suhdeDAO.copyRelations(original, newVersion);
        assertRelations(original, newVersion);
    }

    @Test
    public void copiedRelationsAreActuallyStoredInDb() {
        KoodistoVersio original = versionDAO.read(-1L);
        KoodistoVersio newVersion = givenNewKoodistoVersio(original);
        suhdeDAO.copyRelations(original, newVersion);
        assertRelations(original, newVersion);
    }

    @Test
    public void doesNotCopyPassiveRelations() {
        KoodistoVersio original = versionDAO.read(-911L);
        KoodistoVersio newVersion = givenNewKoodistoVersio(original);
        suhdeDAO.copyRelations(original, newVersion);
        newVersion = versionDAO.read(newVersion.getId());
        assertTrue(newVersion.getAlakoodistos().isEmpty());
        assertTrue(newVersion.getYlakoodistos().isEmpty());
    }
    
    @Test
    public void oldRelationsAreSetToPassiveWhenCopying() {
        KoodistoVersio original = versionDAO.read(-1L);
        KoodistoVersio newVersion = givenNewKoodistoVersio(original);
        suhdeDAO.copyRelations(original, newVersion);
        assertOldRelationsArePassive(original, newVersion);
    }

    @Test
    public void deleRelations() {
        KoodistonSuhde toBeDeleted = suhdeDAO.read(-5L);
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

    private void assertOldRelationsArePassive(KoodistoVersio original, KoodistoVersio newVersion) {
        for (KoodistonSuhde ks : original.getYlakoodistos()) {
            assertTrue(ks.isAlaKoodistoPassive());
        }
        for (KoodistonSuhde ks : original.getAlakoodistos()) {
            assertTrue(ks.isYlaKoodistoPassive());
        }
        for (KoodistonSuhde ks : newVersion.getAlakoodistos()) {
            assertFalse(ks.isPassive());
        }
        for (KoodistonSuhde ks : newVersion.getYlakoodistos()) {
            assertFalse(ks.isPassive());
        }
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
