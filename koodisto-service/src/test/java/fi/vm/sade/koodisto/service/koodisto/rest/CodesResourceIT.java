package fi.vm.sade.koodisto.service.koodisto.rest;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.dto.*;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.*;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.test.support.ResponseStatusExceptionMatcher;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static fi.vm.sade.koodisto.test.support.Assertions.assertException;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class
})
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@DatabaseSetup("classpath:test-data-codes-rest.xml")
@WithMockUser("1.2.3.4.5")
public class CodesResourceIT {

    @Autowired
    private CodesResource resource;

    @Autowired
    private ResourceHelper helper;

    @Autowired
    private KoodiBusinessService service;


    @Test
    public void returns400AndCorrectErrorCodeIfQueryParamsAreMissing() throws IOException {
        String nullString = null;
        String blankString = "";
        KoodistoDto nullCodesDTO = null;
        
        String stubString = "uri";
        InputStream stubInputStream = IOUtils.toInputStream("stubfile");

        assertException(() -> resource.update(nullCodesDTO),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codes"));

        assertException(() -> resource.insert(nullCodesDTO),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codes"));

        assertException(() -> resource.getCodesByCodesUri(nullString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
        assertException(() -> resource.getCodesByCodesUri(blankString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));

        assertException(() -> resource.getCodesByCodesUriAndVersion(nullString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
        assertException(() -> resource.getCodesByCodesUriAndVersion(blankString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));

        assertEquals("error.validation.file", resource.uploadFile(null, stubString, stubString, stubString));
        assertEquals("error.validation.fileformat", resource.uploadFile(new MockMultipartFile("name", stubInputStream), nullString, stubString, stubString));
        assertEquals("error.validation.fileformat", resource.uploadFile(new MockMultipartFile("name", stubInputStream), blankString, stubString, stubString));
        assertEquals("error.validation.fileencoding", resource.uploadFile(new MockMultipartFile("name", stubInputStream), stubString, nullString, stubString));
        assertEquals("error.validation.fileencoding", resource.uploadFile(new MockMultipartFile("name", stubInputStream), stubString, blankString, stubString));
        assertEquals("error.validation.codesuri", resource.uploadFile(new MockMultipartFile("name", stubInputStream), stubString, stubString, nullString));
        assertEquals("error.validation.codesuri", resource.uploadFile(new MockMultipartFile("name", stubInputStream), stubString, stubString, blankString));

        assertException(() -> resource.download(nullString, 0, Format.JHS_XML, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
        assertException(() -> resource.download(blankString, 0, Format.JHS_XML, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
        assertException(() -> resource.download(stubString, 0, null, stubString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.fileformat"));
        assertException(() -> resource.download(stubString, 0, Format.JHS_XML, nullString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.encoding"));
        assertException(() -> resource.download(stubString, 0, Format.JHS_XML, blankString),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.encoding"));

        assertException(() -> resource.delete(nullString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
        assertException(() -> resource.delete(blankString, 0),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST, "error.validation.codesuri"));
    }

    @Test
    public void addsWithinRelationBetweenExistingcodes() {
        String parentUri = "eisuhteitaviela1";
        String childUri = "eisuhteitaviela2";
        KoodistoDto parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1);
        KoodistoDto childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1);

        assertEquals(0, parentCodes.getIncludesCodes().size());
        assertEquals(0, childCodes.getWithinCodes().size());

        resource.addRelation(parentUri, childUri, "SISALTYY");

        parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1);
        childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1);

        assertEquals(1, parentCodes.getIncludesCodes().size());
        assertEquals(0, parentCodes.getLevelsWithCodes().size());
        assertEquals(0, parentCodes.getWithinCodes().size());
        assertEquals(0, childCodes.getIncludesCodes().size());
        assertEquals(0, childCodes.getLevelsWithCodes().size());
        assertEquals(1, childCodes.getWithinCodes().size());
        assertEquals(Tila.HYVAKSYTTY, parentCodes.getTila());
        assertEquals(Tila.HYVAKSYTTY, childCodes.getTila());

    }

    @Test
    public void addsLevesWithRelationBetweenExistingcodes() {
        String parentUri = "eisuhteitaviela3";
        String childUri = "eisuhteitaviela4";
        KoodistoDto parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1);
        KoodistoDto childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1);

        assertEquals(0, parentCodes.getLevelsWithCodes().size());
        assertEquals(0, childCodes.getLevelsWithCodes().size());

        resource.addRelation(parentUri, childUri, "RINNASTEINEN");

        parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1);
        childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1);

        assertEquals(0, parentCodes.getIncludesCodes().size());
        assertEquals(1, parentCodes.getLevelsWithCodes().size());
        assertEquals(0, parentCodes.getWithinCodes().size());
        assertEquals(0, childCodes.getIncludesCodes().size());
        assertEquals(1, childCodes.getLevelsWithCodes().size());
        assertEquals(0, childCodes.getWithinCodes().size());
        assertEquals(Tila.HYVAKSYTTY, parentCodes.getTila());
        assertEquals(Tila.HYVAKSYTTY, childCodes.getTila());

    }

    @Test
    public void removesRelationBetweenExistingcodes() {
        String parentUri = "sisaltyysuhde1";
        String childUri = "sisaltyysuhde2";
        KoodistoDto parentCodes = resource.getCodesByCodesUriAndVersion(parentUri, 1);
        KoodistoDto childCodes = resource.getCodesByCodesUriAndVersion(childUri, 1);

        assertEquals(1, parentCodes.getIncludesCodes().size());
        assertEquals(1, childCodes.getWithinCodes().size());

        resource.removeRelation(parentUri, childUri, "SISALTYY");

        parentCodes = resource.getCodesByCodesUriAndVersion(parentUri, 1);
        childCodes = resource.getCodesByCodesUriAndVersion(childUri, 1);

        assertEquals(0, parentCodes.getIncludesCodes().size());
        assertEquals(0, parentCodes.getLevelsWithCodes().size());
        assertEquals(0, parentCodes.getWithinCodes().size());
        assertEquals(0, childCodes.getIncludesCodes().size());
        assertEquals(0, childCodes.getLevelsWithCodes().size());
        assertEquals(0, childCodes.getWithinCodes().size());
        assertEquals(Tila.HYVAKSYTTY, parentCodes.getTila());
        assertEquals(Tila.HYVAKSYTTY, childCodes.getTila());
    }

    @Test
    public void insertsNewCodes() {
        String koodistoUri = "inserttest";
        String codesGroupUri = "koodistojenlisaaminenkoodistoryhmaan";

        KoodistoDto codesToBeInserted = createDTO(koodistoUri, codesGroupUri);
        resource.insert(codesToBeInserted);

        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
        assertEquals(koodistoUri, codes.getKoodistoUri());
        assertEquals(1, codes.getVersio());
        assertEquals(codesGroupUri, codes.getCodesGroupUri());
    }

    @Test
    public void invalidInsertCausesError() {
        String koodistoUri = "inserttest";
        String codesGroupUri = "koodistojenlisaaminenkoodistoryhmaan";

        KoodistoDto codesToBeInserted = createDTO(koodistoUri, codesGroupUri);
        codesToBeInserted.getMetadata().get(0).setKieli(null);
        codesToBeInserted.setVoimassaLoppuPvm(new Date());
        assertException(() -> resource.insert(codesToBeInserted),
                new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void uudenVersionTallennusSailyttaaVanhanVersionEnnallaan() {
        // uusi koodisto
        LocalDate alkuPvmV1 = LocalDate.of(2000, 1, 1);
        LocalDate loppuPvmV1 = alkuPvmV1.with(lastDayOfYear());
        KoodistoDto koodistoV1a = helper.createKoodisto(newKoodistoDto(
                "koodistojenlisaaminenkoodistoryhmaan", "organisaatio1",
                "test", "koodistoV1",
                alkuPvmV1, loppuPvmV1));
        assertThat(koodistoV1a)
                .returns(1, KoodistoDto::getVersio)
                .returns(Tila.LUONNOS, KoodistoDto::getTila);
        KoodiDto koodiV1a = helper.createKoodi(koodistoV1a.getKoodistoUri(),
                newKoodiDto(koodistoV1a, "koodi1", "koodiV1"));

        // lisätään koodille voimassaoloaika
        koodiV1a.setVoimassaLoppuPvm(java.sql.Date.valueOf(LocalDate.now().plusMonths(1)));
        KoodiDto koodiV1b = helper.updateKoodi(koodiV1a);
        assertThat(koodiV1b)
                .returns(koodiV1a.getVersio(), KoodiDto::getVersio)
                .returns(Tila.LUONNOS, KoodiDto::getTila);

        // hyväksytään ensimmäinen versio
        koodistoV1a = helper.getKoodisto(koodistoV1a.getKoodistoUri(), koodistoV1a.getVersio());
        koodistoV1a.setTila(Tila.HYVAKSYTTY);
        KoodistoDto koodistoV1b = helper.updateKoodisto(koodistoV1a);
        assertThat(koodistoV1b)
                .returns(koodistoV1a.getVersio(), KoodistoDto::getVersio)
                .returns(Tila.HYVAKSYTTY, KoodistoDto::getTila);
        ExtendedKoodiDto koodiV1c = helper.getKoodi(koodiV1a.getKoodiUri(), koodiV1a.getVersio());
        assertThat(koodiV1c)
                .returns(koodiV1a.getVersio(), ExtendedKoodiDto::getVersio)
                .returns(Tila.HYVAKSYTTY, ExtendedKoodiDto::getTila);

        // muokataan koodistoa, pitäisi tulla uusi versio
        koodistoV1a = helper.getKoodisto(koodistoV1a.getKoodistoUri(), koodistoV1b.getVersio());
        koodistoV1a.getMetadata().get(0).setNimi("koodistoV2");
        KoodistoDto koodistoV2a = helper.updateKoodisto(koodistoV1a);
        assertThat(koodistoV2a)
                .returns(2, KoodistoDto::getVersio)
                .returns(Tila.LUONNOS, KoodistoDto::getTila);
        ExtendedKoodiDto koodiV2a = helper.getKoodi(koodiV1c.getKoodiUri(), koodiV1c.getVersio() + 1);
        assertThat(koodiV2a)
                .returns(2, ExtendedKoodiDto::getVersio)
                .returns(Tila.LUONNOS, ExtendedKoodiDto::getTila);

        // hyväksytään uusi versio, versio pitäisi pysyä samana
        koodistoV1a = helper.getKoodisto(koodistoV1a.getKoodistoUri(), koodistoV2a.getVersio());
        LocalDate alkuPvmV2 = alkuPvmV1.plusYears(1);
        LocalDate loppuPvmV2 = alkuPvmV2.with(lastDayOfYear());
        koodistoV1a.setVoimassaAlkuPvm(java.sql.Date.valueOf(alkuPvmV2));
        koodistoV1a.setVoimassaLoppuPvm(java.sql.Date.valueOf(loppuPvmV2));
        koodistoV1a.setTila(Tila.HYVAKSYTTY);
        KoodistoDto koodistoV2b = helper.updateKoodisto(koodistoV1a);
        assertThat(koodistoV2b)
                .returns(koodistoV2a.getVersio(), KoodistoDto::getVersio)
                .returns(Tila.HYVAKSYTTY, KoodistoDto::getTila)
                .returns(java.sql.Date.valueOf(alkuPvmV2), KoodistoDto::getVoimassaAlkuPvm)
                .returns(java.sql.Date.valueOf(loppuPvmV2), KoodistoDto::getVoimassaLoppuPvm);

        // tarkastetaan että v1 on vielä kunnossa
        KoodistoDto koodistoV1c = helper.getKoodisto(koodistoV1a.getKoodistoUri(), koodistoV1b.getVersio());
        assertThat(koodistoV1c).isEqualToIgnoringGivenFields(koodistoV1b, "codesVersions");
        ExtendedKoodiDto koodiV1d = helper.getKoodi(koodiV1a.getKoodiUri(), koodiV1b.getVersio());
        assertThat(koodiV1d).isEqualToIgnoringGivenFields(koodiV1b,
                "version", "koodisto", "paivitysPvm", "tila",
                "withinCodeElements", "includesCodeElements", "levelsWithCodeElements");
    }

    @Test
    public void listsCodesByCodesUri() {
        String koodistoUri = "moniaversioita";
        KoodistoListDto koodistot = resource.getCodesByCodesUri(koodistoUri);
        assertEquals(3, koodistot.getKoodistoVersios().size());
    }

    @Test
    public void getLatestCodeByUri() {
        String koodistoUri = "moniaversioita";
        KoodistoDto codes =  resource.getCodesByCodesUriAndVersion(koodistoUri, 0);
        assertEquals(3, codes.getVersio());
    }

    @Test
    public void listCodes() {
        List<KoodistoRyhmaListDto> codes = resource.listAllCodesGroups();
        assertNotNull(codes);

        List<KoodistoVersioListDto> codes2 = resource.listAllCodesInAllCodeGroups();
        assertNotNull(codes2);

        // TODO:tarkempi assertointi
    }

    @Test
    public void uploadsCSVFiles() {
        InputStream is = null;
        String fileFormat = "CSV";
        String fileEncoding = "UTF-8";
        String codesUri = "csvfileuploaduri";
        resource.uploadFile(createAttachment("csv_example.csv"), fileFormat, fileEncoding, codesUri);
        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(codesUri, 0);
        assertNotNull(codes);
        List<KoodiVersioWithKoodistoItem> koodis = service.getKoodisByKoodisto(codesUri, false);
        assertEquals("csvfileuploaduri_arvo", koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());
    }

    @Test
    public void uploadsXMLFiles() {
        InputStream is = null;
        String fileFormat = "JHS_XML";
        String fileEncoding = "UTF-8";
        String codesUri = "xmlfileuploaduri";
        resource.uploadFile(createAttachment("jhs_xml_example.xml"), fileFormat, fileEncoding, codesUri);
        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(codesUri, 0);
        assertNotNull(codes);
        List<KoodiVersioWithKoodistoItem> koodis = service.getKoodisByKoodisto(codesUri, false);
        assertEquals("xmlfileuploaduri_arvo", koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());
    }

    @Test
    public void uploadsXLSFiles() {
        InputStream is = null;
        String fileFormat = "XLS";
        String fileEncoding = "UTF-8";
        String codesUri = "xlsfileuploaduri";
        resource.uploadFile(createAttachment("excel_example.xls"), fileFormat, fileEncoding, codesUri);
        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(codesUri, 0);
        assertNotNull(codes);
        List<KoodiVersioWithKoodistoItem> koodis = service.getKoodisByKoodisto(codesUri, false);
        assertEquals("xlsfileuploaduri_arvo", koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());
    }

    private MockMultipartFile createAttachment(String sourceUrl) throws IllegalStateException {
        try {
            return new MockMultipartFile("name", ClassLoader.getSystemResourceAsStream(sourceUrl));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to open stream from: " + sourceUrl, e);
        }
    }

    @Test
    public void uploadsSameFileTwice() {
        String fileFormat = "CSV";
        String fileEncoding = "UTF-8";
        String codesUri = "csvfileuploaduri";

        resource.uploadFile(createAttachment("csv_example.csv"), fileFormat, fileEncoding, codesUri);
        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(codesUri, 0);
        assertNotNull(codes);
        List<KoodiVersioWithKoodistoItem> koodis = service.getKoodisByKoodisto(codesUri, false);
        assertEquals("csvfileuploaduri_arvo", koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());

        resource.uploadFile(createAttachment("csv_example.csv"), fileFormat, fileEncoding, codesUri);
    }

    @Test
    public void failsToUploadInvalidFile() {
        InputStream is = null;
        String fileFormat = "CSV";
        String fileEncoding = "UTF-8";
        String codesUri = "csvfileuploaduri";
        // IE9 can not handle upload if server return 400 or 500
        String result = resource.uploadFile(createFailureAttachment(), fileFormat, fileEncoding, codesUri);
        assertEquals("error.codes.importing.empty.file", result);
    }

    private MockMultipartFile createFailureAttachment() throws IllegalStateException {
        try {
            return new MockMultipartFile("id", new ByteArrayInputStream(new byte[0]));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void updatingCodes() {
        String koodistoUri = "updatekoodisto";

        KoodistoDto codes1 = resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
        codes1.getMetadata().get(0).setNimi("Päivitetty Testinimi");
        resource.update(codes1);

        codes1 = resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
        KoodistoDto codes2 = resource.getCodesByCodesUriAndVersion(koodistoUri, 2);
        assertEquals("Update testi", codes1.getMetadata().get(0).getNimi());
        assertEquals("Päivitetty Testinimi", codes2.getMetadata().get(0).getNimi());
        assertNotNull(codes2.getPaivitysPvm());
    }

    @Test
    public void invalidUpdatingCodesFails() {
        String koodistoUri = "updatekoodisto";

        final KoodistoDto codes1 = resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
        codes1.getMetadata().get(0).setNimi(null); // Invalid
        assertException(() -> resource.update(codes1), new ResponseStatusExceptionMatcher(HttpStatus.BAD_REQUEST));

        KoodistoDto codes2 = resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
        assertEquals("Update testi", codes2.getMetadata().get(0).getNimi());
    }

    @Test
    public void downloadsFiles() {
        {
            String codesUri = "filedownloaduri";
            int codesVersion = 1;
            String encoding = "Utf-8";
            Format fileFormat = Format.CSV;
            ResponseEntity<File> response = resource.download(codesUri, codesVersion, fileFormat, encoding);
            String content = fileToString(response.getBody());
            assertTrue(content.contains("Description of downloaded code"));
        }
        {
            String codesUri = "filedownloaduri";
            int codesVersion = 1;
            String encoding = "Utf-8";
            Format fileFormat = Format.JHS_XML;
            ResponseEntity<File> response = resource.download(codesUri, codesVersion, fileFormat, encoding);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            String content = fileToString(response.getBody());
            assertTrue(content.contains("Description of downloaded code"));
        }
        {
            String codesUri = "filedownloaduri";
            int codesVersion = 1;
            String encoding = "Utf-8";
            Format fileFormat = Format.XLS;
            ResponseEntity<File> response = resource.download(codesUri, codesVersion, fileFormat, encoding);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test
    public void deleteCodes() {
        {
            String codesUri = "deletethisuri";
            int codesVersion = 1;
            assertNotNull(resource.getCodesByCodesUriAndVersion(codesUri, codesVersion));
            resource.delete(codesUri, codesVersion);
        }
        {
            String codesUri = "invaliddeleteuri";
            int codesVersion = 99;
            assertException(() -> resource.delete(codesUri, codesVersion),
                    new ResponseStatusExceptionMatcher(HttpStatus.INTERNAL_SERVER_ERROR));
        }

    }
    
    @Test
    public void savesCodesWithNewName() {
        String koodistoUri = "eisuhteitaviela1";
        String nimi = "uusinimi";
        int versio = 1;

        KoodistoDto codesToBeSaved = resource.getCodesByCodesUriAndVersion(koodistoUri, versio);
        assertEquals(Tila.HYVAKSYTTY, codesToBeSaved.getTila());
        assertNotEquals(nimi, codesToBeSaved.getMetadata().get(0).getNimi());

        codesToBeSaved.getMetadata().get(0).setNimi(nimi);
        resource.save(codesToBeSaved);

        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(koodistoUri, versio+1);
        assertEquals(Tila.LUONNOS, codes.getTila());
        assertEquals(nimi, codes.getMetadata().get(0).getNimi());
    }
    
    @Test
    public void savesCodesWithNewNameAndRelations() {
        String koodistoUri = "eisuhteitaviela1";
        String nimi = "uusinimi";
        int versio = 1;

        KoodistoDto codesToBeSaved = resource.getCodesByCodesUriAndVersion(koodistoUri, versio);
        assertEquals(0, codesToBeSaved.getIncludesCodes().size());
        assertEquals(0, codesToBeSaved.getWithinCodes().size());
        assertEquals(0, codesToBeSaved.getLevelsWithCodes().size());

        codesToBeSaved.getMetadata().get(0).setNimi(nimi);
        codesToBeSaved.getIncludesCodes().add(new RelationCodes("eisuhteitaviela2", 1, false, new HashMap<>()));
        codesToBeSaved.getWithinCodes().add(new RelationCodes("eisuhteitaviela3", 1, false, new HashMap<>()));
        codesToBeSaved.getLevelsWithCodes().add(new RelationCodes("eisuhteitaviela4", 1, false, new HashMap<>()));
        resource.save(codesToBeSaved);

        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(koodistoUri, versio+1);
        assertEquals(1, codes.getIncludesCodes().size());
        assertEquals(1, codes.getWithinCodes().size());
        assertEquals(1, codes.getLevelsWithCodes().size());
    }
    
    @Test
    public void returnsNoChangesToCodes() {
        assertEquals(MuutosTila.EI_MUUTOKSIA, resource.getChangesToCodes("moniaversioita", 3, false).muutosTila);
    }
    
    @Test
    public void returnsChangesToCodes() {
        assertEquals(MuutosTila.MUUTOKSIA, resource.getChangesToCodes("moniaversioita", 1, false).muutosTila);
    }
    
    @Test
    public void returnsNoChangesToCodesUsingDate() {
        assertEquals(MuutosTila.EI_MUUTOKSIA, resource.getChangesToCodesWithDate("moniaversioita", 20, 9, 2014, 0, 0, 0, false).muutosTila);
    }
    
    @Test
    public void returnsChangesToCodesUsingDate() {
        assertEquals(MuutosTila.MUUTOKSIA, resource.getChangesToCodesWithDate("moniaversioita", 20, 9, 2012, 0, 0, 0, false).muutosTila);
        assertEquals(MuutosTila.MUUTOKSIA, resource.getChangesToCodesWithDate("moniaversioita", 20, 9, 2013, 0, 0, 0, false).muutosTila);
    }
    
    @Test
    public void returnsChangesToCodesWithLotsOfChanges() {
        assertChanges(resource.getChangesToCodes("paljonmuutoksia", 1, false), 3, 1, 2, 2, 1, 1, Tila.LUONNOS, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges(resource.getChangesToCodes("paljonmuutoksia", 2, false), 3, 0, 2, 1, 1, 0, Tila.LUONNOS, 1, 1, 1, MuutosTila.MUUTOKSIA);
        assertChanges(resource.getChangesToCodes("paljonmuutoksia", 3, false), 3, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }
    
    @Test
    public void returnsChangesToCodesWithLotsOfChangesComparingAgainstLatestAcceptedKoodistoVersio() {
        assertChanges(resource.getChangesToCodes("paljonmuutoksia", 1, true), 2, 1, 0, 1, 0, 1, null, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges(resource.getChangesToCodes("paljonmuutoksia", 2, true), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
        assertChanges(resource.getChangesToCodes("paljonmuutoksia", 3, true), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }
    
    @Test
    public void returnsChangesToCodesWithLotsOfChangesUsingDate() {
        assertChanges(resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2012, 0, 0, 0, false), 3, 1, 2, 2, 1, 1, Tila.LUONNOS, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges(resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 5, 2014, 0, 0, 0, false), 3, 0, 2, 1, 1, 0, Tila.LUONNOS, 1, 1, 1, MuutosTila.MUUTOKSIA);
        assertChanges(resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2014, 0, 0, 0, false), 3, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }
    
    @Test
    public void returnsChangesToCodesWithLotsOfChangesComparingAgainstLatestAcceptedKoodistoVersioAndDate() {
        assertChanges(resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2012, 0, 0, 0, true), 2, 1, 0, 1, 0, 1, null, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges(resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 5, 2014, 0, 0, 0, true), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
        assertChanges(resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2014, 0, 0, 0, true), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }

    private void assertChanges(KoodistoChangesDto changes, int expectedVersio, int removedMetas, int changedMetas, int addedRelations, int passiveRelations, int removedRelations, Tila expectedTila, int addedCodeElements, int changedCodeElements, int removedCodeElements, MuutosTila muutosTila) {
        assertEquals(muutosTila, changes.muutosTila);
        assertEquals(expectedVersio, changes.viimeisinVersio.intValue());
        assertEquals(changedMetas, changes.muuttuneetTiedot.size());
        assertEquals(removedMetas, changes.poistuneetTiedot.size());
        assertEquals(addedRelations, changes.lisatytKoodistonSuhteet.size());
        assertEquals(passiveRelations, changes.passivoidutKoodistonSuhteet.size());
        assertEquals(removedRelations, changes.poistetutKoodistonSuhteet.size());
        assertEquals(expectedTila, changes.tila);
        assertEquals(addedCodeElements, changes.lisatytKoodit.size());
        assertEquals(changedCodeElements, changes.muuttuneetKoodit.size());
        assertEquals(removedCodeElements, changes.poistetutKoodit.size());
    }

    // UTILITIES
    // /////////

    private KoodistoDto createDTO(String koodistoUri, String codesGroupUri) {
        // Load dummy and replace relevant information
        KoodistoDto dto = resource.getCodesByCodesUriAndVersion("dummy", 1);
        dto.setKoodistoUri(koodistoUri);
        dto.setCodesGroupUri(codesGroupUri);
        dto.getMetadata().get(0).setNimi(koodistoUri);
        return dto;
    }

    private KoodistoDto newKoodistoDto(String koodistoRyhmaUri, String organisaatioOid,
                                       String koodistoUri, String nimiFi,
                                       LocalDate alkuPvm, LocalDate loppuPvm) {
        KoodistoDto dto = new KoodistoDto();
        dto.setCodesGroupUri(koodistoRyhmaUri);
        dto.setOrganisaatioOid(organisaatioOid);
        dto.setKoodistoUri(koodistoUri);
        KoodistoMetadata metadata = new KoodistoMetadata();
        metadata.setKieli(Kieli.FI);
        metadata.setNimi(nimiFi);
        dto.setMetadata(singletonList(metadata));
        dto.setVoimassaAlkuPvm(java.sql.Date.valueOf(alkuPvm));
        dto.setVoimassaLoppuPvm(java.sql.Date.valueOf(loppuPvm));
        return dto;
    }

    private KoodiDto newKoodiDto(KoodistoDto koodisto, String arvo, String nimiFi) {
        return newKoodiDto(koodisto, arvo, nimiFi, LocalDate.now());
    }

    private KoodiDto newKoodiDto(KoodistoDto koodisto, String arvo, String nimiFi, LocalDate alkuPvm) {
        KoodiDto dto = new KoodiDto();
        dto.setKoodiUri(String.format("%s_%s", koodisto.getKoodistoUri(), arvo));
        dto.setKoodiArvo(arvo);
        KoodiMetadata metadata = new KoodiMetadata();
        metadata.setKieli(Kieli.FI);
        metadata.setNimi(nimiFi);
        dto.setMetadata(singletonList(metadata));
        dto.setVoimassaAlkuPvm(java.sql.Date.valueOf(alkuPvm));
        return dto;
    }

    private String fileToString(File file) throws IllegalStateException {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
