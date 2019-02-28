package fi.vm.sade.koodisto.service.koodisto.rest;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.dto.*;
import fi.vm.sade.koodisto.dto.KoodistoDto.RelationCodes;
import fi.vm.sade.koodisto.model.Format;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.changes.MuutosTila;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data-codes-rest.xml")
@WithMockUser("1.2.3.4.5")
public class CodesResourceTest {

    @Autowired
    private CodesResource resource;

    @Autowired
    private KoodiBusinessService service;

    @Test
    public void returns400AndCorrectErrorCodeIfQueryParamsAreMissing() {
        String nullString = null;
        String blankString = "";
        KoodistoDto nullCodesDTO = null;
        
        String stubString = "uri";
        InputStream stubInputStream = IOUtils.toInputStream("stubfile");

        assertResponse(resource.update(nullCodesDTO), 400, "error.validation.codes");
        
        assertResponse(resource.insert(nullCodesDTO), 400, "error.validation.codes");
        
        assertResponse(resource.getCodesByCodesUri(nullString), 400, "error.validation.codesuri");
        assertResponse(resource.getCodesByCodesUri(blankString), 400, "error.validation.codesuri");
        
        assertResponse(resource.getCodesByCodesUriAndVersion(nullString, 0), 400, "error.validation.codesuri");
        assertResponse(resource.getCodesByCodesUriAndVersion(blankString, 0), 400, "error.validation.codesuri");
        
        // IE9 can not handle upload if server return 400 or 500
        assertResponse(resource.uploadFile(null, stubString, stubString, stubString), 202, "error.validation.file");
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

        assertResponse(resource.delete(nullString, 0), 400, "error.validation.codesuri");
        assertResponse(resource.delete(blankString, 0), 400, "error.validation.codesuri");
    }

    @Test
    public void addsWithinRelationBetweenExistingcodes() {
        String parentUri = "eisuhteitaviela1";
        String childUri = "eisuhteitaviela2";
        KoodistoDto parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getEntity();
        KoodistoDto childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getEntity();

        assertEquals(0, parentCodes.getIncludesCodes().size());
        assertEquals(0, childCodes.getWithinCodes().size());

        resource.addRelation(parentUri, childUri, "SISALTYY");

        parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getEntity();
        childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getEntity();

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
        KoodistoDto parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getEntity();
        KoodistoDto childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getEntity();

        assertEquals(0, parentCodes.getLevelsWithCodes().size());
        assertEquals(0, childCodes.getLevelsWithCodes().size());

        resource.addRelation(parentUri, childUri, "RINNASTEINEN");

        parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getEntity();
        childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getEntity();

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
        KoodistoDto parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getEntity();
        KoodistoDto childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getEntity();

        assertEquals(1, parentCodes.getIncludesCodes().size());
        assertEquals(1, childCodes.getWithinCodes().size());

        resource.removeRelation(parentUri, childUri, "SISALTYY");

        parentCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(parentUri, 1).getEntity();
        childCodes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(childUri, 1).getEntity();

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

        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getEntity();
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
        assertResponse(resource.insert(codesToBeInserted), 400);

    }

    @Test
    public void listsCodesByCodesUri() {
        String koodistoUri = "moniaversioita";
        KoodistoListDto koodistot = (KoodistoListDto) resource.getCodesByCodesUri(koodistoUri).getEntity();
        assertEquals(3, koodistot.getKoodistoVersios().size());
    }

    @Test
    public void getLatestCodeByUri() {
        String koodistoUri = "moniaversioita";
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 0).getEntity();
        assertEquals(3, codes.getVersio());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void listCodes() {
        List<KoodistoRyhmaListDto> codes = (List<KoodistoRyhmaListDto>) resource.listAllCodesGroups().getEntity();
        assertNotNull(codes);

        List<KoodistoVersioListDto> codes2 = (List<KoodistoVersioListDto>) resource.listAllCodesInAllCodeGroups().getEntity();
        assertNotNull(codes2);

        // TODO:tarkempi assertointi
    }

    @Test
    public void uploadsCSVFiles() {
        InputStream is = null;
        String fileFormat = "CSV";
        String fileEncoding = "UTF-8";
        String codesUri = "csvfileuploaduri";
        assertResponse(resource.uploadFile(createAttachment("csv_example.csv"), fileFormat, fileEncoding, codesUri), 202);
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(codesUri, 0).getEntity();
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
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(codesUri, 0).getEntity();
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
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(codesUri, 0).getEntity();
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
        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(codesUri, 0).getEntity();
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

    @Test
    public void updatingCodes() {
        String koodistoUri = "updatekoodisto";

        KoodistoDto codes1 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getEntity();
        codes1.getMetadata().get(0).setNimi("Päivitetty Testinimi");
        assertResponse(resource.update(codes1), 201);

        codes1 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getEntity();
        KoodistoDto codes2 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 2).getEntity();
        assertEquals("Update testi", codes1.getMetadata().get(0).getNimi());
        assertEquals("Päivitetty Testinimi", codes2.getMetadata().get(0).getNimi());
        assertNotNull(codes2.getPaivitysPvm());
    }

    @Test
    public void invalidUpdatingCodesFails() {
        String koodistoUri = "updatekoodisto";

        KoodistoDto codes1 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getEntity();
        codes1.getMetadata().get(0).setNimi(null); // Invalid
        assertResponse(resource.update(codes1), 400);

        codes1 = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, 1).getEntity();
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

        KoodistoDto codesToBeSaved = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, versio).getEntity();
        assertEquals(Tila.HYVAKSYTTY, codesToBeSaved.getTila());
        assertFalse(nimi.equals(codesToBeSaved.getMetadata().get(0).getNimi()));

        codesToBeSaved.getMetadata().get(0).setNimi(nimi);
        assertResponse(resource.save(codesToBeSaved), 200);

        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, versio+1).getEntity();
        assertEquals(Tila.LUONNOS, codes.getTila());
        assertEquals(nimi, codes.getMetadata().get(0).getNimi());
    }
    
    @Test
    public void savesCodesWithNewNameAndRelations() {
        String koodistoUri = "eisuhteitaviela1";
        String nimi = "uusinimi";
        int versio = 1;

        KoodistoDto codesToBeSaved = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, versio).getEntity();
        assertTrue(codesToBeSaved.getIncludesCodes().size() == 0);
        assertTrue(codesToBeSaved.getWithinCodes().size() == 0);
        assertTrue(codesToBeSaved.getLevelsWithCodes().size() == 0);

        codesToBeSaved.getMetadata().get(0).setNimi(nimi);
        codesToBeSaved.getIncludesCodes().add(new RelationCodes("eisuhteitaviela2", 1, false, new HashMap<>()));
        codesToBeSaved.getWithinCodes().add(new RelationCodes("eisuhteitaviela3", 1, false, new HashMap<>()));
        codesToBeSaved.getLevelsWithCodes().add(new RelationCodes("eisuhteitaviela4", 1, false, new HashMap<>()));
        assertResponse(resource.save(codesToBeSaved), 200);

        KoodistoDto codes = (KoodistoDto) resource.getCodesByCodesUriAndVersion(koodistoUri, versio+1).getEntity();
        assertTrue(codes.getIncludesCodes().size() == 1);
        assertTrue(codes.getWithinCodes().size() == 1);
        assertTrue(codes.getLevelsWithCodes().size() == 1);
    }
    
    @Test
    public void returnsNoChangesToCodes() {
        assertEquals(MuutosTila.EI_MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodes("moniaversioita", 3, false).getEntity()).muutosTila);
    }
    
    @Test
    public void returnsChangesToCodes() {
        assertEquals(MuutosTila.MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodes("moniaversioita", 1, false).getEntity()).muutosTila);
    }
    
    @Test
    public void returnsNoChangesToCodesUsingDate() {
        assertEquals(MuutosTila.EI_MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodesWithDate("moniaversioita", 20, 9, 2014, 0, 0, 0, false).getEntity()).muutosTila);
    }
    
    @Test
    public void returnsChangesToCodesUsingDate() {
        assertEquals(MuutosTila.MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodesWithDate("moniaversioita", 20, 9, 2012, 0, 0, 0, false).getEntity()).muutosTila);
        assertEquals(MuutosTila.MUUTOKSIA, ((KoodistoChangesDto)resource.getChangesToCodesWithDate("moniaversioita", 20, 9, 2013, 0, 0, 0, false).getEntity()).muutosTila);
    }
    
    @Test
    public void returnsChangesToCodesWithLotsOfChanges() {
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 1, false).getEntity(), 3, 1, 2, 2, 1, 1, Tila.LUONNOS, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 2, false).getEntity(), 3, 0, 2, 1, 1, 0, Tila.LUONNOS, 1, 1, 1, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 3, false).getEntity(), 3, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }
    
    @Test
    public void returnsChangesToCodesWithLotsOfChangesComparingAgainstLatestAcceptedKoodistoVersio() {
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 1, true).getEntity(), 2, 1, 0, 1, 0, 1, null, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 2, true).getEntity(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodes("paljonmuutoksia", 3, true).getEntity(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }
    
    @Test
    public void returnsChangesToCodesWithLotsOfChangesUsingDate() {
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2012, 0, 0, 0, false).getEntity(), 3, 1, 2, 2, 1, 1, Tila.LUONNOS, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 5, 2014, 0, 0, 0, false).getEntity(), 3, 0, 2, 1, 1, 0, Tila.LUONNOS, 1, 1, 1, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2014, 0, 0, 0, false).getEntity(), 3, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
    }
    
    @Test
    public void returnsChangesToCodesWithLotsOfChangesComparingAgainstLatestAcceptedKoodistoVersioAndDate() {
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2012, 0, 0, 0, true).getEntity(), 2, 1, 0, 1, 0, 1, null, 1, 1, 0, MuutosTila.MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 5, 2014, 0, 0, 0, true).getEntity(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
        assertChanges((KoodistoChangesDto) resource.getChangesToCodesWithDate("paljonmuutoksia", 20, 9, 2014, 0, 0, 0, true).getEntity(), 2, 0, 0, 0, 0, 0, null, 0, 0, 0, MuutosTila.EI_MUUTOKSIA);
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
        KoodistoDto dto = (KoodistoDto) resource.getCodesByCodesUriAndVersion("dummy", 1).getEntity();
        dto.setKoodistoUri(koodistoUri);
        dto.setCodesGroupUri(codesGroupUri);
        dto.getMetadata().get(0).setNimi(koodistoUri);
        return dto;
    }

    private void assertResponse(Response response, int expectedStatus) {
        assertEquals(expectedStatus, response.getStatus());
    }

    private void assertResponse(Response response, int expectedStatus, Object expectedEntity) {
        assertResponse(response, expectedStatus);
        assertEquals(expectedEntity, response.getEntity());
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
