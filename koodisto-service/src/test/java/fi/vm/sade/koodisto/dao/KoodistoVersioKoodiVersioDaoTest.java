package fi.vm.sade.koodisto.dao;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.BaseEntity;
import fi.vm.sade.koodisto.model.KoodiVersio;
import fi.vm.sade.koodisto.model.KoodistoVersio;
import fi.vm.sade.koodisto.model.KoodistoVersioKoodiVersio;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodistoVersioKoodiVersioDaoTest {

    @Autowired
    private KoodistoVersioKoodiVersioDAO dao;

    @Autowired
    private GenericDAO genericDao;

    @Test
    public void testFindByKoodistoVersioAndKoodiVersio() {
        final Long koodistoVersioId = -303L;
        final Long koodiVersioId = -305L;

        KoodistoVersioKoodiVersio result = dao.findByKoodistoVersioAndKoodiVersio(koodistoVersioId, koodiVersioId);
        assertNotNull(result);
        assertEquals(koodistoVersioId, result.getKoodistoVersio().getId());
        assertEquals(koodiVersioId, result.getKoodiVersio().getId());

        result = dao.findByKoodistoVersioAndKoodiVersio(-1L, -2L);
        assertNull(result);
    }

    @Test
    public void testGetByKoodistoVersioAndKoodi() {
        final Long koodistoVersioId = -481L;
        final Long koodiId = -435L;

        List<KoodistoVersioKoodiVersio> result = dao.getByKoodistoVersioAndKoodi(koodistoVersioId, koodiId);

        assertEquals(1, result.size());
        final Long koodiVersioId = -447L;
        assertEquals(koodistoVersioId, result.get(0).getKoodistoVersio().getId());
        assertEquals(koodiId, result.get(0).getKoodiVersio().getKoodi().getId());
        assertEquals(koodiVersioId, result.get(0).getKoodiVersio().getId());
    }

    @Test
    public void testGetByKoodiVersio() {
        final Long koodiVersioId = -447L;
        List<KoodistoVersioKoodiVersio> result = dao.getByKoodiVersio(koodiVersioId);
        assertEquals(1, result.size());

        final Long koodistoVersioId = -481L;
        assertEquals(koodistoVersioId, result.get(0).getKoodistoVersio().getId());
        assertEquals(koodiVersioId, result.get(0).getKoodiVersio().getId());
    }

    @Test
    public void testGetByKoodistoVersio() {
        final Long koodistoVersioId = -481L;

        List<KoodistoVersioKoodiVersio> result = dao.getByKoodistoVersio(koodistoVersioId);
        assertEquals(2, result.size());

        result.sort(Comparator.comparing(BaseEntity::getId));

        assertEquals(koodistoVersioId, result.get(0).getKoodistoVersio().getId());
        assertEquals(koodistoVersioId, result.get(1).getKoodistoVersio().getId());

        final Long koodiVersioId1 = -447L;
        final Long koodiVersioId2 = -458L;
        assertThat(result)
                .extracting(KoodistoVersioKoodiVersio::getKoodiVersio)
                .extracting(KoodiVersio::getId)
                .containsExactlyInAnyOrder(koodiVersioId1, koodiVersioId2);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInsertIllegalRelationship() {
        final Long koodiVersioId = -411L;
        final Long koodistoVersioId = -399L;

        KoodistoVersioKoodiVersio newRelation = new KoodistoVersioKoodiVersio();
        KoodiVersio koodiVersio = genericDao.read(KoodiVersio.class, koodiVersioId);
        KoodistoVersio koodistoVersio = genericDao.read(KoodistoVersio.class, koodistoVersioId);

        newRelation.setKoodistoVersio(koodistoVersio);
        newRelation.setKoodiVersio(koodiVersio);

        dao.insert(newRelation);
    }
}
