package se.skltp.tak.core.entity

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification


@TestMixin(GrailsUnitTestMixin)
@TestFor(LogiskAdressController) 
@Mock([LogiskAdress])
class LogiskAdressControllerUnitTest extends Specification {

    void testBulkcreate() {
        controller.bulkcreate()
        assert response.text == ''
    }

	void "test bulkcreatevalidate empty parameters"() {
        controller.bulkcreatevalidate()
        assert response.redirectedUrl == '/logiskAdress/bulkcreate'
	}

    void "test bulkcreatevalidate with no valid logiska adresser"() {
        params.logiskaAdresserBulk = "a"
        controller.bulkcreatevalidate()
        assert view == "/logiskAdress/bulkcreate"
        assert model.logiskaAdresserBulk == "a"
    }
    
    void "test bulkcreatevalidate with one valid logiska adresser"() {
        params.logiskaAdresserBulk = "keya,descriptiona"
        controller.bulkcreatevalidate()
        assert view == "/logiskAdress/bulkcreateconfirm"
        assert model.logiskaAdresserBulk.logiskaAdresserBulk == "keya,descriptiona"
    }

    void "test bulksave with missing parameters"() {
        controller.bulksave()
        assert response.redirectedUrl == '/logiskAdress/bulkcreate'
    }
}