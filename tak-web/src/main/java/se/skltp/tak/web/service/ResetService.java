package se.skltp.tak.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.config.NodeResetConfig;
import se.skltp.tak.web.config.ResetConfig;
import se.skltp.tak.web.dto.PodInfo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResetService {
    static final Logger log = LoggerFactory.getLogger(ResetService.class);
    ResetConfig resetConfig;
    K8sApiService k8sApiService;

    public ResetService(@Autowired ResetConfig resetConfig, @Autowired(required = false) K8sApiService k8sApiService) {
        this.resetConfig = resetConfig;
        this.k8sApiService = k8sApiService;
    }

    public List<String> getTakServiceResetUrls() {
        List<String> urls = new ArrayList<>();
        for (NodeResetConfig cfg : resetConfig.getTakServices()) {
            if (resetConfig.getUsePodLookup()) {
                List<PodInfo> pods = k8sApiService.getPods(cfg.getLabel(), resetConfig.getPodNamespace());
                urls.addAll(getRunningPodsResetUrls(cfg, pods));
            }
            else {
                urls.add(cfg.getUrl());
            }
        }
        return urls;
    }

    public List<String> getApplicationResetUrls() {
        List<String> urls = new ArrayList<>();
        for (NodeResetConfig cfg : resetConfig.getApplications()) {
            if (resetConfig.getUsePodLookup()) {
                List<PodInfo> pods = k8sApiService.getPods(cfg.getLabel(), resetConfig.getPodNamespace());
                urls.addAll(getRunningPodsResetUrls(cfg, pods));
            }
            else {
                urls.add(cfg.getUrl());
            }
        }
        return urls;
    }

    public void resetTakServices(OutputStream out, String pubVersion) throws IOException {
        List<String> urls = getTakServiceResetUrls();
        if (pubVersion == null || pubVersion.isEmpty()) {
            callResetEndpoints(out, urls);
        }
        else {
            List<String> versionUrls = urls.stream()
                            .map(url -> String.format("%s?version=%s", url, pubVersion))
                            .collect(Collectors.toList());
            callResetEndpoints(out, versionUrls);
        }
    }

    public void resetApplications(OutputStream out) throws IOException {
        List<String> urls = getApplicationResetUrls();
        callResetEndpoints(out, urls);
    }

    private List<String> getRunningPodsResetUrls(NodeResetConfig cfg, List<PodInfo> pods) {
        return pods.stream()
                .filter(p -> p.getPhase().equals("Running"))
                .map(p -> cfg.getUrl().replace("0.0.0.0", p.getIp()))
                .collect(Collectors.toList());
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
