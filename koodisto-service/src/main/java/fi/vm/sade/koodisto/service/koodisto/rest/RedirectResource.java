package fi.vm.sade.koodisto.service.koodisto.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class RedirectResource {
    @GetMapping("/swagger/index.html")
    public void swaggerRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/rest/api-docs?url=/koodisto-service/rest/swagger.json");
    }
    @GetMapping("/swagger")
    public void swaggerRootRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/rest/api-docs?url=/koodisto-service/rest/swagger.json");
    }
}
