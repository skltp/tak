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
import se.skltp.tak.web.config.NodeResetConfig;
import se.skltp.tak.web.config.ResetConfig;
import se.skltp.tak.web.dto.PodInfo;
import se.skltp.tak.web.service.K8sApiService;
import se.skltp.tak.web.service.ResetService;

import javax.annotation.PostConstruct;

@Controller
@EnableAsync
public class ResetController {

    @Autowired
    SecurityManager securityManager;

    @Autowired
    ResetService resetService;

    @Autowired
    K8sApiService k8sApiService;

    @Autowired
    ResetConfig resetConfig;

    @PostConstruct
    private void initStaticSecurityManager() {
        // Needed by Shiro + StreamingResponseBody
        SecurityUtils.setSecurityManager(securityManager);
    }

    @RequestMapping("/pods")
    @ResponseBody
    public String pods() {
        String info = "";
        for (PodInfo p : k8sApiService.getPods(null)) {
            info += String.format("%s - %s - %s <br/>\n", p.getName(), p.getIp(), p.getPhase());
        }
        return info;
    }

    @RequestMapping("/apps")
    @ResponseBody
    public String apps() {
        StringBuilder info = new StringBuilder();
        for (NodeResetConfig c : resetConfig.getTakServices()) {
            info.append(String.format("%s %s<br/>\n", c.getLabel(), c.getUrl()));
            for (PodInfo p: k8sApiService.getPods(c.getLabel())) {
                info.append(String.format("%s %s <br>\n", p.getName(), p.getIp()));
            }
            for (String u : resetService.getTakServiceResetUrls()) {
                info.append(u + "<br>\n");
            }
        }
        for (NodeResetConfig c : resetConfig.getApplications()) {
            info.append(String.format("%s %s<br/>\n", c.getLabel(), c.getUrl()));
            for (PodInfo p: k8sApiService.getPods(c.getLabel())) {
                info.append(String.format("%s %s <br>\n", p.getName(), p.getIp()));
            }
            for (String u : resetService.getTakServiceResetUrls()) {
                info.append(u + "<br>\n");
            }
        }
        return info.toString();
    }

    @RequestMapping("/reset")
    public String index(Model model, @RequestParam(required = false) String pubVersion) {
        model.addAttribute("takServicesUrls", resetService.getTakServiceResetUrls());
        model.addAttribute("applicationsUrls", resetService.getApplicationResetUrls());
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
