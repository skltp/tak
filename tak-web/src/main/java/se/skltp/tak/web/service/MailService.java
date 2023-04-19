package se.skltp.tak.web.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    static final Logger log = LoggerFactory.getLogger(MailService.class);
    JavaMailSender mailSender;

    public MailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean checkMailSettingsOk() {
        return mailSender != null;
    }

    public void sendSimpleMessage(String from, String to, String subject, String text) {
        log.debug("Sending message from: {} to: {} subject: {} text: {}", from, to, subject, text);
        String[] recipients = StringUtils.stripAll(to.split(","));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipients);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
