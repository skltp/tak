package tak.web

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import tak.web.jsonBestallning.BestallningService
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
class BestallningServiceSpecOld extends Specification {

//    void "test validate delete nonexistent vagval"() {
//        setup:
//        daoMock.getVagval(_, _, _, _) >> new ArrayList(); //finns inte den vagval i databasen
//
//        when:
//        JsonBestallning bestallning = BestallningConstructor.createBestallning() //skapar beställning med vagval att delete
//        bestallningService.validateDeletedVagval(bestallning)
//        then:
//        assertTrue(bestallning.getBestallningInfo().size() > 0) // i db finns ingen vagval
//    }
//
//    void "test validate delete existing vagval"() {
//        setup:
//        List vv = new ArrayList();
//        vv.add(new Vagval())
//        daoMock.getVagval(_, _, _, _) >> vv
//        when:
//        JsonBestallning bestallning = BestallningConstructor.createBestallning()
//        bestallningService.validateDeletedVagval(bestallning)
//        then:
//        assertTrue(bestallning.getBestallningInfo().size() == 0)
//    }
//
//    void "test validate delete nonexistent Anropsbehorighet"() {
//        setup:
//        daoMock.getAnropsbehorighet(_, _, _) >> new ArrayList();
//        when:
//        JsonBestallning bestallning = BestallningConstructor.createBestallning()
//        bestallningService.validateDeletedAnropsbehorigheter(bestallning)
//        then:
//        assertTrue(bestallning.getBestallningInfo().size() > 0)
//    }
//
//    void "test validate delete existing Anropsbehorighet"() {
//        setup:
//        List ab = new ArrayList();
//        ab.add(new Anropsbehorighet())
//        daoMock.getAnropsbehorighet(_, _, _) >> ab
//        when:
//        JsonBestallning bestallning = BestallningConstructor.createBestallning()
//        bestallningService.validateDeletedAnropsbehorigheter(bestallning)
//        then:
//        assertTrue(bestallning.getBestallningInfo().size() == 0)
//    }
//
//
//

}
