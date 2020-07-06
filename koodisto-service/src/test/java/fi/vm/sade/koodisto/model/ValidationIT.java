package fi.vm.sade.koodisto.model;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.dao.GenericDAO;
import fi.vm.sade.koodisto.service.business.DownloadBusinessService;
import fi.vm.sade.koodisto.service.business.KoodiBusinessService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@DatabaseSetup("classpath:test-data.xml")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
@Transactional
@WithMockUser("1.2.3.4.5")
public class ValidationIT {

    @MockBean
    private DownloadBusinessService downloadBusinessService;

    @MockBean
    private KoodiBusinessService koodiBusinessService;

    @Autowired
    private GenericDAO genericDAO;

    private KoodistoMetadata createValidKoodistoMetadata(Kieli kieli) {
        KoodistoMetadata koodistoMetadata = new KoodistoMetadata();
        koodistoMetadata.setNimi("nimi");
        koodistoMetadata.setKuvaus("kuvaus");
        koodistoMetadata.setKieli(kieli);

        return koodistoMetadata;
    }

    private KoodistoVersio insertValidKoodistoVersio(Koodisto koodisto) {
        KoodistoVersio koodistoVersio = createKoodistoVersioWithValidityDates(koodisto, new Date(), null);
        koodistoVersio = genericDAO.insert(koodistoVersio);

        return koodistoVersio;
    }

    private Koodisto insertValidKoodisto() {
        Koodisto koodisto = new Koodisto();
        koodisto.setKoodistoUri("koodistouri");
        koodisto.setOrganisaatioOid("organisaatiooid");
        koodisto = genericDAO.insert(koodisto);
        return koodisto;
    }

    private KoodiVersio insertValidKoodiVersio(Koodi koodi) {
        KoodiVersio koodiVersio = createKoodiVersioWithValidityDates(koodi, new Date(), null);

        koodiVersio = genericDAO.insert(koodiVersio);
        return koodiVersio;
    }

    private KoodiMetadata createValidKoodiMetadata(Kieli kieli) {
        KoodiMetadata koodiMetadata = new KoodiMetadata();
        koodiMetadata.setNimi("nimi");
        koodiMetadata.setLyhytNimi("lyhyt nimi");
        koodiMetadata.setKuvaus("kuvaus");
        koodiMetadata.setKieli(kieli);

        return koodiMetadata;
    }

    private Koodi insertValidKoodi(Koodisto koodisto) {
        Koodi koodi = new Koodi();
        koodi.setKoodisto(koodisto);
        koodi.setKoodiUri("puuppa");

        koodi = genericDAO.insert(koodi);
        return koodi;
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateKoodisto() {
        try {
            insertValidKoodisto();
        } catch (Exception e) {
            fail();
        }

        try {
            genericDAO.insert(new Koodisto());
        } catch (ConstraintViolationException e) {
            assertEquals(2, e.getConstraintViolations().size());
            throw e;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateKoodistoVersio() {
        try {
            insertValidKoodistoVersio(insertValidKoodisto());
        } catch (Exception e) {
            fail();
        }

        try {
            genericDAO.insert(new KoodistoVersio());
        } catch (ConstraintViolationException e) {
            assertEquals(6, e.getConstraintViolations().size());
            throw e;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateKoodistoMetadata() {
        KoodistoVersio validKoodistoVersio = insertValidKoodistoVersio(insertValidKoodisto());

        try {
            KoodistoMetadata metadata = createValidKoodistoMetadata(Kieli.SV);
            metadata.setKoodistoVersio(validKoodistoVersio);
            genericDAO.insert(metadata);
        } catch (Exception e) {
            fail();
        }

        try {
            genericDAO.insert(new KoodistoMetadata());
        } catch (ConstraintViolationException e) {
            assertEquals(3, e.getConstraintViolations().size());
            throw e;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateKoodi() {
        try {
            insertValidKoodi(insertValidKoodisto());
        } catch (Exception e) {
            fail();
        }

        try {
            genericDAO.insert(new Koodi());
        } catch (ConstraintViolationException e) {
            assertEquals(2, e.getConstraintViolations().size());
            throw e;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateKoodiVersio() {
        try {
            insertValidKoodiVersio(insertValidKoodi(insertValidKoodisto()));
        } catch (Exception e) {
            fail();
        }

        try {
            genericDAO.insert(new KoodiVersio());
        } catch (ConstraintViolationException e) {
            assertEquals(7, e.getConstraintViolations().size());
            throw e;
        }
    }

    private KoodiVersio createKoodiVersioWithValidityDates(Koodi koodi, Date voimassaAlkuPvm, Date voimassaLoppuPvm) {
        KoodiVersio koodiVersio = new KoodiVersio();
        koodiVersio.setKoodi(koodi);
        koodiVersio.setKoodiarvo("arvo");
        koodiVersio.setTila(Tila.HYVAKSYTTY);
        koodiVersio.setVersio(1);
        koodiVersio.addMetadata(createValidKoodiMetadata(Kieli.FI));
        koodiVersio.setVoimassaAlkuPvm(voimassaAlkuPvm);
        koodiVersio.setVoimassaLoppuPvm(voimassaLoppuPvm);

        return koodiVersio;
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateInvalidKoodiVersioValidityDates() {
        Koodi koodi = insertValidKoodi(insertValidKoodisto());
        Calendar now = Calendar.getInstance();

        KoodiVersio invalidKoodiVersio = createKoodiVersioWithValidityDates(koodi, now.getTime(),
                new Date(now.getTimeInMillis() - 1L));

        try {
            genericDAO.insert(invalidKoodiVersio);
        } catch (ConstraintViolationException e) {
            assertEquals(2, e.getConstraintViolations().size());
            throw e;
        }
    }

    @Test
    public void validateValidKoodiVersioValidityDates() {
        Koodi koodi = insertValidKoodi(insertValidKoodisto());
        Calendar now = Calendar.getInstance();

        KoodiVersio invalidKoodiVersio = createKoodiVersioWithValidityDates(koodi, now.getTime(),
                new Date(now.getTimeInMillis() + 1L));

        genericDAO.insert(invalidKoodiVersio);
    }

    private KoodistoVersio createKoodistoVersioWithValidityDates(Koodisto koodisto, Date voimassaAlkuPvm,
            Date voimassaLoppuPvm) {
        KoodistoVersio koodistoVersio = new KoodistoVersio();
        koodistoVersio.setKoodisto(koodisto);
        koodistoVersio.setTila(Tila.HYVAKSYTTY);
        koodistoVersio.setVersio(1);
        koodistoVersio.setVoimassaAlkuPvm(voimassaAlkuPvm);
        koodistoVersio.setVoimassaLoppuPvm(voimassaLoppuPvm);

        koodistoVersio.addMetadata(createValidKoodistoMetadata(Kieli.FI));
        return koodistoVersio;
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateInvalidKoodistoVersioValidityDates() {
        Koodisto koodisto = insertValidKoodisto();
        Calendar now = Calendar.getInstance();

        KoodistoVersio invalidKoodistoVersio = createKoodistoVersioWithValidityDates(koodisto, now.getTime(), new Date(
                now.getTimeInMillis() - 1L));
        try {
            genericDAO.insert(invalidKoodistoVersio);
        } catch (ConstraintViolationException e) {
            assertEquals(2, e.getConstraintViolations().size());
            throw e;
        }

    }

    @Test
    public void validateValidKoodistoVersioValidityDates() {
        Koodisto koodisto = insertValidKoodisto();
        Calendar now = Calendar.getInstance();

        KoodistoVersio invalidKoodistoVersio = createKoodistoVersioWithValidityDates(koodisto, now.getTime(), new Date(
                now.getTimeInMillis() + 1L));

        genericDAO.insert(invalidKoodistoVersio);

    }

    @Test(expected = ConstraintViolationException.class)
    public void validateKoodiMetadata() {
        KoodiVersio koodiVersio = insertValidKoodiVersio(insertValidKoodi(insertValidKoodisto()));

        try {
            KoodiMetadata koodiMetadata = createValidKoodiMetadata(Kieli.SV);
            koodiMetadata.setKoodiVersio(koodiVersio);
            genericDAO.insert(koodiMetadata);
        } catch (Exception e) {
            fail();
        }

        try {
            genericDAO.insert(new KoodiMetadata());
        } catch (ConstraintViolationException e) {
            assertEquals(3, e.getConstraintViolations().size());
            throw e;
        }
    }

}
