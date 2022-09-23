package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping("/")
    public String index(ModelMap model) {

        model.addAttribute("administrator", SecurityUtils.getSubject().hasRole("Administrator"));
        return "home/index";
    }

    @RequestMapping("/favicon.ico")
    public String favicon() {
        return "redirect:/static/images/favicon.ico";
    }
}
