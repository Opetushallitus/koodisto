package fi.vm.sade.koodisto.service.koodisto.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.sun.jersey.core.header.FormDataContentDisposition;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.dto.FileDto;
import fi.vm.sade.koodisto.dto.FileFormatDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.dto.KoodistoListDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.dto.KoodistoVersioListDto;
import fi.vm.sade.koodisto.model.Format;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
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
@DataSetLocation("classpath:test-data-codes-rest.xml")
public class CodesResourceTest {

    @Autowired
    private CodesResource resource;

    @Autowired
    private KoodiBusinessService service;

    @Test
    public void returns400OrNullIfQueryParamsAreMissing() {
        String nullString = null;
        KoodistoDto nullCodesDTO = null;

        assertResponse(resource.update(nullCodesDTO), 400);
        assertResponse(resource.insert(nullCodesDTO), 400);
        // assertNull(resource.listAllCodesGroups()); // No params
        // assertNull(resource.listAllCodesInAllCodeGroups()); // No params
        assertNull(resource.getCodesByCodesUri(nullString));
        assertNull(resource.getCodesByCodesUriAndVersion(nullString, 0));
        assertResponse(resource.uploadFile(null, null, nullString, nullString, nullString), 400);
        assertNull(resource.download(nullString, 0, null));
        assertResponse(resource.delete(nullString, 0), 400);
        assertNull(resource.download(nullString, 0, null));
    }

    @Test
    public void addsWithinRelationBetweenExistingcodes() {
        String parentUri = "eisuhteitaviela1";
        String childUri = "eisuhteitaviela2";
        KoodistoDto parentCodes = resource.getCodesByCodesUriAndVersion(parentUri, 1);
        KoodistoDto childCodes = resource.getCodesByCodesUriAndVersion(childUri, 1);

        assertEquals(0, parentCodes.getIncludesCodes().size());
        assertEquals(0, childCodes.getWithinCodes().size());

        resource.addRelation(parentUri, childUri, "SISALTYY");

        parentCodes = resource.getCodesByCodesUriAndVersion(parentUri, 1);
        childCodes = resource.getCodesByCodesUriAndVersion(childUri, 1);

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
        KoodistoDto parentCodes = resource.getCodesByCodesUriAndVersion(parentUri, 1);
        KoodistoDto childCodes = resource.getCodesByCodesUriAndVersion(childUri, 1);

        assertEquals(0, parentCodes.getLevelsWithCodes().size());
        assertEquals(0, childCodes.getLevelsWithCodes().size());

        resource.addRelation(parentUri, childUri, "RINNASTEINEN");

        parentCodes = resource.getCodesByCodesUriAndVersion(parentUri, 1);
        childCodes = resource.getCodesByCodesUriAndVersion(childUri, 1);

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
        assertResponse(resource.insert(codesToBeInserted), 201);

        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
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
        assertResponse(resource.insert(codesToBeInserted), 500);

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
        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(koodistoUri, 0);
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
        try {
            is = getClass().getClassLoader().getResourceAsStream("csv_example.csv");
            FormDataContentDisposition contentDispositionHeader = FormDataContentDisposition.name("file").fileName("test.csv").build();
            assertResponse(resource.uploadFile(is, contentDispositionHeader, fileFormat, fileEncoding, codesUri), 202);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
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
        try {
            is = getClass().getClassLoader().getResourceAsStream("jhs_xml_example.xml");
            FormDataContentDisposition contentDispositionHeader = FormDataContentDisposition.name("file").fileName("test.xml").build();
            assertResponse(resource.uploadFile(is, contentDispositionHeader, fileFormat, fileEncoding, codesUri), 202);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
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
        try {
            is = getClass().getClassLoader().getResourceAsStream("excel_example.xls");
            FormDataContentDisposition contentDispositionHeader = FormDataContentDisposition.name("file").fileName("test.xml").build();
            assertResponse(resource.uploadFile(is, contentDispositionHeader, fileFormat, fileEncoding, codesUri), 202);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(codesUri, 0);
        assertNotNull(codes);
        List<KoodiVersioWithKoodistoItem> koodis = service.getKoodisByKoodisto(codesUri, false);
        assertEquals("xlsfileuploaduri_arvo", koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());
    }

    @Test
    public void uploadsSameFileTwice() {
        InputStream is = null;
        String fileFormat = "CSV";
        String fileEncoding = "UTF-8";
        String codesUri = "csvfileuploaduri";
        try {
            is = getClass().getClassLoader().getResourceAsStream("csv_example.csv");
            FormDataContentDisposition contentDispositionHeader = FormDataContentDisposition.name("file").fileName("test.csv").build();
            assertResponse(resource.uploadFile(is, contentDispositionHeader, fileFormat, fileEncoding, codesUri), 202);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(codesUri, 0);
        assertNotNull(codes);
        List<KoodiVersioWithKoodistoItem> koodis = service.getKoodisByKoodisto(codesUri, false);
        assertEquals("csvfileuploaduri_arvo", koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());

        try {
            is = getClass().getClassLoader().getResourceAsStream("csv_example.csv");
            FormDataContentDisposition contentDispositionHeader = FormDataContentDisposition.name("file").fileName("test.csv").build();
            assertResponse(resource.uploadFile(is, contentDispositionHeader, fileFormat, fileEncoding, codesUri), 202);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Test
    public void failsToUploadInvalidFile() {
        InputStream is = null;
        String fileFormat = "CSV";
        String fileEncoding = "UTF-8";
        String codesUri = "csvfileuploaduri";
        try {
            is = new ByteArrayInputStream("Failure Of Files!".getBytes(Charset.defaultCharset()));
            FormDataContentDisposition contentDispositionHeader = FormDataContentDisposition.name("file").fileName("test.csv").build();
            assertResponse(resource.uploadFile(is, contentDispositionHeader, fileFormat, fileEncoding, codesUri), 500);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException ignore) {
                }
        }
    }

    @Test
    public void updatingCodes() {
        String koodistoUri = "updatekoodisto";

        KoodistoDto codes1 = resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
        codes1.getMetadata().get(0).setNimi("Päivitetty Testinimi");
        assertResponse(resource.update(codes1), 201);

        codes1 = resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
        KoodistoDto codes2 = resource.getCodesByCodesUriAndVersion(koodistoUri, 2);
        assertEquals("Update testi", codes1.getMetadata().get(0).getNimi());
        assertEquals("Päivitetty Testinimi", codes2.getMetadata().get(0).getNimi());
        assertNotNull(codes2.getPaivitysPvm());
    }

    @Test
    public void invalidUpdatingCodesFails() {
        String koodistoUri = "updatekoodisto";

        KoodistoDto codes1 = resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
        codes1.getMetadata().get(0).setNimi(null); // Invalid
        assertResponse(resource.update(codes1), 500);

        codes1 = resource.getCodesByCodesUriAndVersion(koodistoUri, 1);
        assertEquals("Update testi", codes1.getMetadata().get(0).getNimi());
    }

    @Test
    public void downloadsFiles() {
        {
            String codesUri = "filedownloaduri";
            int codesVersion = 1;
            String encoding = "Utf-8";
            Format fileFormat = Format.CSV;
            Response response = resource.download(codesUri, codesVersion, fileFormat, encoding);
            assertResponse(response, 200);
            String content = inputStreamToString(response.getEntity());
            assertTrue(content.contains("Description of downloaded code"));
            try {
                ((InputStream) response.getEntity()).close();
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
            String content = inputStreamToString(response.getEntity());
            assertTrue(content.contains("Description of downloaded code"));
            try {
                ((InputStream) response.getEntity()).close();
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
            assertNotNull(response.getEntity());
            try {
                ((InputStream) response.getEntity()).close();
            } catch (IOException ignore) {
            }
        }
    }

    @Test
    public void downloadsCSVFilesOld() {
        String codesUri = "filedownloaduri";
        int codesVersion = 1;
        FileFormatDto ffDto = new FileFormatDto();
        ffDto.setEncoding("UTF-8");
        ffDto.setFormat("CSV");
        FileDto fileDto = resource.download(codesUri, codesVersion, ffDto);

        assertTrue(fileDto.getData().contains("Description of downloaded code"));
    }

    @Test
    public void downloadsXMLFilesOld() {
        String codesUri = "filedownloaduri";
        int codesVersion = 1;
        FileFormatDto ffDto = new FileFormatDto();
        ffDto.setEncoding("UTF-8");
        ffDto.setFormat("JHS_XML");
        FileDto fileDto = resource.download(codesUri, codesVersion, ffDto);

        assertTrue(fileDto.getData().contains("Description of downloaded code"));
    }

    @Test
    public void downloadsXLSFilesOld() {
        String codesUri = "filedownloaduri";
        int codesVersion = 1;
        FileFormatDto ffDto = new FileFormatDto();
        ffDto.setEncoding("UTF-8");
        ffDto.setFormat("XLS");
        FileDto fileDto = resource.download(codesUri, codesVersion, ffDto);

        assertNotNull(fileDto.getData());
    }

    @Test
    public void downloadingInvaldiFilesFailsOld() {
        String codesUri = "invalidDownloadUri";
        int codesVersion = 1;
        FileFormatDto ffDto = new FileFormatDto();
        ffDto.setEncoding("");
        ffDto.setFormat("JHS_XML");
        FileDto fileDto = resource.download(codesUri, codesVersion, ffDto);

        assertNull(fileDto);
    }

    @Test
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
    public void savesCodesWithNewName() {
        String koodistoUri = "eisuhteitaviela1";
        String nimi = "uusinimi";
        int versio = 1;

        KoodistoDto codesToBeSaved = resource.getCodesByCodesUriAndVersion(koodistoUri, versio);
        assertEquals(Tila.HYVAKSYTTY, codesToBeSaved.getTila());
        assertFalse(nimi.equals(codesToBeSaved.getMetadata().get(0).getNimi()));

        codesToBeSaved.getMetadata().get(0).setNimi(nimi);
        assertResponse(resource.save(codesToBeSaved), 200);

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
        assertTrue(codesToBeSaved.getIncludesCodes().size() == 0);
        assertTrue(codesToBeSaved.getWithinCodes().size() == 0);
        assertTrue(codesToBeSaved.getLevelsWithCodes().size() == 0);

        codesToBeSaved.getMetadata().get(0).setNimi(nimi);
        codesToBeSaved.getIncludesCodes().add(new RelationCodes("eisuhteitaviela2", 1, false));
        codesToBeSaved.getWithinCodes().add(new RelationCodes("eisuhteitaviela3", 1, false));
        codesToBeSaved.getLevelsWithCodes().add(new RelationCodes("eisuhteitaviela4", 1, false));
        assertResponse(resource.save(codesToBeSaved), 200);

        KoodistoDto codes = resource.getCodesByCodesUriAndVersion(koodistoUri, versio+1);
        assertTrue(codes.getIncludesCodes().size() == 1);
        assertTrue(codes.getWithinCodes().size() == 1);
        assertTrue(codes.getLevelsWithCodes().size() == 1);
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

    private void assertResponse(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatus());
    }

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
}
