package fi.vm.sade.koodisto.service.koodisto.rest;

import fi.vm.sade.koodisto.dto.*;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.Kieli;
import fi.vm.sade.koodisto.model.KoodistoMetadata;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.resource.CodesResource;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:test-data-codes-rest.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:truncate_tables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class CodesResourceTest {

    @Autowired
    private CodesResource resource;


    @Autowired
    private ResourceHelper helper;

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void returns400AndCorrectErrorCodeIfQueryParamsAreMissing() {
        String nullString = null;
        String blankString = "";
        KoodistoDto nullCodesDTO = null;

        //String stubString = "uri";
        //InputStream stubInputStream = IOUtils.toInputStream("stubfile");

        assertResponse(resource.update(nullCodesDTO), 400, "error.validation.codes");

        assertResponse(resource.insert(nullCodesDTO), 400, "error.validation.codes");

        assertResponse(resource.getCodesByCodesUri(nullString), 400, "error.validation.codesuri");
        assertResponse(resource.getCodesByCodesUri(blankString), 400, "error.validation.codesuri");

        assertResponse(resource.getCodesByCodesUriAndVersion(nullString, 0), 400, "error.validation.codesuri");
        assertResponse(resource.getCodesByCodesUriAndVersion(blankString, 0), 400, "error.validation.codesuri");

        // IE9 can not handle upload if server return 400 or 500
        /*assertResponse(resource.uploadFile(null, stubString, stubString, stubString), 202, "error.validation.file");
        assertResponse(resource.uploadFile(new Attachment("id", stubInputStream), nullString, stubString, stubString), 202, "error.validation.fileformat");
        assertResponse(resource.uploadFile(new Attachment("id", stubInputStream), blankString, stubString, stubString), 202, "error.validation.fileformat");
        assertResponse(resource.uploadFile(new Attachment("id", stubInputStream), stubString, nullString, stubString), 202, "error.validation.fileencoding");
        assertResponse(resource.uploadFile(new Attachment("id", stubInputStream), stubString, blankString, stubString), 202, "error.validation.fileencoding");
        assertResponse(resource.uploadFile(new Attachment("id", stubInputStream), stubString, stubString, nullString), 202, "error.validation.codesuri");
        assertResponse(resource.uploadFile(new Attachment("id", stubInputStream), stubString, stubString, blankString), 202, "error.validation.codesuri");

        assertResponse(resource.download(nullString, 0, Format.JHS_XML, stubString), 400, "error.validation.codesuri");
        assertResponse(resource.download(blankString, 0, Format.JHS_XML, stubString), 400, "error.validation.codesuri");
        assertResponse(resource.download(stubString, 0, null, stubString), 400, "error.validation.fileformat");
        assertResponse(resource.download(stubString, 0, Format.JHS_XML, nullString), 400, "error.validation.encoding");
        assertResponse(resource.download(stubString, 0, Format.JHS_XML, blankString), 400, "error.validation.encoding");
        */
        assertResponse(resource.delete(nullString, 0), 400, "error.validation.codesuri");
        assertResponse(resource.delete(blankString, 0), 400, "error.validation.codesuri");
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void addsWithinRelationBetweenExistingcodes() {
        String parentUri = "eisuhteitaviela1";
        String childUri = "eisuhteitaviela2";
        KoodistoDto parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getBody();
        KoodistoDto childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getBody();

        assertEquals(0, parentCodes.getIncludesCodes().size());
        assertEquals(0, childCodes.getWithinCodes().size());

        resource.addRelation(parentUri, childUri, "SISALTYY");

        parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getBody();
        childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getBody();

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
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void addsLevesWithRelationBetweenExistingcodes() {
        String parentUri = "eisuhteitaviela3";
        String childUri = "eisuhteitaviela4";
        KoodistoDto parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getBody();
        KoodistoDto childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getBody();

        assertEquals(0, parentCodes.getLevelsWithCodes().size());
        assertEquals(0, childCodes.getLevelsWithCodes().size());

        resource.addRelation(parentUri, childUri, "RINNASTEINEN");

        parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getBody();
        childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getBody();

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
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void removesRelationBetweenExistingcodes() {
        String parentUri = "sisaltyysuhde1";
        String childUri = "sisaltyysuhde2";
        KoodistoDto parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getBody();
        KoodistoDto childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getBody();

        assertEquals(1, parentCodes.getIncludesCodes().size());
        assertEquals(1, childCodes.getWithinCodes().size());

        resource.removeRelation(parentUri, childUri, "SISALTYY");

        parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getBody();
        childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getBody();

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
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void insertsNewCodes() {
        String koodistoUri = "inserttest";
        String codesGroupUri = "koodistojenlisaaminenkoodistoryhmaan";

        KoodistoDto codesToBeInserted = createDTO(koodistoUri, codesGroupUri);
        assertResponse(resource.insert(codesToBeInserted), 201);

        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getBody();
        assertEquals(koodistoUri, codes.getKoodistoUri());
        assertEquals(1, codes.getVersio());
        assertEquals(codesGroupUri, codes.getCodesGroupUri());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void invalidInsertCausesError() {
        String koodistoUri = "inserttest";
        String codesGroupUri = "koodistojenlisaaminenkoodistoryhmaan";

        KoodistoDto codesToBeInserted = createDTO(koodistoUri, codesGroupUri);
        codesToBeInserted.getMetadata().get(0).setKieli(null);
        codesToBeInserted.setVoimassaLoppuPvm(new Date());
        assertResponse(resource.insert(codesToBeInserted), 400);

    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
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
                "withinCodeElements", "includesCodeElements", "levelsWithCodeElements", "metadata");
    }

    @Test
    public void listsCodesByCodesUri() {
        String koodistoUri = "moniaversioita";
        KoodistoListDto koodistot = (KoodistoListDto) resource.getCodesByCodesUri(koodistoUri).getBody();
        assertEquals(3, koodistot.getKoodistoVersios().size());
    }

    @Test
    public void getLatestCodeByUri() {
        String koodistoUri = "moniaversioita";
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 0).getBody();
        assertEquals(3, codes.getVersio());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Transactional
    public void listCodes() {
        List<KoodistoRyhmaListDto> codes = (List<KoodistoRyhmaListDto>) resource.listAllCodesGroups().getBody();
        assertNotNull(codes);

        List<KoodistoVersioListDto> codes2 = (List<KoodistoVersioListDto>) resource.listAllCodesInAllCodeGroups().getBody();
        assertNotNull(codes2);

        // TODO:tarkempi assertointi
    }
    /*
    @Test
    public void uploadsCSVFiles() {
        InputStream is = null;
        String fileFormat = "CSV";
        String fileEncoding = "UTF-8";
        String codesUri = "csvfileuploaduri";
        assertResponse(resource.uploadFile(createAttachment("csv_example.csv"), fileFormat, fileEncoding, codesUri), 202);
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(codesUri, 0).getBody();
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
        assertResponse(resource.uploadFile(createAttachment("jhs_xml_example.xml"), fileFormat, fileEncoding, codesUri), 202);
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(codesUri, 0).getBody();
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
        assertResponse(resource.uploadFile(createAttachment("excel_example.xls"), fileFormat, fileEncoding, codesUri), 202);
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(codesUri, 0).getBody();
        assertNotNull(codes);
        List<KoodiVersioWithKoodistoItem> koodis = service.getKoodisByKoodisto(codesUri, false);
        assertEquals("xlsfileuploaduri_arvo", koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());
    }

    private Attachment createAttachment(String sourceUrl) {
        return new Attachment("id", new DataHandler(new TestDataSource(sourceUrl)), null);
    }

    @Test
    public void uploadsSameFileTwice() {
        String fileFormat = "CSV";
        String fileEncoding = "UTF-8";
        String codesUri = "csvfileuploaduri";

        assertResponse(resource.uploadFile(createAttachment("csv_example.csv"), fileFormat, fileEncoding, codesUri), 202);
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(codesUri, 0).getBody();
        assertNotNull(codes);
        List<KoodiVersioWithKoodistoItem> koodis = service.getKoodisByKoodisto(codesUri, false);
        assertEquals("csvfileuploaduri_arvo", koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());

        assertResponse(resource.uploadFile(createAttachment("csv_example.csv"), fileFormat, fileEncoding, codesUri), 202);
    }

    @Test
    public void failsToUploadInvalidFile() {
        InputStream is = null;
        String fileFormat = "CSV";
        String fileEncoding = "UTF-8";
        String codesUri = "csvfileuploaduri";
            // IE9 can not handle upload if server return 400 or 500
            assertResponse(resource.uploadFile(createFailureAttachment(), fileFormat, fileEncoding, codesUri), 202, "error.codes.importing.empty.file");
    }

    private Attachment createFailureAttachment() {
        return new Attachment("id", new DataHandler(new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream("Failure Of Files!".getBytes(Charset.defaultCharset()));
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }
        }), null);
    }
    */
    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void updatingCodes() {
        String koodistoUri = "updatekoodisto";

        KoodistoDto codes1 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getBody();
        codes1.getMetadata().get(0).setNimi("Päivitetty Testinimi");
        assertResponse(resource.update(codes1), 201);

        codes1 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getBody();
        KoodistoDto codes2 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 2).getBody();
        assertEquals("Update testi", codes1.getMetadata().get(0).getNimi());
        assertEquals("Päivitetty Testinimi", codes2.getMetadata().get(0).getNimi());
        assertNotNull(codes2.getPaivitysPvm());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void invalidUpdatingCodesFails() {
        String koodistoUri = "updatekoodisto";

        KoodistoDto codes1 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getBody();
        codes1.getMetadata().get(0).setNimi(null); // Invalid
        assertResponse(resource.update(codes1), 400);

        codes1 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getBody();
        assertEquals("Update testi", codes1.getMetadata().get(0).getNimi());
    }

   /* @Test
    public void downloadsFiles() {
        {
            String codesUri = "filedownloaduri";
            int codesVersion = 1;
            String encoding = "Utf-8";
            Format fileFormat = Format.CSV;
            Response response = resource.download(codesUri, codesVersion, fileFormat, encoding);
            assertResponse(response, 200);
            String content = inputStreamToString(response.getBody());
            assertTrue(content.contains("Description of downloaded code"));
            try {
                ((InputStream) response.getBody()).close();
            } catch (IOException ignore) {
            }
        }
        {
            String codesUri = "filedownloaduri";
            int codesVersion = 1;
            String encoding = "Utf-8";
            Format fileFormat = Format.JHS_XML;
            Response response = resource.download(codesUri, codesVersion, fileFormat, encoding);
            assertResponse(response, 200);
            String content = inputStreamToString(response.getBody());
            assertTrue(content.contains("Description of downloaded code"));
            try {
                ((InputStream) response.getBody()).close();
            } catch (IOException ignore) {
            }
        }
        {
            String codesUri = "filedownloaduri";
            int codesVersion = 1;
            String encoding = "Utf-8";
            Format fileFormat = Format.XLS;
            Response response = resource.download(codesUri, codesVersion, fileFormat, encoding);
            assertResponse(response, 200);
            assertNotNull(response.getBody());
            try {
                ((InputStream) response.getBody()).close();
            } catch (IOException ignore) {
            }
        }
    }
    */
    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void deleteCodes() {
        {
            String codesUri = "deletethisuri";
            int codesVersion = 1;
            assertNotNull(resource.getCodesByCodesUriAndVersion(codesUri, codesVersion));

            assertResponse(resource.delete(codesUri, codesVersion), 202);
        }
        {
            String codesUri = "invaliddeleteuri";
            int codesVersion = 99;

            assertResponse(resource.delete(codesUri, codesVersion), 500);
        }

    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void savesCodesWithNewName() {
        String koodistoUri = "eisuhteitaviela1";
        String nimi = "uusinimi";
        int versio = 1;

        KoodistoDto codesToBeSaved = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, versio).getBody();
        assertEquals(Tila.HYVAKSYTTY, codesToBeSaved.getTila());
        assertFalse(nimi.equals(codesToBeSaved.getMetadata().get(0).getNimi()));

        codesToBeSaved.getMetadata().get(0).setNimi(nimi);
        assertResponse(resource.save(codesToBeSaved), 200);

        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, versio+1).getBody();
        assertEquals(Tila.LUONNOS, codes.getTila());
        assertEquals(nimi, codes.getMetadata().get(0).getNimi());
    }

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void savesCodesWithNewNameAndRelations() {
        String koodistoUri = "eisuhteitaviela1";
        String nimi = "uusinimi";
        int versio = 1;

        KoodistoDto codesToBeSaved = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, versio).getBody();
        assertTrue(codesToBeSaved.getIncludesCodes().size() == 0);
        assertTrue(codesToBeSaved.getWithinCodes().size() == 0);
        assertTrue(codesToBeSaved.getLevelsWithCodes().size() == 0);

        codesToBeSaved.getMetadata().get(0).setNimi(nimi);
        codesToBeSaved.getIncludesCodes().add(new RelationCodes("eisuhteitaviela2", 1, false, new HashMap<>(), new HashMap<>()));
        codesToBeSaved.getWithinCodes().add(new RelationCodes("eisuhteitaviela3", 1, false, new HashMap<>(), new HashMap<>()));
        codesToBeSaved.getLevelsWithCodes().add(new RelationCodes("eisuhteitaviela4", 1, false, new HashMap<>(), new HashMap<>()));
        assertResponse(resource.save(codesToBeSaved), 200);

        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, versio+1).getBody();
        assertTrue(codes.getIncludesCodes().size() == 1);
        assertTrue(codes.getWithinCodes().size() == 1);
        assertTrue(codes.getLevelsWithCodes().size() == 1);
    }

    @Test
    public void returnsNoChangesToCodes() {
        assertEquals(MuutosTila.EI_MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodes("moniaversioita", 3, false).getBody()).getMuutosTila());
    }

    @Test
    public void returnsChangesToCodes() {
        assertEquals(MuutosTila.MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodes("moniaversioita", 1, false).getBody()).getMuutosTila());
    }

    @Test
    public void returnsNoChangesToCodesUsingDate() {
        assertEquals(MuutosTila.EI_MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodesWithDate("moniaversioita", 20, 9, 2014, 0, 0, 0, false).getBody()).getMuutosTila());
    }

    @Test
    public void returnsChangesToCodesUsingDate() {
        assertEquals(MuutosTila.MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodesWithDate("moniaversioita", 20, 9, 2012, 0, 0, 0, false).getBody()).getMuutosTila());
        assertEquals(MuutosTila.MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodesWithDate("moniaversioita", 20, 9, 2013, 0, 0, 0, false).getBody()).getMuutosTila());
    }

    @Test
    public void returnsChangesToCodesWithLotsOfChanges() {
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 1, false).getBody(), 3, 1, 2, 2, 1, 1, Tila.LUONNOS, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 2, false).getBody(), 3, 0, 2, 1, 1, 0, Tila.LUONNOS, 1, 1, 1, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 3, false).getBody(), 3, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }

    @Test
    public void returnsChangesToCodesWithLotsOfChangesComparingAgainstLatestAcceptedKoodistoVersio() {
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 1, true).getBody(), 2, 1, 0, 1, 0, 1, null, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 2, true).getBody(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 3, true).getBody(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }

    @Test
    public void returnsChangesToCodesWithLotsOfChangesUsingDate() {
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2012, 0, 0, 0, false).getBody(), 3, 1, 2, 2, 1, 1, Tila.LUONNOS, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 5, 2014, 0, 0, 0, false).getBody(), 3, 0, 2, 1, 1, 0, Tila.LUONNOS, 1, 1, 1, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2014, 0, 0, 0, false).getBody(), 3, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }

    @Test
    public void returnsChangesToCodesWithLotsOfChangesComparingAgainstLatestAcceptedKoodistoVersioAndDate() {
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2012, 0, 0, 0, true).getBody(), 2, 1, 0, 1, 0, 1, null, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 5, 2014, 0, 0, 0, true).getBody(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2014, 0, 0, 0, true).getBody(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }

    private void assertChanges(KoodistoChangesDto changes, int expectedVersio, int removedMetas, int changedMetas, int addedRelations, int passiveRelations, int removedRelations, Tila expectedTila, int addedCodeElements, int changedCodeElements, int removedCodeElements, MuutosTila muutosTila) {
        assertEquals(muutosTila, changes.getMuutosTila());
        assertEquals(expectedVersio, changes.getViimeisinVersio().intValue());
        assertEquals(changedMetas, changes.getMuuttuneetTiedot().size());
        assertEquals(removedMetas, changes.getPoistuneetTiedot().size());
        assertEquals(addedRelations, changes.getLisatytKoodistonSuhteet().size());
        assertEquals(passiveRelations, changes.getPassivoidutKoodistonSuhteet().size());
        assertEquals(removedRelations, changes.getPoistetutKoodistonSuhteet().size());
        assertEquals(expectedTila, changes.getTila());
        assertEquals(addedCodeElements, changes.getLisatytKoodit().size());
        assertEquals(changedCodeElements, changes.getMuuttuneetKoodit().size());
        assertEquals(removedCodeElements, changes.getPoistetutKoodit().size());
    }

    // UTILITIES
    // /////////

    private KoodistoDto createDTO(String koodistoUri, String codesGroupUri) {
        // Load dummy and replace relevant information
        KoodistoDto dto = (KoodistoDto) resource.getCodesByCodesUriAndVersion("dummy", 1).getBody();
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
        KoodiMetadataDto metadata = new KoodiMetadataDto();
        metadata.setKieli(Kieli.FI);
        metadata.setNimi(nimiFi);
        dto.setMetadata(singletonList(metadata));
        dto.setVoimassaAlkuPvm(java.sql.Date.valueOf(alkuPvm));
        return dto;
    }

    private void assertResponse(ResponseEntity response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCodeValue());
    }

    private void assertResponse(ResponseEntity response, int expectedStatus, Object expectedEntity) {
        assertResponse(response, expectedStatus);
        assertEquals(expectedEntity, response.getBody());
    }
    /* 
    private String inputStreamToString(Object entity) {
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy((InputStream) entity, writer);
            return writer.toString();
        } catch (IOException e) {
            fail();
        }
        return null;
    }
    
     */

    class TestDataSource implements DataSource {
        private final String url;

        public TestDataSource(String url) {
            this.url = url;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return getClass().getClassLoader().getResourceAsStream(this.url);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    }
}
