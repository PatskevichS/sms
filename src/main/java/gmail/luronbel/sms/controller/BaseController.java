package gmail.luronbel.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Simple way to provide html page.
 */
@ApiIgnore
@Controller
public class BaseController {

    @RequestMapping("/")
    private String root() {
        return "main.html";
    }
}
