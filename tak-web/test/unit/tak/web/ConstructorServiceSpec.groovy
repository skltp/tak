package tak.web

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*
import spock.lang.Specification
import tak.web.jsonBestallning.ConstructorService
import tak.web.jsonBestallning.DAOService
import tak.web.jsonBestallning.ValidatingService

import java.sql.Date

@TestFor(ConstructorService)
@TestMixin(DomainClassUnitTestMixin)
class ConstructorServiceSpec extends Specification {

    ConstructorService constructorService = new ConstructorService()

    DAOService daoMock
    ValidatingService validatingServiceMock

    BestallningsData data = new BestallningsData(BestallningConstructor.createBestallning())

    def setup() {
        daoMock = Mock(DAOService)
        validatingServiceMock = Mock(ValidatingService)
        constructorService.daoService = daoMock
        constructorService.validatingService = validatingServiceMock
    }

    void "createOrUpdate ska skapa ny LogiskAddress om finns inte LogiskAddress med den hsaId i DB"() {
        setup:
        daoMock.getLogiskAdressByHSAId(_) >> null

        LogiskadressBestallning logiskadressBestallning = data.bestallning.getInkludera().getLogiskadresser().get(0)

        when:
        LogiskAdress address = constructorService.createOrUpdate(logiskadressBestallning)

        then:
        assertEquals(address.id, 0)
        assertEquals(address.hsaId, logiskadressBestallning.hsaId)
        assertEquals(address.beskrivning, logiskadressBestallning.beskrivning)
    }

    void "createOrUpdate ska uppdaters LogiskAddress om finns en med den hsaId i DB"() {
        setup:
        LogiskadressBestallning logiskadressBestallning = data.bestallning.getInkludera().getLogiskadresser().get(0)

        LogiskAdress logiskAdressFromDB = new LogiskAdress()
        logiskAdressFromDB.id = 1
        logiskAdressFromDB.hsaId = logiskadressBestallning.hsaId
        logiskAdressFromDB.beskrivning = "ny beskrivning"

        daoMock.getLogiskAdressByHSAId(_) >> logiskAdressFromDB

        expect:
        LogiskAdress address = constructorService.createOrUpdate(logiskadressBestallning)
        assertEquals(address.id, 1)
        assertEquals(address.hsaId, logiskadressBestallning.hsaId)
        assertEquals(address.beskrivning, logiskadressBestallning.beskrivning)
    }

    void "prepareLogiskAdress ska lägga till ny LogiskAdress i data"() {
        setup:
        validatingServiceMock.validate(_) >> new LinkedList<>()

        when:
        constructorService.prepareLogiskAdress(data)

        then:
        LogiskAdress logiskAdress = data.getLogiskAdress(data.bestallning.inkludera.logiskadresser.get(0))
        assertNotNull(logiskAdress)
        assertTrue(logiskAdress.getId() == 0)
        assertNotNull(logiskAdress.hsaId == data.bestallning.inkludera.logiskadresser.get(0).hsaId)
        assertNotNull(logiskAdress.beskrivning == data.bestallning.inkludera.logiskadresser.get(0).beskrivning)
    }

    void "prepareLogiskAdress får inte lägga till ny LogiskAdress i data om det finns validation error"() {
        setup:
        List<String> error = new LinkedList<>()
        error.add("error")
        validatingServiceMock.validate(_) >> error

        when:
        constructorService.prepareLogiskAdress(data)

        then:
        LogiskAdress logiskAdress = data.getLogiskAdress(data.bestallning.inkludera.logiskadresser.get(0))
        assertNull(logiskAdress)
        assertTrue(data.hasErrors())
    }

    void "createOrUpdate ska skapa ny Tjanstekontrakt om finns inte  Tjanstekontrakt med den hsaId i DB"() {
        setup:
        daoMock.getTjanstekontraktByNamnrymd(_) >> null
        TjanstekontraktBestallning tjanstekontraktBestallning = data.bestallning.getInkludera().getTjanstekontrakt().get(0)

        when:
        Tjanstekontrakt tjanstekontrakt = constructorService.createOrUpdate(tjanstekontraktBestallning)

        then:
        assertEquals(tjanstekontrakt.id, 0)
        assertEquals(tjanstekontrakt.namnrymd, tjanstekontraktBestallning.namnrymd)
        assertEquals(tjanstekontrakt.beskrivning, tjanstekontraktBestallning.beskrivning)
    }

    void "createOrUpdate ska uppdaters Tjanstekontrakt om finns en med den namnrymd i DB"() {
        setup:
        TjanstekontraktBestallning tjanstekontraktBestallning = data.bestallning.getInkludera().getTjanstekontrakt().get(0)

        Tjanstekontrakt tjanstekontraktFromDB = new Tjanstekontrakt()
        tjanstekontraktFromDB.setId(1)
        tjanstekontraktFromDB.setNamnrymd(tjanstekontraktBestallning.namnrymd)
        tjanstekontraktFromDB.beskrivning = "beskrivning"
        daoMock.getTjanstekontraktByNamnrymd(_) >> tjanstekontraktFromDB

        when:
        Tjanstekontrakt tjanstekontrakt = constructorService.createOrUpdate(tjanstekontraktBestallning)

        then:
        assertEquals(tjanstekontrakt.id, 1)
        assertEquals(tjanstekontrakt.namnrymd, tjanstekontraktBestallning.namnrymd)
        assertEquals(tjanstekontrakt.beskrivning, tjanstekontraktBestallning.beskrivning)
    }

    void "prepareTjanstekontrakt ska lägga till ny Tjanstekontrakt i data"() {
        setup:
        validatingServiceMock.validate(_) >> new LinkedList<>()

        when:
        constructorService.prepareTjanstekontrakt(data)

        then:
        Tjanstekontrakt tjanstekontrakt = data.getTjanstekontrakt(data.bestallning.inkludera.tjanstekontrakt.get(0))
        assertNotNull(tjanstekontrakt)
        assertTrue(tjanstekontrakt.getId() == 0)
        assertNotNull(tjanstekontrakt.namnrymd == data.bestallning.inkludera.tjanstekontrakt.get(0).namnrymd)
        assertNotNull(tjanstekontrakt.beskrivning == data.bestallning.inkludera.tjanstekontrakt.get(0).beskrivning)
    }

    void "prepareTjanstekontrakt får inte lägga till ny Tjanstekontrakt i data om det finns validation error"() {
        setup:
        List<String> error = new LinkedList<>()
        error.add("error")
        validatingServiceMock.validate(_) >> error

        when:
        constructorService.prepareTjanstekontrakt(data)

        then:
        Tjanstekontrakt tjanstekontrakt = data.getTjanstekontrakt(data.bestallning.inkludera.tjanstekontrakt.get(0))
        assertNull(tjanstekontrakt)
        assertTrue(data.hasErrors())
    }

    void "createOrUpdate ska skapa ny Tjanstekomponent om finns inte Tjanstekomponent med den hsaId i DB"() {
        setup:
        daoMock.getTjanstekomponentByHSAId(_) >> null

        TjanstekomponentBestallning tjanstekomponentBestallning = data.bestallning.getInkludera().getTjanstekomponenter().get(0)

        expect:
        Tjanstekomponent tjanstekomponent = constructorService.createOrUpdate(tjanstekomponentBestallning)
        assertEquals(tjanstekomponent.id, 0)
        assertEquals(tjanstekomponent.hsaId, tjanstekomponentBestallning.hsaId)
        assertEquals(tjanstekomponent.beskrivning, tjanstekomponentBestallning.beskrivning)
    }

    void "createOrUpdate ska uppdaters Tjanstekomponent om finns en med den namnrymd i DB"() {
        setup:
        TjanstekomponentBestallning tjanstekomponentBestallning = data.bestallning.getInkludera().getTjanstekomponenter().get(0)

        Tjanstekomponent tjanstekomponentFromDB = new Tjanstekomponent()
        tjanstekomponentFromDB.setId(1)
        tjanstekomponentFromDB.setHsaId(tjanstekomponentBestallning.hsaId)
        tjanstekomponentFromDB.beskrivning = "beskrivning1"

        daoMock.getTjanstekomponentByHSAId(_) >> tjanstekomponentFromDB

        expect:
        Tjanstekomponent tjanstekomponent = constructorService.createOrUpdate(tjanstekomponentBestallning)
        assertEquals(tjanstekomponent.id, 1)
        assertEquals(tjanstekomponent.hsaId, tjanstekomponentBestallning.hsaId)
        assertEquals(tjanstekomponent.beskrivning, tjanstekomponentBestallning.beskrivning)
    }

    void "prepareTjanstekomponent ska lägga till ny Tjanstekomponent i data"() {
        setup:
        validatingServiceMock.validate(_) >> new LinkedList<>()

        when:
        constructorService.prepareTjanstekomponent(data)

        then:
        Tjanstekomponent tjanstekomponent = data.getTjanstekomponent(data.bestallning.inkludera.tjanstekomponenter.get(0))
        assertNotNull(tjanstekomponent)
        assertTrue(tjanstekomponent.getId() == 0)
        assertNotNull(tjanstekomponent.hsaId == data.bestallning.inkludera.tjanstekomponenter.get(0).hsaId)
        assertNotNull(tjanstekomponent.beskrivning == data.bestallning.inkludera.tjanstekomponenter.get(0).beskrivning)
    }

    void "prepareTjanstekomponent får inte lägga till ny Tjanstekomponent i data om det finns validation error"() {
        setup:
        List<String> error = new LinkedList<>()
        error.add("error")
        validatingServiceMock.validate(_) >> error

        when:
        constructorService.prepareTjanstekomponent(data)

        then:
        Tjanstekomponent tjanstekomponent = data.getTjanstekomponent(data.bestallning.inkludera.tjanstekomponenter.get(0))
        assertNull(tjanstekomponent)
        assertTrue(data.hasErrors())
    }

    void "findAndDeactivate ska hitta och deaktivera Vagval"() {
        setup:

        Vagval vagvalFromDB = ObjectsConstructor.createVagval(new Date(System.currentTimeMillis()))
        List<Vagval> vvlist = new ArrayList<Vagval>()
        vvlist.add(vagvalFromDB)
        daoMock.getVagval(_, _, _, _) >> vvlist

        VagvalBestallning vagvalBestallning = BestallningConstructor.createVagvalBestallning(
                vagvalFromDB.logiskAdress.hsaId,
                vagvalFromDB.anropsAdress.tjanstekomponent.hsaId,
                vagvalFromDB.tjanstekontrakt.namnrymd,
                vagvalFromDB.anropsAdress.adress,
                vagvalFromDB.anropsAdress.rivTaProfil.namn)
        when:
        List<Vagval> list = constructorService.findAndDeactivate(
                vagvalBestallning,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))
        then:
        assertTrue(list.size() == 1)
        assertTrue(list.get(0).tomTidpunkt < data.bestallning.genomforandeTidpunkt)
    }

    void "prepareVagvalForDelete ska deaktivera Vagval och spara den till data"() {
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(new Date(System.currentTimeMillis()))

        List<Vagval> vvlist = new ArrayList<Vagval>()
        vvlist.add(vagvalFromDB)
        daoMock.getVagval(_, _, _, _) >> vvlist

        validatingServiceMock.validateVagvalForDubblett(_) >> new LinkedList<>()
        validatingServiceMock.validateExists(_, _) >> new LinkedList<>()

        when:
        constructorService.prepareVagvalForDelete(data)
        then:
        assertFalse(data.hasErrors())
        BestallningsData.VagvalPair vagvalPair = data.getVagval(data.bestallning.exkludera.vagval.get(0))
        assertNotNull(vagvalPair)
        assertNotNull(vagvalPair.oldVagval)
        assertTrue(vagvalPair.oldVagval.tomTidpunkt < data.bestallning.genomforandeTidpunkt)
    }

    void "prepareVagvalForDelete ska lägga till error om det finns några vagval med samma parametrar"() {
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(new Date(System.currentTimeMillis()))

        List<Vagval> vvlist = new ArrayList<Vagval>()
        vvlist.add(vagvalFromDB)
        daoMock.getVagval(_, _, _, _) >> vvlist

        List<String> error = new LinkedList<String>()
        error.add("error")
        validatingServiceMock.validateVagvalForDubblett(_) >> error
        validatingServiceMock.validateExists(_, _) >> new LinkedList<>()

        when:
        constructorService.prepareVagvalForDelete(data)
        then:
        assertTrue(data.hasErrors())
        BestallningsData.VagvalPair vagvalPair = data.getVagval(data.bestallning.exkludera.vagval.get(0))
        assertNull(vagvalPair)
    }

    void "prepareVagvalForDelete ska lögga till problem om det finns inga vagval med dessa parametrar"() {
        daoMock.getVagval(_, _, _, _) >> new ArrayList<Vagval>()

        List<String> error = new LinkedList<String>()
        error.add("error")
        validatingServiceMock.validateVagvalForDubblett(_) >> new LinkedList<>()
        validatingServiceMock.validateExists(_, _) >> error

        when:
        constructorService.prepareVagvalForDelete(data)
        then:
        assertFalse(data.hasErrors())
        assertFalse(data.getBestallningInfo().isEmpty())
        BestallningsData.VagvalPair vagvalPair = data.getVagval(data.bestallning.exkludera.vagval.get(0))
        assertNull(vagvalPair)
    }

    void "findAndDeactivate ska hitta och deaktivera Anropsbehorighet"() {
        setup:
        Anropsbehorighet anropsbehorighetFromDB = ObjectsConstructor.createAnropsbehorighet(new Date(System.currentTimeMillis()))
        List<Anropsbehorighet> abList = new ArrayList<Anropsbehorighet>()
        abList.add(anropsbehorighetFromDB)
        daoMock.getAnropsbehorighet(_, _, _, _, _) >> abList

        AnropsbehorighetBestallning anropsbehorighetBestallning = BestallningConstructor.createAnropsbehorighetBestallning(
                anropsbehorighetFromDB.logiskAdress.hsaId,
                anropsbehorighetFromDB.tjanstekonsument.hsaId,
                anropsbehorighetFromDB.tjanstekontrakt.namnrymd
                )
        when:
        List<Anropsbehorighet> list = constructorService.findAndDeactivate(
                anropsbehorighetBestallning,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))
        then:
        assertTrue(list.size() == 1)
        assertTrue(list.get(0).tomTidpunkt < data.bestallning.genomforandeTidpunkt)
    }

    void "prepareVagvalForDelete ska deaktivera Anropsbehorighet och spara den till data"() {
        Anropsbehorighet anropsbehorighetFromDB = ObjectsConstructor.createAnropsbehorighet(new Date(System.currentTimeMillis()))
        List<Anropsbehorighet> abList = new ArrayList<Anropsbehorighet>()
        abList.add(anropsbehorighetFromDB)
        daoMock.getAnropsbehorighet(_, _, _, _, _) >> abList

        validatingServiceMock.validateAnropsbehorighetForDubblett(_) >> new LinkedList<>()
        validatingServiceMock.validateExists(_, _) >> new LinkedList<>()

        when:
        constructorService.prepareAnropsbehorighetForDelete(data)
        then:
        assertFalse(data.hasErrors())
        Anropsbehorighet anropsbehorighet = data.getAnropsbehorighet(data.bestallning.exkludera.anropsbehorigheter.get(0))
        assertTrue(anropsbehorighet.tomTidpunkt < data.bestallning.genomforandeTidpunkt)
    }

    void "prepareVagvalForDelete ska lägga till error om det finns några Anropsbehorighet med samma parametrar"() {
        Anropsbehorighet anropsbehorighetFromDB = ObjectsConstructor.createAnropsbehorighet(new Date(System.currentTimeMillis()))
        List<Anropsbehorighet> abList = new ArrayList<Anropsbehorighet>()
        abList.add(anropsbehorighetFromDB)
        daoMock.getAnropsbehorighet(_, _, _, _, _) >> abList

        List<String> error = new LinkedList<String>()
        error.add("error")
        validatingServiceMock.validateAnropsbehorighetForDubblett(_) >> error
        validatingServiceMock.validateExists(_, _) >> new LinkedList<>()

        when:
        constructorService.prepareAnropsbehorighetForDelete(data)
        then:
        assertTrue(data.hasErrors())
        Anropsbehorighet anropsbehorighet = data.getAnropsbehorighet(data.bestallning.exkludera.anropsbehorigheter.get(0))
        assertNull(anropsbehorighet)
    }

    void "prepareVagvalForDelete ska lögga till problem om det finns inga Anropsbehorighet med dessa parametrar"() {
        daoMock.getAnropsbehorighet(_, _, _, _, _) >> new ArrayList<Anropsbehorighet>()

        List<String> error = new LinkedList<String>()
        error.add("error")
        validatingServiceMock.validateAnropsbehorighetForDubblett(_) >> new LinkedList<>()
        validatingServiceMock.validateExists(_, _) >> error

        when:
        constructorService.prepareAnropsbehorighetForDelete(data)
        then:
        assertFalse(data.hasErrors())
        assertFalse(data.getBestallningInfo().isEmpty())
        Anropsbehorighet anropsbehorighet = data.getAnropsbehorighet(data.bestallning.exkludera.anropsbehorigheter.get(0))
        assertNull(anropsbehorighet)
    }
}
