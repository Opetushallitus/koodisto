package fi.vm.sade.koodisto.util;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import fi.vm.sade.generic.rest.JettyJersey;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import junit.framework.Assert;
import org.junit.Test;

import java.util.GregorianCalendar;

/**
 * @author Antti Salonen
 */
public class CachingKoodistoClientTest { // note that because of jersey dependencies CachingKoodistoClient class is in koodisto-api, but the test is here

    @Test
    public void testCachingKoodistoClient() throws Exception {
        JettyJersey.startServer("fi.vm.sade.koodisto.util", "fi.vm.sade.generic.rest.CacheableJerseyFilter");
        CachingKoodistoClient koodistoClient = new CachingKoodistoClient("http://localhost:"+JettyJersey.getPort());

        // first call
        Assert.assertEquals("mockKoodistoRyhma1", koodistoClient.getKoodistoRyhmas().get(0).getKoodistoRyhmaUri());
        // second call comes from cache, counter not raised
        Assert.assertEquals("mockKoodistoRyhma1", koodistoClient.getKoodistoRyhmas().get(0).getKoodistoRyhmaUri());
        // wait until cache expired, counter raised
        Thread.sleep(2000);
        Assert.assertEquals("mockKoodistoRyhma2", koodistoClient.getKoodistoRyhmas().get(0).getKoodistoRyhmaUri());
        JettyJersey.stopServer();
    }

    @Test
    public void testSearchKoodis() {
        // test empty searchcriteria
        CachingKoodistoClient client = new CachingKoodistoClient("testurl");
        SearchKoodisCriteriaType sc = new SearchKoodisCriteriaType();
        Assert.assertEquals("/searchKoodis?", client.buildSearchKoodisUri(sc));

        // test full searchcriteria with multiple values for list
        sc.getKoodiUris().add("475");
        sc.getKoodiUris().add("476");
        sc.setKoodiArvo("3");
        sc.getKoodiTilas().add(TilaType.HYVAKSYTTY);
        sc.setValidAt(new XMLGregorianCalendarImpl(new GregorianCalendar(2013, 0, 1)));
        sc.setKoodiVersio(1);
        sc.setKoodiVersioSelection(SearchKoodisVersioSelectionType.ALL);
        Assert.assertEquals("/searchKoodis?&koodiUris=475&koodiUris=476&koodiArvo=3&koodiTilas=HYVAKSYTTY&validAt=2013-01-01&koodiVersio=1&koodiVersioSelection=ALL", client.buildSearchKoodisUri(sc));
    }

}
