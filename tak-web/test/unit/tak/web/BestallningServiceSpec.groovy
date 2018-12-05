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
        i18nServiceMock.msg(_,_) >> "error"
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
        daoMock.getVagval(_, _, _, _) >> new ArrayList(); //finns inte den vagval i databasen

        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning() //skapar beställning med vagval att delete
        bestallningService.validateDeletedVagval(bestallning)
        then:
        assertTrue(bestallning.getBestallningInfo().size() > 0) // i db finns ingen vagval
    }

    void "test validate delete existing vagval"() {
        setup:
        List vv = new ArrayList();
        vv.add(new Vagval())
        daoMock.getVagval(_, _, _, _) >> vv
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.validateDeletedVagval(bestallning)
        then:
        assertTrue(bestallning.getBestallningInfo().size() == 0)
    }

    void "test validate delete nonexistent Anropsbehorighet"() {
        setup:
        daoMock.getAnropsbehorighet(_, _, _) >> new ArrayList();
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.validateDeletedAnropsbehorigheter(bestallning)
        then:
        assertTrue(bestallning.getBestallningInfo().size() > 0)
    }

    void "test validate delete existing Anropsbehorighet"() {
        setup:
        List ab = new ArrayList();
        ab.add(new Anropsbehorighet())
        daoMock.getAnropsbehorighet(_, _, _) >> ab
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.validateDeletedAnropsbehorigheter(bestallning)
        then:
        assertTrue(bestallning.getBestallningInfo().size() == 0)
    }

    void "test create LogiskAdress"() {
        setup:
        daoMock.getLogiskAdressByHSAId(_) >> null
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        LogiskadressBestallning logiskadressBestallning = bestallning.getInkludera().getLogiskadresser().get(0)
        logiskadressBestallning.beskrivning = "beskrivning"

        expect :
        LogiskAdress address = bestallningService.createOrUpdateLogiskAddress(logiskadressBestallning)
        assertEquals(address.id, 0)
        assertEquals(address.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(address.beskrivning, "beskrivning")
    }

    void "test uppdate LogiskAdress"() {
        setup:
        LogiskAdress logiskAdressFromDB = new LogiskAdress()
        logiskAdressFromDB.setId(1)
        logiskAdressFromDB.setHsaId(BestallningConstructor.LOGISK_ADRESS)
        logiskAdressFromDB.beskrivning = "beskrivning"

        daoMock.getLogiskAdressByHSAId(_) >> logiskAdressFromDB
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        LogiskadressBestallning logiskadressBestallning = bestallning.getInkludera().getLogiskadresser().get(0)
        logiskadressBestallning.beskrivning = "beskrivning2"

        expect :
        LogiskAdress address = bestallningService.createOrUpdateLogiskAddress(logiskadressBestallning)
        assertEquals(address.id, 1)
        assertEquals(address.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(address.beskrivning, "beskrivning2")
    }

    void "test create Tjanstekontrakt"() {
        setup:
        daoMock.getTjanstekontraktByNamnrymd(_) >> null

        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        TjanstekontraktBestallning tjanstekontraktBestallning = bestallning.getInkludera().getTjanstekontrakt().get(0)
        tjanstekontraktBestallning.beskrivning = "beskrivning"

        expect :
        Tjanstekontrakt tjanstekontrakt = bestallningService.createOrUpdateTjanstekontrakt(tjanstekontraktBestallning)
        assertEquals(tjanstekontrakt.id, 0)
        assertEquals(tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(tjanstekontrakt.beskrivning, "beskrivning")
    }

    void "test update Tjanstekontrakt"() {
        setup:
        Tjanstekontrakt tjanstekontraktFromDB = new Tjanstekontrakt()
        tjanstekontraktFromDB.setId(1)
        tjanstekontraktFromDB.setNamnrymd(BestallningConstructor.TJANSTEKONTRAKT)
        tjanstekontraktFromDB.beskrivning = "beskrivning"

        daoMock.getTjanstekontraktByNamnrymd(_) >> tjanstekontraktFromDB

        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        TjanstekontraktBestallning tjanstekontraktBestallning = bestallning.getInkludera().getTjanstekontrakt().get(0)
        tjanstekontraktBestallning.beskrivning = "beskrivning1"

        expect :
        Tjanstekontrakt tjanstekontrakt = bestallningService.createOrUpdateTjanstekontrakt(tjanstekontraktBestallning)
        assertEquals(tjanstekontrakt.id, 1)
        assertEquals(tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(tjanstekontrakt.beskrivning, "beskrivning1")
    }

    void "test create Tjanstekomponent"(){
        setup:
        daoMock.getTjanstekomponentByHSAId(_) >> null

        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        TjanstekomponentBestallning tjanstekomponentBestallning = bestallning.getInkludera().getTjanstekomponenter().get(0)
        tjanstekomponentBestallning.beskrivning = "beskrivning"

        expect :
        Tjanstekomponent tjanstekomponent = bestallningService.createOrUpdateTjanstekomponent(tjanstekomponentBestallning)
        assertEquals(tjanstekomponent.id, 0)
        assertEquals(tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(tjanstekomponent.beskrivning, "beskrivning")
    }

    void "test update Tjanstekomponent"(){
        setup:
        Tjanstekomponent tjanstekomponentFromDB = new Tjanstekomponent()
        tjanstekomponentFromDB.setId(1)
        tjanstekomponentFromDB.setHsaId(BestallningConstructor.TJANSTEKOMPONENT)
        tjanstekomponentFromDB.beskrivning = "beskrivning"

        daoMock.getTjanstekomponentByHSAId(_) >> tjanstekomponentFromDB

        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        TjanstekomponentBestallning tjanstekomponentBestallning = bestallning.getInkludera().getTjanstekomponenter().get(0)
        tjanstekomponentBestallning.beskrivning = "beskrivning1"

        expect :
        Tjanstekomponent tjanstekomponent = bestallningService.createOrUpdateTjanstekomponent(tjanstekomponentBestallning)
        assertEquals(tjanstekomponent.id, 1)
        assertEquals(tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(tjanstekomponent.beskrivning, "beskrivning1")
    }


}
