package se.skltp.tak.web.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.dto.PodInfo;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(value="tak.reset.use-pod-lookup")
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

    public List<PodInfo> getPods(String labelSelector, String podNamespace) {
        List<PodInfo> pods = new ArrayList<>();
        try {
            V1PodList list =
                    api.listNamespacedPod(podNamespace, null, false, null, null, labelSelector, null, null, null, 10, false);
            for (V1Pod item : list.getItems()) {

                pods.add(new PodInfo(item.getMetadata().getName(), item.getStatus().getPodIP(), item.getStatus().getPhase()));
            }
        }
        catch (Exception e) {
            log.error("Failed to get pods.", e);
        }

        log.debug("Found pods: " + pods);
        return pods;
    }

}
