package tak.web.alerter


class MailAlerterService implements PubliceringAlerterService {
    def mailService

    def toAddress
    def fromAddress
    def mailSubject = 'TAK Publicering'
    def mailBody = 'this is some text'

    @Override
    void alert() {
        if (toAddress == null || fromAddress == null) {
            throw new RuntimeException("Mail settings(toAddress or fromAddress) are null. Email is not sent.");
        }

        mailService.sendMail {
            from this.fromAddress
            to this.toAddress
            subject this.mailSubject
            body this.mailBody
        }
    }
}
