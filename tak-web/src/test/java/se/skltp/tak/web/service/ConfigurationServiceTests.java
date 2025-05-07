package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.servlet.ServletContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class ConfigurationServiceTests {

    @Mock ServletContext context;

    @Autowired ConfigurationService service;

    @Test
    public void testDefaultValues() throws Exception {
        service.init();
        assertEquals("inera-logo.png", service.getLogoImage());
        assertEquals("#ffffff;", service.getBackgroundStyle());
        assertEquals(false, service.getBestallningOn());
        assertEquals("pkcs12", service.getBestallningClientCertType());
        assertEquals("jks", service.getBestallningServerCertType());
    }

    @Test
    public void testFileValues() throws Exception {
        service.init();
        assertEquals("SKLTP-TEST", service.getPlatform());
    }

    @Test
    public void testMultipleBestallningUrls() throws Exception {
        service.init();
        List<String> urls = service.getBestallningUrls();
        assertNotNull(urls);
        assertEquals(3, urls.size());
        assertEquals("https://first.example.com", urls.get(0));
        assertEquals("https://third.com", urls.get(2));
    }
}
