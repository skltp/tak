package se.skltp.tak.web.service;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.config.NodeResetConfig;
import se.skltp.tak.web.config.ResetConfig;
import se.skltp.tak.web.dto.PodInfo;

import java.io.*;
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
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            if (!response.isSuccessful()) {
                log.warn("Reset response status was: {}", response.message());
            }
            return response.body().string();
        } catch (Exception e) {
            log.error("Reset failed", e);
            return e.toString();
        }
    }
}
