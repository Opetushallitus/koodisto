package fi.vm.sade.koodisto.configuration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserInterfaceRedirectController {
    @GetMapping("/")
    public String redirectToUI() {
        return "redirect:/ui";
    }
}