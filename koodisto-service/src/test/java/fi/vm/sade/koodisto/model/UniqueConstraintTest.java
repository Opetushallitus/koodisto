package fi.vm.sade.koodisto.model;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.generic.dao.GenericDAO;
import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.koodisto.util.JtaCleanInsertTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.Date;

import static org.junit.Assert.fail;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = { JtaCleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class UniqueConstraintTest {

    @Autowired
    private GenericDAO genericDAO;

    @Test(expected = PersistenceException.class)
    public void testDuplicateKoodiUri() {
        Koodisto koodisto = genericDAO.read(Koodisto.class, 422L);
        Koodi koodi = new Koodi();
        koodi.setKoodiUri("123456");
        koodi.setKoodisto(koodisto);

        insertEntityTwice(koodi);
    }

    @Test(expected = PersistenceException.class)
    public void testDuplicateKoodiMetadata() {
        final Long koodiVersioId = 411L;
        KoodiVersio koodiVersio = genericDAO.read(KoodiVersio.class, koodiVersioId);

        KoodiMetadata metadata = new KoodiMetadata();
        metadata.setKieli(Kieli.EN);
        metadata.setNimi("nimi");
        metadata.setLyhytNimi("lyhytnimi");
        metadata.setKuvaus("kuvaus");
        metadata.setKoodiVersio(koodiVersio);
        insertEntityTwice(metadata);
    }

    @Test(expected = PersistenceException.class)
    public void testDuplicateKoodinSuhde() {
        final Long ylaKoodiId = 412L;
        final Long alaKoodiId = 411L;

        KoodinSuhde suhde = new KoodinSuhde();
        suhde.setAlakoodiVersio(genericDAO.read(KoodiVersio.class, alaKoodiId));
        suhde.setYlakoodiVersio(genericDAO.read(KoodiVersio.class, ylaKoodiId));
        suhde.setSuhteenTyyppi(SuhteenTyyppi.SISALTYY);
        suhde.setVersio(1);

        insertEntityTwice(suhde);
    }

    @Test(expected = PersistenceException.class)
    public void testDuplicateKoodistoUri() {
        Koodisto koodisto = new Koodisto();
        koodisto.setKoodistoUri("duplicate uri");
        koodisto.setLukittu(false);
        koodisto.setOmistaja("omistaja");
        koodisto.setOrganisaatioOid("organisaatioid");
        insertEntityTwice(koodisto);
    }

    @Test(expected = PersistenceException.class)
    public void testDuplicateKoodistoMetadata() {
        final Long koodistoVersioId = 423L;
        KoodistoVersio koodistoVersio = genericDAO.read(KoodistoVersio.class, koodistoVersioId);

        KoodistoMetadata metadata = new KoodistoMetadata();
        metadata.setKieli(Kieli.EN);
        metadata.setNimi("nimi");
        metadata.setKuvaus("kuvaus");
        metadata.setKoodistoVersio(koodistoVersio);

        insertEntityTwice(metadata);
    }

    @Test(expected = PersistenceException.class)
    public void testDuplicateKoodistoRyhmaUri() {
        KoodistoRyhma ryhma = new KoodistoRyhma();
        ryhma.setKoodistoRyhmaUri("duplicate uri");

        KoodistoRyhmaMetadata metadata = new KoodistoRyhmaMetadata();
        metadata.setKieli(Kieli.FI);
        metadata.setNimi("nimi");
        ryhma.addKoodistoRyhmaMetadata(metadata);

        insertEntityTwice(ryhma);
    }

    @Test(expected = PersistenceException.class)
    public void testDuplicateKoodistoRyhmaMetadata() {
        final Long koodistoRyhmaId = 407L;
        KoodistoRyhma koodistoRyhma = genericDAO.read(KoodistoRyhma.class, koodistoRyhmaId);

        KoodistoRyhmaMetadata metadata = new KoodistoRyhmaMetadata();
        metadata.setKoodistoRyhma(koodistoRyhma);
        metadata.setKieli(Kieli.EN);
        metadata.setNimi("duplicate metadata");

        insertEntityTwice(metadata);
    }

    @Test(expected = PersistenceException.class)
    public void testDuplicateKoodistoVersio() {
        final Long koodistoId = 422L;
        final Integer newKoodistoVersio = 2;
        Koodisto koodisto = genericDAO.read(Koodisto.class, koodistoId);

        KoodistoVersio versio = new KoodistoVersio();
        versio.setTila(Tila.HYVAKSYTTY);
        versio.setVersio(newKoodistoVersio);
        versio.setVoimassaAlkuPvm(new Date());
        versio.setKoodisto(koodisto);

        KoodistoMetadata metadata = new KoodistoMetadata();
        metadata.setNimi("nimi");
        metadata.setKuvaus("kuvaus");
        metadata.setKieli(Kieli.FI);
        versio.addMetadata(metadata);

        insertEntityTwice(versio);
    }

    @Test(expected = PersistenceException.class)
    public void testDuplicateKoodiVersio() {
        final Long koodiId = 411L;
        final Integer newKoodiVersio = 3;
        Koodi koodi = genericDAO.read(Koodi.class, koodiId);

        KoodiVersio versio = new KoodiVersio();
        versio.setTila(Tila.HYVAKSYTTY);
        versio.setVersio(newKoodiVersio);
        versio.setKoodi(koodi);
        versio.setVoimassaAlkuPvm(new Date());
        versio.setKoodiarvo("arvo");

        KoodiMetadata metadata = new KoodiMetadata();
        metadata.setNimi("nimi");
        metadata.setKieli(Kieli.FI);
        metadata.setLyhytNimi("lyhyt nimi");
        metadata.setKuvaus("kuvaus");
        versio.addMetadata(metadata);

        insertEntityTwice(versio);
    }

    private <E extends BaseEntity> void insertEntityTwice(E entity) {
        try {
            genericDAO.insert(entity);
        } catch (Exception e) {
            fail();
        }

        entity.setId(null);
        genericDAO.insert(entity);
    }
}
