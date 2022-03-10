package fi.vm.sade.koodisto.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/configuration")
public class Configuration {
    @GetMapping(path = "/frontProperties.js", produces = "text/javascript")
    public String hello() {
        return "window.urlProperties={}";
    }
}
