package fi.vm.sade.koodisto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConfigController {

    private final UrlConfiguration urlProperties;

    @Autowired
    public ConfigController(UrlConfiguration urlProperties) {
        this.urlProperties = urlProperties;
    }

    /**
     * Used by oph-urls-js.js
     * @return front url properties as js object set to window
     */
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
