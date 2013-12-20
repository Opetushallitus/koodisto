package fi.vm.sade.koodisto.service.business.it;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.dao.KoodiDAO;
import fi.vm.sade.koodisto.dao.KoodiVersioDAO;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.KoodistoBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodiVersioHasRelationsException;
import fi.vm.sade.koodisto.service.business.exception.KoodiVersioNotPassiivinenException;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;

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
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class KoodistoBusinessServiceTest {

    @Autowired
    private KoodistoBusinessService koodistoBusinessService;

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private KoodiVersioDAO koodiVersioDAO;

    @Test
    public void testCreate() {
        KoodistoVersio koodistoVersio = createKoodisto("omistaja", "organisaatioOid", new Date(), null,
                "uusi luotu testikoodisto");
        assertNotNull(koodistoVersio);
        assertNotNull(koodistoVersio.getId());
        assertNotNull(koodistoVersio.getKoodisto());
        assertNotNull(koodistoVersio.getKoodisto().getId());
    }

    private List<KoodistoVersio> listAllKoodistoVersions(String koodistoUri) {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.koodistoVersiosByUri(koodistoUri);
        return koodistoBusinessService.searchKoodistos(searchType);
    }

    private KoodistoVersio getKoodistoByUri(String koodistoUri) {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);
        List<KoodistoVersio> koodistos = koodistoBusinessService.searchKoodistos(searchType);
        if (koodistos.size() != 1) {
            throw new RuntimeException("Failing");
        }

        return koodistos.get(0);
    }

    @Test
    public void testCreateNewVersion() {
        String koodistoUri = "http://www.kunnat.fi/kunta";

        List<KoodistoVersio> currentVersions = listAllKoodistoVersions(koodistoUri);
        assertEquals(1, currentVersions.size());
        KoodistoVersio newVersion = koodistoBusinessService.createNewVersion(koodistoUri);
        assertNotSame(currentVersions.get(0).getVersio(), newVersion.getVersio());
        currentVersions = listAllKoodistoVersions(koodistoUri);
        assertEquals(2, currentVersions.size());
    }

    @Test
    public void testListAllVersions() {

        String uri = "http://www.kunnat.fi/kunta";

        List<KoodistoVersio> koodistos = listAllKoodistoVersions(uri);
        assertEquals(1, koodistos.size());
    }

    private KoodistoVersio createKoodisto(String omistaja, String organisaatioOid, Date voimassaAlkuPvm, Date voimassaLoppuPvm, String nimi) {
        List<KoodistoRyhma> ryhmas = koodistoBusinessService.listAllKoodistoRyhmas();
        List<String> ryhmaUris = new ArrayList<String>();
        ryhmaUris.add(ryhmas.get(0).getKoodistoRyhmaUri());

        CreateKoodistoDataType createKoodistoDataType = fi.vm.sade.koodisto.service.it.DataUtils.createCreateKoodistoDataType(
                omistaja, organisaatioOid, voimassaAlkuPvm, voimassaLoppuPvm, nimi);
        KoodistoVersio koodisto = koodistoBusinessService.createKoodisto(ryhmaUris, createKoodistoDataType);
        return koodisto;
    }

    @Test(expected = KoodiVersioHasRelationsException.class)  
    public void testDeleteVersioWithKoodiVersioHasRelationsException() {
        final String koodistoUri = "http://testikoodisto.fi";
        final Integer koodistoVersio = 2;

        final int numberOfVersiosBeforeDelete = 2;
        final int numberOfVersiosAfterDelete = 2;

        KoodistoVersio koodisto = getKoodistoByUri(koodistoUri);
        final List<KoodistoVersio> before = listAllKoodistoVersions(koodisto.getKoodisto().getKoodistoUri());
        assertEquals(numberOfVersiosBeforeDelete, before.size());
       
        koodistoBusinessService.delete(koodistoUri, koodistoVersio);
  
        koodisto = getKoodistoByUri(koodistoUri);
        final List<KoodistoVersio> after = listAllKoodistoVersions(koodisto.getKoodisto().getKoodistoUri());
        assertEquals(numberOfVersiosAfterDelete, after.size());
    }

    @Test
    public void testDeleteKoodisto() {

        final String koodistoUri = "http://koodisto20";
        final Integer koodistoVersio = 1;

        assertTrue(koodistoBusinessService.koodistoExists(koodistoUri));

        try {
            koodistoBusinessService.delete(koodistoUri, koodistoVersio);
        } catch (KoodiVersioNotPassiivinenException e) {
        }

        assertTrue(koodistoBusinessService.koodistoExists(koodistoUri));

        koodiBusinessService.delete("478", 1, true);

        try {
            koodistoBusinessService.delete(koodistoUri, koodistoVersio);
        } catch (KoodiVersioNotPassiivinenException e) {
        }

        assertFalse(koodistoBusinessService.koodistoExists(koodistoUri));

    }

}
