package tak.web.alerter


class MailAlerterService implements PubliceringAlerterService {
    def mailService

    def toAddress
    def fromAddress
    def mailSubject

    @Override
    void alert() {
        mailService.sendMail {
            to this.toAddress
            from this.fromAddress
            subject this.mailSubject
            body 'this is some text'
        }
    }
}
