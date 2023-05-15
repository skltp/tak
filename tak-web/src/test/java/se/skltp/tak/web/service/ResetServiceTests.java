package se.skltp.tak.web.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.web.config.NodeResetConfig;
import se.skltp.tak.web.config.ResetConfig;
import se.skltp.tak.web.dto.PodInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResetServiceTests {

    ResetService service;
    ResetConfig config;
    MockWebServer mockResetEndpoint;
    @Mock K8sApiService k8sApiMock;

    @BeforeEach
    public void setUp() throws IOException {
        mockResetEndpoint = new MockWebServer();
        mockResetEndpoint.start();
        String baseUrl = String.format("http://localhost:%s", mockResetEndpoint.getPort());

        MockitoAnnotations.openMocks(this);
        config = getTestConfig(baseUrl);
        service = new ResetService(config, k8sApiMock);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockResetEndpoint.shutdown();
    }

    @Test
    public void testGetTakServiceResetUrlsNoLookup() {
        List<String> result = service.getTakServiceResetUrls();
        assertEquals(1, result.size());
    }

    @Test
    public void testGetTakServiceResetUrlsWithLookup() {
        config.setUsePodLookup(true);
        config.getTakServices().get(0).setLabel("my-label=my-value");
        config.getTakServices().get(0).setUrl("http://0.0.0.0:3456/reset");
        List<PodInfo> pods = getTestPods();
        Mockito.when(k8sApiMock.getPods("my-label=my-value")).thenReturn(pods);
        List<String> result = service.getTakServiceResetUrls();
        assertEquals(2, result.size());
        assertEquals("http://10.11.7.8:3456/reset", result.get(0));
        assertEquals("http://10.0.1.2:3456/reset", result.get(1));
    }

    @Test
    public void testResetTakServices() throws IOException, InterruptedException {
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset"));
        OutputStream outputStream = new ByteArrayOutputStream();

        service.resetTakServices(outputStream, null);
        RecordedRequest recordedRequest = mockResetEndpoint.takeRequest();
        assertTrue(outputStream.toString().contains("Hello reset"));
        assertEquals("/pv", recordedRequest.getPath());
    }

    @Test
    public void testResetTakServicesToSpecificVersion() throws IOException, InterruptedException {
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset"));
        OutputStream outputStream = new ByteArrayOutputStream();

        service.resetTakServices(outputStream, "42");
        RecordedRequest recordedRequest = mockResetEndpoint.takeRequest();
        assertTrue(outputStream.toString().contains("Hello reset"));
        assertEquals("/pv?version=42", recordedRequest.getPath());
    }

    @Test
    public void testResetApplications() throws IOException {
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset 1"));
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset 2"));
        OutputStream outputStream = new ByteArrayOutputStream();

        service.resetApplications(outputStream);
        assertTrue(outputStream.toString().contains("Hello reset 1"));
        assertTrue(outputStream.toString().contains("Hello reset 2"));
    }

    @Test
    public void testResetApplicationsWithLookup() throws IOException {
        config.setUsePodLookup(true);
        config.getApplications().get(0).setLabel("app=my-app");
        config.getApplications().get(0).setUrl(String.format("http://0.0.0.0:%d/resetApp", mockResetEndpoint.getPort()));
        List<PodInfo> pods = getMockedPod();
        Mockito.when(k8sApiMock.getPods("app=my-app")).thenReturn(pods);

        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset app"));
        OutputStream outputStream = new ByteArrayOutputStream();

        service.resetApplications(outputStream);
        assertTrue(outputStream.toString().contains("Hello reset app"));
    }

    @Test
    public void testResetFailed() throws IOException {
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(500).setBody("No good"));
        OutputStream outputStream = new ByteArrayOutputStream();

        service.resetTakServices(outputStream, null);
        assertTrue(outputStream.toString().contains("No good"));
    }

    private ResetConfig getTestConfig(String baseUrl) {
        ResetConfig cfg = new ResetConfig();

        NodeResetConfig takServicesNode = new NodeResetConfig();
        takServicesNode.setUrl(baseUrl + "/pv");
        cfg.getTakServices().add(takServicesNode);

        NodeResetConfig appNode1 = new NodeResetConfig();
        appNode1.setUrl(baseUrl + "/url1");
        cfg.getApplications().add(appNode1);
        NodeResetConfig appNode2 = new NodeResetConfig();
        appNode2.setUrl(baseUrl + "/url2");
        cfg.getApplications().add(appNode2);

        return cfg;
    }

    private List<PodInfo> getTestPods() {
        List<PodInfo> pods = new ArrayList<>();
        pods.add(new PodInfo("pod1-abc", "10.11.7.8", "Running"));
        pods.add(new PodInfo("pod2-stopped", "10.0.1.1", "Stopped"));
        pods.add(new PodInfo("pod3-qwerty", "10.0.1.2", "Running"));
        return pods;
    }

    private List<PodInfo> getMockedPod() {
        List<PodInfo> pods = new ArrayList<>();
        pods.add(new PodInfo("mocked-pod", "127.0.0.1", "Running"));
        return pods;
    }
}
