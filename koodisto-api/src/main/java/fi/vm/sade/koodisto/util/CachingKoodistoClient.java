package fi.vm.sade.koodisto.util;

import fi.vm.sade.javautils.httpclient.*;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.properties.OphProperties;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.List;

import static fi.vm.sade.javautils.httpclient.OphHttpClient.JSON;

/**
 * Caching koodisto REST client.
 * @see OphHttpClient
 */
public class CachingKoodistoClient implements KoodistoClient {

    private final ObjectMapper mapper;
    private Logger logger = LoggerFactory.getLogger(getClass());


    // NOTE! client is static because we need application-scoped rest cache for koodisto-service
    private static OphHttpClient client = new OphHttpClient(ApacheOphHttpClient
            .createCustomBuilder()
            .createCachingClient(50 * 1000, 10 * 1024 * 1024)
            .setDefaultConfiguration(5 * 60 * 1000, 60).build(), "koodisto-client");

    public CachingKoodistoClient(String hostUrl) {
        OphProperties ophProperties = new OphProperties("/koodisto-client-url.properties").addDefault("url-koodisto", hostUrl);
        client.setUrlProperties(ophProperties);
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public CachingKoodistoClient setClientSubSystemCode(String clientSubSystemCode) {
        client.setClientSubSystemCode(clientSubSystemCode);
        return this;
    }

    private <T> T execute(OphHttpRequest resource, final TypeReference<T> type) {
        return resource
                .accept(JSON)
                .execute(response -> mapper.readValue(response.asInputStream(), type));
    }

    @Override
    public KoodistoType getKoodistoTypeByUri(String koodistoUri) {
        return execute(client.get("koodisto-service.getKoodistoTypeByUri", koodistoUri), new TypeReference<KoodistoType>() {
        });
    }

    @Override
    public List<KoodiType> getKoodisForKoodisto(String koodistoUri, Integer koodistoVersio) {
        return execute(
                client.get("koodisto-service.getKoodisForKoodisto", koodistoUri)
                        .param("koodistoVersio", koodistoVersio != null ? String.valueOf(koodistoVersio) : "")
                , new TypeReference<List<KoodiType>>() {
                });
    }

    @Override
    public List<KoodiType> getKoodisForKoodisto(String koodistoUri, Integer koodistoVersio, boolean onlyValidKoodis) {
        return execute(
                client.get("koodisto-service.getKoodisForKoodisto", koodistoUri)
                        .param("koodistoVersio", koodistoVersio != null ? String.valueOf(koodistoVersio) : "")
                        .param("onlyValidKoodis", onlyValidKoodis)
                , new TypeReference<List<KoodiType>>() {
                });
    }

    @Override
    public List<KoodistoRyhmaListType> getKoodistoRyhmas() {
        return execute(
                client.get("koodisto-service.getKoodistoRyhmas")
                , new TypeReference<List<KoodistoRyhmaListType>>() {
                });
    }

    @Override
    public List<KoodiType> getAlakoodis(String koodiUri) {
        return execute(
                client.get("koodisto-service.getAlakoodis", koodiUri)
                , new TypeReference<List<KoodiType>>() {
                });
    }

    @Override
    public List<KoodiType> getYlakoodis(String koodiUri) {
        return execute(
                client.get("koodisto-service.getYlakoodis", koodiUri)
                , new TypeReference<List<KoodiType>>() {
                });
    }

    @Override
    public List<KoodiType> getRinnasteiset(String koodiUri) {
        return execute(
                client.get("koodisto-service.getRinnasteiset", koodiUri)
                , new TypeReference<List<KoodiType>>() {
                });
    }

    @Override
    public List<KoodiType> searchKoodis(SearchKoodisCriteriaType sc) {
        return execute(
                client.get("koodisto-service.searchKoodis")
                        .param("koodiUris", sc.getKoodiUris())
                        .param("koodiArvo", sc.getKoodiArvo())
                        .param("koodiTilas", sc.getKoodiTilas())
                        .param("validAt", new SimpleDateFormat("yyyy-MM-dd").format(sc.getValidAt().toGregorianCalendar().getTime()))
                        .param("koodiVersio", sc.getKoodiVersio())
                        .param("koodiVersioSelection", sc.getKoodiVersioSelection())
                , new TypeReference<List<KoodiType>>() {
                });
    }
}
