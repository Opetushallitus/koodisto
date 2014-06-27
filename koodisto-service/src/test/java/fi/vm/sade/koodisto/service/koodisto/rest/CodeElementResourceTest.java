package fi.vm.sade.koodisto.service.koodisto.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.dto.KoodiRelaatioListaDto;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data-multiple-relations.xml")
public class CodeElementResourceTest {

    @Autowired
    private CodeElementResource resource;

    @Autowired
    private KoodiBusinessService service;

    @Test
    public void returns400IfQueryParamsAreMissing() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        assertResponse(resource.addRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), kr), 400);
        assertResponse(resource.removeRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), kr), 400);

        kr.setRelations(new ArrayList<String>());
        assertResponse(resource.addRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), kr), 400);
        assertResponse(resource.removeRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), kr), 400);
    }

    @Test
    public void returns500IfErrorOccurs() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("koodi"));
        assertResponse(resource.addRelations("codeelementuri", "SISALTYY", kr), 500);
        assertResponse(resource.removeRelations("codeelementuri", "SISALTYY", kr), 500);

        kr.setRelations(Arrays.asList("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3"));
        assertResponse(resource.addRelations("codeelementuri", "asd", kr), 500);
        assertResponse(resource.removeRelations("codeelementuri", "asd", kr), 500);
    }

    @Test
    public void removesMultipleCodeElementRelationsWithTypeRINNASTEINEN() {
        String codeElementUri = "sisaltaakoodisto1koodit";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3"));
        assertResponse(resource.removeRelations(codeElementUri, "RINNASTEINEN", kr), 200);
        assertTrue(service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).isEmpty());
    }

    @Test
    public void addsMultipleCodeElementRelationsWithTypeRINNASTEINEN() {
        String codeElementUri = "lisaarinnasteinen14";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("lisaarinnasteinen14kanssa1", "lisaarinnasteinen14kanssa2", "lisaarinnasteinen14kanssa3"));
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
        assertResponse(resource.addRelations(codeElementUri, "RINNASTEINEN", kr), 200);
        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
    }

    @Test
    public void removesMultipleCodeElementRelationsWithTypeSISALTYY() {
        String codeElementUri = "sisaltaakoodisto1koodit";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde4kanssa1", "sisaltyysuhde4kanssa2"));
        assertResponse(resource.removeRelations(codeElementUri, "SISALTYY", kr), 200);
        assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void addsMultipleCodeElementRelationsWithTypeSISALTYY() {
        String codeElementUri = "lisaasisaltyy18";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("lisaasisaltyy18kanssa1", "lisaasisaltyy18kanssa2"));
        kr.setChild(false);
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
        assertResponse(resource.addRelations(codeElementUri, "SISALTYY", kr), 200);
        assertEquals(2, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void removesMultipleCodeElementRelationsWithTypeSISALTYYAndCodeElementBeingLower() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde4kanssa1", "sisaltyysuhde4kanssa2", "sisaltyysuhde4kanssa3"));
        kr.setChild(true);
        assertResponse(resource.removeRelations("sisaltyykoodisto1koodienkanssa", "SISALTYY", kr), 200);
        assertEquals(0, service.listByRelation("sisaltyysuhde4kanssa1", 2, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(1, service.listByRelation("sisaltyysuhde4kanssa1", 1, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(0, service.listByRelation("sisaltyysuhde4kanssa2", 2, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(1, service.listByRelation("sisaltyysuhde4kanssa2", 1, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(0, service.listByRelation("sisaltyysuhde4kanssa3", 2, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(1, service.listByRelation("sisaltyysuhde4kanssa3", 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void addsMultipleCodeElementRelationsWithTypeSISALTYYAndCodeElementBeingLower() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("lisaasisaltyy18kanssa1", "lisaasisaltyy18kanssa2"));
        kr.setChild(true);
        assertResponse(resource.addRelations("lisaasisaltyy18", "SISALTYY", kr), 200);
        assertEquals(1, service.listByRelation("lisaasisaltyy18kanssa1", 2, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(0, service.listByRelation("lisaasisaltyy18kanssa1", 1, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(1, service.listByRelation("lisaasisaltyy18kanssa2", 2, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(0, service.listByRelation("lisaasisaltyy18kanssa2", 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void removesMultipleCodeElementRelationsThatBelongToDifferentCodes() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde9kanssa1", "sisaltyysuhde9kanssa2", "sisaltyysuhde9kanssa3"));
        kr.setChild(false);
        String codeElementUri = "sisaltaakoodisto6ja7ja8koodit";
        assertResponse(resource.removeRelations(codeElementUri, "SISALTYY", kr), 200);
        assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void addsMultipleCodeElementRelationsThatBelongToDifferentCodes() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde9kanssa1", "sisaltyysuhde9kanssa2", "sisaltyysuhde9kanssa3"));
        kr.setChild(false);
        String codeElementUri = "sisaltaakoodisto6ja7ja8koodit";
        assertResponse(resource.removeRelations(codeElementUri, "SISALTYY", kr), 200);
        assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    private void assertResponse(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatus());
    }
}
