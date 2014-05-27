package fi.vm.sade.koodisto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class ConfigController {

    @Value("${koodisto-ui.koodisto-service-url.rest}")
    private String koodistoServiceRestURL;

    @Value("${koodisto-ui.organisaatio-service-url}")
    private String organisaatioServiceURL;

    @Value("${auth.mode:}")
    private String authMode;

    @Value("${valintalaskenta-ui.cas.url:/cas/myroles}")
    private String casUrl;
    
    @Value("${koodisto-ui.session-keepalive-interval.seconds}")
    private Integer sessionKeepAliveIntervalInSeconds;

    @RequestMapping(value = "/configuration.js", method = RequestMethod.GET, produces = "text/javascript")
    @ResponseBody
    public String index() {
        StringBuilder b = new StringBuilder();
        append(b, "SERVICE_URL_BASE", koodistoServiceRestURL);
        append(b, "ORGANIZATION_SERVICE_URL_BASE", organisaatioServiceURL);

        append(b, "TEMPLATE_URL_BASE", "");

        append(b, "CAS_URL", casUrl);
        append(b, "SESSION_KEEPALIVE_INTERVAL_IN_SECONDS", Integer.toString(sessionKeepAliveIntervalInSeconds));
        if (!authMode.isEmpty()) {
            append(b, "AUTH_MODE", authMode);

        }
        return b.toString();
    }

    private void append(StringBuilder b, String key, String value) {
        b.append(key);
        b.append(" = \"");
        b.append(value);
        b.append("\";\n");
    }

}
