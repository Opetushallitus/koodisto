package fi.vm.sade.koodisto.util;

import fi.vm.sade.generic.common.EnhancedProperties;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Caching koodisto REST client that uses CachingRestClient which in turn uses http-commons and http-commons-cache under the hood.
 * 
 * @see CachingRestClient
 * @author Antti Salonen
 */
public class CachingKoodistoClient implements KoodistoClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // NOTE! cachingRestClient is static because we need application-scoped rest cache for koodisto-service
    private static CachingRestClient cachingRestClient = new CachingRestClient();
    private String koodistoServiceWebappUrl;

    public CachingKoodistoClient(String koodistoServiceWebappUrl) {
        this.koodistoServiceWebappUrl = koodistoServiceWebappUrl;
    }

    public CachingKoodistoClient() {
        // read koodisto-service url from common.properties
        FileInputStream fis = null;
        try {
            Properties props = new EnhancedProperties();
            fis = new FileInputStream(new File(System.getProperty("user.home"), "oph-configuration/common.properties"));
            props.load(fis);
            this.koodistoServiceWebappUrl = props.getProperty("cas.service.koodisto-service");
        } catch (IOException e) {
            throw new RuntimeException("failed to read common.properties", e);
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException ignore) {
                }
            }
        }

    }

    public void setKoodistoServiceWebappUrl(String koodistoServiceWebappUrl) {
        this.koodistoServiceWebappUrl = koodistoServiceWebappUrl;
    }

    private <T> T get(String uri, Class<? extends T> resultClass) {
        try {
            long t0 = System.currentTimeMillis();
            T result = cachingRestClient.get(koodistoServiceWebappUrl + "/rest/json" + uri, resultClass);
            logger.debug("koodisto rest get done, uri: {}, took: {} ms, cacheStatus: {} result: {}", new Object[] { uri, (System.currentTimeMillis() - t0),
                    cachingRestClient.getCacheStatus(), result });
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public KoodistoType getKoodistoTypeByUri(String koodistoUri) {
        return get("/" + koodistoUri, KoodistoType.class);
    }

    @Override
    public List<KoodiType> getKoodisForKoodisto(String koodistoUri, Integer koodistoVersio) {
        return Arrays.asList(get("/" + koodistoUri + "/koodi" + (koodistoVersio != null ? "?koodistoVersio=" + koodistoVersio : ""), KoodiType[].class));
    }

    @Override
    public List<KoodiType> getKoodisForKoodisto(String koodistoUri, Integer koodistoVersio, boolean onlyValidKoodis) {
        return Arrays.asList(get("/" + koodistoUri + "/koodi?onlyValidKoodis=" + onlyValidKoodis + ""
                + (koodistoVersio != null ? "&koodistoVersio=" + koodistoVersio : ""), KoodiType[].class));
    }

    @Override
    public List<KoodistoRyhmaListType> getKoodistoRyhmas() {
        return Arrays.asList(get("", KoodistoRyhmaListType[].class));
    }

    @Override
    public List<KoodiType> getAlakoodis(String koodiUri) {
        return Arrays.asList(get("/relaatio/sisaltyy-alakoodit/" + koodiUri, KoodiType[].class));
    }

    @Override
    public List<KoodiType> getYlakoodis(String koodiUri) {
        return Arrays.asList(get("/relaatio/sisaltyy-ylakoodit/" + koodiUri, KoodiType[].class));
    }

    @Override
    public List<KoodiType> searchKoodis(SearchKoodisCriteriaType sc) {
        return Arrays.asList(get(buildSearchKoodisUri(sc), KoodiType[].class));
    }

    @Override
    public String buildSearchKoodisUri(SearchKoodisCriteriaType sc) {
        return "/searchKoodis?"
                +param("koodiUris", sc.getKoodiUris())
                +param("koodiArvo", sc.getKoodiArvo())
                +param("koodiTilas", sc.getKoodiTilas())
                +param("validAt", sc.getValidAt())
                +param("koodiVersio", sc.getKoodiVersio())
                +param("koodiVersioSelection", sc.getKoodiVersioSelection());
    }

    private String param(String name, Object val) {
        if (val == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        if (val instanceof Collection) {
            for (Object o : (Collection) val) {
                sb.append(param(name, o));
            }
        } else {
            String valString;
            if (val instanceof XMLGregorianCalendar) {
                valString = new SimpleDateFormat("yyyy-MM-dd").format(((XMLGregorianCalendar) val).toGregorianCalendar().getTime());
            } else {
                valString = val.toString();
            }

            sb.append("&").append(name).append("=").append(valString);
        }

        return sb.toString();
    }
}
