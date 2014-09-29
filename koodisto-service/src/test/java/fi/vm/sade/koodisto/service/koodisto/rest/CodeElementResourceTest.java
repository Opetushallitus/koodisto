package fi.vm.sade.koodisto.service.koodisto.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto.RelationCodeElement;
import fi.vm.sade.koodisto.dto.KoodiChangesDto;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodiRelaatioListaDto;
import fi.vm.sade.koodisto.dto.SimpleKoodiDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    public void returns400AndCorrectErrorCodeIfQueryParamsAreMissing() {

        String nullString = null;
        String blankString = "";
        KoodiRelaatioListaDto nullRelationList = null;
        KoodiDto nullDto = null;

        String stubString = "uri";
        KoodiDto codeelementDTO = new KoodiDto();

        assertResponse(resource.addRelation(nullString, stubString, stubString), 400, "error.validation.codeelementuri");
        assertResponse(resource.addRelation(blankString, stubString, stubString), 400, "error.validation.codeelementuri");
        assertResponse(resource.addRelation(stubString, nullString, stubString), 400, "error.validation.codeelementuritoadd");
        assertResponse(resource.addRelation(stubString, blankString, stubString), 400, "error.validation.codeelementuritoadd");
        assertResponse(resource.addRelation(stubString, stubString, nullString), 400, "error.validation.relationtype");
        assertResponse(resource.addRelation(stubString, stubString, blankString), 400, "error.validation.relationtype");

        assertResponse(resource.addRelations(nullRelationList), 400, "error.validation.codeelementrelationlist");

        assertResponse(resource.delete(nullString, 0), 400, "error.validation.codeelementuri");
        assertResponse(resource.delete(blankString, 0), 400, "error.validation.codeelementuri");

        assertResponse(resource.getAllCodeElementsByCodesUriAndVersion(nullString, 0), 400, "error.validation.codesuri");
        assertResponse(resource.getAllCodeElementsByCodesUriAndVersion(blankString, 0), 400, "error.validation.codesuri");

        assertResponse(resource.getAllCodeElementVersionsByCodeElementUri(nullString), 400, "error.validation.codeelementuri");
        assertResponse(resource.getAllCodeElementVersionsByCodeElementUri(blankString), 400, "error.validation.codeelementuri");

        assertResponse(resource.getCodeElementByCodeElementUri(nullString, 0, stubString), 400, "error.validation.codesuri");
        assertResponse(resource.getCodeElementByCodeElementUri(blankString, 0, stubString), 400, "error.validation.codesuri");
        assertResponse(resource.getCodeElementByCodeElementUri(stubString, 0, nullString), 400, "error.validation.codeelementuri");
        assertResponse(resource.getCodeElementByCodeElementUri(stubString, 0, blankString), 400, "error.validation.codeelementuri");

        assertResponse(resource.getCodeElementByUriAndVersion(nullString, 0), 400, "error.validation.codeelementuri");
        assertResponse(resource.getCodeElementByUriAndVersion(blankString, 0), 400, "error.validation.codeelementuri");

        assertResponse(resource.getLatestCodeElementVersionsByCodeElementUri(nullString), 400, "error.validation.codeelementuri");
        assertResponse(resource.getLatestCodeElementVersionsByCodeElementUri(blankString), 400, "error.validation.codeelementuri");

        assertResponse(resource.insert(nullString, codeelementDTO), 400, "error.validation.codesuri");
        assertResponse(resource.insert(blankString, codeelementDTO), 400, "error.validation.codesuri");
        assertResponse(resource.insert(stubString, nullDto), 400, "error.validation.codeelement");

        assertResponse(resource.removeRelation(nullString, stubString, stubString), 400, "error.validation.codeelementuri");
        assertResponse(resource.removeRelation(blankString, stubString, stubString), 400, "error.validation.codeelementuri");
        assertResponse(resource.removeRelation(stubString, nullString, stubString), 400, "error.validation.codeelementuritoremove");
        assertResponse(resource.removeRelation(stubString, blankString, stubString), 400, "error.validation.codeelementuritoremove");
        assertResponse(resource.removeRelation(stubString, stubString, nullString), 400, "error.validation.relationtype");
        assertResponse(resource.removeRelation(stubString, stubString, blankString), 400, "error.validation.relationtype");

        assertResponse(resource.removeRelations(nullRelationList), 400, "error.validation.codeelementrelationlist");

        assertResponse(resource.update(nullDto), 400, "error.validation.codeelement");

    }

    @Test
    public void returns500IfErrorOccurs() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("koodi"));
        assertResponse(this.addRelations("codeelementuri", "SISALTYY", kr), 500, "error.codeelement.not.found");
        assertResponse(this.removeRelations("codeelementuri", "SISALTYY", kr), 500, "error.codeelement.not.found");

        kr.setRelations(Arrays.asList("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3"));
        assertResponse(this.addRelations("codeelementuri", "asd", kr), 500, "error.codes.generic");
        assertResponse(this.removeRelations("codeelementuri", "asd", kr), 500, "error.codes.generic");
    }

    @Test
    public void removesMultipleCodeElementRelationsWithTypeRINNASTEINEN() {
        String codeElementUri = "sisaltaakoodisto1koodit";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3"));
        assertResponse(this.removeRelations(codeElementUri, "RINNASTEINEN", kr), 200);
        assertTrue(service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).isEmpty());
    }

    @Test
    public void addsMultipleCodeElementRelationsWithTypeRINNASTEINEN() {
        String codeElementUri = "lisaarinnasteinen14";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("lisaarinnasteinen14kanssa1", "lisaarinnasteinen14kanssa2", "lisaarinnasteinen14kanssa3"));
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
        assertResponse(this.addRelations(codeElementUri, "RINNASTEINEN", kr), 200);
        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
    }

    @Test
    public void removesMultipleCodeElementRelationsWithTypeSISALTYY() {
        String codeElementUri = "sisaltaakoodisto1koodit";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde4kanssa1", "sisaltyysuhde4kanssa2"));
        assertResponse(this.removeRelations(codeElementUri, "SISALTYY", kr), 200);
        assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void addsMultipleCodeElementRelationsWithTypeSISALTYY() {
        String codeElementUri = "lisaasisaltyy18";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("lisaasisaltyy18kanssa1", "lisaasisaltyy18kanssa2"));
        kr.setChild(false);
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
        assertResponse(this.addRelations(codeElementUri, "SISALTYY", kr), 200);
        assertEquals(2, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void removesMultipleCodeElementRelationsWithTypeSISALTYYAndCodeElementBeingLower() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde4kanssa1", "sisaltyysuhde4kanssa2", "sisaltyysuhde4kanssa3"));
        kr.setChild(true);
        assertResponse(this.removeRelations("sisaltyykoodisto1koodienkanssa", "SISALTYY", kr), 200);
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
        kr.setRelations(Arrays.asList("lisaasisaltyy18kanssa3", "lisaasisaltyy18kanssa4"));
        kr.setChild(true);
        assertResponse(this.addRelations("lisaasisaltyy18", "SISALTYY", kr), 200);
        assertEquals(1, service.listByRelation("lisaasisaltyy18kanssa3", 2, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(0, service.listByRelation("lisaasisaltyy18kanssa3", 1, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(1, service.listByRelation("lisaasisaltyy18kanssa4", 2, false, SuhteenTyyppi.SISALTYY).size());
        assertEquals(0, service.listByRelation("lisaasisaltyy18kanssa4", 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void removesMultipleCodeElementRelationsThatBelongToDifferentCodes() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde9kanssa1", "sisaltyysuhde9kanssa2", "sisaltyysuhde9kanssa3"));
        kr.setChild(false);
        String codeElementUri = "sisaltaakoodisto6ja7ja8koodit";
        assertResponse(this.removeRelations(codeElementUri, "SISALTYY", kr), 200);
        assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void addsMultipleCodeElementRelationsThatBelongToDifferentCodes() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde9kanssa1", "sisaltyysuhde9kanssa2", "sisaltyysuhde9kanssa3"));
        kr.setChild(false);
        String codeElementUri = "sisaltaakoodisto6ja7ja8koodit";
        assertResponse(this.removeRelations(codeElementUri, "SISALTYY", kr), 200);
        assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void testGetAllCodeElementVersionsByCodeElementUri() {
        List<SimpleKoodiDto> response = (List<SimpleKoodiDto>) resource.getAllCodeElementVersionsByCodeElementUri("sisaltyysuhde4kanssa1").getEntity();
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("sisaltyysuhde4kanssa1", response.get(0).getKoodiUri());

        List<SimpleKoodiDto> response2 = (List<SimpleKoodiDto>) resource.getAllCodeElementVersionsByCodeElementUri("montaversiota").getEntity();
        assertNotNull(response2);
        assertEquals(3, response2.size());
    }

    @Test
    public void testGetAllCodeElementVersionsByCodeElementUriInvalid() {
        assertResponse(resource.getAllCodeElementVersionsByCodeElementUri("invaliduri"), 200);
        assertResponse(resource.getAllCodeElementVersionsByCodeElementUri(""), 400);
        assertResponse(resource.getAllCodeElementVersionsByCodeElementUri(null), 400);
    }

    @Test
    public void testGetCodeElementByUriAndVersion() {
        ExtendedKoodiDto response = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 1).getEntity();
        assertNotNull(response);
        assertEquals("ss4k1", response.getKoodiArvo());
        assertEquals("sisaltyysuhde4kanssa1", response.getKoodiUri());
        assertEquals(Tila.HYVAKSYTTY, response.getTila());
        assertEquals(1, response.getVersio());
        assertEquals("sisaltyysuhde2kanssa", response.getKoodisto().getKoodistoUri());
    }

    @Test
    public void testGetCodeElementByUriAndVersionInvalid() {
        assertResponse(resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 0), 400);
        assertResponse(resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", -1), 400);
        assertResponse(resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 9999), 500, "error.codeelement.not.found");
        assertResponse(resource.getCodeElementByUriAndVersion("invaliduriisnotfound", 1), 500, "error.codeelement.not.found");
        assertResponse(resource.getCodeElementByUriAndVersion("", 1), 400);
        assertResponse(resource.getCodeElementByUriAndVersion(null, 1), 400);
    }

    @Test
    public void testGetCodeElementByCodeElementUri() {
        KoodiDto response = (KoodiDto) resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, "sisaltyysuhde4kanssa1").getEntity();
        assertNotNull(response);
        assertEquals("ss4k1", response.getKoodiArvo());
        assertEquals("sisaltyysuhde4kanssa1", response.getKoodiUri());
        assertEquals(Tila.HYVAKSYTTY, response.getTila());
        assertEquals(1, response.getVersio());
        assertEquals("sisaltyysuhde2kanssa", response.getKoodisto().getKoodistoUri());
    }

    @Test
    public void testGetCodeElementByCodeElementUriInvalid() {
        assertResponse(resource.getCodeElementByCodeElementUri("invalidcodeelementuri", 1, "sisaltyysuhde4kanssa1"), 500, "error.codes.not.found");
        assertResponse(resource.getCodeElementByCodeElementUri("", 1, "sisaltyysuhde4kanssa1"), 400);
        assertResponse(resource.getCodeElementByCodeElementUri(null, 1, "sisaltyysuhde4kanssa1"), 400);

        assertResponse(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 0, "sisaltyysuhde4kanssa1"), 400);
        assertResponse(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", -1, "sisaltyysuhde4kanssa1"), 400, "error.validation.codesversion");
        assertResponse(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 9999, "sisaltyysuhde4kanssa1"), 500);

        assertResponse(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, "invalidcodesuri"), 500, "error.codeelement.not.found");
        assertResponse(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, ""), 400);
        assertResponse(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, null), 400);
    }

    @Test
    public void testGetAllCodeElementsByCodesUriAndVersion() {
        List<SimpleKoodiDto> response = (List<SimpleKoodiDto>) resource.getAllCodeElementsByCodesUriAndVersion("lisaarinnasteinen2", 1).getEntity();
        assertNotNull(response);
        assertEquals(3, response.size());

        // FIXME Tämä palauttaa ilmeisesti palauttaa jotain mutta miksi? Mikä olisi toivottu toiminnallisuus?
        List<SimpleKoodiDto> response2 = (List<SimpleKoodiDto>) resource.getAllCodeElementsByCodesUriAndVersion("lisaasisaltyy3", 0).getEntity();
        assertNotNull(response2);
    }

    @Test
    public void testGetAllCodeElementsByCodesUriAndVersionInvalid() {
        assertResponse(resource.getAllCodeElementsByCodesUriAndVersion("lisaarinnasteinen2", -1), 400);
        assertResponse(resource.getAllCodeElementsByCodesUriAndVersion("", 1), 400);
        assertResponse(resource.getAllCodeElementsByCodesUriAndVersion("uridoesnotexist", 1), 500, "error.codes.not.found");
        assertResponse(resource.getAllCodeElementsByCodesUriAndVersion(null, 1), 400);
    }

    @Test
    public void testGetLatestCodeElementVersionsByCodeElementUri() {
        KoodiDto response = (KoodiDto) resource.getLatestCodeElementVersionsByCodeElementUri("montaversiota").getEntity();
        assertEquals(3, response.getVersio());
        assertEquals("mv3", response.getKoodiArvo());
        assertEquals("montaversiota", response.getKoodiUri());
        assertEquals(Tila.HYVAKSYTTY, response.getTila());
        assertEquals("lisaasisaltyy3", response.getKoodisto().getKoodistoUri());
        assertEquals("Monta versiota 3", response.getMetadata().get(0).getNimi());
    }

    @Test
    public void testGetLatestCodeElementVersionsByCodeElementUriInvalid() {
        assertResponse(resource.getLatestCodeElementVersionsByCodeElementUri("eioleolemassa"), 500, "error.codeelement.not.found");
        assertResponse(resource.getLatestCodeElementVersionsByCodeElementUri(""), 400);
        assertResponse(resource.getLatestCodeElementVersionsByCodeElementUri(null), 400);
    }

    @Test
    public void testInsert() {
        KoodiDto validDto = createValidCodeElementDto("value", "Nimi", 3);

        assertResponse(resource.insert("inserttestkoodisto", validDto), 201);

        KoodiDto newdto = (KoodiDto) resource.getCodeElementByCodeElementUri("inserttestkoodisto", 1, "inserttestkoodisto_value").getEntity();
        assertEquals("value", newdto.getKoodiArvo());

        KoodiDto validDto2 = createValidCodeElementDto("value2", "Nimi2", 3);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, 1, 1, 0, 0, 0);
        Date voimassaAlkuPvm = calendar.getTime();
        validDto2.setVoimassaAlkuPvm(voimassaAlkuPvm);
        calendar.set(2014, 1, 10, 0, 0, 0);
        Date voimassaLoppuPvm = calendar.getTime();
        validDto2.setVoimassaLoppuPvm(voimassaLoppuPvm);

        assertResponse(resource.insert("inserttestkoodisto", validDto2), 201);

        KoodiDto newDto2 = (KoodiDto) resource.getCodeElementByCodeElementUri("inserttestkoodisto", 1, "inserttestkoodisto_value2").getEntity();
        assertEquals("value2", newDto2.getKoodiArvo());
        assertDatesEquals(voimassaLoppuPvm, newDto2.getVoimassaLoppuPvm());
    }

    @Test
    public void testInsertInvalid() {
        KoodiDto validDto = createValidCodeElementDto("newdtouri", "Name", 3);
        assertResponse(resource.insert(null, validDto), 400, "error.validation.codesuri");
        assertResponse(resource.insert("", validDto), 400, "error.validation.codesuri");
        assertResponse(resource.insert("totallyInvalidKoodistoUri", validDto), 500, "error.codes.not.found");

        assertResponse(resource.insert("lisaasisaltyy3", null), 400, "error.validation.codeelement");
        assertResponse(resource.insert("lisaasisaltyy3", new KoodiDto()), 400, "error.validation.value");

        KoodiDto invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setKoodiArvo("");
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400, "error.validation.value");

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setKoodiArvo(null);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400, "error.validation.value");

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setMetadata(null);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400, "error.validation.metadata");

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        ArrayList<KoodiMetadata> metadatas = new ArrayList<KoodiMetadata>();
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400, "error.validation.metadata");

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setVoimassaLoppuPvm(new Date(0L));
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400, "error.validation.enddate");

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        KoodiMetadata invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi(""); // Invalid
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi(null); // Invalid
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi(""); // Invalid
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi(null); // Invalid
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus(""); // Invalid
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus(null); // Invalid
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 400);

    }

    @Test
    public void testAddRelation() {
        String codeElementUri = "lisaarinnasteinen14";
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
        assertResponse(resource.addRelation(codeElementUri, "lisaarinnasteinen14kanssa1", "RINNASTEINEN"), 200);
        assertEquals(1, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        String codeElementUri2 = "lisaasisaltyy18";
        assertEquals(0, service.listByRelation(codeElementUri2, 1, false, SuhteenTyyppi.SISALTYY).size());
        assertResponse(resource.addRelation(codeElementUri2, "lisaasisaltyy18kanssa1", "SISALTYY"), 200);
        assertEquals(1, service.listByRelation(codeElementUri2, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void testAddRelationInvalid() {
        String codeElementUri = "lisaarinnasteinen14";
        String codeElementUriToAdd = "lisaarinnasteinen14kanssa1";
        String codeElementUriToAddWithoutCodesRelation = "sisaltyysuhde4kanssa1";
        String relationType = "RINNASTEINEN";
        assertResponse(resource.addRelation("", codeElementUriToAdd, relationType), 400, "error.validation.codeelementuri");
        assertResponse(resource.addRelation(null, codeElementUriToAdd, relationType), 400, "error.validation.codeelementuri");
        assertResponse(resource.addRelation(codeElementUri, "", relationType), 400, "error.validation.codeelementuritoadd");
        assertResponse(resource.addRelation(codeElementUri, null, relationType), 400, "error.validation.codeelementuritoadd");
        assertResponse(resource.addRelation(codeElementUri, codeElementUriToAdd, ""), 400, "error.validation.relationtype");
        assertResponse(resource.addRelation(codeElementUri, codeElementUriToAdd, null), 400, "error.validation.relationtype");

        assertResponse(resource.addRelation(codeElementUri, codeElementUriToAdd, "doenostexist"), 500, "error.codes.generic");
        assertResponse(resource.addRelation(codeElementUri, codeElementUri, relationType), 500, "error.codeelement.relation.to.self");
        assertResponse(resource.addRelation("doenotexist", codeElementUriToAdd, relationType), 500, "error.codeelement.not.found");
        assertResponse(resource.addRelation(codeElementUri, "doesnotexist", relationType), 500, "error.codeelement.not.found");
        assertResponse(resource.addRelation(codeElementUri, codeElementUriToAddWithoutCodesRelation, relationType), 500,
                "error.codeelement.codes.have.no.relation");

        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        codeElementUri = "lisaasisaltyy18";
        codeElementUriToAdd = "lisaasisaltyy18kanssa1";
        codeElementUriToAddWithoutCodesRelation = "sisaltyysuhde4kanssa1";
        relationType = "SISALTYY";
        assertResponse(resource.addRelation("", codeElementUriToAdd, relationType), 400, "error.validation.codeelementuri");
        assertResponse(resource.addRelation(null, codeElementUriToAdd, relationType), 400, "error.validation.codeelementuri");
        assertResponse(resource.addRelation(codeElementUri, "", relationType), 400, "error.validation.codeelementuritoadd");
        assertResponse(resource.addRelation(codeElementUri, null, relationType), 400, "error.validation.codeelementuritoadd");
        assertResponse(resource.addRelation(codeElementUri, codeElementUriToAdd, ""), 400, "error.validation.relationtype");
        assertResponse(resource.addRelation(codeElementUri, codeElementUriToAdd, null), 400, "error.validation.relationtype");

        assertResponse(resource.addRelation(codeElementUri, codeElementUriToAdd, "doenostexist"), 500, "error.codes.generic");
        assertResponse(resource.addRelation(codeElementUri, codeElementUri, relationType), 500, "error.codeelement.relation.to.self");
        assertResponse(resource.addRelation("doenotexist", codeElementUriToAdd, relationType), 500, "error.codeelement.not.found");
        assertResponse(resource.addRelation(codeElementUri, "doesnotexist", relationType), 500, "error.codeelement.not.found");
        assertResponse(resource.addRelation(codeElementUri, codeElementUriToAddWithoutCodesRelation, relationType), 500,
                "error.codeelement.codes.have.no.relation");

        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void testAddRelationToSameCodes() {
        String codeElementUri = "lisaasisaltyy18kanssa1";
        String codeElementUriToAdd = "lisaasisaltyy18kanssa2";
        String relationType = "SISALTYY";

        assertResponse(resource.addRelation(codeElementUri, codeElementUriToAdd, relationType), 200);

    }

    @Test
    public void savesCodeElementWithNameAndRelationsToSameCodesChanges() {
        String koodiUri = "savekoodisuhteillaomaankoodistoon";
        String nimi = "uusinimi";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();
        assertEquals(1, codeElementToBeSaved.getIncludesCodeElements().size());
        assertEquals("samankoodistonsisalla1", codeElementToBeSaved.getIncludesCodeElements().get(0).codeElementUri);
        assertEquals(1, codeElementToBeSaved.getWithinCodeElements().size());
        assertEquals("samankoodistonsisalla2", codeElementToBeSaved.getWithinCodeElements().get(0).codeElementUri);

        codeElementToBeSaved.getMetadata().get(0).setNimi(nimi);
        codeElementToBeSaved.getIncludesCodeElements().clear();
        codeElementToBeSaved.getWithinCodeElements().clear();
        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusisamankoodistonsisalla1", 1, false));
        codeElementToBeSaved.getWithinCodeElements().add(new RelationCodeElement("uusisamankoodistonsisalla2", 1, false));
        assertResponse(resource.save(codeElementToBeSaved), 200);

        ExtendedKoodiDto codeElement = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio + 1).getEntity();
        assertEquals(nimi, codeElement.getMetadata().get(0).getNimi());
        assertEquals(2, codeElement.getIncludesCodeElements().size());
        for (RelationCodeElement relation : codeElement.getIncludesCodeElements()) {
            if (relation.codeElementUri.equals("uusisamankoodistonsisalla1")) {
                assertFalse(relation.passive);
            } else if (relation.codeElementUri.equals("samankoodistonsisalla1")) {
                assertTrue(relation.passive);
            } else {
                fail();
            }
        }
        assertEquals(2, codeElement.getWithinCodeElements().size());
        for (RelationCodeElement relation : codeElement.getWithinCodeElements()) {
            if (relation.codeElementUri.equals("uusisamankoodistonsisalla2")) {
                assertFalse(relation.passive);
            } else if (relation.codeElementUri.equals("samankoodistonsisalla2")) {
                assertTrue(relation.passive);
            } else {
                fail();
            }
        }
    }

    @Test
    public void testRemoveRelation() {
        String codeElementUri = "sisaltaakoodisto1koodit";
        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
        resource.removeRelation(codeElementUri, "rinnastuu4kanssa1", "RINNASTEINEN");
        assertEquals(2, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        codeElementUri = "sisaltaakoodisto1koodit";
        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
        resource.removeRelation(codeElementUri, "sisaltyysuhde4kanssa1", "SISALTYY");
        assertEquals(2, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void testRemoveRelationInvalid() {
        String codeElementUri = "sisaltaakoodisto1koodit";
        String codeElementUriToRemove = "rinnastuu4kanssa1";
        String relationType = "RINNASTEINEN";
        resource.removeRelation("", codeElementUriToRemove, relationType);
        resource.removeRelation(null, codeElementUriToRemove, relationType);
        resource.removeRelation(codeElementUri, "", relationType);
        resource.removeRelation(codeElementUri, null, relationType);
        resource.removeRelation(codeElementUri, codeElementUriToRemove, "");
        resource.removeRelation(codeElementUri, codeElementUriToRemove, null);
        resource.removeRelation(codeElementUri, codeElementUriToRemove, "doenostexist");

        assertResponse(resource.removeRelation("doenotexist", codeElementUriToRemove, relationType), 500, "error.codeelement.not.found");
        assertResponse(resource.removeRelation(codeElementUri, "doesnotexist", relationType), 500, "error.codeelement.relation.list.empty");

        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        codeElementUri = "sisaltaakoodisto1koodit";
        codeElementUriToRemove = "sisaltyysuhde4kanssa1";
        relationType = "SISALTYY";
        resource.removeRelation("", codeElementUriToRemove, relationType);
        resource.removeRelation(null, codeElementUriToRemove, relationType);
        resource.removeRelation(codeElementUri, "", relationType);
        resource.removeRelation(codeElementUri, null, relationType);
        resource.removeRelation(codeElementUri, codeElementUriToRemove, "");
        resource.removeRelation(codeElementUri, codeElementUriToRemove, null);
        resource.removeRelation(codeElementUri, codeElementUriToRemove, "doenostexist");

        assertResponse(resource.removeRelation("doenotexist", codeElementUriToRemove, relationType), 500, "error.codeelement.not.found");
        assertResponse(resource.removeRelation(codeElementUri, "doesnotexist", relationType), 500, "error.codeelement.relation.list.empty");

        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void testDelete() {
        String codeElementUri = "tuhottavatestikoodi";
        int codeElementVersion = 1;
        assertNotNull(resource.getCodeElementByUriAndVersion(codeElementUri, codeElementVersion));
        assertResponse(resource.delete(codeElementUri, codeElementVersion), 202);
        assertResponse(resource.getCodeElementByUriAndVersion(codeElementUri, codeElementVersion), 500, "error.codeelement.not.found");
    }

    @Test
    public void testDeleteInvalid() {
        assertResponse(resource.delete("tuhottavatestikoodi", 0), 400);
        assertResponse(resource.delete("tuhottavatestikoodi", -1), 400);
        assertResponse(resource.delete("", 1), 400);
        assertResponse(resource.delete(null, 1), 400);
        assertResponse(resource.delete("thisisnotexistinguri", 1), 500, "error.codeelement.not.found");
        assertResponse(resource.delete("sisaltaakoodisto1koodit", 1), 500, "error.codeelement.not.passive");

        assertNotNull(resource.getCodeElementByUriAndVersion("tuhottavatestikoodi", 1));
    }

    @Test
    public void testUpdate() {
        KoodiDto original = (KoodiDto) resource.getCodeElementByCodeElementUri("updatetestkoodisto", 1, "paivitettavatestikoodi").getEntity();
        assertNotNull(original);
        assertEquals(1, original.getMetadata().size());

        List<KoodiMetadata> koodiMetadata = new ArrayList<KoodiMetadata>();
        KoodiMetadata o1 = original.getMetadata().get(0);
        o1.setNimi("Modified Name");
        koodiMetadata.add(o1);
        original.setMetadata(koodiMetadata);

        assertResponse(resource.update(original), 201);

        KoodiDto updated = (KoodiDto) resource.getCodeElementByCodeElementUri("updatetestkoodisto", 2, "paivitettavatestikoodi").getEntity();
        assertNotNull(updated);
        assertEquals(1, updated.getMetadata().size());
        assertEquals("Modified Name", updated.getMetadata().get(0).getNimi());

    }

    @Test
    public void testUpdateInvalid() {
        assertResponse(resource.update(null), 400);
        // TODO Make better test
    }

    @Test
    public void returnsChangesForCodeElement() {
        KoodiChangesDto dto = (KoodiChangesDto) resource.getChangesToCodeElement("montaversiota", 1, false).getEntity();
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertEquals("Monta versiota 3", dto.muuttuneetTiedot.get(0).nimi);
        assertNull(dto.voimassaAlkuPvm);
        assertEquals(3, dto.viimeisinVersio.intValue());
    }

    @Test
    public void returnsNoChangesForCodeElement() {
        KoodiChangesDto dto = (KoodiChangesDto) resource.getChangesToCodeElement("montaversiota", 3, false).getEntity();
        assertEquals(MuutosTila.EI_MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.muuttuneetTiedot.isEmpty());
        assertEquals(3, dto.viimeisinVersio.intValue());
    }

    @Test
    public void comparesAgainstLatestAcceptedCodeElementVersion() {
        KoodiChangesDto dto = (KoodiChangesDto) resource.getChangesToCodeElement("viimeinenonluonnos", 1, true).getEntity();
        assertEquals(2, dto.viimeisinVersio.intValue());
        assertNull(dto.tila);
    }

    @Test
    public void comparesAgainstLatestCodeElementVersion() {
        KoodiChangesDto dto = (KoodiChangesDto) resource.getChangesToCodeElement("viimeinenonluonnos", 1, false).getEntity();
        assertEquals(3, dto.viimeisinVersio.intValue());
        assertEquals(Tila.LUONNOS, dto.tila);
    }

    @Test
    public void comparesAgainstLatestAcceptedCodeElementVersionUsingDate() {
        KoodiChangesDto dto = (KoodiChangesDto) resource.getChangesToCodeElementWithDate("viimeinenonluonnos", 12, 1, 2000, 0, 0, 0, true).getEntity();
        assertEquals(2, dto.viimeisinVersio.intValue());
        assertNull(dto.tila);
    }

    @Test
    public void comparesAgainstLatestCodeElementVersionUsingDate() {
        KoodiChangesDto dto = (KoodiChangesDto) resource.getChangesToCodeElementWithDate("viimeinenonluonnos", 12, 12, 2000, 0, 0, 0, false).getEntity();
        assertEquals(3, dto.viimeisinVersio.intValue());
        assertEquals(Tila.LUONNOS, dto.tila);
    }

    @Test
    public void returnsChangesForCodeElementUsingDateFromPast() {
        KoodiChangesDto dto = (KoodiChangesDto) resource.getChangesToCodeElementWithDate("montaversiota", 12, 1, 2000, 0, 0, 0, false).getEntity();
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertEquals("Monta versiota 3", dto.muuttuneetTiedot.get(0).nimi);
        assertNull(dto.voimassaAlkuPvm);
        assertEquals(3, dto.viimeisinVersio.intValue());
    }

    @Test
    public void returnsNoChangesForCodeElementUsingDateFromFuture() {
        KoodiChangesDto dto = (KoodiChangesDto) resource.getChangesToCodeElementWithDate("montaversiota", 12, 1, 4000, 0, 0, 0, false).getEntity();
        assertEquals(MuutosTila.EI_MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.muuttuneetTiedot.isEmpty());
        assertEquals(3, dto.viimeisinVersio.intValue());
    }

    @Test
    public void returnsCodeElementHasBeenRemovedWhenItIsNotFoundInLatestCodesVersion() {
        assertEquals(MuutosTila.POISTETTU, ((KoodiChangesDto) resource.getChangesToCodeElement("poistettu", 1, false).getEntity()).muutosTila);
    }

    @Test
    public void returnsBadRequestWhenVersionNumberIsZeroForQueryingCodeElementChanges() {
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resource.getChangesToCodeElement("poistettu", 0, false).getStatus());
    }

    @Test
    public void returnsBadRequestForBadDateParametersWhenQueryingCodeElementChanges() {
        int badRequest = Response.Status.BAD_REQUEST.getStatusCode();
        assertEquals(badRequest, resource.getChangesToCodeElementWithDate("montaversiota", 12, 1, 4000, 0, 0, -1, false).getStatus());
        assertEquals(badRequest, resource.getChangesToCodeElementWithDate("montaversiota", 12, 1, 4000, 0, -1, 0, false).getStatus());
        assertEquals(badRequest, resource.getChangesToCodeElementWithDate("montaversiota", 12, 1, 4000, 25, 0, 0, false).getStatus());
        assertEquals(badRequest, resource.getChangesToCodeElementWithDate("montaversiota", 32, 1, 4000, 0, 0, 0, false).getStatus());
        assertEquals(badRequest, resource.getChangesToCodeElementWithDate("montaversiota", 12, 15, 4000, 0, 0, 0, true).getStatus());
    }

    @Test
    public void savesCodeElementWithNewName() {
        String koodiUri = "sisaltyysuhde4kanssa1";
        String nimi = "uusinimi";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();
        assertEquals(Tila.HYVAKSYTTY, codeElementToBeSaved.getTila());
        assertFalse(nimi.equals(codeElementToBeSaved.getMetadata().get(0).getNimi()));

        codeElementToBeSaved.getMetadata().get(0).setNimi(nimi);
        assertResponse(resource.save(codeElementToBeSaved), 200);

        ExtendedKoodiDto codes = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio + 1).getEntity();
        assertEquals(Tila.LUONNOS, codes.getTila());
        assertEquals(nimi, codes.getMetadata().get(0).getNimi());
    }

    @Test
    public void savesCodeElementWithNewNameAndRelations() {
        String koodiUri = "savekoodineljallasuhteella";
        String nimi = "uusinimi";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();
        assertEquals(1, codeElementToBeSaved.getIncludesCodeElements().size());
        assertEquals(2, codeElementToBeSaved.getLevelsWithCodeElements().size());
        assertEquals(1, codeElementToBeSaved.getWithinCodeElements().size());

        codeElementToBeSaved.getMetadata().get(0).setNimi(nimi);
        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde1", 1, false));
        codeElementToBeSaved.getLevelsWithCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde2", 1, false));
        codeElementToBeSaved.getWithinCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde3", 1, false));
        assertResponse(resource.save(codeElementToBeSaved), 200);

        ExtendedKoodiDto codeElement = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio + 1).getEntity();
        assertEquals(2, codeElement.getIncludesCodeElements().size());
        assertEquals(3, codeElement.getLevelsWithCodeElements().size());
        assertEquals(3, codeElement.getWithinCodeElements().size());
    }

    @Test
    public void savesCodeElementRelationIncludes() {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();

        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde1", 1, false));
        assertResponse(resource.save(codeElementToBeSaved), 200);

        ExtendedKoodiDto codeElement = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio + 1).getEntity();
        assertEquals(2, codeElement.getIncludesCodeElements().size());
        assertEquals(1, codeElement.getWithinCodeElements().size());
        assertEquals(2, codeElement.getLevelsWithCodeElements().size());
    }

    @Test
    public void savesCodeElementRelationWithin() {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();

        codeElementToBeSaved.getWithinCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde3", 1, false));
        assertResponse(resource.save(codeElementToBeSaved), 200);

        ExtendedKoodiDto codeElement = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();
        assertEquals(1, codeElement.getIncludesCodeElements().size());
        assertEquals(3, codeElement.getWithinCodeElements().size()); // Parentcodes versions, so the old relation is duplicated.
        assertEquals(2, codeElement.getLevelsWithCodeElements().size());
    }

    @Test
    public void savesCodeElementRelationLevelsWith() {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();

        codeElementToBeSaved.getLevelsWithCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde2", 1, false));
        assertResponse(resource.save(codeElementToBeSaved), 200);

        ExtendedKoodiDto codeElement = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();
        assertEquals(1, codeElement.getIncludesCodeElements().size());
        assertEquals(1, codeElement.getWithinCodeElements().size());
        assertEquals(3, codeElement.getLevelsWithCodeElements().size());
    }

    @Test
    public void savesCodeElementWithAllRelationChanges() {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();
        assertEquals(1, codeElementToBeSaved.getIncludesCodeElements().size());
        assertEquals(2, codeElementToBeSaved.getLevelsWithCodeElements().size());
        assertEquals(1, codeElementToBeSaved.getWithinCodeElements().size());

        codeElementToBeSaved.getIncludesCodeElements().clear();
        codeElementToBeSaved.getWithinCodeElements().clear();
        codeElementToBeSaved.getLevelsWithCodeElements().clear();
        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde1", 1, false));
        codeElementToBeSaved.getLevelsWithCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde2", 1, false));
        codeElementToBeSaved.getWithinCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde3", 1, false));
        assertResponse(resource.save(codeElementToBeSaved), 200);

        ExtendedKoodiDto oldCodeElement = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();
        ExtendedKoodiDto newCodeElement = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio + 1).getEntity();
        
        assertEquals(1, newCodeElement.getIncludesCodeElements().size());
        assertEquals(1, newCodeElement.getLevelsWithCodeElements().size());
        assertEquals(2, newCodeElement.getWithinCodeElements().size());
        assertEquals("uusisavekoodinsuhde1", newCodeElement.getIncludesCodeElements().get(0).codeElementUri);
        assertEquals("uusisavekoodinsuhde2", newCodeElement.getLevelsWithCodeElements().get(0).codeElementUri);

        for (RelationCodeElement relation : newCodeElement.getWithinCodeElements()) {
            if(relation.codeElementUri.equals("uusisavekoodinsuhde3")){
                assertFalse(relation.passive);
            } else if(relation.codeElementUri.equals("savekoodinsuhde4")){
                assertTrue(relation.passive);
            } else {
                fail();
            }
        }

        assertEquals(1, oldCodeElement.getIncludesCodeElements().size());
        assertEquals(2, oldCodeElement.getLevelsWithCodeElements().size());
        assertEquals(1, oldCodeElement.getWithinCodeElements().size());

    }

    @Test
    public void savesCodeElementRelationChangesIfRelationHasExistedBefore() {
        String koodiUri = "uusirelaatiovanhantilalle1";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio).getEntity();
        assertEquals(1, codeElementToBeSaved.getIncludesCodeElements().size());
        assertTrue(codeElementToBeSaved.getIncludesCodeElements().get(0).passive);
        assertEquals(0, codeElementToBeSaved.getLevelsWithCodeElements().size());
        assertEquals(0, codeElementToBeSaved.getWithinCodeElements().size());

        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusirelaatiovanhantilalle2", 1, false));
        assertResponse(resource.save(codeElementToBeSaved), 200);

        ExtendedKoodiDto codeElement = (ExtendedKoodiDto) resource.getCodeElementByUriAndVersion(koodiUri, versio + 1).getEntity();
        assertEquals(1, codeElement.getIncludesCodeElements().size());
        assertEquals(0, codeElement.getLevelsWithCodeElements().size());
        assertEquals(0, codeElement.getWithinCodeElements().size());

        assertEquals("uusirelaatiovanhantilalle2", codeElement.getIncludesCodeElements().get(0).codeElementUri);
        assertFalse(codeElement.getIncludesCodeElements().get(0).passive);
    }

    // UTILITIES
    // /////////

    private void assertResponse(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatus());
    }

    private void assertResponse(Response response, int expectedStatus, Object expectedEntity) {
        assertResponse(response, expectedStatus);
        assertEquals(expectedEntity, response.getEntity());
    }

    private Response addRelations(String codeElementUri, String st, KoodiRelaatioListaDto kr) {
        kr.setCodeElementUri(codeElementUri);
        kr.setRelationType(st);
        return resource.addRelations(kr);
    }

    private Response removeRelations(String codeElementUri, String st, KoodiRelaatioListaDto kr) {
        kr.setCodeElementUri(codeElementUri);
        kr.setRelationType(st);
        return resource.removeRelations(kr);
    }

    private KoodiDto createValidCodeElementDto(String value, String name, int amountOfMetadatas) {

        KoodiDto dto = new KoodiDto();

        dto.setVoimassaAlkuPvm(new Date());
        dto.setVoimassaLoppuPvm(null);
        dto.setKoodiArvo(value);
        List<KoodiMetadata> mds = new ArrayList<KoodiMetadata>();
        for (int i = 0; i < amountOfMetadatas; i++) {
            KoodiMetadata md = new KoodiMetadata();
            md.setKieli(Kieli.values()[i % Kieli.values().length]);
            md.setNimi(name);
            md.setLyhytNimi(name);
            md.setKuvaus("Kuvaus");
            mds.add(md);
        }
        dto.setMetadata(mds);
        return dto;
    }

    private void assertDatesEquals(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        assertEquals(c1.get(Calendar.DATE), c2.get(Calendar.DATE));
        assertEquals(c1.get(Calendar.MONTH), c2.get(Calendar.MONTH));
        assertEquals(c1.get(Calendar.YEAR), c2.get(Calendar.YEAR));
    }
}
