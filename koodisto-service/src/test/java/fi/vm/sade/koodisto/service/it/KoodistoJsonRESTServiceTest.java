package fi.vm.sade.koodisto.service.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.model.JsonViews;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.impl.KoodistoJsonRESTService;
import fi.vm.sade.koodisto.service.provider.ObjectMapperProvider;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodistoJsonRESTServiceTest {

    @Autowired
    private KoodistoJsonRESTService koodistoJsonRESTService;

    private ObjectMapper mapper = new ObjectMapperProvider().locateMapper(KoodistoJsonRESTService.class, MediaType.APPLICATION_JSON_TYPE);


    @Test
    public void testListAllKoodistoRyhmas() throws IOException {
        List<KoodistoRyhmaListDto> ryhmas = koodistoJsonRESTService.listAllKoodistoRyhmas();
        assertEquals(4, ryhmas.size());

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(ryhmas);
    }

    @Test
    public void testGetKoodistoByUri() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 2;
        KoodistoDto koodisto = koodistoJsonRESTService.getKoodistoByUri(koodistoUri, null);
        assertEquals(koodistoUri, koodisto.getKoodistoUri());
        assertEquals(koodistoVersio, koodisto.getVersio());
    }

    @Test
    public void testGetKoodistoByUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;
        KoodistoDto koodisto = koodistoJsonRESTService.getKoodistoByUri(koodistoUri, koodistoVersio);
        assertEquals(koodistoUri, koodisto.getKoodistoUri());
        assertEquals(koodistoVersio, koodisto.getVersio());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetNonExistingKoodistoByUri() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            koodistoJsonRESTService.getKoodistoByUri(koodistoUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetNonExistingKoodistoByUriAndVersio() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            final int koodistoVersio = 1;
            koodistoJsonRESTService.getKoodistoByUri(koodistoUri, koodistoVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetKoodisByKoodistoUri() {
        final String koodistoUri = "http://koodisto17";

        List<KoodiDto> koodis = koodistoJsonRESTService.getKoodisByKoodisto(koodistoUri, null, false);
        assertEquals(2, koodis.size());
    }

    @Test
    public void testGetKoodisByKoodistoUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;

        List<KoodiDto> koodis =
                koodistoJsonRESTService.getKoodisByKoodisto(koodistoUri, koodistoVersio, false);
        assertEquals(1, koodis.size());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByNonExistingKoodistoUri() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            koodistoJsonRESTService.getKoodisByKoodisto(koodistoUri, null, false);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByNonExistingKoodistoUriAndVersio() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            final int koodistoVersio = 1;
            koodistoJsonRESTService.getKoodisByKoodisto(koodistoUri, koodistoVersio, false);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetKoodisByArvoWithKoodistoUri() {
        final String koodistoUri = "http://koodisto17";
        final String koodiArvo = "28";

        final String koodiUri = "471";
        final int koodiVersio = 2;

        List<KoodiDto> koodis =
                koodistoJsonRESTService.getKoodisByArvo(koodistoUri, koodiArvo, null);
        assertEquals(1, koodis.size());

        KoodiDto koodi = koodis.get(0);
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetKoodisByArvoWithKoodistoUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;
        final String koodiArvo = "28";

        final String koodiUri = "471";
        final int koodiVersio = 1;

        List<KoodiDto> koodis =
                koodistoJsonRESTService.getKoodisByArvo(koodistoUri, koodiArvo, koodistoVersio);
        assertEquals(1, koodis.size());

        KoodiDto koodi = koodis.get(0);
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByArvoWithNonExistingKoodistoUri() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            final String koodiArvo = "123";
            koodistoJsonRESTService.getKoodisByArvo(koodistoUri, koodiArvo, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByArvoWithNonExistingKoodistoUriAndVersio() throws Throwable {
        try {
            final String koodistoUri = "http://koodisto17";
            final int koodistoVersio = 3;
            final String koodiArvo = "123";
            koodistoJsonRESTService.getKoodisByArvo(koodistoUri, koodiArvo, koodistoVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetKoodiByUriWithKoodistoUri() {
        final String koodistoUri = "http://koodisto17";
        final String koodiUri = "471";
        final int koodiVersio = 2;

        KoodiDto koodi = koodistoJsonRESTService.getKoodiByUri(koodistoUri, koodiUri, null);
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetKoodiByUriWithKoodistoUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;
        final String koodiUri = "471";
        final int koodiVersio = 1;

        KoodiDto koodi = koodistoJsonRESTService.getKoodiByUri(koodistoUri, koodiUri, koodistoVersio);
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodiByUriWithNonExistingKoodistoUri() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            final String koodiUri = "471";
            koodistoJsonRESTService.getKoodiByUri(koodistoUri, koodiUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodiByUriWithNonExistingKoodistoUriAndVersio() throws Throwable {
        try {
            final String koodistoUri = "http://koodisto17";
            final int koodistoVersio = 3;
            final String koodiUri = "471";
            koodistoJsonRESTService.getKoodiByUri(koodistoUri, koodiUri, koodistoVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetKoodiByNonExistingUri() throws Throwable {
        try {
            final String koodistoUri = "http://koodisto17";
            final String koodiUri = "ei-ole-olemassa";
            koodistoJsonRESTService.getKoodiByUri(koodistoUri, koodiUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetAlakoodisByKoodiUri() {
        final String koodiUri = "473";

        List<KoodiDto> koodis = koodistoJsonRESTService.getAlakoodis(koodiUri, null);
        assertEquals(1, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetAlakoodisByKoodiUriAndVersio() {
        final String koodiUri = "473";
        final Integer koodiVersio = 1;

        List<KoodiDto> koodis = koodistoJsonRESTService.getAlakoodis(koodiUri, koodiVersio);
        assertEquals(1, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetYlakoodisByKoodiUri() {
        final String koodiUri = "474";

        List<KoodiDto> koodis = koodistoJsonRESTService.getYlakoodis(koodiUri, null);
        assertEquals(2, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetYlakoodisByKoodiUriAndVersio() {
        final String koodiUri = "474";
        final Integer koodiVersio = 1;

        List<KoodiDto> koodis = koodistoJsonRESTService.getYlakoodis(koodiUri, koodiVersio);
        assertEquals(2, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetRinnasteinenByKoodiUri() {
        final String koodiUri = "475";

        List<KoodiDto> koodis = koodistoJsonRESTService.getRinnasteinenKoodis(koodiUri, null);
        assertEquals(1, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "474";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetRinnasteinenByKoodiUriAndVersio() {
        final String koodiUri = "475";
        final Integer koodiVersio = 1;

        List<KoodiDto> koodis = koodistoJsonRESTService.getRinnasteinenKoodis(koodiUri, koodiVersio);
        assertEquals(1, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "474";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetAlakoodiByNonExistingKoodiUri() throws Throwable {
        try {
            final String koodiUri = "ei-ole-olemassa";
            koodistoJsonRESTService.getAlakoodis(koodiUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetAlakoodiByNonExistingKoodiUriAndVersio() throws Throwable {
        try {
            final String koodiUri = "475";
            final Integer koodiVersio = 2;
            koodistoJsonRESTService.getAlakoodis(koodiUri, koodiVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetYlakoodiByNonExistingKoodiUri() throws Throwable {
        try {
            final String koodiUri = "ei-ole-olemassa";
            koodistoJsonRESTService.getYlakoodis(koodiUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetYlakoodiByNonExistingKoodiUriAndVersio() throws Throwable {
        try {
            final String koodiUri = "475";
            final Integer koodiVersio = 2;
            koodistoJsonRESTService.getYlakoodis(koodiUri, koodiVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetRinnasteinenByNonExistingKoodiUri() throws Throwable {
        try {
            final String koodiUri = "ei-ole-olemassa";
            koodistoJsonRESTService.getRinnasteinenKoodis(koodiUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetRinnasteinenByNonExistingKoodiUriAndVersio() throws Throwable {
        try {
            final String koodiUri = "475";
            final Integer koodiVersio = 2;
            koodistoJsonRESTService.getRinnasteinenKoodis(koodiUri, koodiVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testSearchKoodis() throws Exception {
        // all
        assertEquals(0, koodistoJsonRESTService.searchKoodis(null, null, null, null, null, null).size()); // Empty search disabled for performance reasons
        // by koodiuri
        assertEquals(1, koodistoJsonRESTService.searchKoodis(Arrays.asList("475"), null, null, null, null, null).size());
        // by koodiarvo
        assertEquals(6, koodistoJsonRESTService.searchKoodis(null, "3", null, null, null, null).size());
        // by tila
        assertEquals(0, koodistoJsonRESTService.searchKoodis(null, null, Arrays.asList(TilaType.LUONNOS, TilaType.PASSIIVINEN), null, null, null).size());  // Empty search disabled for performance reasons
        assertEquals(1, koodistoJsonRESTService.searchKoodis(null, "27", Arrays.asList(TilaType.LUONNOS, TilaType.PASSIIVINEN), null, null, null).size());
        // by validAtDate
        assertEquals(134, koodistoJsonRESTService.searchKoodis(null, null, null, "2013-01-01", null, null).size());
        // by versio & versioselectiontype
        assertEquals(0, koodistoJsonRESTService.searchKoodis(null, null, null, null, 2, SearchKoodisVersioSelectionType.SPECIFIC).size()); // Empty search disabled for performance reasons
        assertEquals(2, koodistoJsonRESTService.searchKoodis(null, "versio 10", null, null, 10, SearchKoodisVersioSelectionType.SPECIFIC).size());
    }

    @Test
    public void testGetKoodisByKoodistoOnlyValidKoodis() throws Exception {
        // all
        assertEquals(2, koodistoJsonRESTService.getKoodisByKoodisto("http://paljon_versioita.fi/1", null, false).size());
        // only valid
        assertEquals(1, koodistoJsonRESTService.getKoodisByKoodisto("http://paljon_versioita.fi/1", null, true).size());
    }

}
