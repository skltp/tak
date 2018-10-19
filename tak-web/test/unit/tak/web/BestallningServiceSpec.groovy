package tak.web

import grails.test.mixin.TestFor
import org.apache.commons.io.FileUtils
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.Vagval
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(BestallningService)
class BestallningServiceSpec extends Specification {

    def bestallningService = new BestallningService()

    def daoMock
    def i18nServiceMock

    def setup() {
        daoMock = Mock(DAOService)
        i18nServiceMock = Mock(I18nService)
        bestallningService.daoService = daoMock
        bestallningService.i18nService = i18nServiceMock

        i18nServiceMock.msg(_) >> "error"
    }

    def cleanup() {
    }

    void "create JsonBestallning from json"() {
        when:
        String bestallning = FileUtils.readFileToString(new File("test/unit/resources/besallning.json"))
        bestallningService.createOrderObject(bestallning)
        then:
        noExceptionThrown()
    }

    void "test validate delete nonexistent vagval"() {
        setup:
        daoMock.getVagval(_, _, _, _, _, _) >> null //finns inte den vagval i databasen
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning() //skapar beställning med vagval att delete
        bestallningService.validateDeletedVagval(bestallning)
        then:
        assertTrue(bestallning.getBestallningInfo().size() > 0) // i db finns ingen vagval
    }

    void "test validate delete existing vagval"() {
        setup:
        daoMock.getVagval(_, _, _, _, _, _) >> new Vagval()
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.validateDeletedVagval(bestallning)
        then:
        assertTrue(bestallning.getBestallningInfo().size() == 0)
    }

    void "test validate delete nonexistent Anropsbehorighet"() {
        setup:
        daoMock.getAnropsbehorighet(_, _, _, _) >> null
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.validateDeletedAnropsbehorigheter(bestallning)
        then:
        assertTrue(bestallning.getBestallningInfo().size() > 0)
    }

    void "test validate delete existing Anropsbehorighet"() {
        setup:
        daoMock.getAnropsbehorighet(_, _, _, _) >> new Anropsbehorighet()
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.validateDeletedAnropsbehorigheter(bestallning)
        then:
        assertTrue(bestallning.getBestallningInfo().size() == 0)
    }

    void "test saving LogiskaAdresser to order"() {
        setup:
        daoMock.getLogiskAdressByHSAId(_) >> new LogiskAdress()
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.saveLogiskaAdresserToOrder(bestallning)
        then:
        assertNotNull(bestallning.getInkludera().getLogiskadresser().get(0).logiskAdress)
    }

    void "test non saving nonexistent/deleted LogiskaAdresser to order "() {
        when:
        daoMock.getLogiskAdressByHSAId(_) >> null
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.saveLogiskaAdresserToOrder(bestallning)
        then:
        assertNull(bestallning.getInkludera().getLogiskadresser().get(0).logiskAdress)

        when:
        LogiskAdress adress = new LogiskAdress()
        adress.setDeleted(null)
        daoMock.getLogiskAdressByHSAId(_) >> adress
        bestallningService.saveLogiskaAdresserToOrder(bestallning)
        then:
        assertNull(bestallning.getInkludera().getLogiskadresser().get(0).logiskAdress)
    }

    void "test saving Tjanstekomponent to order "(){
        setup:
        daoMock.getTjanstekomponentByHSAId(_) >> new Tjanstekomponent();
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.saveTjanstekomponenterToOrder(bestallning)
        then:
        assertNotNull(bestallning.getInkludera().getTjanstekomponenter().get(0).tjanstekomponent)
    }

    void "test non saving nonexistent Tjanstekomponent to order "(){
        when:
        daoMock.getTjanstekomponentByHSAId(_) >> null;
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.saveTjanstekomponenterToOrder(bestallning)
        then:
        assertNull(bestallning.getInkludera().getTjanstekomponenter().get(0).tjanstekomponent)
    }

    void "test non saving deleted Tjanstekomponent to order "(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        Tjanstekomponent tjanstekomponent = new Tjanstekomponent()
        tjanstekomponent.setDeleted(null)
        daoMock.getTjanstekomponentByHSAId(_) >> tjanstekomponent;
        bestallningService.saveTjanstekomponenterToOrder(bestallning)
        then:
        assertNull(bestallning.getInkludera().getTjanstekomponenter().get(0).tjanstekomponent)
    }


    void "test saving Tjanstekontrakt to order "(){
        setup:
        daoMock.getTjanstekontraktByNamnrymd(_) >> new Tjanstekontrakt()
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.saveTjanstekontraktToOrder(bestallning)
        then:
        assertNotNull(bestallning.getInkludera().getTjanstekontrakt().get(0).tjanstekontrakt)
    }

    void "test non saving nonexistent Tjanstekontrakt to order "() {
        when:
        daoMock.getTjanstekontraktByNamnrymd(_) >> null
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.saveTjanstekontraktToOrder(bestallning)
        then:
        assertNull(bestallning.getInkludera().getTjanstekontrakt().get(0).tjanstekontrakt)
    }
        void "test non saving deleted Tjanstekontrakt to order "() {
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        Tjanstekontrakt tjanstekontrakt = new Tjanstekontrakt()
        tjanstekontrakt.setDeleted(null)
        daoMock.getTjanstekontraktByNamnrymd(_) >> tjanstekontrakt
        bestallningService.saveTjanstekontraktToOrder(bestallning)
        then:
        assertNull(bestallning.getInkludera().getTjanstekontrakt().get(0).tjanstekontrakt)
    }

    void "test non saving nonexistent LogiskAdress to order"() {
        when:
        daoMock.getLogiskAdressByHSAId(_) >> null
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.saveLogiskaAdresserToOrder(bestallning)
        then:
        assertNull(bestallning.getInkludera().getLogiskadresser().get(0).logiskAdress)
    }
    void "test non saving deleted LogiskAdress to order"() {
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        LogiskAdress logiskAdress = new LogiskAdress()
        logiskAdress.setDeleted(null)
        daoMock.getLogiskAdressByHSAId(_) >> logiskAdress
        bestallningService.saveLogiskaAdresserToOrder(bestallning)
        then:
        assertNull(bestallning.getInkludera().getLogiskadresser().get(0).logiskAdress)
    }

    void "test existsLogiskAdressInDBorInOrder in DB"(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        daoMock.getLogiskAdressByHSAId(_) >> new LogiskAdress()
        then:
        assertTrue(bestallningService.existsLogiskAdressInDBorInOrder(BestallningConstructor.SOME_STRING, bestallning))
    }

    void "test existsLogiskAdressInDBorInOrder in Order"(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        BestallningConstructor.addLogiskAddress(BestallningConstructor.SOME_STRING, bestallning)
        then:
        assertTrue(bestallningService.existsLogiskAdressInDBorInOrder(BestallningConstructor.SOME_STRING, bestallning))
    }

    void "test existsLogiskAdressInDBorInOrder not exists"(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        daoMock.getLogiskAdressByHSAId(_) >> null
        then:
        assertFalse(bestallningService.existsLogiskAdressInDBorInOrder(BestallningConstructor.SOME_STRING, bestallning))
    }

    void "test existsTjanstekomponentInDBorInOrder in DB"(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        daoMock.getTjanstekomponentByHSAId(_) >> new Tjanstekomponent()
        then:
        assertTrue(bestallningService.existsTjanstekomponentInDBorInOrder(BestallningConstructor.SOME_STRING, bestallning))
    }

    void "test existsTjanstekomponentInDBorInOrder in Order"(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        BestallningConstructor.addTjanstekomponent(BestallningConstructor.SOME_STRING, bestallning)
        then:
        assertTrue(bestallningService.existsTjanstekomponentInDBorInOrder(BestallningConstructor.SOME_STRING, bestallning))
    }

    void "test existsTjanstekomponentInDBorInOrder not exists"(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        daoMock.getTjanstekomponentByHSAId(_) >> null
        then:
        assertFalse(bestallningService.existsTjanstekomponentInDBorInOrder(BestallningConstructor.SOME_STRING, bestallning))
    }

    void "test existsTjanstekontraktInDBorInOrder in DB"(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        daoMock.getTjanstekontraktByNamnrymd(_) >> new Tjanstekontrakt()
        then:
        assertTrue(bestallningService.existsTjanstekontraktInDBorInOrder(BestallningConstructor.SOME_STRING, bestallning))
    }

    void "test existsTjanstekontraktInDBorInOrder in Order"(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        BestallningConstructor.addTjanstekontrakt(BestallningConstructor.SOME_STRING, bestallning)
        then:
        assertTrue(bestallningService.existsTjanstekontraktInDBorInOrder(BestallningConstructor.SOME_STRING, bestallning))
    }

    void "test existsTjanstekontraktInDBorInOrder not exists"(){
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        daoMock.getTjanstekontraktByNamnrymd(_) >> null
        then:
        assertFalse(bestallningService.existsTjanstekontraktInDBorInOrder(BestallningConstructor.SOME_STRING, bestallning))
    }
}
