package fi.vm.sade.koodisto.config;

import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class ConfigController {
    
    @Autowired
    private OphProperties urlProperties;

    @Value("${auth.mode:}")
    private String authMode;

    @Value("${cas.myroles}")
    private String casUrl;
    
    @Value("${koodisto-ui.session-keepalive-interval.seconds}")
    private Integer sessionKeepAliveIntervalInSeconds;
    
    @Value("${koodisto-ui.session-max-idle-time.seconds}")
    private Integer maxSessionIdleTimeInSeconds;

    @RequestMapping(value = "/configuration.js", method = RequestMethod.GET, produces = "text/javascript")
    @ResponseBody
    public String index() {
        StringBuilder b = new StringBuilder();
        append(b, "SERVICE_URL_BASE", urlProperties.getProperty("koodisto-service.baseUrl"));
        append(b, "ORGANIZATION_SERVICE_URL_BASE", urlProperties.getProperty("organization-service.baseUrl"));
        append(b, "ORGANIZATION_SERVICE_URL_BY_OID", urlProperties.getProperty("organization-service.byOid", ":oid"));
        append(b, "ORGANIZATION_SERVICE_URL_HAE", urlProperties.getProperty("organization-service.hae"));
        append(b, "TEMPLATE_URL_BASE", "");

        append(b, "CAS_URL", casUrl);
        append(b, "SESSION_KEEPALIVE_INTERVAL_IN_SECONDS", Integer.toString(sessionKeepAliveIntervalInSeconds));
        append(b, "MAX_SESSION_IDLE_TIME_IN_SECONDS", Integer.toString(maxSessionIdleTimeInSeconds));
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
