package fi.vm.sade.koodisto.service.koodisto.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodiRelaatioListaDto;
import fi.vm.sade.koodisto.dto.SimpleKoodiDto;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
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
        assertResponse(this.addRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), kr), 400);
        assertResponse(this.removeRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), kr), 400);

        kr.setRelations(new ArrayList<String>());
        assertResponse(this.addRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), kr), 400);
        assertResponse(this.removeRelations("codeelementuri", SuhteenTyyppi.SISALTYY.toString(), kr), 400);
    }

    @Test
    public void returns500IfErrorOccurs() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("koodi"));
        assertResponse(this.addRelations("codeelementuri", "SISALTYY", kr), 500);
        assertResponse(this.removeRelations("codeelementuri", "SISALTYY", kr), 500);

        kr.setRelations(Arrays.asList("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3"));
        assertResponse(this.addRelations("codeelementuri", "asd", kr), 500);
        assertResponse(this.removeRelations("codeelementuri", "asd", kr), 500);
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
        kr.setRelations(Arrays.asList("lisaasisaltyy18kanssa1", "lisaasisaltyy18kanssa2"));
        kr.setChild(true);
        assertResponse(this.addRelations("lisaasisaltyy18", "SISALTYY", kr), 200);
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
        List<SimpleKoodiDto> response = resource.getAllCodeElementVersionsByCodeElementUri("sisaltyysuhde4kanssa1");
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("sisaltyysuhde4kanssa1", response.get(0).getKoodiUri());

        List<SimpleKoodiDto> response2 = resource.getAllCodeElementVersionsByCodeElementUri("montaversiota");
        assertNotNull(response2);
        assertEquals(3, response2.size());
    }

    @Test
    public void testGetAllCodeElementVersionsByCodeElementUriInvalid() {
        assertEquals(0, resource.getAllCodeElementVersionsByCodeElementUri("invaliduri").size());
        assertEquals(0, resource.getAllCodeElementVersionsByCodeElementUri("").size());
        assertEquals(0, resource.getAllCodeElementVersionsByCodeElementUri(null).size());
    }

    @Test
    public void testGetCodeElementByUriAndVersion() {
        ExtendedKoodiDto response = resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 1);
        assertNotNull(response);
        assertEquals("ss4k1", response.getKoodiArvo());
        assertEquals("sisaltyysuhde4kanssa1", response.getKoodiUri());
        assertEquals(Tila.HYVAKSYTTY, response.getTila());
        assertEquals(1, response.getVersio());
        assertEquals("sisaltyysuhde2kanssa", response.getKoodisto().getKoodistoUri());
    }

    @Test
    public void testGetCodeElementByUriAndVersionInvalid() {
        assertNull(resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 0));
        assertNull(resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", -1));
        assertNull(resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 9999));
        assertNull(resource.getCodeElementByUriAndVersion("invaliduriisnotfound", 1));
        assertNull(resource.getCodeElementByUriAndVersion("", 1));
        assertNull(resource.getCodeElementByUriAndVersion(null, 1));
    }

    @Test
    public void testGetCodeElementByCodeElementUri() {
        KoodiDto response = resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, "sisaltyysuhde4kanssa1");
        assertNotNull(response);
        assertEquals("ss4k1", response.getKoodiArvo());
        assertEquals("sisaltyysuhde4kanssa1", response.getKoodiUri());
        assertEquals(Tila.HYVAKSYTTY, response.getTila());
        assertEquals(1, response.getVersio());
        assertEquals("sisaltyysuhde2kanssa", response.getKoodisto().getKoodistoUri());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetCodeElementByCodeElementUriInvalid() {
        assertNull(resource.getCodeElementByCodeElementUri("invalidcodeelementuri", 1, "sisaltyysuhde4kanssa1"));
        assertNull(resource.getCodeElementByCodeElementUri("", 1, "sisaltyysuhde4kanssa1"));
        assertNull(resource.getCodeElementByCodeElementUri(null, 1, "sisaltyysuhde4kanssa1"));

        assertNull(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 0, "sisaltyysuhde4kanssa1"));
        assertNull(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", -1, "sisaltyysuhde4kanssa1"));
        assertNull(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 9999, "sisaltyysuhde4kanssa1"));

        assertNull(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, "invalidcodesuri"));
        assertNull(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, ""));
        assertNull(resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, null));
    }

    @Test
    public void testGetAllCodeElementsByCodesUriAndVersion() {
        List<SimpleKoodiDto> response = resource.getAllCodeElementsByCodesUriAndVersion("lisaarinnasteinen2", 1);
        assertNotNull(response);
        assertEquals(3, response.size());

        // FIXME Tämä palauttaa ilmeisesti palauttaa jotain mutta miksi? Mikä olisi toivottu toiminnallisuus?
        List<SimpleKoodiDto> response2 = resource.getAllCodeElementsByCodesUriAndVersion("lisaasisaltyy3", 0);
        assertNotNull(response2);
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetAllCodeElementsByCodesUriAndVersionInvalid() {
        assertEquals(0, resource.getAllCodeElementsByCodesUriAndVersion("lisaarinnasteinen2", -1).size());
        assertEquals(0, resource.getAllCodeElementsByCodesUriAndVersion("", 1).size());
        assertEquals(0, resource.getAllCodeElementsByCodesUriAndVersion("uridoesnotexist", 1).size());
        assertEquals(0, resource.getAllCodeElementsByCodesUriAndVersion(null, 1).size());
    }

    @Test
    public void testGetLatestCodeElementVersionsByCodeElementUri() {
        KoodiDto response = resource.getLatestCodeElementVersionsByCodeElementUri("montaversiota");
        assertEquals(3, response.getVersio());
        assertEquals("mv3", response.getKoodiArvo());
        assertEquals("montaversiota", response.getKoodiUri());
        assertEquals(Tila.HYVAKSYTTY, response.getTila());
        assertEquals("lisaasisaltyy3", response.getKoodisto().getKoodistoUri());
        assertEquals("Monta versiota 3", response.getMetadata().get(0).getNimi());
    }

    @Test
    public void testGetLatestCodeElementVersionsByCodeElementUriInvalid() {
        assertNull(resource.getLatestCodeElementVersionsByCodeElementUri("eioleolemassa"));
        assertNull(resource.getLatestCodeElementVersionsByCodeElementUri(""));
        assertNull(resource.getLatestCodeElementVersionsByCodeElementUri(null));
    }

    @Test
    public void testInsert() {
        KoodiDto validDto = createValidCodeElementDto("value", "Nimi", 3);

        assertResponse(resource.insert("inserttestkoodisto", validDto), 201);

        KoodiDto newdto = resource.getCodeElementByCodeElementUri("inserttestkoodisto", 1, "inserttestkoodisto_value");
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

        KoodiDto newDto2 = resource.getCodeElementByCodeElementUri("inserttestkoodisto", 1, "inserttestkoodisto_value2");
        assertEquals("value2", newDto2.getKoodiArvo());
        assertDatesEquals(voimassaLoppuPvm, newDto2.getVoimassaLoppuPvm());
    }

    @Test
    public void testInsertInvalid() {
        KoodiDto validDto = createValidCodeElementDto("newdtouri", "Name", 3);
        assertResponse(resource.insert(null, validDto), 400);
        assertResponse(resource.insert("", validDto), 400);
        assertResponse(resource.insert("totallyInvalidKoodistoUri", validDto), 500);

        assertResponse(resource.insert("lisaasisaltyy3", null), 400);
        assertResponse(resource.insert("lisaasisaltyy3", new KoodiDto()), 500);

        KoodiDto invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setKoodiArvo("");
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setKoodiArvo(null);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setMetadata(null);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        ArrayList<KoodiMetadata> metadatas = new ArrayList<KoodiMetadata>();
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setVoimassaLoppuPvm(new Date(0L));
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        KoodiMetadata invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi(""); // Invalid
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi(null); // Invalid
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi(""); // Invalid
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi(null); // Invalid
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus(""); // Invalid
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

        invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus(null); // Invalid
        metadatas.add(invalidMd);
        invalidDto.setMetadata(metadatas);
        assertResponse(resource.insert("lisaasisaltyy3", invalidDto), 500);

    }

    @Test
    public void testAddRelation() {
        String codeElementUri = "lisaarinnasteinen14";
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
        resource.addRelation(codeElementUri, "lisaarinnasteinen14kanssa1", "RINNASTEINEN");
        assertEquals(1, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        String codeElementUri2 = "lisaasisaltyy18";
        assertEquals(0, service.listByRelation(codeElementUri2, 1, false, SuhteenTyyppi.SISALTYY).size());
        resource.addRelation(codeElementUri2, "lisaasisaltyy18kanssa1", "SISALTYY");
        assertEquals(1, service.listByRelation(codeElementUri2, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void testAddRelationInvalid() {
        String codeElementUri = "lisaarinnasteinen14";
        String codeElementUriToAdd = "lisaarinnasteinen14kanssa1";
        String relationType = "RINNASTEINEN";
        resource.addRelation("", codeElementUriToAdd, relationType);
        resource.addRelation(null, codeElementUriToAdd, relationType);
        resource.addRelation(codeElementUri, "", relationType);
        resource.addRelation(codeElementUri, null, relationType);
        resource.addRelation(codeElementUri, codeElementUriToAdd, "");
        resource.addRelation(codeElementUri, codeElementUriToAdd, null);
        resource.addRelation(codeElementUri, codeElementUriToAdd, "doenostexist");

        try {
            resource.addRelation("doenotexist", codeElementUriToAdd, relationType);
            fail("Expected error");
        } catch (KoodiNotFoundException ignore) {
        }
        try {
            resource.addRelation(codeElementUri, "doesnotexist", relationType);
            fail("Expected error");
        } catch (KoodiNotFoundException ignore) {
        }
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        codeElementUri = "lisaasisaltyy18";
        codeElementUriToAdd = "lisaasisaltyy18kanssa1";
        relationType = "SISALTYY";
        resource.addRelation("", codeElementUriToAdd, relationType);
        resource.addRelation(null, codeElementUriToAdd, relationType);
        resource.addRelation(codeElementUri, "", relationType);
        resource.addRelation(codeElementUri, null, relationType);
        resource.addRelation(codeElementUri, codeElementUriToAdd, "");
        resource.addRelation(codeElementUri, codeElementUriToAdd, null);
        resource.addRelation(codeElementUri, codeElementUriToAdd, "doenostexist");

        try {
            resource.addRelation("doenotexist", codeElementUriToAdd, relationType);
            fail("Expected error");
        } catch (KoodiNotFoundException ignore) {
        }
        try {
            resource.addRelation(codeElementUri, "doesnotexist", relationType);
            fail("Expected error");
        } catch (KoodiNotFoundException ignore) {
        }
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
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

        try {
            resource.removeRelation("doenotexist", codeElementUriToRemove, relationType);
            fail("Expected error");
        } catch (KoodiNotFoundException ignore) {
        }
        try {
            resource.removeRelation(codeElementUri, "doesnotexist", relationType);
            fail("Expected error");
        } catch (KoodiNotFoundException ignore) {
        }
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

        try {
            resource.removeRelation("doenotexist", codeElementUriToRemove, relationType);
            fail("Expected error");
        } catch (KoodiNotFoundException ignore) {
        }
        try {
            resource.removeRelation(codeElementUri, "doesnotexist", relationType);
            fail("Expected error");
        } catch (KoodiNotFoundException ignore) {
        }
        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void testDelete() {
        String codeElementUri = "tuhottavatestikoodi";
        int codeElementVersion = 1;
        assertNotNull(resource.getCodeElementByUriAndVersion(codeElementUri, codeElementVersion));
        assertResponse(resource.delete(codeElementUri, codeElementVersion), 202);
        assertNull(resource.getCodeElementByUriAndVersion(codeElementUri, codeElementVersion));
    }

    @Test
    public void testDeleteInvalid() {
        assertResponse(resource.delete("tuhottavatestikoodi", 0), 400);
        assertResponse(resource.delete("tuhottavatestikoodi", -1), 400);
        assertResponse(resource.delete("", 1), 400);
        assertResponse(resource.delete(null, 1), 400);
        assertResponse(resource.delete("thisisnotexistinguri", 1), 500);
        assertResponse(resource.delete("sisaltaakoodisto1koodit", 1), 500);

        assertNotNull(resource.getCodeElementByUriAndVersion("tuhottavatestikoodi", 1));
        assertNotNull(resource.getCodeElementByUriAndVersion("sisaltaakoodisto1koodit", 1));
    }

    @Test
    public void testUpdate() {
        KoodiDto original = resource.getCodeElementByCodeElementUri("updatetestkoodisto", 1, "paivitettavatestikoodi");
        assertNotNull(original);
        assertEquals(1, original.getMetadata().size());

        List<KoodiMetadata> koodiMetadata = new ArrayList<KoodiMetadata>();
        KoodiMetadata o1 = original.getMetadata().get(0);
        o1.setNimi("Modified Name");
        koodiMetadata.add(o1);
        original.setMetadata(koodiMetadata);

        assertResponse(resource.update(original), 201);

        KoodiDto updated = resource.getCodeElementByCodeElementUri("updatetestkoodisto", 2, "paivitettavatestikoodi");
        assertNotNull(updated);
        assertEquals(1, updated.getMetadata().size());
        assertEquals("Modified Name", updated.getMetadata().get(0).getNimi());

    }

    @Test
    public void testUpdateInvalid() {
        assertResponse(resource.update(null), 400);
        // TODO Make better test
    }

    // UTILITIES
    // /////////

    private void assertResponse(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatus());
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
