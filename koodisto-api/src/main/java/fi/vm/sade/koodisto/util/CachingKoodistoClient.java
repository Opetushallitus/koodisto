package fi.vm.sade.koodisto.util;

import static fi.vm.sade.javautils.httpclient.OphHttpClient.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpRequest;
import fi.vm.sade.javautils.httpclient.OphHttpResponse;
import fi.vm.sade.javautils.httpclient.OphHttpResponseHandler;
import fi.vm.sade.javautils.httpclient.apache.ApacheOphHttpClient;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisVersioSelectionType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.properties.OphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

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
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public KoodistoClient setCallerId(String callerId) {
        client.setCallerId(callerId);
        return this;
    }


    private <T> T execute(OphHttpRequest resource, final TypeReference<T> type) {
        return resource
                .accept(JSON)
                .execute(new OphHttpResponseHandler<T>() {
                    public T handleResponse(OphHttpResponse response) throws IOException {
                        return mapper.readValue(response.asInputStream(), type);
                    }
                });
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
        OphHttpRequest request = buildSearchKoodiRequest(sc);
        return execute(request, new TypeReference<List<KoodiType>>() {});
    }

    @Override
    public OphHttpRequest buildSearchKoodiRequest(SearchKoodisCriteriaType sc) {
        OphHttpRequest request = client.get("koodisto-service.searchKoodis");

        List<String> koodiUris = sc.getKoodiUris();
        if (koodiUris != null && !koodiUris.isEmpty()) {
            request = request.param("koodiUris", koodiUris.toArray());
        }

        String koodiArvo = sc.getKoodiArvo();
        if (koodiArvo != null) {
            request = request.param("koodiArvo", koodiArvo);
        }

        List<TilaType> koodiTilas = sc.getKoodiTilas();
        if (koodiTilas != null && !koodiTilas.isEmpty()) {
            request = request.param("koodiTilas", koodiTilas.toArray());
        }

        XMLGregorianCalendar validAt = sc.getValidAt();
        if (validAt != null) {
            request = request.param("validAt", new SimpleDateFormat("yyyy-MM-dd").format(validAt.toGregorianCalendar().getTime()));
        }

        Integer koodiVersio = sc.getKoodiVersio();
        if (koodiVersio != null) {
            request = request.param("koodiVersio", koodiVersio);
        }

        SearchKoodisVersioSelectionType koodiVersioSelection = sc.getKoodiVersioSelection();
        if (koodiVersioSelection != null) {
            request = request.param("koodiVersioSelection", koodiVersioSelection);
        }

        return request;
    }
}
