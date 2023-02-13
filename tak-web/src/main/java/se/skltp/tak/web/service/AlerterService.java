package se.skltp.tak.web.service;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlerterService {
    static final Logger log = LoggerFactory.getLogger(AlerterService.class);

    static final String FROM_MAIL = "alerter.mail.fromAddress";
    static final String TO_MAIL = "alerter.mail.toAddress";
    static final String SUBJECT_PUBLISH = "alerter.mail.publicering.subject";
    static final String CONTENT_PUBLISH = "alerter.mail.publicering.text";
    static final String SUBJECT_ROLLBACK = "alerter.mail.rollback.subject";
    static final String CONTENT_ROLLBACK = "alerter.mail.rollback.text";
    static final String TO_MAIL_NEW_TK = "mail.alerter.ny.tjanstekontrakt.toAddress";
    static final String SUBJECT_NEW_TK = "mail.alerter.ny.tjanstekontrakt.subject";
    static final String CONTENT_NEW_TK = "mail.alerter.ny.tjanstekontrakt.text";

    MailService mailService;
    ConfigurationService configurationService;
    private final SettingsService settingsService;

    @Autowired
    public AlerterService(MailService mailService, ConfigurationService configurationService, SettingsService settingsService) {
        this.mailService = mailService;
        this.configurationService = configurationService;
        this.settingsService = settingsService;
    }

    public void alertOnNewContract(String contractName, Date date) {
        log.warn("Nytt tjänstekontrakt tillagt: {}", contractName);
        if (mailAlertAvailable()) {
            Map<String,String> messageData = new HashMap<>();
            messageData.put("date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            messageData.put("contractName", contractName);

            mailService.sendSimpleMessage(
                    formatString(FROM_MAIL, null),
                    formatString(TO_MAIL_NEW_TK, null),
                    formatString(SUBJECT_NEW_TK, messageData),
                    formatString(CONTENT_NEW_TK, messageData));
        }
    }

    private boolean mailAlertAvailable() {
        return configurationService.getAlertOn() && mailService.checkMailSettingsOk();
    }

    private String formatString(String settingName, Map<String, String> messageData) {
        String template = settingsService.getSettingValue(settingName);
        if (messageData == null) return template;
        StringSubstitutor substitutor = new StringSubstitutor(messageData);
        return substitutor.replace(template);
    }
}
