package tak.web.alerter


class MailAlerterService implements PubliceringAlerterService {
    def mailService

    def toAddress
    def fromAddress
    def mailSubject
    def mailBody = 'this is some text'

    @Override
    void alert() {
//        def username = mailService.mailConfig.get("username")

        mailService.sendMail {
            from fromAddress
            to this.toAddress
            subject this.mailSubject
            body this.mailBody
        }
    }
}
