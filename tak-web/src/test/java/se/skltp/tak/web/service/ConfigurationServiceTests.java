package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.servlet.ServletContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ConfigurationServiceTests {

    @Mock ServletContext context;

    @Autowired ConfigurationService service;

    @Test
    void testDefaultValues() throws Exception {
        service.init();
        assertEquals("inera-logo.png", service.getLogoImage());
        assertEquals("#ffffff;", service.getBackgroundStyle());
        assertFalse(service.getBestallningOn());
        assertEquals("pkcs12", service.getBestallningClientCertType());
        assertEquals("jks", service.getBestallningServerCertType());
    }

    @Test
    void testFileValues() throws Exception {
        service.init();
        assertEquals("SKLTP-TEST", service.getPlatform());
    }

    @Test
    void testMultipleBestallningUrlsWithNames() throws Exception {
        service.init();
        List<Map<String, String>> urls = service.getBestallningUrlsWithNames();
        assertNotNull(urls);
        assertEquals(3, urls.size());

        Map<String, String> expectedFirst = new HashMap<>();
        expectedFirst.put("url", "https://first.example.com/");
        expectedFirst.put("name", "FIRST");
        assertEquals(expectedFirst, urls.get(0));

        Map<String, String> expectedLast = new HashMap<>();
        expectedLast.put("url", "https://third.example.com/");
        expectedLast.put("name", "THIRD");

        assertEquals(expectedLast, urls.get(2));
    }
}
