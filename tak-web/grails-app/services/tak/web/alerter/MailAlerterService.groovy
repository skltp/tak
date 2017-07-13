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
import tak.web.I18nService

class MailAlerterService implements PubliceringAlerterService {
    def mailService

    def toAddress
    def fromAddress

    I18nService i18nService;

    @Override
    void alertOnPublicering(PubVersion pubVersionInstance) {
        checkMailSettings()

        def contents = createBody(pubVersionInstance)
        def subject = i18nService.message(code: 'pubVersion.mail.subject', attrs: [pubVersionInstance.id]);

        sendMail(subject, contents)
    }

    @Override
    void alertOnRollback(PubVersion pubVersionInstance) {
        checkMailSettings()

        def contents = createRollbackBody(pubVersionInstance)
        def subject = i18nService.message(code: 'pubVersion.rollback.mail.subject', attrs: [pubVersionInstance.id]);

        sendMail(subject, contents)
    }

    private MailMessage sendMail(mailSubject, contents) {
        mailService.sendMail {
            to this.toAddress
            from this.fromAddress
            subject mailSubject
            body contents
        }
    }

    private void checkMailSettings() {
        if (toAddress == null || fromAddress == null) {
            log.error(i18nService.message(code:'pubVersion.mail.installningar.fel'))
            throw new RuntimeException(i18nService.message(code:'pubVersion.mail.installningar.fel'));
        }
    }

    def createBody(PubVersion pubVersion) {
        def listOfChanges = getChangesAsTextLines(pubVersion)
        Map namedattrs = ['pubVersion.id':pubVersion.id,'pubVersion.formatVersion':pubVersion.formatVersion,'pubVersion.time':pubVersion.time,'pubVersion.utforare':pubVersion.utforare,'pubVersion.kommentar':pubVersion.kommentar,'listOfChanges':listOfChanges]
        def body = i18nService.message(code:'pubVersion.mail.layout', namedattrs: namedattrs);
        return body;
    }

    def createRollbackBody(PubVersion pubVersion) {
        Map namedattrs = ['pubVersion.id':pubVersion.id,'pubVersion.time':pubVersion.time,'pubVersion.utforare':pubVersion.utforare,'pubVersion.kommentar':pubVersion.kommentar]
        def body = i18nService.message(code:'pubVersion.rollback.mail.layout', namedattrs: namedattrs);
        return body;
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
        return allChangesTxt.join("\n")
    }

    def getTxtLinesForOneType(String typeLabel, List<AbstractVersionInfo> entityList){
        String changedType = i18nService.message(code:typeLabel);
        List<String> result = new ArrayList();
        for( AbstractVersionInfo versionInfo: entityList){
            def updatedTypeLabel = getUpdatedTypeLabel(versionInfo);
            def msg = i18nService.message(code:updatedTypeLabel, namedattrs: [changedType:changedType, changedInfo:versionInfo.getPublishInfo()]);
            result.add(msg)
        }
        return result
    }

    def getUpdatedTypeLabel(AbstractVersionInfo versionInfo) {
        if(versionInfo.isDeletedInPublishedVersion()){
            return 'pubVersion.mail.deleted.line'
        }
        return 'pubVersion.mail.created.line'
    }
}
