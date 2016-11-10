package se.skltp.tak.core.entity

import grails.test.mixin.TestMixin
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.support.GrailsUnitTestMixin
import se.skltp.tak.web.command.LogiskaAdresserBulk
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

    void testBulkdelete() {
        controller.bulkdelete()
        assert response.text == ''
    }

    void "test bulkdeletevalidate empty parameters"() {
        controller.bulkdeletevalidate()
        assert response.redirectedUrl == '/logiskAdress/bulkdelete'
    }

    void "test bulkdeleteevalidate with one non existing logiska adresser"() {
        params.logiskaAdresserBulk = "val"
        controller.bulkdeletevalidate()
        assert view == "/logiskAdress/bulkdelete"
        assert model.logiskaAdresserBulk == "val"
    }

    void "test bulkdeletevalidate with one existing logiska adresser"() {
//        params.logiskaAdresserBulk = "2"
//        LogiskAdress.findAllByHsaIdIlike("2") >> "2"
//        controller.bulkdeletevalidate()
//        model.logiskaAdresserBulk == "2"
//        view == "/logiskAdress/bulkdeleteconfirm"
    }

    void "test bulkdeleteexecute with missing parameters"() {
        controller.bulkdeleteexecute()
        assert response.redirectedUrl == '/logiskAdress/bulkdelete'
    }

    void "test bulkdeleteexecute with correct parameters"() {
        flash.lb = new LogiskaAdresserBulk();
        flash.lb.logiskaAdresserBulk = "anyvalue"
        controller.bulkdeleteexecute()
        assert response.redirectedUrl == '/logiskAdress/list'
    }
}