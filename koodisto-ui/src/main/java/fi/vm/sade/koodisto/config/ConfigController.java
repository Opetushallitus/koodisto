package fi.vm.sade.koodisto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConfigController {

    @Autowired
    private UrlConfiguration urlProperties;

    @Value("${auth.mode:}")
    private String authMode;
    
    @Value("${koodisto-ui.session-keepalive-interval.seconds}")
    private Integer sessionKeepAliveIntervalInSeconds;
    
    @Value("${koodisto-ui.session-max-idle-time.seconds}")
    private Integer maxSessionIdleTimeInSeconds;

    @RequestMapping(value = "/configuration.js", method = RequestMethod.GET, produces = "text/javascript")
    @ResponseBody
    public String index() {
        StringBuilder b = new StringBuilder();
        append(b, "window.SESSION_KEEPALIVE_INTERVAL_IN_SECONDS", Integer.toString(sessionKeepAliveIntervalInSeconds));
        append(b, "window.MAX_SESSION_IDLE_TIME_IN_SECONDS", Integer.toString(maxSessionIdleTimeInSeconds));
        if (!authMode.isEmpty()) {
            append(b, "AUTH_MODE", authMode);

        }
        return b.toString();
    }

    @RequestMapping(value = "/frontProperties.js", method = RequestMethod.GET, produces = "text/javascript")
    @ResponseBody
    public String frontProperties() {
        return "window.urlProperties=" + urlProperties.frontPropertiesToJson();
    }

    private void append(StringBuilder b, String key, String value) {
        b.append(key);
        b.append(" = \"");
        b.append(value);
        b.append("\";\n");
    }

}
