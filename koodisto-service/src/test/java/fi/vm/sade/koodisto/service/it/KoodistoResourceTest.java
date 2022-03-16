package fi.vm.sade.koodisto.service.it;

import fi.vm.sade.koodisto.dto.KoodiDto;
import fi.vm.sade.koodisto.dto.KoodistoDto;
import fi.vm.sade.koodisto.dto.KoodistoRyhmaListDto;
import fi.vm.sade.koodisto.resource.KoodistoResource;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@org.springframework.test.context.jdbc.Sql(
        scripts = "classpath:truncate_tables.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RunWith(SpringRunner.class)
public class KoodistoResourceTest {

    @Autowired
    private KoodistoResource koodistoResource;

    @Test
    public void testListAllKoodistoRyhmas() {
        List<KoodistoRyhmaListDto> ryhmas = koodistoResource.listAllKoodistoRyhmas();
        assertEquals(4, ryhmas.size());
    }

    @Test
    public void testGetKoodistoByUri() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 2;
        KoodistoDto koodisto = koodistoResource.getKoodistoByUri(koodistoUri, null);
        assertEquals(koodistoUri, koodisto.getKoodistoUri());
        assertEquals(koodistoVersio, koodisto.getVersio());
    }

    @Test
    public void testGetKoodistoByUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;
        KoodistoDto koodisto = koodistoResource.getKoodistoByUri(koodistoUri, koodistoVersio);
        assertEquals(koodistoUri, koodisto.getKoodistoUri());
        assertEquals(koodistoVersio, koodisto.getVersio());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetNonExistingKoodistoByUri() {
        final String koodistoUri = "ei-ole-olemassa";
        koodistoResource.getKoodistoByUri(koodistoUri, null);
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetNonExistingKoodistoByUriAndVersio() {
        final String koodistoUri = "ei-ole-olemassa";
        final int koodistoVersio = 1;
        koodistoResource.getKoodistoByUri(koodistoUri, koodistoVersio);
    }

    @Test
    public void testGetKoodisByKoodistoUri() {
        final String koodistoUri = "http://koodisto17";

        List<KoodiDto> koodis = koodistoResource.getKoodisByKoodisto(koodistoUri, null, false);
        assertEquals(2, koodis.size());
    }

    @Test
    public void testGetKoodisByKoodistoUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;

        List<KoodiDto> koodis =
                koodistoResource.getKoodisByKoodisto(koodistoUri, koodistoVersio, false);
        assertEquals(1, koodis.size());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByNonExistingKoodistoUri() {
        final String koodistoUri = "ei-ole-olemassa";
        koodistoResource.getKoodisByKoodisto(koodistoUri, null, false);
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByNonExistingKoodistoUriAndVersio() {
        final String koodistoUri = "ei-ole-olemassa";
        final int koodistoVersio = 1;
        koodistoResource.getKoodisByKoodisto(koodistoUri, koodistoVersio, false);
    }

    @Test
    public void testGetKoodisByArvoWithKoodistoUri() {
        final String koodistoUri = "http://koodisto17";
        final String koodiArvo = "28";

        final String koodiUri = "471";
        final int koodiVersio = 2;

        List<KoodiDto> koodis =
                koodistoResource.getKoodisByArvo(koodistoUri, koodiArvo, null);
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
                koodistoResource.getKoodisByArvo(koodistoUri, koodiArvo, koodistoVersio);
        assertEquals(1, koodis.size());

        KoodiDto koodi = koodis.get(0);
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByArvoWithNonExistingKoodistoUri() {
        final String koodistoUri = "ei-ole-olemassa";
        final String koodiArvo = "123";
        koodistoResource.getKoodisByArvo(koodistoUri, koodiArvo, null);
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByArvoWithNonExistingKoodistoUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 3;
        final String koodiArvo = "123";
        koodistoResource.getKoodisByArvo(koodistoUri, koodiArvo, koodistoVersio);
    }

    @Test
    public void testGetKoodiByUriWithKoodistoUri() {
        final String koodistoUri = "http://koodisto17";
        final String koodiUri = "471";
        final int koodiVersio = 2;

        KoodiDto koodi = koodistoResource.getKoodiByUri(koodistoUri, koodiUri, null);
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetKoodiByUriWithKoodistoUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;
        final String koodiUri = "471";
        final int koodiVersio = 1;

        KoodiDto koodi = koodistoResource.getKoodiByUri(koodistoUri, koodiUri, koodistoVersio);
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodiByUriWithNonExistingKoodistoUri() {
        final String koodistoUri = "ei-ole-olemassa";
        final String koodiUri = "471";
        koodistoResource.getKoodiByUri(koodistoUri, koodiUri, null);
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodiByUriWithNonExistingKoodistoUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 3;
        final String koodiUri = "471";
        koodistoResource.getKoodiByUri(koodistoUri, koodiUri, koodistoVersio);
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetKoodiByNonExistingUri() {
        final String koodistoUri = "http://koodisto17";
        final String koodiUri = "ei-ole-olemassa";
        koodistoResource.getKoodiByUri(koodistoUri, koodiUri, null);
    }

    @Test
    public void testGetAlakoodisByKoodiUri() {
        final String koodiUri = "473";

        List<KoodiDto> koodis = koodistoResource.getAlakoodis(koodiUri, null);
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

        List<KoodiDto> koodis = koodistoResource.getAlakoodis(koodiUri, koodiVersio);
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

        List<KoodiDto> koodis = koodistoResource.getYlakoodis(koodiUri, null);
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

        List<KoodiDto> koodis = koodistoResource.getYlakoodis(koodiUri, koodiVersio);
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

        List<KoodiDto> koodis = koodistoResource.getRinnasteinenKoodis(koodiUri, null);
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

        List<KoodiDto> koodis = koodistoResource.getRinnasteinenKoodis(koodiUri, koodiVersio);
        assertEquals(1, koodis.size());
        KoodiDto koodi = koodis.get(0);

        final String alakoodiUri = "474";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetAlakoodiByNonExistingKoodiUri() {
        final String koodiUri = "ei-ole-olemassa";
        koodistoResource.getAlakoodis(koodiUri, null);
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetAlakoodiByNonExistingKoodiUriAndVersio() {
            final String koodiUri = "475";
            final Integer koodiVersio = 2;
            koodistoResource.getAlakoodis(koodiUri, koodiVersio);
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetYlakoodiByNonExistingKoodiUri() {
        final String koodiUri = "ei-ole-olemassa";
        koodistoResource.getYlakoodis(koodiUri, null);
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetYlakoodiByNonExistingKoodiUriAndVersio() {
        final String koodiUri = "475";
        final Integer koodiVersio = 2;
        koodistoResource.getYlakoodis(koodiUri, koodiVersio);
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetRinnasteinenByNonExistingKoodiUri() {
        final String koodiUri = "ei-ole-olemassa";
        koodistoResource.getRinnasteinenKoodis(koodiUri, null);
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetRinnasteinenByNonExistingKoodiUriAndVersio() {
        final String koodiUri = "475";
        final Integer koodiVersio = 2;
        koodistoResource.getRinnasteinenKoodis(koodiUri, koodiVersio);
    }

    @Test
    public void testSearchKoodis() throws Exception {
        // all
        assertEquals(0, koodistoResource.searchKoodis(null, null, null, null, null, null).size()); // Empty search disabled for performance reasons
        // by koodiuri
        assertEquals(1, koodistoResource.searchKoodis(Collections.singletonList("475"), null, null, null, null, null).size());
        // by koodiarvo
        assertEquals(6, koodistoResource.searchKoodis(null, "3", null, null, null, null).size());
        // by tila
        assertEquals(0, koodistoResource.searchKoodis(null, null, Arrays.asList(TilaType.LUONNOS, TilaType.PASSIIVINEN), null, null, null).size());  // Empty search disabled for performance reasons
        assertEquals(1, koodistoResource.searchKoodis(null, "27", Arrays.asList(TilaType.LUONNOS, TilaType.PASSIIVINEN), null, null, null).size());
        // by validAtDate
        assertEquals(134, koodistoResource.searchKoodis(null, null, null, "2013-01-01", null, null).size());
        // by versio & versioselectiontype
        assertEquals(0, koodistoResource.searchKoodis(null, null, null, null, 2, SearchKoodisVersioSelectionType.SPECIFIC).size()); // Empty search disabled for performance reasons
        assertEquals(2, koodistoResource.searchKoodis(null, "versio 10", null, null, 10, SearchKoodisVersioSelectionType.SPECIFIC).size());
    }

    @Test
    public void testGetKoodisByKoodistoOnlyValidKoodis() {
        // all
        assertEquals(2, koodistoResource.getKoodisByKoodisto("http://paljon_versioita.fi/1", null, false).size());
        // only valid
        assertEquals(1, koodistoResource.getKoodisByKoodisto("http://paljon_versioita.fi/1", null, true).size());
    }

}
