package tak.web

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import tak.web.jsonBestallning.BestallningService
import tak.web.jsonBestallning.ConstructorService
import tak.web.jsonBestallning.DAOService
import org.apache.commons.io.FileUtils
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.Vagval
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import se.skltp.tak.web.jsonBestallning.LogiskadressBestallning
import se.skltp.tak.web.jsonBestallning.TjanstekomponentBestallning
import se.skltp.tak.web.jsonBestallning.TjanstekontraktBestallning
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(BestallningService)
@TestMixin(DomainClassUnitTestMixin)
class BestallningServiceSpec extends Specification {

    def bestallningService = new BestallningService()

    def i18nServiceMock
    def constructorService

    def setup() {

        i18nServiceMock = Mock(I18nService)
        constructorService = Mock(ConstructorService)
        bestallningService.i18nService = i18nServiceMock
        bestallningService.constructorService = constructorService

        i18nServiceMock.msg(_) >> "error"
        i18nServiceMock.msg(_,_) >> "error"
    }

    void "create JsonBestallning from json"() {
        when:
        String bestallning = FileUtils.readFileToString(new File("test/unit/resources/besallning.json"))
        bestallningService.createOrderObject(bestallning)
        then:
        noExceptionThrown()
    }



}
