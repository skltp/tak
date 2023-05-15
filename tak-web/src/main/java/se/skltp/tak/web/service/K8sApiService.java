package se.skltp.tak.web.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.dto.PodInfo;

import java.util.ArrayList;
import java.util.List;

@Service
public class K8sApiService {

    private CoreV1Api api;

    public K8sApiService() {
        try {
            ApiClient client = Config.defaultClient();
            api = new CoreV1Api(client);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PodInfo> getPods(String labelSelector) {
        List<PodInfo> pods = new ArrayList<>();
        try {
            V1PodList list =
                    api.listNamespacedPod("ntjp-dev", null, false, null, null, labelSelector, null, null, null, 10, false);
            for (V1Pod item : list.getItems()) {

                pods.add(new PodInfo(item.getMetadata().getName(), item.getStatus().getPodIP(), item.getStatus().getPhase()));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return pods;
    }

}
