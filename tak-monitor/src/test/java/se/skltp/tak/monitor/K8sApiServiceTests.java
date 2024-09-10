package se.skltp.tak.monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

import java.io.IOException;
import java.util.List;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodStatus;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import se.skltp.tak.monitor.service.K8sApiService;

public class K8sApiServiceTests {

  @Mock CoreV1Api apiMock;
  private K8sApiService service;

  @BeforeEach
  public void setUp() throws IOException {
    MockitoAnnotations.openMocks(this);
    service = new K8sApiService(apiMock);
  }

  @Test
  public void getRunningPodIps_returnsOnlyRunning() throws ApiException {
    V1PodList podList = new V1PodList();
    podList.addItemsItem(getTestPod("Pod 1", "10.1.0.1", "Starting"));
    podList.addItemsItem(getTestPod("Pod 2", "10.1.0.2", "Running"));

    Mockito.when(apiMock.listNamespacedPod(eq("NAMESPACE"), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(podList);

    List<String> runningPodIps = service.getRunningPodIps("LABEL_SELECTOR", "NAMESPACE");

    assertEquals(1, runningPodIps.size());
    assertEquals("10.1.0.2", runningPodIps.get(0));
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


