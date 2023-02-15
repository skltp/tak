package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.core.entity.PubVersion;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AlerterServiceTests {

    AlerterService service;

    @Mock MailService mailServiceMock;
    @Mock ConfigurationService configurationServiceMock;
    @Mock SettingsService settingsServiceMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AlerterService(mailServiceMock, configurationServiceMock, settingsServiceMock);
    }

    @Test
    public void testGetAlertStatusWhenActiveContainsMailAddress() {
        when(configurationServiceMock.getAlertOn()).thenReturn(true);
        when(mailServiceMock.checkMailSettingsOk()).thenReturn(true);
        when(settingsServiceMock.getSettingValue("alerter.mail.toAddress")).thenReturn("to@example.com");
        String status = service.getMailAlertStatusMessage();
        assertTrue(status.contains("to@example.com"));
    }

    @Test
    public void testAlertOnNewContractInactive() {
        when(configurationServiceMock.getAlertOn()).thenReturn(false);
        service.alertOnNewContract("urn:a.b.c", new Date());
        verify(mailServiceMock, times(0)).sendSimpleMessage(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testAlertOnNewContractNoMailSender() {
        when(configurationServiceMock.getAlertOn()).thenReturn(true);
        service.alertOnNewContract("urn:a.b.c", new Date());
        verify(mailServiceMock, times(0)).sendSimpleMessage(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testAlertOnNewContractParameters() {
        when(configurationServiceMock.getAlertOn()).thenReturn(true);
        when(mailServiceMock.checkMailSettingsOk()).thenReturn(true);
        when(settingsServiceMock.getSettingValue("alerter.mail.fromAddress")).thenReturn("from@example.com");
        when(settingsServiceMock.getSettingValue("mail.alerter.ny.tjanstekontrakt.toAddress")).thenReturn("to@example.com");
        when(settingsServiceMock.getSettingValue("mail.alerter.ny.tjanstekontrakt.subject")).thenReturn("SUBJECT");
        when(settingsServiceMock.getSettingValue("mail.alerter.ny.tjanstekontrakt.text")).thenReturn("TEXT");
        service.alertOnNewContract("urn:a.b.c", new Date());
        verify(mailServiceMock, times(1))
                .sendSimpleMessage(eq("from@example.com"), eq("to@example.com"), eq("SUBJECT"), eq("TEXT"));
    }

    @Test
    public void testAlertOnNewContractTemplateParameters() {
        when(configurationServiceMock.getAlertOn()).thenReturn(true);
        when(mailServiceMock.checkMailSettingsOk()).thenReturn(true);
        when(settingsServiceMock.getSettingValue(anyString())).thenReturn("Kontrakt ${contractName} datum ${date}");
        Date date = java.sql.Date.valueOf(LocalDate.parse("2023-02-14"));
        service.alertOnNewContract("urn:a.b.c", date);
        verify(mailServiceMock, times(1))
                .sendSimpleMessage(anyString(), anyString(),
                        eq("Kontrakt urn:a.b.c datum 2023-02-14"), eq("Kontrakt urn:a.b.c datum 2023-02-14"));
    }

    @Test
    public void testAlertOnPubliceringTemplateParameters() {
        when(configurationServiceMock.getAlertOn()).thenReturn(true);
        when(mailServiceMock.checkMailSettingsOk()).thenReturn(true);
        when(settingsServiceMock.getSettingValue(anyString()))
                .thenReturn("Id ${pubVersion.id} Kommentar ${pubVersion.kommentar}");
        PubVersion pv = new PubVersion();
        pv.setId(42);
        pv.setTime(new Date());
        pv.setKommentar("Publiceringstest");
        service.alertOnPublicering(pv);
        verify(mailServiceMock, times(1))
                .sendSimpleMessage(anyString(), anyString(), anyString(), eq("Id 42 Kommentar Publiceringstest"));
    }
}
