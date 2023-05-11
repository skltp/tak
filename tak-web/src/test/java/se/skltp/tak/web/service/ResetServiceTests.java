package se.skltp.tak.web.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResetServiceTests {

    @Mock ConfigurationService configurationServiceMock;
    ResetService service;
    MockWebServer mockResetEndpoint;
    String baseUrl;

    @BeforeEach
    public void setUp() throws IOException {
        mockResetEndpoint = new MockWebServer();
        mockResetEndpoint.start();
        baseUrl = String.format("http://localhost:%s", mockResetEndpoint.getPort());

        MockitoAnnotations.openMocks(this);
        service = new ResetService(configurationServiceMock);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockResetEndpoint.shutdown();
    }

    @Test
    public void testResetTakServices() throws IOException, InterruptedException {
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset"));
        List<String> urls = new ArrayList<>();
        urls.add(baseUrl + "/pv");
        Mockito.when(configurationServiceMock.getTakServiceResetUrls()).thenReturn(urls);
        OutputStream outputStream = new ByteArrayOutputStream();

        service.resetTakServices(outputStream, null);
        RecordedRequest recordedRequest = mockResetEndpoint.takeRequest();
        assertTrue(outputStream.toString().contains("Hello reset"));
        assertEquals("/pv", recordedRequest.getPath());
    }

    @Test
    public void testResetTakServicesToSpecificVersion() throws IOException, InterruptedException {
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset"));
        List<String> urls = new ArrayList<>();
        urls.add(baseUrl + "/pv");
        Mockito.when(configurationServiceMock.getTakServiceResetUrls()).thenReturn(urls);
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
        List<String> urls = new ArrayList<>();
        urls.add(baseUrl + "/url1");
        urls.add(baseUrl + "/url2");
        Mockito.when(configurationServiceMock.getApplicationResetUrls()).thenReturn(urls);
        OutputStream outputStream = new ByteArrayOutputStream();

        service.resetApplications(outputStream);
        assertTrue(outputStream.toString().contains("Hello reset 1"));
        assertTrue(outputStream.toString().contains("Hello reset 2"));
    }

    @Test
    public void testResetFailed() throws IOException {
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(500).setBody("No good"));
        List<String> urls = new ArrayList<>();
        urls.add(baseUrl);
        Mockito.when(configurationServiceMock.getTakServiceResetUrls()).thenReturn(urls);
        OutputStream outputStream = new ByteArrayOutputStream();

        service.resetTakServices(outputStream, null);
        assertTrue(outputStream.toString().contains("No good"));
    }
}
