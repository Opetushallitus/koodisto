package fi.vm.sade.koodisto.service.koodisto.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("session")
@Api(value = "/rest/session", description = "Sessionhallinta")
public class SessionResource {

    @GetMapping(
            value = "/maxinactiveinterval",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(
            value = "Palauttaa session erääntymisen aikarajan sekunteina. Luo session jos ei ole ennestään olemassa.",
            notes = "Tarvitsee HTTP kutsun, jossa on session id",
            response = String.class)
    public String maxInactiveInterval(HttpServletRequest request) {
        return Integer.toString(request.getSession().getMaxInactiveInterval());
    }
}
