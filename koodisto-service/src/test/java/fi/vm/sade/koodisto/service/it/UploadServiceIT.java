package fi.vm.sade.koodisto.service.it;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
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
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test") // konfiguroi mock authorizerin
@DataJpaTest
@DatabaseSetup("classpath:test-data.xml")
@Transactional
@WithMockUser("1.2.3.4.5")
public class UploadServiceIT {

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

    // Muutettu niin, että koodit ilman koodiUria hyväksytään [koodistoUri_koodiArvo] -urilla 26.3.14
    private void testUpload(ExportImportFormatType format, String testFile) throws IOException {
        final String koodistoUri = "http://koodisto16";
        final int koodistoVersio = 1;
        final TilaType koodistoTila = TilaType.HYVAKSYTTY;
        
        final int koodistoVersioAfter = 2;
        final TilaType koodistoTilaAfter = TilaType.LUONNOS;

        KoodistoType koodisto = getKoodistoByUri(koodistoUri);
        assertEquals("koodistoVersio", koodistoVersio, koodisto.getVersio());
        assertEquals("koodistoTila", koodistoTila, koodisto.getTila());

        assertEquals("getKoodisByKoodisto", 0, getKoodisByKoodisto(koodistoUri).size());

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(testFile);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(IOUtils.toByteArray(inputStream)));

        uploadService.upload(koodistoUri, format, "UTF-8", handler);

        koodisto = getKoodistoByUri(koodistoUri);
        assertEquals("koodistoVersioAfter", koodistoVersioAfter, koodisto.getVersio());
        assertEquals("koodistoTilaAfter", koodistoTilaAfter, koodisto.getTila());

        assertEquals("getKoodisByKoodistoAfter", 1, getKoodisByKoodisto(koodistoUri).size());
    }
    
    @Test
    public void testUploadCsv() throws IOException {
        testUpload(ExportImportFormatType.CSV, "csv_example.csv");
    }
    
    @Test
    public void testUploadJhsXml() throws IOException {
        testUpload(ExportImportFormatType.JHS_XML, "jhs_xml_example.xml");
    }

    @Test
    public void testUploadJhsXls() throws IOException {
        testUpload(ExportImportFormatType.XLS, "excel_example.xls");
    }

}
