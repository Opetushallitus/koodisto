package fi.vm.sade.koodisto.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.model.Koodi;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;

import static org.junit.Assert.assertNotNull;

@DatabaseSetup("classpath:test-data.xml")
public class KoodiDaoTest extends DaoTest {

    @Autowired
    private KoodiDAO koodiDAO;

    @Test(expected = NoResultException.class)
    public void testDelete() {
        Koodi k = koodiDAO.readByUri("381");
        koodiDAO.delete(k.getKoodiUri());
        koodiDAO.flush();
        koodiDAO.readByUri(k.getKoodiUri());
    }

    @Test
    public void testReadByUri() {
        Koodi k = koodiDAO.readByUri("3");
        assertNotNull(k);
    }
}
