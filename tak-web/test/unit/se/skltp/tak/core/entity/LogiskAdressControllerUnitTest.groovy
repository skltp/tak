package se.skltp.tak.core.entity

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import org.junit.Before


@TestMixin(GrailsUnitTestMixin)
@TestFor(LogiskAdressController) 
@Mock(LogiskAdress)
class LogiskAdressControllerUnitTest extends AbstractCRUDControllerUnitTest {
	
	@Before
	void before() {
		setupUser()
	}

	def getEntityName() {
		"logiskAdress"
	}

	def getEntityClass() {
		LogiskAdress
	}

	def createValidEntity() {
		def paramsMap = [:]
		populateValidParams(paramsMap)
		new LogiskAdress(paramsMap)
	}

	def createEntityWithNotSetDeletedDependencies() {
		def logiskAdress = new LogiskAdress()
		logiskAdress.setVagval([new Vagval()] as Set)
		logiskAdress.setAnropsbehorigheter([new Anropsbehorighet()] as Set)
		logiskAdress
	}

	def populateValidParams(paramsMap) {
		paramsMap['hsaId'] = 'HSA-VKK123'
		paramsMap['beskrivning'] = 'Test HSA-ID'
	}

	def populateInvalidParams(paramsMap) {
		paramsMap['hsaId'] = null
		paramsMap['beskrivning'] = 'Test HSA-ID'
	}
}