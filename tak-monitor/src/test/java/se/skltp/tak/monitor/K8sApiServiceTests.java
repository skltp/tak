/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.monitor.service.K8sApiService;

class K8sApiServiceTests {

  @Mock CoreV1Api apiMock;
  private K8sApiService service;
  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
    service = new K8sApiService(apiMock);
  }

  @AfterEach
  void tearDown() throws Exception {
    mocks.close();
  }

  @Test
  void getRunningPodIps_returnsOnlyRunning_fluent2() throws ApiException {
    V1PodList podList = new V1PodList();
    podList.addItemsItem(getTestPod("Pod 1", "10.1.0.1", "Starting"));
    podList.addItemsItem(getTestPod("Pod 2", "10.1.0.2", "Running"));

    CoreV1Api.APIlistNamespacedPodRequest request =
            mock(CoreV1Api.APIlistNamespacedPodRequest.class);

    when(apiMock.listNamespacedPod("NAMESPACE")).thenReturn(request);
    when(request.allowWatchBookmarks(false)).thenReturn(request);
    when(request.labelSelector("LABEL_SELECTOR")).thenReturn(request);
    when(request.limit(10)).thenReturn(request);
    when(request.watch(false)).thenReturn(request);
    when(request.execute()).thenReturn(podList);

    assertEquals(
            List.of("10.1.0.2"),
            service.getRunningPodIps("LABEL_SELECTOR", "NAMESPACE")
    );
  }

  private V1Pod getTestPod(String name, String ip, String phase) {
    V1Pod pod = new V1Pod();
    V1PodStatus status = new V1PodStatus();
    status.setPodIP(ip);
    status.setPhase(phase);
    pod.setStatus(status);
    V1ObjectMeta meta = new V1ObjectMeta();
    meta.setName(name);
    pod.setMetadata(meta);
    return pod;
  }

}


