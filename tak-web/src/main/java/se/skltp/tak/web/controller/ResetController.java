package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import se.skltp.tak.web.service.ConfigurationService;
import se.skltp.tak.web.service.ResetService;

import javax.annotation.PostConstruct;

@Controller
@EnableAsync
public class ResetController {

    @Autowired
    SecurityManager securityManager;

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    ResetService resetService;

    @PostConstruct
    private void initStaticSecurityManager() {
        // Needed by Shiro + StreamingResponseBody
        SecurityUtils.setSecurityManager(securityManager);
    }

    @RequestMapping("/reset")
    public String index(Model model, @RequestParam(required = false) String pubVersion) {
        model.addAttribute("takServicesUrls", configurationService.getTakServiceResetUrls());
        model.addAttribute("applicationsUrls", configurationService.getApplicationResetUrls());
        if (pubVersion != null && !pubVersion.isEmpty()) {
            model.addAttribute("pubVersion", pubVersion);
        }
        return "reset/index";
    }

    @PostMapping("/reset/perform")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> performReset(@RequestParam(defaultValue = "false") boolean resetTakServices,
                                                              @RequestParam(defaultValue = "false") boolean resetApplications,
                                                              @RequestParam(required = false) String pubVersion) {
        StreamingResponseBody stream = out -> {
            if (resetTakServices) resetService.resetTakServices(out, pubVersion);
            if (resetApplications) resetService.resetApplications(out);
        };
        return new ResponseEntity<>(stream, HttpStatus.OK);
    }
}
