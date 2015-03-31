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

	void "test bulkvalidate empty parameters"() {
        controller.bulkvalidate()
        assert response.redirectedUrl == '/logiskAdress/bulkcreate'
	}

    void "test bulkvalidate with no valid logiska adresser"() {
        params.logiskaAdresserBulk = "a"
        controller.bulkvalidate()
        assert view == "/logiskAdress/bulkcreate"
        assert model.logiskaAdresserBulk == "a"
    }
    
    void "test bulkvalidate with one valid logiska adresser"() {
        params.logiskaAdresserBulk = "keya,descriptiona"
        controller.bulkvalidate()
        assert view == "/logiskAdress/bulkconfirm"
        assert model.logiskaAdresserBulk.logiskaAdresserBulk == "keya,descriptiona"
    }

    void "test bulksave with missing parameters"() {
        controller.bulksave()
        assert response.redirectedUrl == '/logiskAdress/bulkcreate'
    }
}