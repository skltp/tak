package se.skltp.tak.monitor.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(value="tak.monitor.reset.use-pod-lookup")
public class K8sApiService {

    static final Logger log = LoggerFactory.getLogger(K8sApiService.class);
    private CoreV1Api api;

    public K8sApiService() {
        try {
            ApiClient client = Config.defaultClient();
            api = new CoreV1Api(client);
        }
        catch (Exception e) {
            log.error("Failed to initialize client.", e);
        }
    }

    public List<String> getRunningPodIps(String labelSelector, String podNamespace) {
        List<String> pods = new ArrayList<>();
        try {
            V1PodList list =
                    api.listNamespacedPod(podNamespace, null, false, null, null, labelSelector, null, null, null, 10, false);
            for (V1Pod item : list.getItems()) {
                log.debug("Pod: {} IP: {} Phase: {}", item.getMetadata().getName(), item.getStatus().getPodIP(), item.getStatus().getPhase());
                if (item.getStatus().getPhase().equals("Running")) {
                    pods.add(item.getStatus().getPodIP());
                }
            }
        }
        catch (Exception e) {
            log.error("Failed to get pods.", e);
        }
        return pods;
    }

}
