package fi.vm.sade.koodisto.service.it;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.koodisto.service.DownloadService;
import fi.vm.sade.koodisto.service.types.common.ExportImportFormatType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;

import static org.junit.Assert.assertNotNull;
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
@Transactional
public class DownloadServiceTest {

    @Autowired
    private DownloadService downloadService;

    @Test
    public void testDownloadCsv() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        final int koodistoVersio = 1;

        DataHandler handler = downloadService.download(koodistoUri, koodistoVersio, ExportImportFormatType.CSV, "UTF-8");
        assertNotNull(handler);
    }

    @Test
    public void testDownloadJhsXml() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        final int koodistoVersio = 1;

        DataHandler handler = downloadService.download(koodistoUri, koodistoVersio, ExportImportFormatType.JHS_XML, "UTF-8");
        assertNotNull(handler);
    }
}
