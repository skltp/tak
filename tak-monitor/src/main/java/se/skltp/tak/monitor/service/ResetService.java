package se.skltp.tak.monitor.service;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.monitor.config.ResetConfig;
import se.skltp.tak.monitor.config.NodeResetConfig;

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

    public void resetAll() {
        log.info("Resetting TAK Services ...");
        resetTakServices();
        log.info("Resetting Applications ...");
        resetApplications();
        log.info("Reset done.");
    }

    public void resetTakServices() {
        List<String> urls = new ArrayList<>();
        for (NodeResetConfig cfg : resetConfig.getTakServices()) {
            if (resetConfig.getUsePodLookup()) {
                urls.addAll(getPodsResetUrls(cfg));
            }
            else {
                urls.add(cfg.getUrl());
            }
        }
        callResetEndpoints(urls);
    }

    public void resetApplications() {
        List<String> urls = new ArrayList<>();
        for (NodeResetConfig cfg : resetConfig.getApplications()) {
            if (resetConfig.getUsePodLookup()) {
                urls.addAll(getPodsResetUrls(cfg));
            }
            else {
                urls.add(cfg.getUrl());
            }
        }
        callResetEndpoints(urls);
    }

    private List<String> getPodsResetUrls(NodeResetConfig cfg) {
        List<String> pods = k8sApiService.getRunningPodIps(cfg.getLabel(), resetConfig.getPodNamespace());
        return pods.stream()
                .map(p -> cfg.getUrl().replace("0.0.0.0", p))
                .collect(Collectors.toList());
    }

    private void callResetEndpoints(List<String> urls) {
        for(String url : urls) {
            callResetEndpoint(url);
        }
    }

    private void callResetEndpoint(String url) {
        try {
            log.info("Calling reset url: {}", url);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            if (!response.isSuccessful()) {
                log.warn("Reset response status was: {}", response.message());
            }
            log.info("Reset response body: {}", response.body().string());
        } catch (Exception e) {
            log.error("Reset failed", e);
        }
    }
}
