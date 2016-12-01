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

	def getEntity() {
		populateValidParams(params)
		return new LogiskAdress(params)
	}

	def getEntityClass() {
		return LogiskAdress;
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
}