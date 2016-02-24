package se.skltp.tak.core.entity

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import org.junit.Before


@TestMixin(GrailsUnitTestMixin)
@TestFor(LogiskAdressController) 
@Mock(LogiskAdress)
class LogiskAdressControllerCreateUnitTest extends AbstractTestSetup {
	
	@Before
	void before() {
		setupUser()
	}
	
	def populateValidParams(params) {
		params['hsaId'] = 'HSA-VKK123'
		params['beskrivning'] = 'Test HSA-ID'
		
		assert params != null
	}
	
	def populateInvalidParams(params) {
		params['hsaId'] = null
		
		assert params != null
	}
	
	def getEntity() {
		populateValidParams(params)
		return new LogiskAdress(params)
	}

	void testSave() {
		//Test first without data then with data
		testSaveEntity(controller, '/logiskAdress/create', '/logiskAdress/show/0')
	}
	
	void testUpdate() {
		testUpdateEntity(controller, 'logiskAdress')
	}

	void testDelete() {
		testDeleteEntity(controller, '/logiskAdress/list')
	}
	
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
        //assert LogiskAdress.count() == 1
    }
}