package fi.vm.sade.koodisto.service.koodisto.rest;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.dto.*;
import fi.vm.sade.koodisto.dto.ExtendedKoodiDto.RelationCodeElement;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodiMetadata;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.test.support.ResponseStatusExceptionMatcher;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.*;
import java.util.stream.Collectors;

import static fi.vm.sade.koodisto.test.support.Assertions.assertException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestPropertySource(locations = "classpath:application.properties")
@DataJpaTest
@DatabaseSetup("classpath:test-data-multiple-relations.xml")
@ActiveProfiles("test")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@WithMockUser("1.2.3.4.5")
public class CodeElementResourceIT {

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

        assertException(() -> resource.addRelation(nullString, stubString, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.addRelation(blankString, stubString, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.addRelation(stubString, nullString, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuritoadd"));
        assertException(() -> resource.addRelation(stubString, blankString, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuritoadd"));
        assertException(() -> resource.addRelation(stubString, stubString, nullString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.relationtype"));
        assertException(() -> resource.addRelation(stubString, stubString, blankString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.relationtype"));

        assertException(() -> resource.addRelations(nullRelationList),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementrelationlist"));

        assertException(() -> resource.delete(nullString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.delete(blankString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        
        assertException(() -> resource.getAllCodeElementsByCodesUriAndVersion(nullString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codesuri"));
        assertException(() -> resource.getAllCodeElementsByCodesUriAndVersion(blankString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codesuri"));

        assertException(() -> resource.getAllCodeElementVersionsByCodeElementUri(nullString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.getAllCodeElementVersionsByCodeElementUri(blankString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));

        assertException(() -> resource.getCodeElementByCodeElementUri(nullString, 0, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codesuri"));
        assertException(() -> resource.getCodeElementByCodeElementUri(blankString, 0, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codesuri"));
        assertException(() -> resource.getCodeElementByCodeElementUri(stubString, 0, nullString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.getCodeElementByCodeElementUri(stubString, 0, blankString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));

        assertException(() -> resource.getCodeElementByUriAndVersion(nullString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));;
        assertException(() -> resource.getCodeElementByUriAndVersion(blankString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));

        assertException(() -> resource.getLatestCodeElementVersionsByCodeElementUri(nullString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.getLatestCodeElementVersionsByCodeElementUri(blankString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));

        assertException(() -> resource.insert(nullString, codeelementDTO),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
        assertException(() -> resource.insert(blankString, codeelementDTO),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
        assertException(() -> resource.insert(stubString, nullDto),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelement"));

        assertException(() -> resource.removeRelation(nullString, stubString, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.removeRelation(blankString, stubString, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.removeRelation(stubString, nullString, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuritoremove"));
        assertException(() -> resource.removeRelation(stubString, blankString, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuritoremove"));
        assertException(() -> resource.removeRelation(stubString, stubString, nullString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.relationtype"));
        assertException(() -> resource.removeRelation(stubString, stubString, blankString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.relationtype"));

        assertException(() -> resource.removeRelations(nullRelationList),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementrelationlist"));

        assertException(() -> resource.update(nullDto),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelement"));

    }

    @Test
    public void returns500IfErrorOccurs() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Collections.singletonList("koodi"));
        assertException(() -> this.addRelations("codeelementuri", "SISALTYY", kr),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> this.removeRelations("codeelementuri", "SISALTYY", kr),
            new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));

        kr.setRelations(Arrays.asList("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3"));
        assertException(() -> this.addRelations("codeelementuri", "asd", kr),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codes.generic"));
        assertException(() -> this.removeRelations("codeelementuri", "asd", kr),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codes.generic"));
    }

    @Test
    public void removesMultipleCodeElementRelationsWithTypeRINNASTEINEN() {
        String codeElementUri = "sisaltaakoodisto1koodit";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("rinnastuu4kanssa1", "rinnastuu4kanssa2", "rinnastuu4kanssa3"));
        this.removeRelations(codeElementUri, "RINNASTEINEN", kr);
        assertTrue(service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).isEmpty());
    }

    @Test
    public void addsMultipleCodeElementRelationsWithTypeRINNASTEINEN() {
        String codeElementUri = "lisaarinnasteinen14";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("lisaarinnasteinen14kanssa1", "lisaarinnasteinen14kanssa2", "lisaarinnasteinen14kanssa3"));
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
        this.addRelations(codeElementUri, "RINNASTEINEN", kr);
        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());
    }

    @Test
    public void removesMultipleCodeElementRelationsWithTypeSISALTYY() {
        String codeElementUri = "sisaltaakoodisto1koodit";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde4kanssa1", "sisaltyysuhde4kanssa2"));
        this.removeRelations(codeElementUri, "SISALTYY", kr);
        assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void addsMultipleCodeElementRelationsWithTypeSISALTYY() {
        String codeElementUri = "lisaasisaltyy18";
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("lisaasisaltyy18kanssa1", "lisaasisaltyy18kanssa2"));
        kr.setChild(false);
        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
        this.addRelations(codeElementUri, "SISALTYY", kr);
        assertEquals(2, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void removesMultipleCodeElementRelationsWithTypeSISALTYYAndCodeElementBeingLower() {
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde4kanssa1", "sisaltyysuhde4kanssa2", "sisaltyysuhde4kanssa3"));
        kr.setChild(true);
        this.removeRelations("sisaltyykoodisto1koodienkanssa", "SISALTYY", kr);
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
        this.addRelations("lisaasisaltyy18", "SISALTYY", kr);
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
        this.removeRelations(codeElementUri, "SISALTYY", kr);
        assertEquals(1, service.listByRelation(codeElementUri, 2, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void addsMultipleCodeElementRelationsThatBelongToDifferentCodes() {
        // TODO: nimeäminen / toinen testi, eihän tämä tee mitään nimen implikoimaa?
        KoodiRelaatioListaDto kr = new KoodiRelaatioListaDto();
        kr.setRelations(Arrays.asList("sisaltyysuhde9kanssa1", "sisaltyysuhde9kanssa2", "sisaltyysuhde9kanssa3"));
        kr.setChild(false);
        String codeElementUri = "sisaltaakoodisto6ja7ja8koodit";
        this.removeRelations(codeElementUri, "SISALTYY", kr);
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
        List<SimpleKoodiDto> results = resource.getAllCodeElementVersionsByCodeElementUri("invaliduri");
        assertTrue(results.isEmpty());
        assertException(() -> resource.getAllCodeElementVersionsByCodeElementUri(""),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.getAllCodeElementVersionsByCodeElementUri(null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
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
        assertException(() -> resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", -1),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.getCodeElementByUriAndVersion("sisaltyysuhde4kanssa1", 9999),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR,"error.codeelement.not.found"));
        assertException(() -> resource.getCodeElementByUriAndVersion("invaliduriisnotfound", 1),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR,"error.codeelement.not.found"));
        assertException(() -> resource.getCodeElementByUriAndVersion("", 1),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.getCodeElementByUriAndVersion(null, 1),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
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

    @Test
    public void testGetCodeElementByCodeElementUriInvalid() {
        assertException(() -> resource.getCodeElementByCodeElementUri("invalidcodeelementuri", 1, "sisaltyysuhde4kanssa1"),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codes.not.found"));
        assertException(() -> resource.getCodeElementByCodeElementUri("", 1, "sisaltyysuhde4kanssa1"),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.getCodeElementByCodeElementUri(null, 1, "sisaltyysuhde4kanssa1"),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));

        assertException(() -> resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 0, "sisaltyysuhde4kanssa1"),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", -1, "sisaltyysuhde4kanssa1"),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesversion"));
        assertException(() -> resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 9999, "sisaltyysuhde4kanssa1"),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR));

        assertException(() -> resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, "invalidcodesuri"),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, ""),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.getCodeElementByCodeElementUri("sisaltyysuhde2kanssa", 1, null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
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

    @Test
    public void testGetAllCodeElementsByCodesUriAndVersionInvalid() {
        assertException(() -> resource.getAllCodeElementsByCodesUriAndVersion("lisaarinnasteinen2", -1),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.getAllCodeElementsByCodesUriAndVersion("", 1),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.getAllCodeElementsByCodesUriAndVersion("uridoesnotexist", 1),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codes.not.found"));
        assertException(() -> resource.getAllCodeElementsByCodesUriAndVersion(null, 1),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
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
        assertException(() -> resource.getLatestCodeElementVersionsByCodeElementUri("eioleolemassa"),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> resource.getLatestCodeElementVersionsByCodeElementUri(""),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuri"));
        assertException(() -> resource.getLatestCodeElementVersionsByCodeElementUri(null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuri"));
    }

    @Test
    public void testInsert() {
        KoodiDto validDto = createValidCodeElementDto("value", "Nimi", 3);

        resource.insert("inserttestkoodisto", validDto);

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

        resource.insert("inserttestkoodisto", validDto2);

        KoodiDto newDto2 = resource.getCodeElementByCodeElementUri("inserttestkoodisto", 1, "inserttestkoodisto_value2");
        assertEquals("value2", newDto2.getKoodiArvo());
        assertDatesEquals(voimassaLoppuPvm, newDto2.getVoimassaLoppuPvm());
    }

    @Test
    public void testInsertInvalid() { // TODO: parametrisoitu testi!
        KoodiDto validDto = createValidCodeElementDto("newdtouri", "Name", 3);
        assertException(() -> resource.insert(null, validDto),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
        assertException(() -> resource.insert("", validDto),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
        assertException(() -> resource.insert("totallyInvalidKoodistoUri", validDto),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codes.not.found"));

        assertException(() -> resource.insert("lisaasisaltyy3", null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelement"));
        assertException(() -> resource.insert("lisaasisaltyy3", new KoodiDto()),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.value"));

        final KoodiDto invalidDto = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setKoodiArvo("");
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.value"));

        final KoodiDto invalidDto2 = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto2.setKoodiArvo(null);
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto2),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.value"));

        final KoodiDto invalidDto3 = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto3.setMetadata(null);
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto3),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.metadata"));

        final KoodiDto invalidDto4 = createValidCodeElementDto("newdtouri", "Name", 3);
        ArrayList<KoodiMetadata> metadatas = new ArrayList<>();
        invalidDto4.setMetadata(metadatas);
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto4),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.metadata"));

        final KoodiDto invalidDto5 = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidDto.setVoimassaLoppuPvm(new Date(0L));
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto5),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.enddate"));

        final KoodiDto invalidDto6 = createValidCodeElementDto("newdtouri", "Name", 3);
        KoodiMetadata invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi(""); // Invalid
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto6.setMetadata(metadatas);
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto6),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));

        final KoodiDto invalidDto7 = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi(null); // Invalid
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto7.setMetadata(metadatas);
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto7),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));

        final KoodiDto invalidDto8 = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi(""); // Invalid
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto8.setMetadata(metadatas);
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto8),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));

        final KoodiDto invalidDto9 = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi(null); // Invalid
        invalidMd.setKuvaus("Kuvaus");
        metadatas.add(invalidMd);
        invalidDto9.setMetadata(metadatas);
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto9),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));

        final KoodiDto invalidDto10 = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus(""); // Invalid
        metadatas.add(invalidMd);
        invalidDto10.setMetadata(metadatas);
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto10),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));

        final KoodiDto invalidDto11 = createValidCodeElementDto("newdtouri", "Name", 3);
        invalidMd = new KoodiMetadata();
        invalidMd.setKieli(Kieli.FI);
        invalidMd.setNimi("Name");
        invalidMd.setLyhytNimi("Name");
        invalidMd.setKuvaus(null); // Invalid
        metadatas.add(invalidMd);
        invalidDto11.setMetadata(metadatas);
        assertException(() -> resource.insert("lisaasisaltyy3", invalidDto11),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));

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
        final String codeElementUri = "lisaarinnasteinen14";
        final String codeElementUriToAdd = "lisaarinnasteinen14kanssa1";
        final String codeElementUriToAddWithoutCodesRelation = "sisaltyysuhde4kanssa1";
        final String relationType = "RINNASTEINEN";
        assertException(() -> resource.addRelation("", codeElementUriToAdd, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuri"));
        assertException(() -> resource.addRelation(null, codeElementUriToAdd, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuri"));
        assertException(() -> resource.addRelation(codeElementUri, "", relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuritoadd"));
        assertException(() -> resource.addRelation(codeElementUri, null, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuritoadd"));
        assertException(() -> resource.addRelation(codeElementUri, codeElementUriToAdd, ""),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.relationtype"));
        assertException(() -> resource.addRelation(codeElementUri, codeElementUriToAdd, null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.relationtype"));

        assertException(() -> resource.addRelation(codeElementUri, codeElementUriToAdd, "doenostexist"),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codes.generic"));
        assertException(() -> resource.addRelation(codeElementUri, codeElementUri, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR,"error.codeelement.relation.to.self"));
        assertException(() -> resource.addRelation("doenotexist", codeElementUriToAdd, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> resource.addRelation(codeElementUri, "doesnotexist", relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> resource.addRelation(codeElementUri, codeElementUriToAddWithoutCodesRelation, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR,"error.codeelement.codes.have.no.relation"));

        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        final String codeElementUri2 = "lisaasisaltyy18";
        final String codeElementUriToAdd2 = "lisaasisaltyy18kanssa1";
        final String codeElementUriToAddWithoutCodesRelation2 = "sisaltyysuhde4kanssa1";
        final String relationType2 = "SISALTYY";
        assertException(() -> resource.addRelation("", codeElementUriToAdd2, relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.addRelation(null, codeElementUriToAdd2, relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuri"));
        assertException(() -> resource.addRelation(codeElementUri2, "", relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuritoadd"));
        assertException(() -> resource.addRelation(codeElementUri2, null, relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.codeelementuritoadd"));
        assertException(() -> resource.addRelation(codeElementUri2, codeElementUriToAdd2, ""),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.relationtype"));
        assertException(() -> resource.addRelation(codeElementUri2, codeElementUriToAdd2, null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST,"error.validation.relationtype"));

        assertException(() -> resource.addRelation(codeElementUri2, codeElementUriToAdd2, "doenostexist"),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codes.generic"));
        assertException(() -> resource.addRelation(codeElementUri2, codeElementUri2, relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.relation.to.self"));
        assertException(() -> resource.addRelation("doenotexist", codeElementUriToAdd2, relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> resource.addRelation(codeElementUri2, "doesnotexist", relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> resource.addRelation(codeElementUri2, codeElementUriToAddWithoutCodesRelation2, relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.codes.have.no.relation"));

        assertEquals(0, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void testAddRelationToSameCodes() {
        String codeElementUri = "lisaasisaltyy18kanssa1";
        String codeElementUriToAdd = "lisaasisaltyy18kanssa2";
        String relationType = "SISALTYY";

        resource.addRelation(codeElementUri, codeElementUriToAdd, relationType);

    }

    @Test
    public void savesCodeElementWithNameAndRelationsToSameCodesChanges() {
        String koodiUri = "savekoodisuhteillaomaankoodistoon";
        String nimi = "uusinimi";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = clone(resource.getCodeElementByUriAndVersion(koodiUri, versio));
        assertEquals(1, codeElementToBeSaved.getIncludesCodeElements().size());
        assertEquals("samankoodistonsisalla1", codeElementToBeSaved.getIncludesCodeElements().get(0).codeElementUri);
        assertEquals(1, codeElementToBeSaved.getWithinCodeElements().size());
        assertEquals("samankoodistonsisalla2", codeElementToBeSaved.getWithinCodeElements().get(0).codeElementUri);

        codeElementToBeSaved.getMetadata().get(0).setNimi(nimi);
        codeElementToBeSaved.getIncludesCodeElements().clear();
        codeElementToBeSaved.getWithinCodeElements().clear();
        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusisamankoodistonsisalla1", 1, false));
        codeElementToBeSaved.getWithinCodeElements().add(new RelationCodeElement("uusisamankoodistonsisalla2", 1, false));
        resource.save(codeElementToBeSaved);

        ExtendedKoodiDto codeElement = resource.getCodeElementByUriAndVersion(koodiUri, versio + 1);
        assertEquals(nimi, codeElement.getMetadata().get(0).getNimi());
        assertEquals(2, codeElement.getIncludesCodeElements().size());
        for (RelationCodeElement relation : codeElement.getIncludesCodeElements()) {
            if ("uusisamankoodistonsisalla1".equals(relation.codeElementUri)) {
                assertFalse(relation.passive);
            } else if ("samankoodistonsisalla1".equals(relation.codeElementUri)) {
                assertTrue(relation.passive);
            } else {
                fail();
            }
        }
        assertEquals(2, codeElement.getWithinCodeElements().size());
        for (RelationCodeElement relation : codeElement.getWithinCodeElements()) {
            if ("uusisamankoodistonsisalla2".equals(relation.codeElementUri)) {
                assertFalse(relation.passive);
            } else if ("samankoodistonsisalla2".equals(relation.codeElementUri)) {
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
        final String codeElementUri = "sisaltaakoodisto1koodit";
        final String codeElementUriToRemove = "rinnastuu4kanssa1";
        final String relationType = "RINNASTEINEN";
        assertException(() -> resource.removeRelation("", codeElementUriToRemove, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuri"));
        assertException(() -> resource.removeRelation(null, codeElementUriToRemove, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuri"));
        assertException(() -> resource.removeRelation("doenotexist", codeElementUriToRemove, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> resource.removeRelation(codeElementUri, "", relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuritoremove"));
        assertException(() -> resource.removeRelation(codeElementUri, null, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuritoremove"));
        assertException(() -> resource.removeRelation(codeElementUri, "doesnotexist", relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.relation.list.empty"));
        assertException(() -> resource.removeRelation(codeElementUri, codeElementUriToRemove, ""),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.relationtype"));
        assertException(() -> resource.removeRelation(codeElementUri, codeElementUriToRemove, null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.relationtype"));
        assertException(() -> resource.removeRelation(codeElementUri, codeElementUriToRemove, "doesnotexist"),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codes.generic"));

        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.RINNASTEINEN).size());

        final String codeElementUri2 = "sisaltaakoodisto1koodit";
        final String codeElementUriToRemove2 = "sisaltyysuhde4kanssa1";
        final String relationType2 = "SISALTYY";
        assertException(() -> resource.removeRelation("", codeElementUriToRemove2, relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuri"));
        assertException(() -> resource.removeRelation(null, codeElementUriToRemove2, relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuri"));
        assertException(() -> resource.removeRelation(codeElementUri2, "", relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuritoremove"));
        assertException(() -> resource.removeRelation(codeElementUri2, null, relationType2),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codeelementuritoremove"));

        assertException(() -> resource.removeRelation(codeElementUri2, codeElementUriToRemove2, ""),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.relationtype"));
        assertException(() -> resource.removeRelation(codeElementUri2, codeElementUriToRemove2, null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.relationtype"));
        assertException(() -> resource.removeRelation(codeElementUri2, codeElementUriToRemove2, "doesnotexist"),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codes.generic"));

        assertException(() -> resource.removeRelation("doenotexist", codeElementUriToRemove, relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> resource.removeRelation(codeElementUri, "doesnotexist", relationType),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.relation.list.empty"));

        assertEquals(3, service.listByRelation(codeElementUri, 1, false, SuhteenTyyppi.SISALTYY).size());
    }

    @Test
    public void testDelete() {
        String codeElementUri = "tuhottavatestikoodi";
        int codeElementVersion = 1;
        assertNotNull(resource.getCodeElementByUriAndVersion(codeElementUri, codeElementVersion));
        resource.delete(codeElementUri, codeElementVersion);
        assertException(() -> resource.getCodeElementByUriAndVersion(codeElementUri, codeElementVersion),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
    }

    @Test
    public void testDeleteInvalid() {
        assertException(() -> resource.delete("tuhottavatestikoodi", 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.delete("tuhottavatestikoodi", -1),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.delete("", 1),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.delete(null, 1),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        assertException(() -> resource.delete("thisisnotexistinguri", 1),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.found"));
        assertException(() -> resource.delete("sisaltaakoodisto1koodit", 1),
                new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR, "error.codeelement.not.passive"));

        assertNotNull(resource.getCodeElementByUriAndVersion("tuhottavatestikoodi", 1));
    }

    @Test
    public void testUpdate() {
        KoodiDto original = resource.getCodeElementByCodeElementUri("updatetestkoodisto", 1, "paivitettavatestikoodi");
        assertNotNull(original);
        assertEquals(1, original.getMetadata().size());

        List<KoodiMetadata> koodiMetadata = new ArrayList<>();
        KoodiMetadata o1 = clone(original.getMetadata().get(0));
        o1.setNimi("Modified Name");
        koodiMetadata.add(o1);
        original.setMetadata(koodiMetadata);

        resource.update(original);

        KoodiDto updated = resource.getCodeElementByCodeElementUri("updatetestkoodisto", 2, "paivitettavatestikoodi");
        assertNotNull(updated);
        assertEquals(1, updated.getMetadata().size());
        assertEquals("Modified Name", updated.getMetadata().get(0).getNimi());

    }

    @Test
    public void testUpdateInvalid() {
        assertException(() -> resource.update(null),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
        // TODO Make better test
    }

    @Test
    public void returnsChangesForCodeElement() {
        KoodiChangesDto dto = resource.getChangesToCodeElement("montaversiota", 1, false);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertEquals("Monta versiota 3", dto.muuttuneetTiedot.get(0).nimi);
        assertNull(dto.voimassaAlkuPvm);
        assertEquals(3, dto.viimeisinVersio.intValue());
    }

    @Test
    public void returnsNoChangesForCodeElement() {
        KoodiChangesDto dto = resource.getChangesToCodeElement("montaversiota", 3, false);
        assertEquals(MuutosTila.EI_MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.muuttuneetTiedot.isEmpty());
        assertEquals(3, dto.viimeisinVersio.intValue());
    }

    @Test
    public void comparesAgainstLatestAcceptedCodeElementVersion() {
        KoodiChangesDto dto = resource.getChangesToCodeElement("viimeinenonluonnos", 1, true);
        assertEquals(2, dto.viimeisinVersio.intValue());
        assertNull(dto.tila);
    }

    @Test
    public void comparesAgainstLatestCodeElementVersion() {
        KoodiChangesDto dto = resource.getChangesToCodeElement("viimeinenonluonnos", 1, false);
        assertEquals(3, dto.viimeisinVersio.intValue());
        assertEquals(Tila.LUONNOS, dto.tila);
    }

    @Test
    public void comparesAgainstLatestAcceptedCodeElementVersionUsingDate() {
        KoodiChangesDto dto = resource.getChangesToCodeElementWithDate("viimeinenonluonnos", 12, 1, 2000, 0, 0, 0, true);
        assertEquals(2, dto.viimeisinVersio.intValue());
        assertNull(dto.tila);
    }

    @Test
    public void comparesAgainstLatestCodeElementVersionUsingDate() {
        KoodiChangesDto dto = resource.getChangesToCodeElementWithDate("viimeinenonluonnos", 12, 12, 2000, 0, 0, 0, false);
        assertEquals(3, dto.viimeisinVersio.intValue());
        assertEquals(Tila.LUONNOS, dto.tila);
    }

    @Test
    public void returnsChangesForCodeElementUsingDateFromPast() {
        KoodiChangesDto dto = resource.getChangesToCodeElementWithDate("montaversiota", 12, 1, 2000, 0, 0, 0, false);
        assertEquals(MuutosTila.MUUTOKSIA, dto.muutosTila);
        assertEquals("Monta versiota 3", dto.muuttuneetTiedot.get(0).nimi);
        assertNull(dto.voimassaAlkuPvm);
        assertEquals(3, dto.viimeisinVersio.intValue());
    }

    @Test
    public void returnsNoChangesForCodeElementUsingDateFromFuture() {
        KoodiChangesDto dto = resource.getChangesToCodeElementWithDate("montaversiota", 12, 1, 4000, 0, 0, 0, false);
        assertEquals(MuutosTila.EI_MUUTOKSIA, dto.muutosTila);
        assertTrue(dto.muuttuneetTiedot.isEmpty());
        assertEquals(3, dto.viimeisinVersio.intValue());
    }

    @Test
    public void returnsCodeElementHasBeenRemovedWhenItIsNotFoundInLatestCodesVersion() {
        assertEquals(MuutosTila.POISTETTU, resource.getChangesToCodeElement("poistettu", 1, false).muutosTila);
    }

    @Test
    public void returnsBadRequestWhenVersionNumberIsZeroForQueryingCodeElementChanges() {
        assertException(() -> resource.getChangesToCodeElement("poistettu", 0, false),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void returnsBadRequestForBadDateParametersWhenQueryingCodeElementChanges() {
        ResponseStatusExceptionMatcher matcher = new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST);
        assertException(() -> resource.getChangesToCodeElementWithDate(
                "montaversiota", 12, 1, 4000, 0, 0, -1, false),
                matcher);
        assertException(() -> resource.getChangesToCodeElementWithDate(
                "montaversiota", 12, 1, 4000, 0, -1, 0, false),
                matcher);
        assertException(() -> resource.getChangesToCodeElementWithDate(
                "montaversiota", 12, 1, 4000, 25, 0, 0, false),
                matcher);
        assertException(() -> resource.getChangesToCodeElementWithDate(
                "montaversiota", 32, 1, 4000, 0, 0, 0, false),
                matcher);
        assertException(() -> resource.getChangesToCodeElementWithDate(
                "montaversiota", 12, 15, 4000, 0, 0, 0, true),
                matcher);
    }

    @Test
    public void savesCodeElementWithNewName() {
        String koodiUri = "sisaltyysuhde4kanssa1";
        String nimi = "uusinimi";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = clone(resource.getCodeElementByUriAndVersion(koodiUri, versio));
        assertEquals(Tila.HYVAKSYTTY, codeElementToBeSaved.getTila());
        assertNotEquals(nimi, codeElementToBeSaved.getMetadata().get(0).getNimi());

        codeElementToBeSaved.getMetadata().get(0).setNimi(nimi);
        String uusiVersio = resource.save(codeElementToBeSaved);

        ExtendedKoodiDto codes = resource.getCodeElementByUriAndVersion(koodiUri, Integer.parseInt(uusiVersio));
        assertEquals(Tila.LUONNOS, codes.getTila());
        assertEquals(nimi, codes.getMetadata().get(0).getNimi());
    }

    @Test
    public void savesCodeElementWithNewNameAndRelations() {
        String koodiUri = "savekoodineljallasuhteella";
        String nimi = "uusinimi";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = clone(resource.getCodeElementByUriAndVersion(koodiUri, versio));
        assertEquals(1, codeElementToBeSaved.getIncludesCodeElements().size());
        assertEquals(2, codeElementToBeSaved.getLevelsWithCodeElements().size());
        assertEquals(1, codeElementToBeSaved.getWithinCodeElements().size());

        codeElementToBeSaved.getMetadata().get(0).setNimi(nimi);
        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde1", 1, false));
        codeElementToBeSaved.getLevelsWithCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde2", 1, false));
        codeElementToBeSaved.getWithinCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde3", 1, false));
        resource.save(codeElementToBeSaved);

        ExtendedKoodiDto codeElement = resource.getCodeElementByUriAndVersion(koodiUri, versio + 1);
        assertEquals(2, codeElement.getIncludesCodeElements().size());
        assertEquals(3, codeElement.getLevelsWithCodeElements().size());
        assertEquals(3, codeElement.getWithinCodeElements().size());
    }

    @Test
    public void savesCodeElementRelationIncludes() {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = clone(resource.getCodeElementByUriAndVersion(koodiUri, versio));

        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde1", 1, false));
        resource.save(codeElementToBeSaved);

        ExtendedKoodiDto codeElement = resource.getCodeElementByUriAndVersion(koodiUri, versio + 1);
        assertEquals(2, codeElement.getIncludesCodeElements().size());
        assertEquals(1, codeElement.getWithinCodeElements().size());
        assertEquals(2, codeElement.getLevelsWithCodeElements().size());
    }

    @Test
    public void savesCodeElementRelationWithin() {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = clone(resource.getCodeElementByUriAndVersion(koodiUri, versio));

        codeElementToBeSaved.getWithinCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde3", 1, false));
        resource.save(codeElementToBeSaved);

        ExtendedKoodiDto codeElement = resource.getCodeElementByUriAndVersion(koodiUri, versio);
        assertEquals(1, codeElement.getIncludesCodeElements().size());
        assertEquals(3, codeElement.getWithinCodeElements().size()); // Parentcodes versions, so the old relation is duplicated.
        assertEquals(2, codeElement.getLevelsWithCodeElements().size());
    }

    @Test
    public void savesCodeElementRelationLevelsWith() {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = clone(resource.getCodeElementByUriAndVersion(koodiUri, versio));

        codeElementToBeSaved.getLevelsWithCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde2", 1, false));
        resource.save(codeElementToBeSaved);

        ExtendedKoodiDto codeElement = resource.getCodeElementByUriAndVersion(koodiUri, versio);
        assertEquals(1, codeElement.getIncludesCodeElements().size());
        assertEquals(1, codeElement.getWithinCodeElements().size());
        assertEquals(3, codeElement.getLevelsWithCodeElements().size());
    }

    @Test
    public void savesCodeElementWithAllRelationChanges() {
        String koodiUri = "savekoodineljallasuhteella";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = clone(resource.getCodeElementByUriAndVersion(koodiUri, versio));
        ExtendedKoodiDto oldCodeElementBeforeSave = clone(codeElementToBeSaved);
        assertEquals(1, codeElementToBeSaved.getIncludesCodeElements().size());
        assertEquals(2, codeElementToBeSaved.getLevelsWithCodeElements().size());
        assertEquals(1, codeElementToBeSaved.getWithinCodeElements().size());
        assertThat(oldCodeElementBeforeSave.getIncludesCodeElements())
                .extracting(RelationCodeElement::getCodeElementUri)
                .containsExactlyInAnyOrder("savekoodinsuhde1");
        assertThat(oldCodeElementBeforeSave.getLevelsWithCodeElements())
                .extracting(RelationCodeElement::getCodeElementUri)
                .containsExactlyInAnyOrder("savekoodinsuhde3", "savekoodinsuhde2");
        assertThat(oldCodeElementBeforeSave.getWithinCodeElements())
                .extracting(RelationCodeElement::getCodeElementUri)
                .containsExactlyInAnyOrder("savekoodinsuhde4");

        codeElementToBeSaved.getIncludesCodeElements().clear();
        codeElementToBeSaved.getWithinCodeElements().clear();
        codeElementToBeSaved.getLevelsWithCodeElements().clear();
        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde1", 1, false));
        codeElementToBeSaved.getLevelsWithCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde2", 1, false));
        codeElementToBeSaved.getWithinCodeElements().add(new RelationCodeElement("uusisavekoodinsuhde3", 1, false));
        String uusiVersio = resource.save(codeElementToBeSaved);
        assertThat(uusiVersio).isNotEqualTo(versio);

        ExtendedKoodiDto oldCodeElement = resource.getCodeElementByUriAndVersion(koodiUri, versio);
        assertThat(oldCodeElement.getIncludesCodeElements())
                .extracting(RelationCodeElement::getCodeElementUri)
                .containsExactlyInAnyOrder("savekoodinsuhde1");
        assertThat(oldCodeElement.getLevelsWithCodeElements())
                .extracting(RelationCodeElement::getCodeElementUri)
                .containsExactlyInAnyOrder("savekoodinsuhde3", "savekoodinsuhde2");
        assertThat(oldCodeElement.getWithinCodeElements())
                .extracting(RelationCodeElement::getCodeElementUri)
                .containsExactlyInAnyOrder("savekoodinsuhde4");

        ExtendedKoodiDto newCodeElement = resource.getCodeElementByUriAndVersion(koodiUri, Integer.parseInt(uusiVersio));
        assertThat(newCodeElement.getIncludesCodeElements())
                .extracting(RelationCodeElement::getCodeElementUri)
                .containsExactlyInAnyOrder("uusisavekoodinsuhde1");
        assertThat(newCodeElement.getLevelsWithCodeElements())
                .extracting(RelationCodeElement::getCodeElementUri)
                .containsExactlyInAnyOrder("uusisavekoodinsuhde2");
        assertThat(newCodeElement.getWithinCodeElements())
                .extracting(RelationCodeElement::getCodeElementUri, RelationCodeElement::isPassive)
                .containsExactlyInAnyOrder(Tuple.tuple("savekoodinsuhde4", true), Tuple.tuple("uusisavekoodinsuhde3", false));
    }

    @Test
    public void savesCodeElementRelationChangesIfRelationHasExistedBefore() {
        String koodiUri = "uusirelaatiovanhantilalle1";
        int versio = 1;

        ExtendedKoodiDto codeElementToBeSaved = clone(resource.getCodeElementByUriAndVersion(koodiUri, versio));
        assertEquals(1, codeElementToBeSaved.getIncludesCodeElements().size());
        assertTrue(codeElementToBeSaved.getIncludesCodeElements().get(0).passive);
        assertEquals(0, codeElementToBeSaved.getLevelsWithCodeElements().size());
        assertEquals(0, codeElementToBeSaved.getWithinCodeElements().size());

        codeElementToBeSaved.getIncludesCodeElements().add(new RelationCodeElement("uusirelaatiovanhantilalle2", 1, false));
        resource.save(codeElementToBeSaved);

        ExtendedKoodiDto codeElement = resource.getCodeElementByUriAndVersion(koodiUri, versio + 1);
        assertEquals(1, codeElement.getIncludesCodeElements().size());
        assertEquals(0, codeElement.getLevelsWithCodeElements().size());
        assertEquals(0, codeElement.getWithinCodeElements().size());

        assertEquals("uusirelaatiovanhantilalle2", codeElement.getIncludesCodeElements().get(0).codeElementUri);
        assertFalse(codeElement.getIncludesCodeElements().get(0).passive);
    }

    // UTILITIES
    // /////////

    private void addRelations(String codeElementUri, String st, KoodiRelaatioListaDto kr) {
        kr.setCodeElementUri(codeElementUri);
        kr.setRelationType(st);
        resource.addRelations(kr);
    }

    private void removeRelations(String codeElementUri, String st, KoodiRelaatioListaDto kr) {
        kr.setCodeElementUri(codeElementUri);
        kr.setRelationType(st);
        resource.removeRelations(kr);
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

    private ExtendedKoodiDto clone(ExtendedKoodiDto dto) {
        ExtendedKoodiDto cloned = new ExtendedKoodiDto();
        cloned.setIncludesCodeElements(dto.getIncludesCodeElements().stream()
                .map(this::clone).collect(Collectors.toList()));
        cloned.setKoodiArvo(dto.getKoodiArvo());
        cloned.setKoodiUri(dto.getKoodiUri());
        cloned.setKoodisto(dto.getKoodisto());
        cloned.setLevelsWithCodeElements(dto.getLevelsWithCodeElements().stream()
                .map(this::clone).collect(Collectors.toList()));
        cloned.setPaivittajaOid(dto.getPaivittajaOid());
        cloned.setPaivitysPvm(dto.getPaivitysPvm());
        cloned.setResourceUri(dto.getResourceUri());
        cloned.setTila(dto.getTila());
        cloned.setVoimassaAlkuPvm(dto.getVoimassaAlkuPvm());
        cloned.setVoimassaLoppuPvm(dto.getVoimassaLoppuPvm());
        cloned.setVersio(dto.getVersio());
        cloned.setVersion(dto.getVersion());
        cloned.setWithinCodeElements(dto.getWithinCodeElements().stream()
                .map(this::clone).collect(Collectors.toList()));
        cloned.setMetadata(dto.getMetadata().stream()
                .map(this::clone).collect(Collectors.toList()));
        return cloned;
    }

    private ExtendedKoodiDto.RelationCodeElement clone(ExtendedKoodiDto.RelationCodeElement element) {
        return new ExtendedKoodiDto.RelationCodeElement(
                element.codeElementUri, element.codeElementVersion, element.codeElementValue,
                element.relationMetadata, element.parentMetadata, element.passive
        );
    }

    private KoodiMetadata clone(KoodiMetadata meta) {
        KoodiMetadata cloned = new KoodiMetadata();
        cloned.setEiSisallaMerkitysta(meta.getEiSisallaMerkitysta());
        cloned.setHuomioitavaKoodi(meta.getHuomioitavaKoodi());
        cloned.setId(meta.getId());
        cloned.setKasite(meta.getKasite());
        cloned.setKayttoohje(meta.getKayttoohje());
        cloned.setKieli(meta.getKieli());
        cloned.setKoodiVersio(meta.getKoodiVersio());
        cloned.setKuvaus(meta.getKuvaus());
        cloned.setLyhytNimi(meta.getLyhytNimi());
        cloned.setNimi(meta.getNimi());
        cloned.setSisaltaaKoodiston(meta.getSisaltaaKoodiston());
        cloned.setSisaltaaMerkityksen(meta.getSisaltaaMerkityksen());
        cloned.setVersion(meta.getVersion());
        return cloned;
    }
}
