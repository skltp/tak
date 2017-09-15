package tak.web.alerter

import org.springframework.mail.MailMessage
import se.skltp.tak.core.entity.AbstractVersionInfo
import se.skltp.tak.core.entity.AnropsAdress
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.Filter
import se.skltp.tak.core.entity.Filtercategorization
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.PubVersion
import se.skltp.tak.core.entity.RivTaProfil
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.Vagval
import se.skltp.tak.web.entity.TAKSettings

class MailAlerterService implements PubliceringAlerterService {
    static final String TO_MAIL = "alerter.mail.toAddress"
    static final String FROM_MAIL = "alerter.mail.fromAddress"
    static final String SUBJECT_PUBLISH = "alerter.mail.publicering.subject"
    static final String CONTENT_PUBLISH = "alerter.mail.publicering.text"
    static final String SUBJECT_ROLLBACK = "alerter.mail.rollback.subject"
    static final String CONTENT_ROLLBACK = "alerter.mail.rollback.text"

    boolean asyncron = true
    def mailService
    def i18nService;

    String[] toAddress
    String fromAddress


    @Override
    void alertOnPublicering(PubVersion pubVersion) {
        def listOfChanges = getChangesAsTextLines(pubVersion)
        Map data = ['pubVersion.id'           : pubVersion.id,
                          'pubVersion.formatVersion': pubVersion.formatVersion,
                          'pubVersion.time'         : pubVersion.time?.format('yyyy-MM-dd hh:mm'),
                          'pubVersion.utforare'     : pubVersion.utforare,
                          'pubVersion.kommentar'    : pubVersion.kommentar,
                          'listOfChanges'           : listOfChanges,
                          'separator'               : System.getProperty("line.separator")]
        alert(CONTENT_PUBLISH, SUBJECT_PUBLISH, data)
    }

    @Override
    void alertOnRollback(PubVersion pubVersion) {
        Map data = ['pubVersion.id'       : pubVersion.id,
                          'pubVersion.time'     : pubVersion.time?.format('yyyy-MM-dd hh:mm'),
                          'pubVersion.utforare' : pubVersion.utforare,
                          'pubVersion.kommentar': pubVersion.kommentar,
                          'separator'           : System.getProperty("line.separator")
        ]

        alert(CONTENT_ROLLBACK, SUBJECT_ROLLBACK, data)
    }

    void alert(String contentPropName, String subjectPropName, Map data) {
        checkMailSettings()

        def subject = i18nService.message(dbCode: subjectPropName, namedattrs: [date: new Date().format('yyyy-MM-dd')])
        def contents = i18nService.message(dbCode: contentPropName, namedattrs: data)

        sendMail(subject, contents)
    }

    private MailMessage sendMail(mailSubject, contents) {
        mailService.sendMail {
            async this.asyncron
            to this.toAddress
            from this.fromAddress
            subject mailSubject
            body contents
        }
    }

    private void checkMailSettings() {
        toAddress = TAKSettings.findBySettingName(TO_MAIL)?.settingValue?.split(',')
        fromAddress = TAKSettings.findBySettingName(FROM_MAIL)?.settingValue

        if (toAddress == null || fromAddress == null) {
            def errorMsg = i18nService.message(code: 'pubVersion.mail.installningar.fel')
            log.error(errorMsg)
            throw new RuntimeException(errorMsg);
        }

    }

    def getChangesAsTextLines(PubVersion pubVersionInstance) {
        def rivTaProfilList = RivTaProfil.findAllByPubVersion(pubVersionInstance.id)
        def anropsAdressList = AnropsAdress.findAllByPubVersion(pubVersionInstance.id)
        def anropsbehorighetList = Anropsbehorighet.findAllByPubVersion(pubVersionInstance.id)
        def filtercategorizationList = Filtercategorization.findAllByPubVersion(pubVersionInstance.id)
        def filterList = Filter.findAllByPubVersion(pubVersionInstance.id)
        def logiskAdressList = LogiskAdress.findAllByPubVersion(pubVersionInstance.id)
        def tjanstekomponentList = Tjanstekomponent.findAllByPubVersion(pubVersionInstance.id)
        def tjanstekontraktList = Tjanstekontrakt.findAllByPubVersion(pubVersionInstance.id)
        def vagvalList = Vagval.findAllByPubVersion(pubVersionInstance.id)


        List<String> allChangesTxt = new ArrayList<>();
        allChangesTxt.addAll(getTxtLinesForOneType("rivtaProfil.label", rivTaProfilList))
        allChangesTxt.addAll(getTxtLinesForOneType("anropsAdress.label", anropsAdressList))
        allChangesTxt.addAll(getTxtLinesForOneType("anropsbehorighet.label", anropsbehorighetList))
        allChangesTxt.addAll(getTxtLinesForOneType("filtercategorization.label", filtercategorizationList))
        allChangesTxt.addAll(getTxtLinesForOneType("filter.label", filterList))
        allChangesTxt.addAll(getTxtLinesForOneType("logiskAdress.label", logiskAdressList))
        allChangesTxt.addAll(getTxtLinesForOneType("tjanstekomponent.label", tjanstekomponentList))
        allChangesTxt.addAll(getTxtLinesForOneType("tjanstekontrakt.label", tjanstekontraktList))
        allChangesTxt.addAll(getTxtLinesForOneType("vagval.label", vagvalList))
        return allChangesTxt.join(System.getProperty("line.separator"))
    }

    def getTxtLinesForOneType(String typeLabel, List<AbstractVersionInfo> entityList) {
        String changedType = i18nService.message(code: typeLabel);
        List<String> result = new ArrayList();
        for (AbstractVersionInfo versionInfo : entityList) {
            def updatedTypeLabel = getUpdatedTypeLabel(versionInfo);
            def msg = i18nService.message(code: updatedTypeLabel, namedattrs: [changedType: changedType, changedInfo: versionInfo.getPublishInfo()]);
            result.add(msg)
        }
        return result
    }

    def getUpdatedTypeLabel(AbstractVersionInfo versionInfo) {
        if (versionInfo.isDeletedInPublishedVersion()) {
            return 'pubVersion.mail.deleted.line'
        }
        return 'pubVersion.mail.created.line'
    }
}
