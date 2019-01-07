package tak.web

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.Vagval
import se.skltp.tak.web.jsonBestallning.AnropsbehorighetBestallning
import se.skltp.tak.web.jsonBestallning.BestallningsData
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import se.skltp.tak.web.jsonBestallning.VagvalBestallning
import spock.lang.Specification
import tak.web.jsonBestallning.ConstructorService
import tak.web.jsonBestallning.DAOService
import tak.web.jsonBestallning.ValidatingService

import java.sql.Date

@TestFor(ConstructorService)
@TestMixin(DomainClassUnitTestMixin)
class ConstructorServiceWithComplexObjectsSpec extends Specification {

    ConstructorService constructorService = new ConstructorService()

    DAOService daoMock
    ValidatingService validatingServiceMock

    def setup() {
        daoMock = Mock(DAOService)
        validatingServiceMock = Mock(ValidatingService)
        constructorService.daoService = daoMock
        constructorService.validatingService = validatingServiceMock
    }

    void "prepareAnropsbehorighet ska hitta och ändra Anropsbehorighet om den redan finns i DB"() {
        setup:
        validatingServiceMock.validateAnropsbehorighetForDubblett(_) >> new LinkedList<>()

        BestallningsData data = BestallningDataConstructor.createBestallningDataForAnropsbehorighetAndRelations()

        Anropsbehorighet anropsbehorighetFromDB = ObjectsConstructor.createAnropsbehorighet(data.bestallning.genomforandeTidpunkt
        )

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

    void "prepareAnropsbehorighet ska skapa Anropsbehorighet om den finns inte i DB"() {
        setup:
        validatingServiceMock.validateAnropsbehorighetForDubblett(_) >> new LinkedList<>()
        daoMock.getAnropsbehorighet(_, _, _, _, _) >> new ArrayList<Anropsbehorighet>()

        BestallningsData data = BestallningDataConstructor.createBestallningDataForAnropsbehorighetAndRelations()

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

    void "prepareVagval ska lägga till error om det finns några vagval med samma parametrar"() {
        setup:
        JsonBestallning jsonBestallning = BestallningConstructor.createBestallning()
        BestallningsData data = new BestallningsData(jsonBestallning)

        List<String> error = new LinkedList<>()
        error.add("error")
        validatingServiceMock.validateVagvalForDubblett(_) >> error

        when:
        constructorService.prepareVagval(jsonBestallning.inkludera.vagval.get(0), data,
                jsonBestallning.genomforandeTidpunkt,
                BestallningConstructor.generateTomDate(jsonBestallning.genomforandeTidpunkt))
        then:
        assertNull(data.getVagval(jsonBestallning.inkludera.vagval.get(0)))
        assertTrue(data.hasErrors())
    }

    void "prepareVagval ska skapa ny Vagval och lägga till den i data"() {
        setup:
        validatingServiceMock.validateVagvalForDubblett(_) >> new LinkedList<>()
        daoMock.getVagval(_, _, _, _) >> new ArrayList<Vagval>()

        BestallningsData data = BestallningDataConstructor.createBestallningDataForVagvalAndRelations()

        VagvalBestallning vvBestallning = data.bestallning.inkludera.vagval.get(0)
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

    void "prepareVagval för [old vagvals AnropsAdress == new vagvals AnropAdress && oldvagval.fromdate > genomforandeTidpunkt] ska ändra oldvagval.fromdate = genomforandeTidpunkt"() {
        setup:
        validatingServiceMock.validateVagvalForDubblett(_) >> new LinkedList<>()


        BestallningsData data = BestallningDataConstructor.createBestallningDataForVagvalAndRelations()
        VagvalBestallning vvBestallning = data.bestallning.inkludera.vagval.get(0)

        Date fromDate = BestallningConstructor.generateDateGreaterThan(data.bestallning.genomforandeTidpunkt)
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                data.getVagvalRelations(vvBestallning).logiskadress,
                data.getVagvalRelations(vvBestallning).tjanstekomponent,
                data.getVagvalRelations(vvBestallning).tjanstekontrakt,
                data.getVagvalRelations(vvBestallning).profil,
                BestallningConstructor.ADRESS,
                fromDate)

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
        assertEquals(vagvalFromDB.fromTidpunkt, data.bestallning.genomforandeTidpunkt)
        assertEquals(data.getVagval(vvBestallning).oldVagval.logiskAdress.hsaId, BestallningConstructor.LOGISK_ADRESS)
        assertEquals(data.getVagval(vvBestallning).oldVagval.tjanstekontrakt.namnrymd, BestallningConstructor.TJANSTEKONTRAKT)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.tjanstekomponent.hsaId, BestallningConstructor.TJANSTEKOMPONENT)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.rivTaProfil.namn, BestallningConstructor.RIVTA_PROFIL)
        assertEquals(data.getVagval(vvBestallning).oldVagval.anropsAdress.adress, BestallningConstructor.ADRESS)

    }

    void "prepareVagval för [old vagvals AnropsAdress == new vagvals AnropAdress && oldvagval.fromdate < genomforandeTidpunkt]"() {
        setup:
        validatingServiceMock.validateVagvalForDubblett(_) >> new LinkedList<>()
        BestallningsData data = BestallningDataConstructor.createBestallningDataForVagvalAndRelations()
        VagvalBestallning vvBestallning = data.bestallning.inkludera.vagval.get(0)


        Date fromDate = BestallningConstructor.generateDateLowerThan(data.bestallning.genomforandeTidpunkt)
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                data.getVagvalRelations(vvBestallning).logiskadress,
                data.getVagvalRelations(vvBestallning).tjanstekomponent,
                data.getVagvalRelations(vvBestallning).tjanstekontrakt,
                data.getVagvalRelations(vvBestallning).profil,
                BestallningConstructor.ADRESS,
                fromDate)


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

    void "prepareVagval för [old vagvals AnropsAdress != new vagvals AnropAdress && oldvagval.fromdate > genomforandeTidpunkt] ska skapa ny vagval och oldvagval.delete=true"() {
        setup:
        validatingServiceMock.validateVagvalForDubblett(_) >> new LinkedList<>()
        BestallningsData data = BestallningDataConstructor.createBestallningDataForVagvalAndRelations()

        VagvalBestallning vvBestallning = data.bestallning.inkludera.vagval.get(0)


        Date fromDate = BestallningConstructor.generateDateGreaterThan(data.bestallning.genomforandeTidpunkt)
        String annanAddress = "annan adress"
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                data.getVagvalRelations(vvBestallning).logiskadress,
                data.getVagvalRelations(vvBestallning).tjanstekomponent,
                data.getVagvalRelations(vvBestallning).tjanstekontrakt,
                data.getVagvalRelations(vvBestallning).profil,
                annanAddress,
                fromDate)


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

    void "prepareVagval för [old vagvals AnropsAdress != new vagvals AnropAdress && oldvagval.fromdate < genomforandeTidpunkt] ska skapa ny vagval och deaktivera oldvagval"() {
        setup:
        validatingServiceMock.validateVagvalForDubblett(_) >> new LinkedList<>()

        BestallningsData data = BestallningDataConstructor.createBestallningDataForVagvalAndRelations()
        VagvalBestallning vvBestallning = data.bestallning.inkludera.vagval.get(0)


        Date fromDate = BestallningConstructor.generateDateLowerThan(data.bestallning.genomforandeTidpunkt)
        String annanAddress = "annan adress"
        Vagval vagvalFromDB = ObjectsConstructor.createVagval(
                data.getVagvalRelations(vvBestallning).logiskadress,
                data.getVagvalRelations(vvBestallning).tjanstekomponent,
                data.getVagvalRelations(vvBestallning).tjanstekontrakt,
                data.getVagvalRelations(vvBestallning).profil,
                annanAddress,
                fromDate)


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
