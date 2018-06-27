package fi.vm.sade.koodisto.service.it;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoVersioSelectionType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
@Transactional
public class KoodiServiceTest {

    @Autowired
    private KoodiService koodiService;

    @Test
    public void searchSingleKoodiByUri() {
        final String notExistsUri = "ei-ole-olemassa";

        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(notExistsUri);
        List<KoodiType> koodis = koodiService.searchKoodis(searchType);
        assertEquals(0, koodis.size());

        final String koodiUri = "410";
        final String koodistoUri = "http://testikoodisto.fi";
        final String organisaatioOid = "1.2.2004.4";
        searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(koodiUri);

        koodis = koodiService.searchKoodis(searchType);
        assertEquals(1, koodis.size());
        assertEquals(2, koodis.get(0).getVersio());
        assertEquals(koodiUri, koodis.get(0).getKoodiUri());
        assertEquals(koodistoUri, koodis.get(0).getKoodisto().getKoodistoUri());
        assertEquals(organisaatioOid, koodis.get(0).getKoodisto().getOrganisaatioOid());
    }

    @Test
    public void searchSingleKoodiByUriAndVersion() {
        final String uri = "410";
        // version 150 does not exist
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, 150);
        List<KoodiType> koodis = koodiService.searchKoodis(searchType);
        assertEquals(0, koodis.size());

        searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, 1);
        koodis = koodiService.searchKoodis(searchType);
        assertEquals(1, koodis.size());
        assertEquals(1, koodis.get(0).getVersio());
        assertEquals(uri, koodis.get(0).getKoodiUri());
    }

    @Test(expected = RuntimeException.class)
    public void searchSingleKoodiWithoutVersionNumber() {
        final String uri = "410";
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, null);
        koodiService.searchKoodis(searchType);
    }

    @Test
    public void searchKoodisByKoodisto() {
        final String koodistoUri = "http://paljon_versioita.fi/1";
        SearchKoodisByKoodistoCriteriaType koodistoSearchType = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUri(koodistoUri);

        List<KoodiType> koodis = koodiService.searchKoodisByKoodisto(koodistoSearchType);
        assertEquals(2, koodis.size());
        for (KoodiType k : koodis) {
            assertEquals(11, k.getVersio());
        }
    }

    @Test
    public void searchKoodisByKoodistoWithKoodiUris() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        String[] koodiUris = new String[]{"29"};

        SearchKoodisByKoodistoCriteriaType koodistoSearchType = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(
                Arrays.asList(koodiUris), koodistoUri);
        List<KoodiType> koodis = koodiService.searchKoodisByKoodisto(koodistoSearchType);
        assertEquals(1, koodis.size());

        koodiUris = new String[]{"29", "117", "133"};

        koodistoSearchType = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(Arrays.asList(koodiUris),
                koodistoUri);
        koodis = koodiService.searchKoodisByKoodisto(koodistoSearchType);
        assertEquals(3, koodis.size());
    }

    @Test
    public void searchKoodisByKoodistoVersio() {
        final String koodistoUri = "http://paljon_versioita.fi/1";
        SearchKoodisByKoodistoCriteriaType koodistoSearchType = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUriAndKoodistoVersio(koodistoUri, 1);

        List<KoodiType> koodis = koodiService.searchKoodisByKoodisto(koodistoSearchType);
        assertEquals(2, koodis.size());
        Collections.sort(koodis, new Comparator<KoodiType>() {

            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return Integer.valueOf(o1.getVersio()).compareTo(o2.getVersio());
            }

        });

        assertEquals(4, koodis.get(0).getVersio());
        assertEquals(6, koodis.get(1).getVersio());
    }

    @Test
    public void searchKoodiByArvo() {
        final String notExistsArvo = "ei-ole-olemassa";
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndArvo(null,
                notExistsArvo);
        List<KoodiType> koodis = koodiService.searchKoodis(searchType);
        assertEquals(0, koodis.size());

        final String arvo = "ekakOOdi vEr 1";
        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndArvo(null, arvo);

        final String uri = "410";
        koodis = koodiService.searchKoodis(searchType);
        assertEquals(1, koodis.size());
        assertEquals(uri, koodis.get(0).getKoodiUri());
        assertEquals(1, koodis.get(0).getVersio());
    }

    @Test
    public void searchKoodiByPartialArvo() {
        final String arvo = "veRSio";
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndArvo(null, arvo);
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.LATEST);
        List<KoodiType> koodis = koodiService.searchKoodis(searchType);
        assertEquals(2, koodis.size());

        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndArvo(null, arvo);
        koodis = koodiService.searchKoodis(searchType);
        assertEquals(22, koodis.size());
    }

    @Test
    public void searchKoodiByArvoFromKoodisto() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        final String notExistsArvo = "ei-ole-olemassa";

        SearchKoodisByKoodistoCriteriaType koodistoSearchType = KoodiServiceSearchCriteriaBuilder
                .koodisByArvoAndKoodistoUri(notExistsArvo, koodistoUri);
        List<KoodiType> koodis = koodiService.searchKoodisByKoodisto(koodistoSearchType);
        assertEquals(0, koodis.size());

        final String uri = "181";
        final String existsArvo = "235";
        koodistoSearchType = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(existsArvo, koodistoUri);

        koodis = koodiService.searchKoodisByKoodisto(koodistoSearchType);
        assertEquals(1, koodis.size());
        assertEquals(uri, koodis.get(0).getKoodiUri());
    }

    @Test
    public void searchAllKoodiVersios() {
        final String koodiUri = "435";

        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(koodiUri);

        List<KoodiType> versios = koodiService.searchKoodis(searchType);
        assertEquals(11, versios.size());
        Collections.sort(versios, new Comparator<KoodiType>() {

            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return Integer.valueOf(o1.getVersio()).compareTo(o2.getVersio());
            }
        });

        int lastVersio = Integer.MIN_VALUE;
        for (KoodiType koodi : versios) {
            assertEquals(koodiUri, koodi.getKoodiUri());
            assertTrue(lastVersio < koodi.getVersio());
            lastVersio = koodi.getVersio();
        }
    }

    @Test
    public void searchByTila() {
        final String koodiUri = "435";
        final TilaType luonnosTila = TilaType.LUONNOS;
        final TilaType hyvaksyttyTila = TilaType.HYVAKSYTTY;
        final TilaType passiivinenTila = TilaType.PASSIIVINEN;

        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndTila(koodiUri,
                luonnosTila);

        List<KoodiType> versios = koodiService.searchKoodis(searchType);
        assertEquals(1, versios.size());
        assertEquals(luonnosTila, versios.get(0).getTila());

        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndTila(koodiUri, hyvaksyttyTila);

        versios = koodiService.searchKoodis(searchType);
        assertEquals(10, versios.size());
        for (KoodiType k : versios) {
            assertEquals(hyvaksyttyTila, k.getTila());
        }

        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndTila(koodiUri, passiivinenTila);
        versios = koodiService.searchKoodis(searchType);
        assertEquals(0, versios.size());

        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndTila(koodiUri, hyvaksyttyTila, luonnosTila);
        versios = koodiService.searchKoodis(searchType);
        assertEquals(11, versios.size());
    }

    @Test
    public void searchByDate() {
        final String koodiUri = "435";
        SearchKoodisCriteriaType searchType = new SearchKoodisCriteriaType();
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.SPECIFIC);
        searchType.setKoodiVersio(11);
        searchType.getKoodiUris().add(koodiUri);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, 3);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        searchType.setValidAt(DateHelper.DateToXmlCal(calendar.getTime()));

        List<KoodiType> versios = koodiService.searchKoodis(searchType);
        assertEquals(1, versios.size());

        KoodiType k = versios.get(0);
        assertTrue(DateHelper.xmlCalToDate(k.getVoimassaAlkuPvm()).before(calendar.getTime())
                && DateHelper.xmlCalToDate(k.getVoimassaLoppuPvm()).after(calendar.getTime()));

        calendar.set(Calendar.DAY_OF_MONTH, 24);

        searchType = new SearchKoodisCriteriaType();
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.SPECIFIC);
        searchType.setKoodiVersio(11);
        searchType.getKoodiUris().add(koodiUri);
        searchType.setValidAt(DateHelper.DateToXmlCal(calendar.getTime()));

        versios = koodiService.searchKoodis(searchType);
        assertEquals(0, versios.size());
    }

    @Test
    public void searchByKoodistoDate() {
        final String koodistoUri = "http://paljon_versioita.fi/1";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, 9);
        calendar.set(Calendar.DAY_OF_MONTH, 2);

        SearchKoodisByKoodistoCriteriaType koodistoSearchType = new SearchKoodisByKoodistoCriteriaType();
        koodistoSearchType.setKoodistoUri(koodistoUri);
        koodistoSearchType.setValidAt(DateHelper.DateToXmlCal(calendar.getTime()));
        koodistoSearchType.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.LATEST);

        List<KoodiType> versios = koodiService.searchKoodisByKoodisto(koodistoSearchType);
        assertEquals(2, versios.size());
        Collections.sort(versios, new Comparator<KoodiType>() {

            @Override
            public int compare(KoodiType o1, KoodiType o2) {
                return Integer.valueOf(o1.getVersio()).compareTo(o2.getVersio());
            }

        });
        assertEquals(4, versios.get(0).getVersio());
        assertEquals(6, versios.get(1).getVersio());

        calendar.set(Calendar.DAY_OF_MONTH, 5);
        koodistoSearchType = new SearchKoodisByKoodistoCriteriaType();
        koodistoSearchType.setKoodistoUri(koodistoUri);
        koodistoSearchType.setValidAt(DateHelper.DateToXmlCal(calendar.getTime()));
        koodistoSearchType.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.LATEST);
        versios = koodiService.searchKoodisByKoodisto(koodistoSearchType);
        assertEquals(2, versios.size());
        for (KoodiType k : versios) {
            assertEquals(11, k.getVersio());
        }
    }

    @Test
    public void testListByRelation() {
        String koodiUri = "3";

        KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
        kv.setKoodiUri(koodiUri);
        kv.setVersio(1);

        List<KoodiType> rinnastuvat = koodiService.listKoodiByRelation(kv, false, SuhteenTyyppiType.RINNASTEINEN);
        assertEquals(2, rinnastuvat.size());
        assertNotNull(rinnastuvat.get(0).getKoodisto());
        assertNotNull(rinnastuvat.get(1).getKoodisto());
    }

    @Test
    public void testSearchMultipleKoodisLatestVersions() {
        final String koodiUri1 = "3";
        final String koodiUri2 = "435";

        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder
                .latestKoodisByUris(koodiUri1, koodiUri2);
        List<KoodiType> koodis = koodiService.searchKoodis(searchType);
        assertEquals(2, koodis.size());

    }

    @Test
    public void testSearchMultipleKoodisSpecificVersions() {
        final String koodiUri1 = "3";
        final String koodiUri2 = "435";

        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder
                .latestKoodisByUris(koodiUri1, koodiUri2);
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.SPECIFIC);
        searchType.setKoodiVersio(1);
        List<KoodiType> koodis = koodiService.searchKoodis(searchType);
        assertEquals(2, koodis.size());
    }

    @Test
    public void testGetLatestAcceptedKoodi() {
        final String koodiUri = "435";
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestAcceptedKoodiByUri(koodiUri);

        final int koodiVersio = 10;
        List<KoodiType> koodis = koodiService.searchKoodis(searchType);
        assertEquals(1, koodis.size());
        assertEquals(koodiVersio, koodis.get(0).getVersio());

    }

    @Test
    public void testGetKoodiVersioThatIsNotAttachedToAnyKoodistoVersio() {
        final String koodiUri = "435";
        final int koodiVersio = 9;

        SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(koodiUri,
                koodiVersio);
        List<KoodiType> koodis = koodiService.searchKoodis(searchCriteria);
        assertEquals(1, koodis.size());
        KoodiType koodi = koodis.get(0);
        assertNotNull(koodi.getKoodisto());
        assertFalse(StringUtils.isBlank(koodi.getKoodisto().getOrganisaatioOid()));
        assertTrue(koodi.getKoodisto().getKoodistoVersio().isEmpty());
    }

    @Test
    public void testSearchLatestValidAcceptedKoodisByKoodisto() {
        final String koodistoUri = "http://koodisto";
        final int koodistoVersio = 1;

        assertEquals(
                3,
                koodiService.searchKoodisByKoodisto(
                        KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(koodistoUri,
                                koodistoVersio)).size());

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 11, 5);

        SearchKoodisByKoodistoCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder
                .validAcceptedKoodisByKoodistoUriAndKoodistoVersio(koodistoUri, koodistoVersio);
        searchCriteria.getKoodiSearchCriteria().setValidAt(DateHelper.DateToXmlCal(calendar.getTime()));

        List<KoodiType> koodis = koodiService.searchKoodisByKoodisto(searchCriteria);
        assertEquals(1, koodis.size());

        final String koodiUri = "441";
        assertEquals(koodiUri, koodis.get(0).getKoodiUri());
    }
}
