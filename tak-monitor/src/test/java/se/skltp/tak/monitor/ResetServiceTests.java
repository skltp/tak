package se.skltp.tak.monitor;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.monitor.config.NodeResetConfig;
import se.skltp.tak.monitor.config.ResetConfig;
import se.skltp.tak.monitor.service.K8sApiService;
import se.skltp.tak.monitor.service.ResetService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

public class ResetServiceTests {

    MockWebServer mockResetEndpoint;
    String mockWebServerBaseUrl;
    @Mock K8sApiService k8sApiMock;

    @BeforeEach
    public void setUp() throws IOException {
        mockResetEndpoint = new MockWebServer();
        mockResetEndpoint.start();
        mockWebServerBaseUrl = String.format("http://localhost:%s", mockResetEndpoint.getPort());

        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockResetEndpoint.shutdown();
    }

    @Test
    public void resetNodes_staticUrls() throws InterruptedException {
        ResetService service = new ResetService(getStaticConfig(mockWebServerBaseUrl), k8sApiMock);
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset"));
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset"));
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset"));
        service.resetNodes();
        RecordedRequest recordedRequestTS = mockResetEndpoint.takeRequest();
        RecordedRequest recordedRequestA1 = mockResetEndpoint.takeRequest();
        RecordedRequest recordedRequestA2 = mockResetEndpoint.takeRequest();
        assertEquals("/pv", recordedRequestTS.getPath());
        assertEquals("/url1", recordedRequestA1.getPath());
        assertEquals("/url2", recordedRequestA2.getPath());
    }


    @Test
    public void resetNodes_podLookup() throws InterruptedException {
        ResetService service = new ResetService(getLookupConfig(mockWebServerBaseUrl), k8sApiMock);
        List<String> pods = new ArrayList<>();
        pods.add("127.0.0.1");
        Mockito.when(k8sApiMock.getRunningPodIps(anyString(), eq("pod-namespace"))).thenReturn(pods);

        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset pod 1"));
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset pod 2"));

        service.resetNodes();

        RecordedRequest recordedRequest1 = mockResetEndpoint.takeRequest();
        RecordedRequest recordedRequest2 = mockResetEndpoint.takeRequest();
        assertEquals("/resetPod1", recordedRequest1.getPath());
        assertEquals("/resetPod2", recordedRequest2.getPath());
    }


    private ResetConfig getStaticConfig(String baseUrl) {
        ResetConfig cfg = new ResetConfig();

        NodeResetConfig takServicesNode = new NodeResetConfig();
        takServicesNode.setUrl(baseUrl + "/pv");
        cfg.getNodes().add(takServicesNode);

        NodeResetConfig appNode1 = new NodeResetConfig();
        appNode1.setUrl(baseUrl + "/url1");
        cfg.getNodes().add(appNode1);
        NodeResetConfig appNode2 = new NodeResetConfig();
        appNode2.setUrl(baseUrl + "/url2");
        cfg.getNodes().add(appNode2);

        return cfg;
    }

    private ResetConfig getLookupConfig(String baseUrl) {
        ResetConfig cfg = new ResetConfig();
        cfg.setUsePodLookup(true);
        cfg.setPodNamespace("pod-namespace");

        NodeResetConfig takServicesPod = new NodeResetConfig();
        takServicesPod.setLabel("app=tak-services");
        takServicesPod.setUrl(String.format("http://0.0.0.0:%d/resetPod1", mockResetEndpoint.getPort()));
        cfg.getNodes().add(takServicesPod);

        NodeResetConfig appPod = new NodeResetConfig();
        appPod.setLabel("app=my-app");
        appPod.setUrl(String.format("http://0.0.0.0:%d/resetPod2", mockResetEndpoint.getPort()));
        cfg.getNodes().add(appPod);

        return cfg;
    }
}
