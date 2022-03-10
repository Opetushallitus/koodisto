package fi.vm.sade.koodisto.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController( )
@RequestMapping("/health")
public class HealthCheck {
    @GetMapping(path = "", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        return String.valueOf(System.currentTimeMillis());
    }
}
