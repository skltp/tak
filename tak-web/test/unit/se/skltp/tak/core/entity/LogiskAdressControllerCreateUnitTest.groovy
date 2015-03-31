package se.skltp.tak.core.entity

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification


@TestMixin(GrailsUnitTestMixin)
@TestFor(LogiskAdressController) 
@Mock(LogiskAdress)
class LogiskAdressControllerCreateUnitTest extends Specification {

    void testSaveInvalidLogiskAdress() {
        controller.bulksave()
        assert response.redirectedUrl == '/logiskAdress/bulkcreate'
    }

    void testValidateAndSaveNewLogiskAdress() {
        params.logiskaAdresserBulk = "keya,descriptiona"
        controller.bulkvalidate()
        assert view == "/logiskAdress/bulkconfirm"
        assert model.logiskaAdresserBulk.logiskaAdresserBulk == "keya,descriptiona"
        
        controller.bulksave()
        assert response.redirectedUrl == '/anropsbehorighet/bulkadd'
        assert flash.message == 'createdlogicaladdresses'
        assert LogiskAdress.count() == 1
    }
}