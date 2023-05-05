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

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Controller
@EnableAsync
public class ResetController {

    @Autowired
    SecurityManager securityManager;

    @Autowired
    ConfigurationService configurationService;

    @PostConstruct
    private void initStaticSecurityManager() {
        // Needed by Shiro + StreamingResponseBody
        SecurityUtils.setSecurityManager(securityManager);
    }

    @RequestMapping("/reset")
    public String index(Model model) {
        model.addAttribute("takServicesUrls", configurationService.getTakServiceResetUrls());
        model.addAttribute("applicationsUrls", configurationService.getApplicationResetUrls());
        return "reset/index";
    }

    @PostMapping("/reset/perform")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> performReset(@RequestParam(defaultValue = "false") boolean resetTakServices,
                                                              @RequestParam(defaultValue = "false") boolean resetApplications) {
        List<String> urls = new ArrayList<>();
        if (resetTakServices) {
            urls.addAll(configurationService.getTakServiceResetUrls());
        }
        if (resetApplications) {
            urls.addAll(configurationService.getApplicationResetUrls());
        }
        StreamingResponseBody stream = out -> {
            for(String url : urls) {
                out.write((url + "\n").getBytes());
                String response = callResetEndpoint(url);
                out.write((response + "\n\n").getBytes());
                out.flush();
            }
        };
        return new ResponseEntity(stream, HttpStatus.OK);
    }

    private String callResetEndpoint(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(30000);

            int status = con.getResponseCode();
            Reader streamReader = status > 299
                    ? new InputStreamReader(con.getErrorStream())
                    : new InputStreamReader(con.getInputStream());
            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) content.append(inputLine);
            in.close();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
