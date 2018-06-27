package fi.vm.sade.koodisto.dao;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.model.Tila;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoVersioSelectionType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
@Transactional
public class KoodiVersioDaoTest {

    @Autowired
    private KoodiVersioDAO koodiVersioDAO;

    @Test
    public void searchSingleKoodiByUri() {
        final String notExistsUri = "ei-ole-olemassa";

        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(notExistsUri);
        List<KoodiVersioWithKoodistoItem> koodis = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(0, koodis.size());

        final String uri = "410";
        searchType = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(uri);

        koodis = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(1, koodis.size());
        assertEquals(2, koodis.get(0).getKoodiVersio().getVersio().intValue());
        assertEquals(uri, koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());
    }

    @Test
    public void searchSingleKoodiByUriAndVersion() {
        final String uri = "410";
        // version 150 does not exist
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, 150);
        List<KoodiVersioWithKoodistoItem> koodis = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(0, koodis.size());

        searchType = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, 1);
        koodis = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(1, koodis.size());
        assertEquals(1, koodis.get(0).getKoodiVersio().getVersio().intValue());
        assertEquals(uri, koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());
    }

    @Test
    public void searchKoodisByKoodisto() {
        final String koodistoUri = "http://paljon_versioita.fi/1";
        SearchKoodisByKoodistoCriteriaType koodistoSearchType = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUri(koodistoUri);

        List<KoodiVersioWithKoodistoItem> koodis = koodiVersioDAO.searchKoodis(koodistoSearchType);
        assertEquals(2, koodis.size());
        for (KoodiVersioWithKoodistoItem k : koodis) {
            assertEquals(11, k.getKoodiVersio().getVersio().intValue());
        }
    }

    @Test
    public void searchKoodisByKoodistoVersio() {
        final String koodistoUri = "http://paljon_versioita.fi/1";
        SearchKoodisByKoodistoCriteriaType koodistoSearchType = KoodiServiceSearchCriteriaBuilder
                .koodisByKoodistoUriAndKoodistoVersio(koodistoUri, 1);

        List<KoodiVersioWithKoodistoItem> koodis = koodiVersioDAO.searchKoodis(koodistoSearchType);
        assertEquals(2, koodis.size());
        Collections.sort(koodis, new Comparator<KoodiVersioWithKoodistoItem>() {

            @Override
            public int compare(KoodiVersioWithKoodistoItem o1, KoodiVersioWithKoodistoItem o2) {
                return o1.getKoodiVersio().getVersio().compareTo(o2.getKoodiVersio().getVersio());
            }

        });

        assertEquals(4, koodis.get(0).getKoodiVersio().getVersio().intValue());
        assertEquals(6, koodis.get(1).getKoodiVersio().getVersio().intValue());
    }

    @Test
    public void searchKoodiByArvo() {
        final String notExistsArvo = "ei-ole-olemassa";
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndArvo(null,
                notExistsArvo);
        List<KoodiVersioWithKoodistoItem> koodis = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(0, koodis.size());

        final String arvo = "ekakOOdi vEr 1";
        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndArvo(null, arvo);

        final String uri = "410";
        koodis = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(1, koodis.size());
        assertEquals(uri, koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());
        assertEquals(1, koodis.get(0).getKoodiVersio().getVersio().intValue());
    }

    @Test
    public void searchKoodiByPartialArvo() {
        final String arvo = "veRSio";
        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndArvo(null, arvo);
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.LATEST);
        List<KoodiVersioWithKoodistoItem> koodis = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(2, koodis.size());

        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndArvo(null, arvo);
        koodis = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(22, koodis.size());
    }

    @Test
    public void searchKoodiByArvoFromKoodisto() {
        final String koodistoUri = "http://www.kunnat.fi/kunta";
        final String notExistsArvo = "ei-ole-olemassa";

        SearchKoodisByKoodistoCriteriaType koodistoSearchType = KoodiServiceSearchCriteriaBuilder
                .koodisByArvoAndKoodistoUri(notExistsArvo, koodistoUri);
        List<KoodiVersioWithKoodistoItem> koodis = koodiVersioDAO.searchKoodis(koodistoSearchType);
        assertEquals(0, koodis.size());

        final String uri = "181";
        final String existsArvo = "235";
        koodistoSearchType = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(existsArvo, koodistoUri);

        koodis = koodiVersioDAO.searchKoodis(koodistoSearchType);
        assertEquals(1, koodis.size());
        assertEquals(uri, koodis.get(0).getKoodiVersio().getKoodi().getKoodiUri());
    }

    @Test
    public void searchAllKoodiVersios() {
        final String koodiUri = "435";

        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUri(koodiUri);

        List<KoodiVersioWithKoodistoItem> versios = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(11, versios.size());
        Collections.sort(versios, new Comparator<KoodiVersioWithKoodistoItem>() {

            @Override
            public int compare(KoodiVersioWithKoodistoItem o1, KoodiVersioWithKoodistoItem o2) {
                return o1.getKoodiVersio().getVersio().compareTo(o2.getKoodiVersio().getVersio());
            }
        });

        int lastVersio = Integer.MIN_VALUE;
        for (KoodiVersioWithKoodistoItem koodi : versios) {
            assertEquals(koodiUri, koodi.getKoodiVersio().getKoodi().getKoodiUri());
            assertTrue(lastVersio < koodi.getKoodiVersio().getVersio());
            lastVersio = koodi.getKoodiVersio().getVersio();
        }
    }

    @Test
    public void searchByTila() {
        final String koodiUri = "435";
        final Tila luonnosTila = Tila.LUONNOS;
        final Tila hyvaksyttyTila = Tila.HYVAKSYTTY;
        final Tila passiivinenTila = Tila.PASSIIVINEN;

        SearchKoodisCriteriaType searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndTila(koodiUri,
                TilaType.valueOf(luonnosTila.name()));

        List<KoodiVersioWithKoodistoItem> versios = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(1, versios.size());
        assertEquals(luonnosTila, versios.get(0).getKoodiVersio().getTila());

        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndTila(koodiUri,
                TilaType.valueOf(hyvaksyttyTila.name()));

        versios = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(10, versios.size());
        for (KoodiVersioWithKoodistoItem k : versios) {
            assertEquals(hyvaksyttyTila, k.getKoodiVersio().getTila());
        }

        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndTila(koodiUri,
                TilaType.valueOf(passiivinenTila.name()));
        versios = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(0, versios.size());

        searchType = KoodiServiceSearchCriteriaBuilder.koodiVersiosByUriAndTila(koodiUri,
                TilaType.valueOf(hyvaksyttyTila.name()), TilaType.valueOf(luonnosTila.name()));
        versios = koodiVersioDAO.searchKoodis(searchType);
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

        List<KoodiVersioWithKoodistoItem> versios = koodiVersioDAO.searchKoodis(searchType);
        assertEquals(1, versios.size());

        KoodiVersio k = versios.get(0).getKoodiVersio();
        assertTrue(k.getVoimassaAlkuPvm().before(calendar.getTime())
                && k.getVoimassaLoppuPvm().after(calendar.getTime()));

        calendar.set(Calendar.DAY_OF_MONTH, 24);

        searchType = new SearchKoodisCriteriaType();
        searchType.setKoodiVersioSelection(SearchKoodisVersioSelectionType.SPECIFIC);
        searchType.setKoodiVersio(11);
        searchType.getKoodiUris().add(koodiUri);
        searchType.setValidAt(DateHelper.DateToXmlCal(calendar.getTime()));

        versios = koodiVersioDAO.searchKoodis(searchType);
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

        List<KoodiVersioWithKoodistoItem> versios = koodiVersioDAO.searchKoodis(koodistoSearchType);
        assertEquals(2, versios.size());
        Collections.sort(versios, new Comparator<KoodiVersioWithKoodistoItem>() {

            @Override
            public int compare(KoodiVersioWithKoodistoItem o1, KoodiVersioWithKoodistoItem o2) {
                return o1.getKoodiVersio().getVersio().compareTo(o2.getKoodiVersio().getVersio());
            }

        });
        assertEquals(4, versios.get(0).getKoodiVersio().getVersio().intValue());
        assertEquals(6, versios.get(1).getKoodiVersio().getVersio().intValue());

        calendar.set(Calendar.DAY_OF_MONTH, 5);
        koodistoSearchType = new SearchKoodisByKoodistoCriteriaType();
        koodistoSearchType.setKoodistoUri(koodistoUri);
        koodistoSearchType.setValidAt(DateHelper.DateToXmlCal(calendar.getTime()));
        koodistoSearchType.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.LATEST);
        versios = koodiVersioDAO.searchKoodis(koodistoSearchType);
        assertEquals(2, versios.size());
        for (KoodiVersioWithKoodistoItem k : versios) {
            assertEquals(11, k.getKoodiVersio().getVersio().intValue());
        }
    }

    @Test
    public void testListByParentRelation() {
        KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
        kv.setKoodiUri("3");
        kv.setVersio(1);

        List<KoodiVersioWithKoodistoItem> k = koodiVersioDAO.listByParentRelation(kv, SuhteenTyyppi.RINNASTEINEN);
        assertEquals(1, k.size());
        assertEquals("009", k.get(0).getKoodiVersio().getKoodiarvo());
    }

    @Test
    public void testListBychildRelation() {
        KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
        kv.setKoodiUri("3");
        kv.setVersio(1);

        List<KoodiVersioWithKoodistoItem> k = koodiVersioDAO.listByChildRelation(kv, SuhteenTyyppi.RINNASTEINEN);
        Assert.assertEquals("010", k.get(0).getKoodiVersio().getKoodiarvo());
        assertEquals(1, k.size());
    }
    
    @Test
    public void testgetKoodiVersios() {
        KoodiUriAndVersioType kv1 = new KoodiUriAndVersioType();
        kv1.setKoodiUri("3");
        kv1.setVersio(1);
        
        KoodiUriAndVersioType kv2 = new KoodiUriAndVersioType();
        kv2.setKoodiUri("181");
        kv2.setVersio(1);
        
        List<KoodiVersio> koodiVersios = koodiVersioDAO.getKoodiVersios(kv1, kv2);
        assertEquals(2, koodiVersios.size());
    }

    @Test
    public void testGetPreviousKoodiVersio() {
        final String koodiUri = "455";
        final Integer koodiVersio = 4;

        final Integer previousVersio = 2;
        KoodiVersio previous = koodiVersioDAO.getPreviousKoodiVersio(koodiUri, koodiVersio);
        assertEquals(previousVersio, previous.getVersio());
        assertEquals(koodiUri, previous.getKoodi().getKoodiUri());

    }
    
    @Test
    public void shouldNotBeLatestKoodiVersio() {
        assertFalse(koodiVersioDAO.isLatestKoodiVersio("436", 3));
    }
    
    @Test
    public void shouldBeLatestKoodiVersio() {
        assertTrue(koodiVersioDAO.isLatestKoodiVersio("436", 11));
    }
    
    @Test
    public void fetchesLatestKoodiVersios() {
        Map<String, Integer> map = koodiVersioDAO.getLatestVersionNumbersForUris("436", "455");
        assertEquals(2, map.size());
        assertEquals(Integer.valueOf(11), map.get("436"));
        assertEquals(Integer.valueOf(4), map.get("455"));
    }    

    @Test
    public void fetchesNothingForEmptyUriList() {
        Map<String, Integer> map = koodiVersioDAO.getLatestVersionNumbersForUris();
        assertEquals(0, map.size());
    }    

}
