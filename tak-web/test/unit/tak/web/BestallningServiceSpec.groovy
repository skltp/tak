package tak.web

import grails.test.mixin.TestFor
import org.apache.commons.io.FileUtils
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.LogiskAdress
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
    def i18nService

    def setup() {
        daoMock = Mock(DAOService)
        i18nService = Mock(I18nService)
        bestallningService.daoService = daoMock
        bestallningService.i18nService = i18nService

        i18nService.msg(_) >> "error"
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

//	void "test validate added vagval with "() {
//		given:
//
//		bestallningService.daoService.getLogiskAdressByHSAId() >> null
//
//		JsonBestallning bestallning = new JsonBestallning()
//		KollektivData addData = new KollektivData()
//		addData.setLogiskadresser(new LinkedList<LogiskadressBestallning>())
//
//		VagvalBestallning vagvalBestallning = new VagvalBestallning()
//		vagvalBestallning.logiskAdress = ""
//
//		List<VagvalBestallning> bestallningLinkedList = new LinkedList<VagvalBestallning>()
//		bestallningLinkedList.add(vagvalBestallning)
//		addData.setVagval(bestallningLinkedList)
//		bestallning.setInkludera(addData)
//
//		when:
//			bestallningService.validateAddedVagval(bestallning)
//		then:
//			println bestallning.bestallningErrors.size()
//
//
//	}

    void "test validate nonexistent vagval"() {
        setup:
        daoMock.getVagval(_, _, _, _, _) >> new ArrayList<Vagval>() //finns inte den vagval i databasen
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning() //skapar beställning med vagval att delete
        bestallningService.validateDeletedVagval(bestallning)
        then:
        assertFalse(bestallning.isValidBestallning()) // i db finns ingen vagval
    }

    void "test validate existing vagval"() {
        setup:
        List vagvalList = new ArrayList<Vagval>()
        vagvalList.add(new Vagval())
        daoMock.getVagval(_, _, _, _, _) >> vagvalList //finns inte den vagval i databasen
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning() //skapar beställning med vagval att delete
        bestallningService.validateDeletedVagval(bestallning)
        then:
        assertTrue(bestallning.isValidBestallning()) // i db finns ingen vagval
    }

    void "test validate nonexistent Anropsbehorighet"() {
        setup:
        daoMock.getAnropsbehorighet(_, _, _) >> new ArrayList<Anropsbehorighet>()
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.validateDeletedAnropsbehorigheter(bestallning)
        then:
        assertFalse(bestallning.isValidBestallning())
    }

    void "test validate existing Anropsbehorighet"() {
        setup:
        List anropsbehorighetList = new ArrayList<Anropsbehorighet>()
        anropsbehorighetList.add(new Anropsbehorighet())
        daoMock.getAnropsbehorighet(_, _, _) >> anropsbehorighetList
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.validateDeletedAnropsbehorigheter(bestallning)
        then:
        assertTrue(bestallning.isValidBestallning())
    }

    void "test saveing LogiskaAdresser to order "() {
        setup:
        daoMock.getLogiskAdressByHSAId(_) >> new LogiskAdress()
        when:
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        bestallningService.saveLogiskaAdresserToOrder(bestallning)
        then:
        assertNotNull(bestallning.getInkludera().getLogiskadresser().get(0).logiskAdress)
    }
}
