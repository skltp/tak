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
    ValidatingService validatingService

    def setup() {
        daoMock = Mock(DAOService)
        validatingService = Mock(ValidatingService)
        constructorService.daoService = daoMock
        constructorService.validatingService = validatingService
    }

    void "test prepate LogiskAdress for create"() {
        setup:
        daoMock.getLogiskAdressByHSAId(_) >> null
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        LogiskadressBestallning logiskadressBestallning = bestallning.getInkludera().getLogiskadresser().get(0)
        logiskadressBestallning.beskrivning = "beskrivning"

        expect:
        LogiskAdress address = constructorService.createByBestallning(logiskadressBestallning)
        assertEquals(address.id, 0)
        assertEquals(address.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(address.beskrivning, "beskrivning")
    }

    void "test prepate LogiskAdress for update"() {
        setup:
        LogiskAdress logiskAdressFromDB = new LogiskAdress()
        logiskAdressFromDB.setId(1)
        logiskAdressFromDB.setHsaId(BestallningConstructor.LOGISK_ADRESS)
        logiskAdressFromDB.beskrivning = "beskrivning"

        daoMock.getLogiskAdressByHSAId(_) >> logiskAdressFromDB
        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        LogiskadressBestallning logiskadressBestallning = bestallning.getInkludera().getLogiskadresser().get(0)
        logiskadressBestallning.beskrivning = "beskrivning2"

        expect:
        LogiskAdress address = constructorService.createByBestallning(logiskadressBestallning)
        assertEquals(address.id, 1)
        assertEquals(address.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(address.beskrivning, "beskrivning2")
    }

    void "test prepate Tjanstekontrakt for create"() {
        setup:
        daoMock.getTjanstekontraktByNamnrymd(_) >> null

        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        TjanstekontraktBestallning tjanstekontraktBestallning = bestallning.getInkludera().getTjanstekontrakt().get(0)
        tjanstekontraktBestallning.beskrivning = "beskrivning"

        expect:
        Tjanstekontrakt tjanstekontrakt = constructorService.createByBestallning(tjanstekontraktBestallning)
        assertEquals(tjanstekontrakt.id, 0)
        assertEquals(tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(tjanstekontrakt.beskrivning, "beskrivning")
    }

    void "test prepate Tjanstekontrakt for update"() {
        setup:
        Tjanstekontrakt tjanstekontraktFromDB = new Tjanstekontrakt()
        tjanstekontraktFromDB.setId(1)
        tjanstekontraktFromDB.setNamnrymd(BestallningConstructor.TJANSTEKONTRAKT)
        tjanstekontraktFromDB.beskrivning = "beskrivning"

        daoMock.getTjanstekontraktByNamnrymd(_) >> tjanstekontraktFromDB

        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        TjanstekontraktBestallning tjanstekontraktBestallning = bestallning.getInkludera().getTjanstekontrakt().get(0)
        tjanstekontraktBestallning.beskrivning = "beskrivning1"

        expect:
        Tjanstekontrakt tjanstekontrakt = constructorService.createByBestallning(tjanstekontraktBestallning)
        assertEquals(tjanstekontrakt.id, 1)
        assertEquals(tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(tjanstekontrakt.beskrivning, "beskrivning1")
    }

    void "test prepate Tjanstekomponent for create"() {
        setup:
        daoMock.getTjanstekomponentByHSAId(_) >> null

        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        TjanstekomponentBestallning tjanstekomponentBestallning = bestallning.getInkludera().getTjanstekomponenter().get(0)
        tjanstekomponentBestallning.beskrivning = "beskrivning"

        expect:
        Tjanstekomponent tjanstekomponent = constructorService.createByBestallning(tjanstekomponentBestallning)
        assertEquals(tjanstekomponent.id, 0)
        assertEquals(tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(tjanstekomponent.beskrivning, "beskrivning")
    }

    void "test prepate Tjanstekomponent for update"() {
        setup:
        Tjanstekomponent tjanstekomponentFromDB = new Tjanstekomponent()
        tjanstekomponentFromDB.setId(1)
        tjanstekomponentFromDB.setHsaId(BestallningConstructor.TJANSTEKOMPONENT)
        tjanstekomponentFromDB.beskrivning = "beskrivning"

        daoMock.getTjanstekomponentByHSAId(_) >> tjanstekomponentFromDB

        JsonBestallning bestallning = BestallningConstructor.createBestallning()
        TjanstekomponentBestallning tjanstekomponentBestallning = bestallning.getInkludera().getTjanstekomponenter().get(0)
        tjanstekomponentBestallning.beskrivning = "beskrivning1"

        expect:
        Tjanstekomponent tjanstekomponent = constructorService.createByBestallning(tjanstekomponentBestallning)
        assertEquals(tjanstekomponent.id, 1)
        assertEquals(tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(tjanstekomponent.beskrivning, "beskrivning1")
    }

    void "test prepate Vagval for delete"() {
        setup:
        //init db
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL,
                BestallningConstructor.generateDateGreaterThan(jsonBestallning.genomforandeTidpunkt))

        List<Vagval> vvlist = new ArrayList<Vagval>()
        vvlist.add(vagvalFromDB)
        daoMock.getVagval(_, _, _, _, _, _) >> vvlist

        VagvalBestallning vagvalBestallning = BestallningConstructor.createVagvalBestallning(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL)
        when:
        List<Vagval> list = constructorService.createByBestallningForDelete(
                vagvalBestallning,
                jsonBestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(jsonBestallning.genomforandeTidpunkt))
        then:
        assertTrue(list.size() == 1)
        assertTrue(list.get(0).tomTidpunkt < jsonBestallning.genomforandeTidpunkt)
    }

    void "test prepare Anropsbehorighet for delete"() {
        setup:
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        Anropsbehorighet anropsbehorighetFromDB = ObjectsConstructor.createAnropsbehorighet(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.generateDateGreaterThan(jsonBestallning.genomforandeTidpunkt))

        List<Anropsbehorighet> anropsbehorighetList = new ArrayList<Anropsbehorighet>()
        anropsbehorighetList.add(anropsbehorighetFromDB)
        daoMock.getAnropsbehorighet(_, _, _, _, _) >> anropsbehorighetList

        AnropsbehorighetBestallning anropsbehorighetBestallning = BestallningConstructor.createAnropsbehorighetBestallning(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT
        )
        when:
        List<Anropsbehorighet> list = constructorService.createByBestallningForDelete(anropsbehorighetBestallning,
                jsonBestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(jsonBestallning.genomforandeTidpunkt))
        then:
        assertTrue(list.size() == 1)
        assertTrue(list.get(0).tomTidpunkt < jsonBestallning.genomforandeTidpunkt)
    }

    void "test prepare Anropsbehorighet for create with dubletter i db"() {
        setup:
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        Anropsbehorighet anropsbehorighetFromDB = ObjectsConstructor.createAnropsbehorighet(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.generateDateGreaterThan(jsonBestallning.genomforandeTidpunkt))

        List<Anropsbehorighet> anropsbehorighetList = new ArrayList<Anropsbehorighet>()
        anropsbehorighetList.add(anropsbehorighetFromDB)
        anropsbehorighetList.add(anropsbehorighetFromDB)
        daoMock.getAnropsbehorighet(_, _, _, _, _) >> anropsbehorighetList

        AnropsbehorighetBestallning anropsbehorighetBestallning = BestallningConstructor.createAnropsbehorighetBestallning(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT
        )
        BestallningsData data = BestallningConstructor.createBestallningsData(jsonBestallning)
        when:
        constructorService.prepareAnropsbehorighet(anropsbehorighetBestallning, data,
                jsonBestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(jsonBestallning.genomforandeTidpunkt))
        then:
        assertNull(data.getAnropsbehorighet(anropsbehorighetBestallning))
    }

    void "test prepare Anropsbehorighet for uppdate"() {
        setup:
        BestallningsData data = BestallningDataConstructor.createBestallningDataForNewAnropsbehorighet(BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT)

        Anropsbehorighet anropsbehorighetFromDB = ObjectsConstructor.createAnropsbehorighet(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                data.bestallning.genomforandeTidpunkt)

        List<Anropsbehorighet> anropsbehorighetList = new ArrayList<Anropsbehorighet>()
        anropsbehorighetList.add(anropsbehorighetFromDB)
        daoMock.getAnropsbehorighet(_, _, _, _, _) >> anropsbehorighetList


        AnropsbehorighetBestallning anropsbehorighetBestallning = data.bestallning.inkludera.anropsbehorigheter.get(0)
        when:
        Date fromDate = BestallningConstructor.generateDateGreaterThan(data.bestallning.genomforandeTidpunkt)
        anropsbehorighetFromDB.fromTidpunkt = fromDate
        constructorService.prepareAnropsbehorighet(anropsbehorighetBestallning, data,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))
        then:
        assertNotNull(data.getAnropsbehorighet(anropsbehorighetBestallning))
        assertEquals(anropsbehorighetFromDB, data.getAnropsbehorighet(anropsbehorighetBestallning))
        assertEquals(data.bestallning.genomforandeTidpunkt, data.getAnropsbehorighet(anropsbehorighetBestallning).fromTidpunkt)

        when:
        fromDate = BestallningConstructor.generateDateLowerThan(data.bestallning.genomforandeTidpunkt)
        anropsbehorighetFromDB.fromTidpunkt = fromDate
        constructorService.prepareAnropsbehorighet(anropsbehorighetBestallning, data,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))
        then:
        assertNotNull(data.getAnropsbehorighet(anropsbehorighetBestallning))
        assertEquals(anropsbehorighetFromDB, data.getAnropsbehorighet(anropsbehorighetBestallning))
        assertEquals(fromDate, data.getAnropsbehorighet(anropsbehorighetBestallning).fromTidpunkt)
    }

    void "test prepare Anropsbehorighet for create"() {
        setup:
        daoMock.getAnropsbehorighet(_, _, _, _, _) >> new ArrayList<Anropsbehorighet>()

        BestallningsData data = BestallningDataConstructor.createBestallningDataForNewAnropsbehorighet(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT)
        AnropsbehorighetBestallning anropsbehorighetBestallning = data.bestallning.inkludera.anropsbehorigheter.get(0)
        when:
        constructorService.prepareAnropsbehorighet(anropsbehorighetBestallning, data,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))
        Anropsbehorighet newAnropsbehorighet = data.getAnropsbehorighet(anropsbehorighetBestallning)
        then:
        assertNotNull(newAnropsbehorighet)
        assertTrue(newAnropsbehorighet.fromTidpunkt == data.bestallning.genomforandeTidpunkt)
        assertTrue(newAnropsbehorighet.tomTidpunkt == BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))
    }

    void "test prepare Vagval for create with dubletter i db"() {
        setup:
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL,
                BestallningConstructor.generateDateGreaterThan(jsonBestallning.genomforandeTidpunkt))

        List<Vagval> vvList = new ArrayList<Vagval>()
        vvList.add(vagvalFromDB)
        vvList.add(vagvalFromDB)
        daoMock.getVagval(_, _, _, _) >> vvList

        VagvalBestallning vagvalBestallning = BestallningConstructor.createVagvalBestallning(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL)
        BestallningsData data = new BestallningsData(jsonBestallning)
        when:
        constructorService.prepareVagval(vagvalBestallning, data,
                jsonBestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(jsonBestallning.genomforandeTidpunkt))
        then:
        assertNull(data.getVagval(vagvalBestallning))
    }

    void "test prepare Vagval for create"() {
        setup:
        daoMock.getVagval(_, _, _, _) >> new ArrayList<Anropsbehorighet>()

        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        VagvalBestallning vvBestallning = BestallningConstructor.createVagvalBestallning(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL)
        jsonBestallning.inkludera.vagval.add(vvBestallning)
        BestallningsData data = new BestallningsData(jsonBestallning)

        BestallningDataConstructor.fillDataAboutRelations(data, vvBestallning,
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.RIVTA_PROFIL)

        when:
        constructorService.prepareVagval(vvBestallning, data,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))
        BestallningsData.VagvalPair newVagval = data.getVagval(vvBestallning)
        then:
        assertNotNull(newVagval)
        assertTrue(newVagval.newVagval.fromTidpunkt == data.bestallning.genomforandeTidpunkt)
        assertTrue(newVagval.newVagval.tomTidpunkt == BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))
        assertEquals(data.getVagval(vvBestallning).newVagval.logiskAdress.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(data.getVagval(vvBestallning).newVagval.tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(data.getVagval(vvBestallning).newVagval.anropsAdress.tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(data.getVagval(vvBestallning).newVagval.anropsAdress.rivTaProfil.namn, BestallningConstructor.RIVTA_PROFIL)
        assertEquals(data.getVagval(vvBestallning).newVagval.anropsAdress.adress, BestallningConstructor.ADRESS)

        assertNull(newVagval.oldVagval)
    }

    void "test prepare Vagval for uppdate [old vagvals AnropsAdress == new vagvals AnropAdress && oldvagval.fromdate > genomforandeTidpunkt]"() {
        setup:
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        VagvalBestallning vvBestallning = BestallningConstructor.createVagvalBestallning(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL)
        jsonBestallning.inkludera.vagval.add(vvBestallning)
        BestallningsData data = new BestallningsData(jsonBestallning)

        BestallningDataConstructor.fillDataAboutRelations(data, vvBestallning,
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.RIVTA_PROFIL)

        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                data.getVagvalRelations(vvBestallning).logiskadress,
                data.getVagvalRelations(vvBestallning).tjanstekomponent,
                data.getVagvalRelations(vvBestallning).tjanstekontrakt,
                data.getVagvalRelations(vvBestallning).profil,
                BestallningConstructor.ADRESS,
                BestallningConstructor.generateDateGreaterThan(jsonBestallning.genomforandeTidpunkt))


        List<Vagval> vvList = new ArrayList<Vagval>()
        vvList.add(vagvalFromDB)
        daoMock.getVagval(_, _, _, _) >> vvList


        when:
        constructorService.prepareVagval(vvBestallning, data,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))

        then:
        assertNull(data.getVagval(vvBestallning).newVagval)
        assertNotNull(data.getVagval(vvBestallning).oldVagval)
        assertEquals(vagvalFromDB, data.getVagval(vvBestallning).oldVagval)
        assertEquals(vagvalFromDB.fromTidpunkt, jsonBestallning.genomforandeTidpunkt)
        assertEquals(data.getVagval(vvBestallning).oldVagval.logiskAdress.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(data.getVagval(vvBestallning).oldVagval.tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.rivTaProfil.namn, BestallningConstructor.RIVTA_PROFIL)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.adress, BestallningConstructor.ADRESS)

    }

    void "test prepare Vagval for uppdate [old vagvals AnropsAdress == new vagvals AnropAdress && oldvagval.fromdate < genomforandeTidpunkt]"() {
        setup:
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        VagvalBestallning vvBestallning = BestallningConstructor.createVagvalBestallning(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL)
        jsonBestallning.inkludera.vagval.add(vvBestallning)
        BestallningsData data = new BestallningsData(jsonBestallning)

        BestallningDataConstructor.fillDataAboutRelations(data, vvBestallning,
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.RIVTA_PROFIL)

        Date fromDate = BestallningConstructor.generateDateLowerThan(jsonBestallning.genomforandeTidpunkt)
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                data.getVagvalRelations(vvBestallning).logiskadress,
                data.getVagvalRelations(vvBestallning).tjanstekomponent,
                data.getVagvalRelations(vvBestallning).tjanstekontrakt,
                data.getVagvalRelations(vvBestallning).profil,
                BestallningConstructor.ADRESS,
                BestallningConstructor.generateDateLowerThan(jsonBestallning.genomforandeTidpunkt))


        List<Vagval> vvList = new ArrayList<Vagval>()
        vvList.add(vagvalFromDB)
        daoMock.getVagval(_, _, _, _) >> vvList


        when:
        constructorService.prepareVagval(vvBestallning, data,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))

        then:
        assertNull(data.getVagval(vvBestallning).newVagval)
        assertNotNull(data.getVagval(vvBestallning).oldVagval)
        assertEquals(vagvalFromDB, data.getVagval(vvBestallning).oldVagval)
        assertEquals(vagvalFromDB.fromTidpunkt, fromDate)
        assertEquals(data.getVagval(vvBestallning).oldVagval.logiskAdress.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(data.getVagval(vvBestallning).oldVagval.tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.rivTaProfil.namn, BestallningConstructor.RIVTA_PROFIL)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.adress, BestallningConstructor.ADRESS)
    }

    void "test prepare Vagval for uppdate [old vagvals AnropsAdress != new vagvals AnropAdress && oldvagval.fromdate > genomforandeTidpunkt]"() {
        setup:
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        VagvalBestallning vvBestallning = BestallningConstructor.createVagvalBestallning(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL)
        jsonBestallning.inkludera.vagval.add(vvBestallning)
        BestallningsData data = new BestallningsData(jsonBestallning)

        BestallningDataConstructor.fillDataAboutRelations(data, vvBestallning,
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.RIVTA_PROFIL)

        String annanAddress = "annan adress"
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                data.getVagvalRelations(vvBestallning).logiskadress,
                data.getVagvalRelations(vvBestallning).tjanstekomponent,
                data.getVagvalRelations(vvBestallning).tjanstekontrakt,
                data.getVagvalRelations(vvBestallning).profil,
                annanAddress,
                BestallningConstructor.generateDateGreaterThan(jsonBestallning.genomforandeTidpunkt))


        List<Vagval> vvList = new ArrayList<Vagval>()
        vvList.add(vagvalFromDB)
        daoMock.getVagval(_, _, _, _) >> vvList


        when:
        constructorService.prepareVagval(vvBestallning, data,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))

        then:
        assertNotNull(data.getVagval(vvBestallning).newVagval)
        assertEquals(data.getVagval(vvBestallning).newVagval.id, 0l)
        assertEquals(data.getVagval(vvBestallning).newVagval.logiskAdress.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(data.getVagval(vvBestallning).newVagval.tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(data.getVagval(vvBestallning).newVagval.anropsAdress.tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(data.getVagval(vvBestallning).newVagval.anropsAdress.rivTaProfil.namn, BestallningConstructor.RIVTA_PROFIL)
        assertEquals(data.getVagval(vvBestallning).newVagval.anropsAdress.adress, BestallningConstructor.ADRESS)
        assertEquals(data.getVagval(vvBestallning).newVagval.fromTidpunkt, data.bestallning.genomforandeTidpunkt)

        assertNotNull(data.getVagval(vvBestallning).oldVagval)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.adress, annanAddress)
        assertTrue(data.getVagval(vvBestallning).oldVagval.deleted)

    }

    void "test prepare Vagval for uppdate [old vagvals AnropsAdress != new vagvals AnropAdress && oldvagval.fromdate < genomforandeTidpunkt]"() {
        setup:
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        VagvalBestallning vvBestallning = BestallningConstructor.createVagvalBestallning(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL)
        jsonBestallning.inkludera.vagval.add(vvBestallning)
        BestallningsData data = new BestallningsData(jsonBestallning)

        BestallningDataConstructor.fillDataAboutRelations(data, vvBestallning,
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.RIVTA_PROFIL)

        String annanAddress = "annan adress"
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                data.getVagvalRelations(vvBestallning).logiskadress,
                data.getVagvalRelations(vvBestallning).tjanstekomponent,
                data.getVagvalRelations(vvBestallning).tjanstekontrakt,
                data.getVagvalRelations(vvBestallning).profil,
                annanAddress,
                BestallningConstructor.generateDateLowerThan(jsonBestallning.genomforandeTidpunkt))


        List<Vagval> vvList = new ArrayList<Vagval>()
        vvList.add(vagvalFromDB)
        daoMock.getVagval(_, _, _, _) >> vvList


        when:
        constructorService.prepareVagval(vvBestallning, data,
                data.bestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(data.bestallning.genomforandeTidpunkt))

        then:
        assertNotNull(data.getVagval(vvBestallning).newVagval)
        assertEquals(data.getVagval(vvBestallning).newVagval.id, 0l)
        assertEquals(data.getVagval(vvBestallning).newVagval.logiskAdress.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(data.getVagval(vvBestallning).newVagval.tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(data.getVagval(vvBestallning).newVagval.anropsAdress.tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(data.getVagval(vvBestallning).newVagval.anropsAdress.rivTaProfil.namn, BestallningConstructor.RIVTA_PROFIL)
        assertEquals(data.getVagval(vvBestallning).newVagval.anropsAdress.adress, BestallningConstructor.ADRESS)
        assertEquals(data.getVagval(vvBestallning).newVagval.fromTidpunkt, data.bestallning.genomforandeTidpunkt)

        assertNotNull(data.getVagval(vvBestallning).oldVagval)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.adress, annanAddress)
        assertFalse(data.getVagval(vvBestallning).oldVagval.deleted)
        assertEquals(data.getVagval(vvBestallning).oldVagval.tomTidpunkt, generateDateMinusDag(data.bestallning.genomforandeTidpunkt))
    }

    private Date generateDateMinusDag(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -1);
            Date d = new Date(c.getTime().getTime());
            return d;
        }
        return null;
    }
}
