package se.skltp.tak.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping("/")
    public String index() {
        return "home/index";
    }

    @RequestMapping("/favicon.ico")
    public String favicon() {
        return "redirect:/static/images/favicon.ico";
    }
}
