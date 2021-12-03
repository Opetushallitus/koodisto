package fi.vm.sade.koodisto.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.KoodistoRyhma;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@DatabaseSetup("classpath:test-data.xml")
public class KoodistoRyhmaDaoTest extends DaoTest {

    @Autowired
    KoodistoRyhmaDAO koodistoRyhmaDAO;

    @Test
    public void testListAllKoodistoRyhmas() {
        List<KoodistoRyhma> koodistoRyhmas = koodistoRyhmaDAO.listAllKoodistoRyhmas();
        assertNotNull(koodistoRyhmas);
        assertEquals(4, koodistoRyhmas.size());
    }
}
