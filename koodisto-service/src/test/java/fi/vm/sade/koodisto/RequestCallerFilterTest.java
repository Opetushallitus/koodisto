package fi.vm.sade.koodisto;

import com.github.tomakehurst.wiremock.http.Cookie;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.wiremock.spring.ConfigureWireMock;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "host.virkailija=localhost",
                "cas.service=http://localhost/koodisto-service",
                "cas.base=${wiremock.server.baseUrl}/cas",
                "cas.login=${wiremock.server.baseUrl}/cas/login",
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${wiremock.server.baseUrl}/oauth2/jwks"
        })
@ConfigureWireMock(port = 18081)
@ExtendWith(OutputCaptureExtension.class)
public class RequestCallerFilterTest {

  private static final KeyPair KEY_PAIR = generateKeyPair();

  private static final String KEY_ID = "test-key-id";

  @LocalServerPort
  private int port;

  @Value("${wiremock.server.baseUrl}")
  private String wireMockBaseUrl;

  @Test
  public void logsCallerHenkiloOidWhenCallerAuthenticatedWithOauth2(CapturedOutput output)
          throws Exception {
    var token = generateToken("1.2.246.562.24.43006465835");
    stubFor(
            get(urlEqualTo("/oauth2/jwks"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(buildJwksResponse())));
    stubFor(
            post(urlEqualTo("/oauth2/token"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(buildTokenResponse(token))));

    var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

    var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/koodisto-service/rest/json/kieli"))
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();

    client.send(request, HttpResponse.BodyHandlers.ofString());
    Awaitility.await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
              assertThat(output).contains("\"callerHenkiloOid\": \"1.2.246.562.24.43006465835\"");
            });
  }

  @Test
  public void logsCallerHenkiloOidWhenCallerAuthenticatedWithCasVirkailija(CapturedOutput output)
          throws Exception {
    var cookie = getCookie("/cas-virkailija");
    var ticket = "ST-30-JVB-gESc2Yc3S-zV25JOHbVEeBo-ip-10-0-55-20";
    // Stub cas-virkailija endpoints
    stubFor(
            get(urlEqualTo(
                    "/cas/login?service="
                            + URLEncoder.encode(
                            "http://localhost:" + port + "/koodisto-service", StandardCharsets.UTF_8)))
                    .willReturn(
                            aResponse()
                                    .withStatus(302)
                                    .withHeader(
                                            "Location",
                                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check?ticket="
                                                    + ticket)
                                    .withHeader("Set-Cookie", cookie.toString())));
    stubFor(
            post(urlEqualTo(
                    "/cas/login?service="
                            + URLEncoder.encode(
                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check",
                            StandardCharsets.UTF_8)))
                    .willReturn(
                            aResponse()
                                    .withStatus(302)
                                    .withHeader(
                                            "Location",
                                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check?ticket="
                                                    + ticket)
                                    .withHeader("Set-Cookie", cookie.toString())));
    stubFor(
            get(urlEqualTo(
                    "/cas/login?service="
                            + URLEncoder.encode(
                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check",
                            StandardCharsets.UTF_8)))
                    .willReturn(
                            aResponse()
                                    .withStatus(302)
                                    .withHeader(
                                            "Location",
                                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check?ticket="
                                                    + ticket)
                                    .withHeader("Set-Cookie", cookie.toString())));
    // validate ticket, provide cas response
    stubFor(
            get(urlEqualTo(
                    "/cas/p3/proxyValidate?ticket=%s&service=%s"
                            .formatted(
                                    ticket,
                                    URLEncoder.encode(
                                            "http://localhost/koodisto-service/j_spring_cas_security_check",
                                            StandardCharsets.UTF_8))))
                    .willReturn(
                            aResponse()
                                    .withStatus(200)
                                    .withBody(readResource("/cas-virkailija-auth-response.xml"))));

    var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

    var loginRequest =
            HttpRequest.newBuilder()
                    .uri(
                            URI.create(
                                    wireMockUrl(
                                            "/cas/login?service="
                                                    + URLEncoder.encode(
                                                    "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check",
                                                    StandardCharsets.UTF_8))))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

    var loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());

    var responseHeaders = loginResponse.headers();

    var cookies = responseHeaders.allValues("Set-Cookie").get(0);

    var summaryRequest =
            HttpRequest.newBuilder()
                    .header("Cookie", cookies)
                    .uri(URI.create("http://localhost:" + port + "/koodisto-service/rest/json/kieli"))
                    .GET()
                    .build();

    client.send(summaryRequest, HttpResponse.BodyHandlers.ofString());

    Awaitility.await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
              assertThat(output).contains("\"callerHenkiloOid\": \"1.2.246.562.98.1234567890\"");
            });
  }

  @Test
  public void doesNotLogCallerHenkiloOidWhenUserIsAuthenticatedThroughCasOppija(CapturedOutput output)
          throws IOException, InterruptedException {
    var ticket = "ST-30-JVB-gESc2Yc3S-zV25JOHbVEeBo-ip-10-0-55-20";
    var cookie = getCookie("/cas-oppija");
    stubFor(
            get(urlEqualTo(
                    "/cas/login?service="
                            + URLEncoder.encode(
                            "http://localhost:" + port + "/koodisto-service", StandardCharsets.UTF_8)))
                    .willReturn(
                            aResponse()
                                    .withStatus(302)
                                    .withHeader(
                                            "Location",
                                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check?ticket="
                                                    + ticket)
                                    .withHeader("Set-Cookie", cookie.toString())));
    stubFor(
            post(urlEqualTo(
                    "/cas/login?service="
                            + URLEncoder.encode(
                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check",
                            StandardCharsets.UTF_8)))
                    .willReturn(
                            aResponse()
                                    .withStatus(302)
                                    .withHeader(
                                            "Location",
                                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check?ticket="
                                                    + ticket)
                                    .withHeader("Set-Cookie", cookie.toString())));
    stubFor(
            get(urlEqualTo(
                    "/cas/login?service="
                            + URLEncoder.encode(
                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check",
                            StandardCharsets.UTF_8)))
                    .willReturn(
                            aResponse()
                                    .withStatus(302)
                                    .withHeader(
                                            "Location",
                                            "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check?ticket="
                                                    + ticket)
                                    .withHeader("Set-Cookie", cookie.toString())));
    // service is missing the port since mocking the property in config to have the port is difficult, so it's just "http://localhost/koodisto-service"
    stubFor(
            get(urlEqualTo(
                    "/cas/p3/proxyValidate?ticket=%s&service=%s"
                            .formatted(
                                    ticket,
                                    URLEncoder.encode(
                                            "http://localhost/koodisto-service/j_spring_cas_security_check",
                                            StandardCharsets.UTF_8))))
                    .willReturn(
                            aResponse()
                                    .withStatus(200)
                                    .withBody(readResource("/cas-oppija-auth-response.xml"))));

    var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

    var request =
            HttpRequest.newBuilder()
                    .uri(
                            URI.create(
                                    wireMockUrl(
                                            "/cas/login?service="
                                                    + URLEncoder.encode(
                                                    "http://localhost:" + port + "/koodisto-service/j_spring_cas_security_check",
                                                    StandardCharsets.UTF_8))))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

    var response = client.send(request, HttpResponse.BodyHandlers.ofString());

    var responseHeaders = response.headers();

    var cookies = responseHeaders.allValues("Set-Cookie").get(0);

    var tiedotteetRequest =
            HttpRequest.newBuilder()
                    .header("Cookie", cookies)
                    .uri(URI.create("http://localhost:" + port + "/koodisto-service/rest/json/kieli"))
                    .GET()
                    .build();

    client.send(tiedotteetRequest, HttpResponse.BodyHandlers.ofString());

    Awaitility.await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
              assertThat(output).contains("\"callerHenkiloOid\": \"-\"");
              assertThat(output).doesNotContain("\"callerHenkiloOid\": \"1.2.246.562.98.19783284870\"");
            });
  }

  private static KeyPair generateKeyPair() {
    try {
      KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
      gen.initialize(2048);
      return gen.generateKeyPair();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String generateToken(String subject) throws Exception {
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
            .subject(subject)
            .issuer(wireMockBaseUrl)
            .audience("oppijanumerorekisteri-service")
            .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
            .issueTime(Date.from(Instant.now()))
            .claim(
                    "roles",
                    Map.of(
                            "1.2.246.562.10.00000000001",
                            List.of("APP_OPPIJANUMEROREKISTERI_REKISTERINPITAJA", "APP_OPPIJANUMEROREKISTERI_REKISTERINPITAJA_1.2.246.562.10.00000000001")))
            .claim("sub", subject)
            .build();

    SignedJWT jwt = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
            claims
    );
    jwt.sign(new RSASSASigner(KEY_PAIR.getPrivate()));
    return jwt.serialize();
  }

  private static String buildJwksResponse() throws Exception {
    var publicKey = (RSAPublicKey) KEY_PAIR.getPublic();

    var rsaKey = new RSAKey.Builder(publicKey)
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID(KEY_ID)
            .build();

    var jwkSet = new JWKSet(rsaKey);
    return jwkSet.toString(); // Serializes to {"keys":[{...}]}
  }

  private String buildTokenResponse(String accessToken) throws Exception {
    return """
            {
              "access_token": "%s",
              "token_type": "Bearer",
              "expires_in": 3600,
              "roles": ["1.2.246.562.10.00000000001", "APP_OPPIJANUMEROREKISTERI_REKISTERINPITAJA", "APP_OPPIJANUMEROREKISTERI_REKISTERINPITAJA_1.2.246.562.10.00000000001"]
            }
            """.formatted(accessToken);
  }

  private String wireMockUrl(String path) {
    return wireMockBaseUrl + path;
  }

  private Cookie getCookie(String path) {
    var tgc = "TGC=asd";
    return new Cookie(
            path, tgc, "SameSite=none", "SameSite=None", "Secure", "HttpOnly", "Path=" + path);
  }

  private String readResource(String path) {
    return new String(readBytes(path));
  }

  private byte[] readBytes(String path) {
    try (var inputStream = getClass().getResourceAsStream(path)) {
      if (inputStream == null) {
        throw new RuntimeException("Resource not found: " + path);
      }
      return inputStream.readAllBytes();
    } catch (Exception e) {
      throw new RuntimeException("Failed to read resource: " + path, e);
    }
  }
}
