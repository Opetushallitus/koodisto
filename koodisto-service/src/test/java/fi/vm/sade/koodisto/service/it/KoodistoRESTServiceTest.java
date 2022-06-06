package fi.vm.sade.koodisto.service.it;


import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fi.vm.sade.koodisto.service.business.exception.KoodiNotFoundException;
import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.service.impl.KoodistoRESTService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import junit.framework.Assert;
import junit.framework.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: kwuoti Date: 12.4.2013 Time: 14.04
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DatabaseSetup("classpath:test-data.xml")
@Transactional
public class KoodistoRESTServiceTest {

    @Autowired
    private KoodistoRESTService koodistoRESTService;

    @Test
    public void testListAllKoodistoRyhmas() {
        assertEquals(4, koodistoRESTService.listAllKoodistoRyhmas()
                .getValue().getKoodistoryhma().size());
    }

    @Test
    public void testGetKoodistoByUri() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 2;
        KoodistoType koodisto = koodistoRESTService.getKoodistoByUri(koodistoUri, null).getValue();
        assertEquals(koodistoUri, koodisto.getKoodistoUri());
        assertEquals(koodistoVersio, koodisto.getVersio());
    }

    @Test
    public void testGetKoodistoByUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;
        KoodistoType koodisto = koodistoRESTService.getKoodistoByUri(koodistoUri, koodistoVersio).getValue();
        assertEquals(koodistoUri, koodisto.getKoodistoUri());
        assertEquals(koodistoVersio, koodisto.getVersio());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetNonExistingKoodistoByUri() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            koodistoRESTService.getKoodistoByUri(koodistoUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetNonExistingKoodistoByUriAndVersio() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            final int koodistoVersio = 1;
            koodistoRESTService.getKoodistoByUri(koodistoUri, koodistoVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetKoodisByKoodistoUri() {
        final String koodistoUri = "http://koodisto17";

        List<KoodiType> koodis = koodistoRESTService.getKoodisByKoodisto(koodistoUri, null).getValue().getKoodi();
        assertEquals(2, koodis.size());
    }

    @Test
    public void testGetKoodisByKoodistoUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;

        List<KoodiType> koodis =
                koodistoRESTService.getKoodisByKoodisto(koodistoUri, koodistoVersio).getValue().getKoodi();
        assertEquals(1, koodis.size());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByNonExistingKoodistoUri() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            koodistoRESTService.getKoodisByKoodisto(koodistoUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByNonExistingKoodistoUriAndVersio() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            final int koodistoVersio = 1;
            koodistoRESTService.getKoodisByKoodisto(koodistoUri, koodistoVersio);
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

        List<KoodiType> koodis =
                koodistoRESTService.getKoodisByArvo(koodistoUri, koodiArvo, null).getValue().getKoodi();
        assertEquals(1, koodis.size());

        KoodiType koodi = koodis.get(0);
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

        List<KoodiType> koodis =
                koodistoRESTService.getKoodisByArvo(koodistoUri, koodiArvo, koodistoVersio).getValue().getKoodi();
        assertEquals(1, koodis.size());

        KoodiType koodi = koodis.get(0);
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodisByArvoWithNonExistingKoodistoUri() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            final String koodiArvo = "123";
            koodistoRESTService.getKoodisByArvo(koodistoUri, koodiArvo, null);
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
            koodistoRESTService.getKoodisByArvo(koodistoUri, koodiArvo, koodistoVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetKoodiByUriWithKoodistoUri() {
        final String koodistoUri = "http://koodisto17";
        final String koodiUri = "471";
        final int koodiVersio = 2;

        KoodiType koodi = koodistoRESTService.getKoodiByUri(koodistoUri, koodiUri, null).getValue();
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetKoodiByUriWithKoodistoUriAndVersio() {
        final String koodistoUri = "http://koodisto17";
        final int koodistoVersio = 1;
        final String koodiUri = "471";
        final int koodiVersio = 1;

        KoodiType koodi = koodistoRESTService.getKoodiByUri(koodistoUri, koodiUri, koodistoVersio).getValue();
        assertEquals(koodiUri, koodi.getKoodiUri());
        assertEquals(koodiVersio, koodi.getVersio());
    }

    @Test(expected = KoodistoNotFoundException.class)
    public void testGetKoodiByUriWithNonExistingKoodistoUri() throws Throwable {
        try {
            final String koodistoUri = "ei-ole-olemassa";
            final String koodiUri = "471";
            koodistoRESTService.getKoodiByUri(koodistoUri, koodiUri, null);
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
            koodistoRESTService.getKoodiByUri(koodistoUri, koodiUri, koodistoVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetKoodiByNonExistingUri() throws Throwable {
        try {
            final String koodistoUri = "http://koodisto17";
            final String koodiUri = "ei-ole-olemassa";
            koodistoRESTService.getKoodiByUri(koodistoUri, koodiUri, null);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetAlakoodisByKoodiUri() {
        final String koodiUri = "473";

        List<KoodiType> koodis = koodistoRESTService.getAlakoodis(koodiUri, null).getValue().getKoodi();
        assertEquals(1, koodis.size());
        KoodiType koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetAlakoodisByKoodiUriAndVersio() {
        final String koodiUri = "473";
        final Integer koodiVersio = 1;

        List<KoodiType> koodis = koodistoRESTService.getAlakoodis(koodiUri, koodiVersio).getValue().getKoodi();
        assertEquals(1, koodis.size());
        KoodiType koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetYlakoodisByKoodiUri() {
        final String koodiUri = "474";

        List<KoodiType> koodis = koodistoRESTService.getYlakoodis(koodiUri, null).getValue().getKoodi();
        assertEquals(2, koodis.size());
        KoodiType koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetYlakoodisByKoodiUriAndVersio() {
        final String koodiUri = "474";
        final Integer koodiVersio = 1;

        List<KoodiType> koodis = koodistoRESTService.getYlakoodis(koodiUri, koodiVersio).getValue().getKoodi();
        assertEquals(2, koodis.size());
        KoodiType koodi = koodis.get(0);

        final String alakoodiUri = "475";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetRinnasteinenByKoodiUri() {
        final String koodiUri = "475";

        List<KoodiType> koodis = koodistoRESTService.getRinnasteinenKoodis(koodiUri, null).getValue().getKoodi();
        assertEquals(1, koodis.size());
        KoodiType koodi = koodis.get(0);

        final String alakoodiUri = "474";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test
    public void testGetRinnasteinenByKoodiUriAndVersio() {
        final String koodiUri = "475";
        final Integer koodiVersio = 1;

        List<KoodiType> koodis = koodistoRESTService.getRinnasteinenKoodis(koodiUri, koodiVersio).getValue().getKoodi();
        assertEquals(1, koodis.size());
        KoodiType koodi = koodis.get(0);

        final String alakoodiUri = "474";
        final int alakoodiVersio = 1;

        assertEquals(alakoodiUri, koodi.getKoodiUri());
        assertEquals(alakoodiVersio, koodi.getVersio());
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetAlakoodiByNonExistingKoodiUri() throws Throwable {
        try {
            final String koodiUri = "ei-ole-olemassa";
            koodistoRESTService.getAlakoodis(koodiUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetAlakoodiByNonExistingKoodiUriAndVersio() throws Throwable {
        try {
            final String koodiUri = "475";
            final Integer koodiVersio = 2;
            koodistoRESTService.getAlakoodis(koodiUri, koodiVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetYlakoodiByNonExistingKoodiUri() throws Throwable {
        try {
            final String koodiUri = "ei-ole-olemassa";
            koodistoRESTService.getYlakoodis(koodiUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetYlakoodiByNonExistingKoodiUriAndVersio() throws Throwable {
        try {
            final String koodiUri = "475";
            final Integer koodiVersio = 2;
            koodistoRESTService.getYlakoodis(koodiUri, koodiVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetRinnasteinenByNonExistingKoodiUri() throws Throwable {
        try {
            final String koodiUri = "ei-ole-olemassa";
            koodistoRESTService.getRinnasteinenKoodis(koodiUri, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test(expected = KoodiNotFoundException.class)
    public void testGetRinnasteinenByNonExistingKoodiUriAndVersio() throws Throwable {
        try {
            final String koodiUri = "475";
            final Integer koodiVersio = 2;
            koodistoRESTService.getRinnasteinenKoodis(koodiUri, koodiVersio);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetKoodistoXsdSkeema() {
        final String koodistoUri = "http://koodisto17";
        String xsd = koodistoRESTService.getKoodistoXsdSkeema(koodistoUri, null).replaceAll("\\s", "");
        try {
            Assert.assertEquals(
                    "<?xmlversion=\"1.0\"?><xs:schemaxmlns:xs=\"http://www.w3.org/2001/XMLSchema\"targetNamespace=\"http://service.koodisto.sade.vm.fi/types/koodisto\"xmlns=\"http://service.koodisto.sade.vm.fi/types/koodisto\"elementFormDefault=\"qualified\"><xs:simpleTypename=\"httpkoodisto17\"><xs:restrictionbase=\"xs:string\"><xs:enumerationvalue=\"29\"><xs:annotation><xs:documentationxml:lang=\"fi\">koodi31</xs:documentation></xs:annotation></xs:enumeration><xs:enumerationvalue=\"28\"><xs:annotation><xs:documentationxml:lang=\"fi\">koodi30</xs:documentation></xs:annotation></xs:enumeration></xs:restriction></xs:simpleType></xs:schema>"
                    , xsd);
        } catch (ComparisonFailure e) {
            Assert.assertEquals(
                    "<?xmlversion=\"1.0\"?><xs:schemaxmlns:xs=\"http://www.w3.org/2001/XMLSchema\"targetNamespace=\"http://service.koodisto.sade.vm.fi/types/koodisto\"xmlns=\"http://service.koodisto.sade.vm.fi/types/koodisto\"elementFormDefault=\"qualified\"><xs:simpleTypename=\"httpkoodisto17\"><xs:restrictionbase=\"xs:string\"><xs:enumerationvalue=\"28\"><xs:annotation><xs:documentationxml:lang=\"fi\">koodi30</xs:documentation></xs:annotation></xs:enumeration><xs:enumerationvalue=\"29\"><xs:annotation><xs:documentationxml:lang=\"fi\">koodi31</xs:documentation></xs:annotation></xs:enumeration></xs:restriction></xs:simpleType></xs:schema>"
                    , xsd);
        }
    }

}
