package fi.vm.sade.koodisto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
public class AccessLogValveTest {

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void accessLogIsWrittenInExpectedJsonFormatForRealRequest(CapturedOutput output)
      throws Exception {
    var userAgent = UUID.randomUUID().toString();
    var callerId = UUID.randomUUID().toString();
    var xForwardedFor = UUID.randomUUID().toString();
    var referer = UUID.randomUUID().toString();
    var path = "/actuator/health";

    HttpHeaders headers = new HttpHeaders();
    headers.add("User-Agent", userAgent);
    headers.add("Caller-Id", callerId);
    headers.add("X-Forwarded-For", xForwardedFor);
    headers.add("Referer", referer);

    ResponseEntity<String> response =
        restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    assertEquals(200, response.getStatusCode().value());

    String accessLogLine = waitForAccessLogLine(output, path);
    JsonNode json = new ObjectMapper().readTree(accessLogLine);

    assertEquals("GET", json.get("requestMethod").asText());
    assertEquals("GET " + path + " HTTP/1.1", json.get("request").asText());
    assertEquals("200", json.get("responseCode").asText());
    assertEquals(userAgent, json.get("user-agent").asText());
    assertEquals(callerId, json.get("caller-id").asText());

    assertEquals(xForwardedFor, json.get("x-forwarded-for").asText());
    assertEquals(referer, json.get("referer").asText());

    for (String field :
        new String[] {
          "timestamp",
          "responseCode",
          "requestMapping",
          "request",
          "responseTime",
          "requestMethod",
          "user-agent",
          "callerHenkiloOid",
          "caller-id",
          "x-forwarded-for",
          "remote-ip",
          "response-size",
          "referer",
        }) {
      assertNotNull(json.get(field), "missing field in access log JSON: " + field);
    }
  }

  private static String waitForAccessLogLine(CapturedOutput output, String uriFragment) {
    return Awaitility.await()
        .atMost(2, TimeUnit.SECONDS)
        .pollInterval(20, TimeUnit.MILLISECONDS)
        .until(() -> findAccessLogLine(output, uriFragment), Optional::isPresent)
        .orElseThrow();
  }

  private static Optional<String> findAccessLogLine(CapturedOutput output, String uriFragment) {
    return output
        .getOut()
        .lines()
        .filter(line -> line.startsWith("{") && line.contains(uriFragment))
        .reduce((first, second) -> second);
  }
}
