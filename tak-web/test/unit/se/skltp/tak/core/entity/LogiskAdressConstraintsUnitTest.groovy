package se.skltp.tak.core.entity

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

@TestFor(LogiskAdress)
class LogiskAdressConstraintsUnitTest extends Specification {

    void testValid() {
        def existingLogiskAdress = new LogiskAdress()
        existingLogiskAdress.hsaId = "ABC123"
        mockForConstraintsTests(LogiskAdress, [existingLogiskAdress])
        def logiskAdress = new LogiskAdress()
        assert !logiskAdress.validate()
        assert "nullable" == logiskAdress.errors["hsaId"]
        // assert "nullable" == logiskAdress.errors["beskrivning"]
    }
    
    void testInValidHsaId() {
        def existingLogiskAdress = new LogiskAdress()
        existingLogiskAdress.hsaId = "ABC123"
        mockForConstraintsTests(LogiskAdress, [existingLogiskAdress])
        existingLogiskAdress = new LogiskAdress()
        existingLogiskAdress.hsaId = "*"
        mockForConstraintsTests(LogiskAdress, [existingLogiskAdress])
        def logiskAdress = new LogiskAdress()
        logiskAdress.hsaId = " ABC "
        assert !logiskAdress.validate()
        assert "invalid.content" == logiskAdress.errors["hsaId"]
    }
}