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
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data-multiple-relations.xml")
public class CodeElementResourceTest {
    
    @Autowired
    private CodeElementResource resource;
    
    @Autowired
    private KoodiBusinessService service;
    
    @Test
    public void returns400IfQueryParamsAreMissing() {
        assertResponse(resource.removeRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), new ArrayList<String>()), 400);
        assertResponse(resource.removeRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), null), 400);
    }
    
    
    @Test
    public void returns500IfErrorOccurs() {
        assertResponse(resource.removeRelations("codeelementuri", "SISALTYY", Arrays.asList("koodi")), 500);        
    }
    
    @Test
    public void removesMultipleCodeElementRelationsWithTypeRINNASTEINEN() {
        String codeElementUri = "sisaltaakoodisto1koodit";
        assertResponse(resource.removeRelations(codeElementUri, "RINNASTEINEN", Arrays.asList("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3")), 200);
        assertTrue(service.listByRelation(codeElementUri, 1, true, SuhteenTyyppi.RINNASTEINEN).isEmpty());
    }
    
    @Test
    public void removesMultipleCodeElementRelationsWithTypeSISALTYY() {        
        String codeElementUri = "sisaltaakoodisto1koodit";
        assertResponse(resource.removeRelations(codeElementUri, "SISALTYY", Arrays.asList("sisaltyysuhde4kanssa1", "sisaltyysuhde4kanssa2")), 200);
        assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }
    
    @Test
    public void removesMultipleCodeElementRelationsWithTypeSISALTYYAndCodeElementBeingLower() {
        
    }
    
    @Test
    public void removesMultipleCodeElementRelationsThatBelongToDifferentCodes() {
        
    }
    
    private void assertResponse(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatus());
    }
}
