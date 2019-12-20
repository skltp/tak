package tak.web

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification
import tak.web.jsonBestallning.BestallningStodetConnectionService

@TestFor(BestallningStodetConnectionService)
@TestMixin(DomainClassUnitTestMixin)
class BestallningStodetConnectionServiceSpec extends Specification {
    BestallningStodetConnectionService bestallningStodetConnectionService = new BestallningStodetConnectionService()

    def i18nServiceMock

    def setup() {
        i18nServiceMock = Mock(I18nService)
        bestallningStodetConnectionService.i18nService = i18nServiceMock
        bestallningStodetConnectionService.grailsApplication = grailsApplication
    }

    void "isJsonBestallningOnTest"() {
        when:
        1 == 1
        then:
        !bestallningStodetConnectionService.isJsonBestallningOn()

        when:
        grailsApplication.config.tak.bestallning.on = "true"
        then:
        bestallningStodetConnectionService.isJsonBestallningOn()

        when:
        grailsApplication.config.tak.bestallning.on = "false"
        then:
        !bestallningStodetConnectionService.isJsonBestallningOn()

        when:
        grailsApplication.config.tak.bestallning.on = "abc"
        then:
        !bestallningStodetConnectionService.isJsonBestallningOn()
    }


    void "validateConnectionConfigTest"() {
        when:
        grailsApplication.config.tak.bestallning.url = ""
        grailsApplication.config.tak.bestallning.cert = ""
        grailsApplication.config.tak.bestallning.pw = ""
        grailsApplication.config.tak.bestallning.serverCert = ""
        grailsApplication.config.tak.bestallning.serverPw = ""

        i18nServiceMock.msg("bestallning.error.url") >> "bestallning.error.url.error"
        i18nServiceMock.msg("bestallning.error.cert") >> "bestallning.error.cert.error"
        i18nServiceMock.msg("bestallning.error.pw") >> "bestallning.error.pw.error"
        i18nServiceMock.msg("bestallning.error.serverpw") >> "bestallning.error.serverpw.error"
        i18nServiceMock.msg("bestallning.error.servercert") >> "bestallning.error.servercert.error"
        List<String> error = bestallningStodetConnectionService.validateConnectionConfig()

        then:
        error.contains("bestallning.error.url.error")
        error.contains("bestallning.error.cert.error")
        error.contains("bestallning.error.pw.error")
        error.contains("bestallning.error.servercert.error")
        error.contains("bestallning.error.serverpw.error")

        when:
        grailsApplication.config.tak.bestallning.url = "1"
        grailsApplication.config.tak.bestallning.cert = "2"
        grailsApplication.config.tak.bestallning.pw = "3"
        grailsApplication.config.tak.bestallning.serverCert = "4"
        grailsApplication.config.tak.bestallning.serverPw = "5"
        i18nServiceMock.msg("bestallning.error.certNotFound") >> "bestallning.error.certNotFound.error"
        i18nServiceMock.msg("bestallning.error.serverCertNotFound") >> "bestallning.error.serverCertNotFound.error"


        then:
        List<String> error2 = bestallningStodetConnectionService.validateConnectionConfig()
        error2.contains("bestallning.error.certNotFound.error")
        error2.contains("bestallning.error.serverCertNotFound.error")
    }


    void "simpleValidate"() {
        expect:
        !bestallningStodetConnectionService.simpleValidate("")
        !bestallningStodetConnectionService.simpleValidate("{")
        !bestallningStodetConnectionService.simpleValidate("}")
        bestallningStodetConnectionService.simpleValidate("{}")
    }
}
