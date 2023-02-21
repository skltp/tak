package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.web.entity.TAKSettings;
import se.skltp.tak.web.repository.SettingsRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SettingsServiceTests {

    SettingsService service;

    @Autowired
    SettingsRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {
        service = new SettingsService(repository);
    }

    @Test
    public void testFindById() {
        Optional<TAKSettings> settings = service.findById(2L);
        assertTrue(settings.isPresent());
        assertEquals("alerter.mail.fromAddress", settings.get().getSettingName());
        assertEquals("pubAlert@server.com", settings.get().getSettingValue());
    }

    @Test
    public void testGetSettingValue() {
        String actual = service.getSettingValue("alerter.mail.fromAddress");
        assertEquals("pubAlert@server.com", actual);
    }
}
