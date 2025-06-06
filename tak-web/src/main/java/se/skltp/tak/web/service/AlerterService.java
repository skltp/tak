package se.skltp.tak.web.service;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.PubVersion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String MAIL_ALERT_FAIL_MSG = "Mail alert misslyckades";

    MailService mailService;
    ConfigurationService configurationService;
    private final SettingsService settingsService;

    @Autowired
    public AlerterService(MailService mailService, ConfigurationService configurationService, SettingsService settingsService) {
        this.mailService = mailService;
        this.configurationService = configurationService;
        this.settingsService = settingsService;
    }

    public void alertOnPublicering(PubVersion pv, List<String> listOfChanges) {
        log.info("En ny version har publicerats: {}", pv.getId());
        if (!mailAlertAvailable()) return;
        try {
            Map<String,String> messageData = new HashMap<>();
            messageData.put("pubVersion.id", Long.toString(pv.getId()));
            messageData.put("pubVersion.formatVersion", Long.toString(pv.getFormatVersion()));
            messageData.put("pubVersion.time", new SimpleDateFormat("yyyy-MM-dd hh:mm").format(pv.getTime()));
            messageData.put("pubVersion.utforare", pv.getUtforare());
            messageData.put("pubVersion.kommentar", pv.getKommentar());
            messageData.put("listOfChanges", String.join(LINE_SEPARATOR, listOfChanges));
            messageData.put("separator", LINE_SEPARATOR);
            messageData.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            mailService.sendSimpleMessage(
                    formatString(FROM_MAIL, null),
                    formatString(TO_MAIL, null),
                    formatString(SUBJECT_PUBLISH, messageData),
                    formatString(CONTENT_PUBLISH, messageData));
        }
        catch (Exception e) {
            log.error(MAIL_ALERT_FAIL_MSG, e);
        }
    }

    public void alertOnRollback(PubVersion pv) {
        log.info("Rollback har genomförts för publicerad version: {}", pv.getId());
        if (!mailAlertAvailable()) return;
        try {
            Map<String,String> messageData = new HashMap<>();
            messageData.put("pubVersion.id", Long.toString(pv.getId()));
            messageData.put("pubVersion.time", new SimpleDateFormat("yyyy-MM-dd hh:mm").format(pv.getTime()));
            messageData.put("pubVersion.utforare", pv.getUtforare());
            messageData.put("pubVersion.kommentar", pv.getKommentar());
            messageData.put("separator", LINE_SEPARATOR);

            mailService.sendSimpleMessage(
                    formatString(FROM_MAIL, null),
                    formatString(TO_MAIL, null),
                    formatString(SUBJECT_ROLLBACK, messageData),
                    formatString(CONTENT_ROLLBACK, messageData));
        }
        catch (Exception e) {
            log.error(MAIL_ALERT_FAIL_MSG, e);
        }
    }

    public void alertOnNewContract(String contractName, Date date) {
        log.info("Nytt tjänstekontrakt tillagt: {}", contractName);
        if (!mailAlertAvailable()) return;
        try {
            Map<String,String> messageData = new HashMap<>();
            messageData.put("date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            messageData.put("contractName", contractName);

            mailService.sendSimpleMessage(
                    formatString(FROM_MAIL, null),
                    formatString(TO_MAIL_NEW_TK, null),
                    formatString(SUBJECT_NEW_TK, messageData),
                    formatString(CONTENT_NEW_TK, messageData));
        }
        catch (Exception e) {
            log.error(MAIL_ALERT_FAIL_MSG, e);
        }
    }

    public String getMailAlertStatusMessage() {
        if (!configurationService.getAlertOn()) {
            return "MailAlert är avstängt";
        }
        if (!mailService.checkMailSettingsOk()) {
            return "MailAlert är aktiverat men inställningar för mailserver saknas";
        }
        return String.format("MailAlert är aktiverad. Mail kommer att skickas till %s vid publicering",
                settingsService.getSettingValue(TO_MAIL));
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
