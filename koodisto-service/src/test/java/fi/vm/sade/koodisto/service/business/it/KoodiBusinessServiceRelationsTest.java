package fi.vm.sade.koodisto.service.business.it;

import fi.vm.sade.koodisto.model.SuhteenTyyppi;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import fi.vm.sade.koodisto.service.business.util.KoodiVersioWithKoodistoItem;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
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
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@WithMockUser(value = "1.2.3.4.5", authorities = "APP_KOODISTO_CRUD_1.2.246.562.10.00000000001")
public class KoodiBusinessServiceRelationsTest {

    private final Logger logger = LoggerFactory.getLogger(KoodiBusinessServiceRelationsTest.class);

    @Autowired
    private KoodiBusinessService koodiBusinessService;

    @Test
    public void testListByRelation() {
        KoodiUriAndVersioType kv = givenKoodiUriAndVersioType("3", 1);

        List<KoodiVersioWithKoodistoItem> rinnastuvat = koodiBusinessService.listByRelation(kv,
                SuhteenTyyppi.RINNASTEINEN, false);
        assertEquals(2, rinnastuvat.size());
        for (KoodiVersioWithKoodistoItem k : rinnastuvat) {
            logger.info("Koodi: {} - versio: {}"
                    ,k.getKoodiVersio().getKoodi().getKoodiUri(), k.getKoodiVersio().getVersio());
        }

        List<KoodiVersioWithKoodistoItem> children = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.SISALTYY,
                false);
        assertEquals(2, children.size());
        List<KoodiVersioWithKoodistoItem> parents = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.SISALTYY,
                true);
        assertEquals(1, parents.size());

    }

    @Test
    public void testAddRelation1() {
        KoodiUriAndVersioType ylakoodi = givenKoodiUriAndVersioType("3", 1);
        KoodiUriAndVersioType alakoodi = givenKoodiUriAndVersioType("21", 1);

        List<KoodiVersioWithKoodistoItem> rinnastuvat = koodiBusinessService.listByRelation(ylakoodi,
                SuhteenTyyppi.RINNASTEINEN, false);
        assertEquals(2, rinnastuvat.size());

        koodiBusinessService.addRelation(ylakoodi.getKoodiUri(), Arrays.asList(alakoodi.getKoodiUri()), SuhteenTyyppi.RINNASTEINEN, false);

        rinnastuvat = koodiBusinessService.listByRelation(ylakoodi, SuhteenTyyppi.RINNASTEINEN, false);
        assertEquals(3, rinnastuvat.size());
    }

    @Test
    public void testAddRelation2() {
        KoodiUriAndVersioType kv = givenKoodiUriAndVersioType("369", 1);

        List<KoodiVersioWithKoodistoItem> result = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.RINNASTEINEN,
                false);
        assertEquals(0L, result.size());

        List<String> list = new ArrayList<String>();
        list.add("371");
        list.add("373");
        list.add("375");

        koodiBusinessService.addRelation(kv.getKoodiUri(), list, SuhteenTyyppi.RINNASTEINEN, false);
        result = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.RINNASTEINEN, false);
        assertEquals(3L, result.size());
    }

    //@Ignore("Suddenly started to fail in CI pipeline, works elsewhere?")
    @Test
    public void savedRelationBetweenCodeElementsInSameCodes() {
        koodiBusinessService.addRelation("31", Arrays.asList("33"), SuhteenTyyppi.SISALTYY, false);
        KoodiUriAndVersioType kv = givenKoodiUriAndVersioType("31", 2);
        List<KoodiVersioWithKoodistoItem> result = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.SISALTYY, false);
        assertEquals(1, result.size());
    }

    @Test
    public void testRemoveRelation() {
        KoodiUriAndVersioType kv = givenKoodiUriAndVersioType("7", 1);

        List<KoodiVersioWithKoodistoItem> result = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.RINNASTEINEN,
                true);
        assertEquals(2L, result.size());

        List<String> list = new ArrayList<String>();
        list.add("5");
        list.add("3");

        koodiBusinessService.removeRelation(kv.getKoodiUri(), list, SuhteenTyyppi.RINNASTEINEN, false);
        result = koodiBusinessService.listByRelation(kv, SuhteenTyyppi.RINNASTEINEN, true);
        assertEquals(0L, result.size());
    }

    private KoodiUriAndVersioType givenKoodiUriAndVersioType(String koodiUri, int versio) {
        KoodiUriAndVersioType kv = new KoodiUriAndVersioType();
        kv.setKoodiUri(koodiUri);
        kv.setVersio(versio);
        return kv;
    }
}
