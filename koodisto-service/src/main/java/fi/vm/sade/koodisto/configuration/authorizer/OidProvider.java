package fi.vm.sade.koodisto.configuration.authorizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OidProvider {
    private final String callerId = "KOODISTO";

    @Value("${cas.service.organisaatio-service}")
    private String organisaatioServiceUrl;
    @Value("${root.organisaatio.oid}")
    private String rootOrganisaatioOid;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public List<String> getSelfAndParentOids(String organisaatioOid) {
        try {
            String url = organisaatioServiceUrl+"/rest/organisaatio/"+organisaatioOid+"/parentoids";
            String result = httpGet(url, 200);
            return Arrays.asList(result.split("/"));
        } catch (Exception e) {
            log.warn("failed to getSelfAndParentOids, exception: "+e+", returning only rootOrganisaatioOid and organisaatioOid");
            return Arrays.asList(rootOrganisaatioOid, organisaatioOid);
        }
    }

    private String httpGet(String url, int expectedStatus) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .setHeader("Caller-Id", callerId)
                .timeout(Duration.ofSeconds(60))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("{} {} {}", request.method(), request.uri(), response.statusCode());

            if (response.statusCode() == expectedStatus) {
                return response.body();
            } else {
                throw new RuntimeException("failed to call '"+url+"', invalid status: "+response.statusCode());
            }
        } catch (final Exception e) {
            throw new RuntimeException("failed to call '"+url+"': "+e, e);
        }
    }
}
