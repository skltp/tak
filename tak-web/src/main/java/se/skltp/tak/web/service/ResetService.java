package se.skltp.tak.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class ResetService {
    static final Logger log = LoggerFactory.getLogger(ResetService.class);
    ConfigurationService configurationService;

    public ResetService(@Autowired ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void resetTakServices(OutputStream out) throws IOException {
        List<String> urls = configurationService.getTakServiceResetUrls();
        callResetEndpoints(out, urls);
    }

    public void resetApplications(OutputStream out) throws IOException {
        List<String> urls = configurationService.getApplicationResetUrls();
        callResetEndpoints(out, urls);
    }

    private void callResetEndpoints(OutputStream out, List<String> urls) throws IOException {
        for(String url : urls) {
            out.write((url + "\n").getBytes());
            String response = callResetEndpoint(url);
            out.write((response + "\n\n").getBytes());
            out.flush();
        }
    }

    public String callResetEndpoint(String url) {
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
            log.debug(content.toString());
            return content.toString();
        } catch (Exception e) {
            log.error("Reset failed", e);
            return e.toString();
        }
    }
}
