package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.servlet.ServletContext;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
}
