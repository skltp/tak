/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.monitor.service;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** This Service class only activates if the use-pod-lookup property is set.
 *  Same as the CoreV1Api bean in the main Application class.
 */
@Service
@ConditionalOnProperty(value = "tak.monitor.reset.use-pod-lookup")
public class K8sApiService {

  static final Logger log = LoggerFactory.getLogger(K8sApiService.class);
  private final CoreV1Api api;

  public K8sApiService(@Autowired CoreV1Api api) {
    this.api = api;
  }

  public List<String> getRunningPodIps(String labelSelector, String podNamespace) {
    List<String> pods = new ArrayList<>();
    try {
      V1PodList list =
              api.listNamespacedPod(podNamespace)
                      .allowWatchBookmarks(false) // Might be redundant. Will be ignored unless 'watch' below is true.
                      .labelSelector(labelSelector)
                      .limit(10)
                      .watch(false) // Is redundant - Defaults to false if omitted. It does make the behaviour explicit.
                      .execute();
      for (V1Pod item : list.getItems()) {
        log.debug(
            "Pod: {} IP: {} Phase: {}",
            item.getMetadata().getName(),
            item.getStatus().getPodIP(),
            item.getStatus().getPhase());
        if (item.getStatus().getPhase().equals("Running")) {
          pods.add(item.getStatus().getPodIP());
        }
      }
    } catch (Exception e) {
      log.error("Failed to get pods.", e);
    }
    return pods;
  }
}
