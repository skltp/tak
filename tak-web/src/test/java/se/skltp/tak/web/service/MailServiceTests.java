package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MailServiceTests {

    MailService service;

    @Mock JavaMailSender mailSenderMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new MailService(mailSenderMock);
    }

    @Test
    public void testSendSimpleMessage() {
        service.sendSimpleMessage("from@email.com","test@example.com", "Enhetstest", "Innehåll");
        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSenderMock, times(1)).send(emailCaptor.capture());

        List<SimpleMailMessage> actualList = emailCaptor.getAllValues();
        assertEquals(1, actualList.size());
        assertEquals("from@email.com", actualList.get(0).getFrom());
        assertEquals("test@example.com", actualList.get(0).getTo()[0]);
        assertEquals("Enhetstest", actualList.get(0).getSubject());
        assertEquals("Innehåll", actualList.get(0).getText());
    }

    @Test
    public void testSendSimpleMessageMultipleRecipients() {
        service.sendSimpleMessage("from@email.com","test@example.com, test2@example.com", "Enhetstest", "Innehåll");
        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSenderMock, times(1)).send(emailCaptor.capture());

        List<SimpleMailMessage> actualList = emailCaptor.getAllValues();
        assertEquals(1, actualList.size());
        assertEquals(2, actualList.get(0).getTo().length);
        assertEquals("test@example.com", actualList.get(0).getTo()[0]);
        assertEquals("test2@example.com", actualList.get(0).getTo()[1]);
    }
}
