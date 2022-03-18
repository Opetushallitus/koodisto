package fi.vm.sade.koodisto.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;



@RestController()
@RequestMapping({"/rest/session"})
public class SessionResource {
    @Autowired
    private HttpServletRequest context;
    @GetMapping(path = "/maxinactiveinterval", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("isAuthenticated()")
    public String maxInactiveInterval() {
        return Integer.toString(context.getSession().getMaxInactiveInterval());
    }
}
