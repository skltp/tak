package se.skltp.tak.web.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    static MockWebServer mockResetEndpoint;
    static String baseUrl;

    @BeforeAll
    static void setUpStatic() throws IOException {
        mockResetEndpoint = new MockWebServer();
        mockResetEndpoint.start();
        baseUrl = String.format("http://localhost:%s", mockResetEndpoint.getPort());
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ResetService(configurationServiceMock);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockResetEndpoint.shutdown();
    }

    @Test
    public void testResetTakServices() throws IOException {
        mockResetEndpoint.enqueue(new MockResponse().setResponseCode(200).setBody("Hello reset"));
        List<String> urls = new ArrayList<>();
        urls.add(baseUrl);
        Mockito.when(configurationServiceMock.getTakServiceResetUrls()).thenReturn(urls);
        OutputStream outputStream = new ByteArrayOutputStream();

        service.resetTakServices(outputStream);
        assertTrue(outputStream.toString().contains("Hello reset"));
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

        service.resetTakServices(outputStream);
        assertTrue(outputStream.toString().contains("No good"));
    }
}
