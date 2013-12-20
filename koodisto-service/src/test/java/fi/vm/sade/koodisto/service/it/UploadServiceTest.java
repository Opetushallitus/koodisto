package fi.vm.sade.koodisto.service.it;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.UploadService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.ByteArrayDataSource;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
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

import javax.activation.DataHandler;
import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: kwuoti
 * Date: 11.4.2013
 * Time: 15.24
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class UploadServiceTest {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private KoodiService koodiService;

    private KoodistoType getKoodistoByUri(String koodistoUri) {

        SearchKoodistosCriteriaType searchCriteria = KoodistoServiceSearchCriteriaBuilder
                .latestKoodistoByUri(koodistoUri);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchCriteria);
        if (koodistos.size() != 1) {
            throw new RuntimeException("Failing.");
        }

        return koodistos.get(0);
    }

    private List<KoodiType> getKoodisByKoodisto(String koodistoUri) {
        SearchKoodisByKoodistoCriteriaType searchData = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUri(koodistoUri);
        return koodiService.searchKoodisByKoodisto(searchData);
    }

    private void testUpload(ExportImportFormatType format, String testFile) throws IOException {
        final String koodistoUri = "http://koodisto16";
        final int koodistoVersio = 1;
        final TilaType koodistoTila = TilaType.HYVAKSYTTY;

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(koodistoVersio, koodisto.getVersio());
        assertEquals(koodistoTila, koodisto.getTila());

        assertEquals(0, getKoodisByKoodisto(koodistoUri).size());

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(testFile);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(convertStreamToString(inputStream).getBytes()));

        uploadService.upload(koodistoUri, format, "UTF-8", handler);

        koodisto = getKoodistoByUri(koodistoUri);
        assertEquals(koodistoVersio + 1, koodisto.getVersio());
        assertEquals(TilaType.LUONNOS, koodisto.getTila());

        assertEquals(1, getKoodisByKoodisto(koodistoUri).size());
    }
    
    @Test
    public void testUploadCsv() throws IOException {
        testUpload(ExportImportFormatType.CSV, "csv_example.csv");
    }
    
    @Test
    public void testUploadJhsXml() throws IOException {
        testUpload(ExportImportFormatType.JHS_XML, "jhs_xml_example.xml");
    }

    public static final String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
