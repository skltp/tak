package tak.web

import grails.plugin.spock.IntegrationSpec
import tak.web.jsonBestallning.DAOService
import org.apache.commons.io.FileUtils
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.junit.Before
import se.skltp.tak.core.entity.AnropsAdress
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.Vagval
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import java.sql.Date

class BestallningServiceIntegrationSpec extends IntegrationSpec {
/*
    def bestallningService
    DAOService daoService

    @Before
    void before() {
        def subject = [getPrincipal   : { "iamauser" },
                       isAuthenticated: { true }
        ] as Subject

        ThreadContext.put(ThreadContext.SECURITY_MANAGER_KEY,
                [getSubject: { subject }] as SecurityManager)

        SecurityUtils.metaClass.static.getSubject = { subject }
    }

    void "create JsonBestallning from json"() {
        when:
        String bestallning = FileUtils.readFileToString(new File("test/unit/resources/besallning.json"))
        bestallningService.createOrderObject(bestallning)
        then:
        noExceptionThrown()
    }

    void "test validate logiskaddress with 'ogiltiga tecken' "() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        String hsaId = "hsaID"
        BestallningConstructor2.addLogiskAddress(bestallning, hsaId)

        bestallningService.prepareOrder(bestallning);

        then:
        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("ogiltiga tecken")
        bestallning.getInkludera().getLogiskadresser().get(0).logiskAdress == null
    }

    void "test validate new logiskAdress"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        String hsaId = "HSAID"
        BestallningConstructor2.addLogiskAddress(bestallning, hsaId)

        bestallningService.prepareOrder(bestallning);

        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getLogiskadresser().get(0).logiskAdress != null
        bestallning.getInkludera().getLogiskadresser().get(0).logiskAdress.hsaId == hsaId
    }

    void "test validate tjanstekomponent with 'ogiltiga tecken' "() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        String hsaId = "hsaID"
        BestallningConstructor2.addTjanstekomponent(bestallning, hsaId)

        bestallningService.prepareOrder(bestallning);

        then:
        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("ogiltiga tecken")
        bestallning.getInkludera().getTjanstekomponenter().get(0).tjanstekomponent == null
    }

    void "test validate new tjanstekomponent"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        String hsaId = "HSAID"
        BestallningConstructor2.addTjanstekomponent(bestallning, hsaId)

        bestallningService.prepareOrder(bestallning);

        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getTjanstekomponenter().get(0).tjanstekomponent != null
        bestallning.getInkludera().getTjanstekomponenter().get(0).tjanstekomponent.hsaId == hsaId
    }

    void "test validate tjanstekontrakt with 'ogiltiga tecken' "() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        String namnrymd = "namnrymd*"
        BestallningConstructor2.addTjanstekontrakt(bestallning, namnrymd)

        bestallningService.prepareOrder(bestallning);

        then:
        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("ogiltiga tecken")
        bestallning.getInkludera().getTjanstekontrakt().get(0).tjanstekontrakt == null
    }

    void "test validate new tjanstekontrakt"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        String namnrymd = "urn:riv:ehr:blocking:administration:GetPatientIdsResponder:2"
        BestallningConstructor2.addTjanstekontrakt(bestallning, namnrymd)

        bestallningService.prepareOrder(bestallning);

        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getTjanstekontrakt().get(0).tjanstekontrakt != null
        bestallning.getInkludera().getTjanstekontrakt().get(0).namnrymd == namnrymd
    }

    void "test validate new anropsbehorighet"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)
        BestallningConstructor2.addAnropsbehorighet(bestallning, BestallningConstructor2.LOGISK_ADRESS, BestallningConstructor2.TJANSTEKOMPONENT, BestallningConstructor2.TJANSTEKONTRAKT)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getLogiskadresser().get(0).getLogiskAdress() != null
        bestallning.getInkludera().getTjanstekomponenter().get(0).getTjanstekomponent() != null
        bestallning.getInkludera().getTjanstekontrakt().get(0).getTjanstekontrakt() != null
        bestallning.getInkludera().getAnropsbehorigheter().get(0).getNewAnropsbehorighet() != null
    }

    void "test validate new anropsbehorighet without connected objects"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();

        BestallningConstructor2.addAnropsbehorighet(bestallning, null, null, BestallningConstructor2.TJANSTEKONTRAKT)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("Det saknas information i Json-filen")
        bestallning.getInkludera().getAnropsbehorigheter().get(0).newAnropsbehorighet == null

    }

    void "test validate new anropsbehorighet with nonexistent logiskAddress"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)

        BestallningConstructor2.addAnropsbehorighet(bestallning, "NONEXISTENT", BestallningConstructor2.TJANSTEKOMPONENT, BestallningConstructor2.TJANSTEKONTRAKT)

        bestallningService.prepareOrder(bestallning);
        then:

        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("Skapa Anropsbehorighet: LogiskAdress:en med HSAId = NONEXISTENT finns inte")
        bestallning.getInkludera().getAnropsbehorigheter().get(0).newAnropsbehorighet == null
    }

    void "test validate new anropsbehorighet with nonexistent tjanstekomponent"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)

        BestallningConstructor2.addAnropsbehorighet(bestallning, BestallningConstructor2.LOGISK_ADRESS, "NONEXISTENT", BestallningConstructor2.TJANSTEKONTRAKT)

        bestallningService.prepareOrder(bestallning);
        then:

        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("Skapa Anropsbehorighet: Tjanstekomponent:en med HSAId = NONEXISTENT finns inte")
        bestallning.getInkludera().getAnropsbehorigheter().get(0).newAnropsbehorighet == null
    }

    void "test validate new anropsbehorighet with nonexistent tjanstekontrakt"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)

        BestallningConstructor2.addAnropsbehorighet(bestallning, BestallningConstructor2.LOGISK_ADRESS, BestallningConstructor2.TJANSTEKOMPONENT, "NONEXISTENT")

        bestallningService.prepareOrder(bestallning);
        then:

        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("Skapa Anropsbehorighet: Tjanstekontrakt:et med namnrymd = NONEXISTENT finns inte")
        bestallning.getInkludera().getAnropsbehorigheter().get(0).newAnropsbehorighet == null
    }

    void "test validate new anropsbehorighet with overlap"() {
        when:
        Anropsbehorighet existentAnropsbehorighet = Anropsbehorighet.get(9)
        String logiskAddressFromDB = existentAnropsbehorighet.logiskAdress.hsaId
        String tjanstekomponentFromDB = existentAnropsbehorighet.tjanstekonsument.hsaId
        String tjanstekontraktFromDB = existentAnropsbehorighet.tjanstekontrakt.namnrymd

        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addAnropsbehorighet(bestallning, logiskAddressFromDB, tjanstekomponentFromDB, tjanstekontraktFromDB)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getAnropsbehorigheter().get(0).getNewAnropsbehorighet().getId() == 0
        bestallning.getInkludera().getAnropsbehorigheter().get(0).getOldAnropsbehorighet().get(0).getId() == 9
    }

    void "test validate new anropsbehorighet without overlap(before)"() {
        when:
        Anropsbehorighet existentAnropsbehorighet = Anropsbehorighet.get(9)
        String logiskAddressFromDB = existentAnropsbehorighet.logiskAdress.hsaId
        String tjanstekomponentFromDB = existentAnropsbehorighet.tjanstekonsument.hsaId
        String tjanstekontraktFromDB = existentAnropsbehorighet.tjanstekontrakt.namnrymd

        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, BestallningConstructor2.generateBeforeDate(existentAnropsbehorighet.getFromTidpunkt()))

        BestallningConstructor2.addAnropsbehorighet(bestallning, logiskAddressFromDB, tjanstekomponentFromDB, tjanstekontraktFromDB)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getAnropsbehorigheter().get(0).newAnropsbehorighet != null
    }

    void "test validate new anropsbehorighet without overlap(after)"() {
        when:
        Anropsbehorighet existentAnropsbehorighet = Anropsbehorighet.get(9)
        String logiskAddressFromDB = existentAnropsbehorighet.logiskAdress.hsaId
        String tjanstekomponentFromDB = existentAnropsbehorighet.tjanstekonsument.hsaId
        String tjanstekontraktFromDB = existentAnropsbehorighet.tjanstekontrakt.namnrymd

        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, BestallningConstructor2.generateAfterDate(existentAnropsbehorighet.getTomTidpunkt()))
        BestallningConstructor2.addAnropsbehorighet(bestallning, logiskAddressFromDB, tjanstekomponentFromDB, tjanstekontraktFromDB)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getAnropsbehorigheter().get(0).newAnropsbehorighet != null
    }

    void "test validate new vagval"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)
        BestallningConstructor2.addVagval(bestallning, BestallningConstructor2.LOGISK_ADRESS, BestallningConstructor2.TJANSTEKOMPONENT, BestallningConstructor2.TJANSTEKONTRAKT, BestallningConstructor2.ADRESS, BestallningConstructor2.RIVTA_PROFIL)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getLogiskadresser().get(0).getLogiskAdress() != null
        bestallning.getInkludera().getTjanstekomponenter().get(0).getTjanstekomponent() != null
        bestallning.getInkludera().getTjanstekontrakt().get(0).getTjanstekontrakt() != null
        bestallning.getInkludera().getVagval().get(0).getNewVagval() != null
        bestallning.getInkludera().getVagval().get(0).getNewVagval().getAnropsAdress() != null
        bestallning.getInkludera().getVagval().get(0).getNewVagval().getAnropsAdress().id == 0
    }

    void "test validate new vagval with exist anropAdress"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)

        AnropsAdress anropsAdress = AnropsAdress.get(2)
        BestallningConstructor2.addVagval(bestallning, BestallningConstructor2.LOGISK_ADRESS, anropsAdress.tjanstekomponent.hsaId, BestallningConstructor2.TJANSTEKONTRAKT, anropsAdress.adress, anropsAdress.rivTaProfil.namn)


        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getVagval().get(0).getNewVagval().getAnropsAdress().id == 2
    }

    void "test validate new vagval with error in anropAdress"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)


        BestallningConstructor2.addVagval(bestallning, BestallningConstructor2.LOGISK_ADRESS, BestallningConstructor2.TJANSTEKOMPONENT, BestallningConstructor2.TJANSTEKONTRAKT, "FEL**,", BestallningConstructor2.RIVTA_PROFIL)


        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().get(0).contains("ogiltiga tecken")
        bestallning.getInkludera().getVagval().get(0).getNewVagval() == null
    }

    void "test validate new vagval without connected objects"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();

        BestallningConstructor2.addVagval(bestallning, null, null, BestallningConstructor2.TJANSTEKONTRAKT, BestallningConstructor2.ADRESS, BestallningConstructor2.RIVTA_PROFIL)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("Det saknas information i Json-filen")
        bestallning.getInkludera().getVagval().get(0).getNewVagval() == null

    }

    void "test validate new vagval with nonexistent logiskAddress"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)

        BestallningConstructor2.addVagval(bestallning, "NONEXISTENT", BestallningConstructor2.TJANSTEKOMPONENT, BestallningConstructor2.TJANSTEKONTRAKT, BestallningConstructor2.ADRESS, BestallningConstructor2.RIVTA_PROFIL)

        bestallningService.prepareOrder(bestallning);
        then:

        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("Skapa Vagval: LogiskAdress:en med HSAId = NONEXISTENT finns inte")
        bestallning.getInkludera().getVagval().get(0).getNewVagval() == null
    }

    void "test validate new vagval with nonexistent tjanstekomponent"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)

        BestallningConstructor2.addVagval(bestallning, BestallningConstructor2.LOGISK_ADRESS, "NONEXISTENT", BestallningConstructor2.TJANSTEKONTRAKT, BestallningConstructor2.ADRESS, BestallningConstructor2.RIVTA_PROFIL)

        bestallningService.prepareOrder(bestallning);
        then:

        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("Skapa Vagval: Tjanstekomponent:en med HSAId = NONEXISTENT finns inte")
        bestallning.getInkludera().getVagval().get(0).getNewVagval() == null
    }

    void "test validate new vagval with nonexistent tjanstekontrakt"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)

        BestallningConstructor2.addVagval(bestallning, BestallningConstructor2.LOGISK_ADRESS, BestallningConstructor2.TJANSTEKOMPONENT, "NONEXISTENT", BestallningConstructor2.ADRESS, BestallningConstructor2.RIVTA_PROFIL)

        bestallningService.prepareOrder(bestallning);
        then:

        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("Skapa Vagval: Tjanstekontrakt:et med namnrymd = NONEXISTENT finns inte")
        bestallning.getInkludera().getVagval().get(0).getNewVagval() == null
    }

    void "test validate new vagval with nonexistent rivta-profil"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)

        BestallningConstructor2.addVagval(bestallning, BestallningConstructor2.LOGISK_ADRESS, BestallningConstructor2.TJANSTEKOMPONENT, BestallningConstructor2.TJANSTEKONTRAKT, BestallningConstructor2.ADRESS, "NONEXISTENT")

        bestallningService.prepareOrder(bestallning);
        then:

        bestallning.getBestallningErrors().size() == 1
        bestallning.getBestallningErrors().get(0).contains("Skapa Vagval: RivTaProfil:en med namn = NONEXISTENT finns inte.")
        bestallning.getInkludera().getVagval().get(0).getNewVagval() == null
    }

    void "test validate new vagval with overlap"() {
        when:
        Vagval existentVagval = Vagval.get(10)
        String logiskAddressFromDB = existentVagval.logiskAdress.hsaId
        String tjanstekomponentFromDB = existentVagval.anropsAdress.tjanstekomponent.hsaId
        String tjanstekontraktFromDB = existentVagval.tjanstekontrakt.namnrymd
        String url = existentVagval.anropsAdress.adress
        String rivTaProfil = existentVagval.anropsAdress.rivTaProfil

        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, new Date(System.currentTimeMillis()))
        BestallningConstructor2.addVagval(bestallning, logiskAddressFromDB, tjanstekomponentFromDB, tjanstekontraktFromDB, url, rivTaProfil)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getVagval().get(0).getNewVagval().getId() == 0
        bestallning.getInkludera().getVagval().get(0).getOldVagval().get(0).getId() == 10
    }

    void "test validate new vagval  without overlap(before)"() {
        when:
        Vagval existentVagval = Vagval.get(10)
        String logiskAddressFromDB = existentVagval.logiskAdress.hsaId
        String tjanstekomponentFromDB = existentVagval.anropsAdress.tjanstekomponent.hsaId
        String tjanstekontraktFromDB = existentVagval.tjanstekontrakt.namnrymd
        String url = existentVagval.anropsAdress.adress
        String rivTaProfil = existentVagval.anropsAdress.rivTaProfil


        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, BestallningConstructor2.generateBeforeDate(existentVagval.getFromTidpunkt()))
        BestallningConstructor2.addVagval(bestallning, logiskAddressFromDB, tjanstekomponentFromDB, tjanstekontraktFromDB, url, rivTaProfil)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getVagval().get(0).getNewVagval() != null
        bestallning.getInkludera().getVagval().get(0).getOldVagval().size() == 0
    }

    void "test validate new vagval without overlap(after)"() {
        when:
        Vagval existentVagval = Vagval.get(10)
        String logiskAddressFromDB = existentVagval.logiskAdress.hsaId
        String tjanstekomponentFromDB = existentVagval.anropsAdress.tjanstekomponent.hsaId
        String tjanstekontraktFromDB = existentVagval.tjanstekontrakt.namnrymd
        String url = existentVagval.anropsAdress.adress
        String rivTaProfil = existentVagval.anropsAdress.rivTaProfil


        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, BestallningConstructor2.generateAfterDate(existentVagval.getTomTidpunkt()))
        BestallningConstructor2.addVagval(bestallning, logiskAddressFromDB, tjanstekomponentFromDB, tjanstekontraktFromDB, url, rivTaProfil)

        bestallningService.prepareOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        bestallning.getInkludera().getVagval().get(0).getNewVagval() != null
        bestallning.getInkludera().getVagval().get(0).getOldVagval().size() == 0
    }

    void "test create vagval"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        Date now = new Date(System.currentTimeMillis())
        BestallningConstructor2.setDate(bestallning, now)
        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)
        BestallningConstructor2.addVagval(bestallning, BestallningConstructor2.LOGISK_ADRESS, BestallningConstructor2.TJANSTEKOMPONENT, BestallningConstructor2.TJANSTEKONTRAKT, BestallningConstructor2.ADRESS, BestallningConstructor2.RIVTA_PROFIL)

        bestallningService.prepareOrder(bestallning);
        bestallningService.executeOrder(bestallning);
        then:
        bestallning.getInkludera().getVagval().get(0).getNewVagval().getId() != 0
        bestallning.getInkludera().getVagval().get(0).getOldVagval().size() == 0

        Vagval result = Vagval.get(bestallning.getInkludera().getVagval().get(0).getNewVagval().getId())

        result.logiskAdress.hsaId == BestallningConstructor2.LOGISK_ADRESS
        result.tjanstekontrakt.namnrymd == BestallningConstructor2.TJANSTEKONTRAKT
        result.anropsAdress.tjanstekomponent.hsaId == BestallningConstructor2.TJANSTEKOMPONENT
        result.anropsAdress.rivTaProfil.namn == BestallningConstructor2.RIVTA_PROFIL
        result.anropsAdress.adress == BestallningConstructor2.ADRESS
    }

    void "test create new vagval with overlap"() {
        when:
        Vagval existentVagval = Vagval.get(10)
        String logiskAddressFromDB = existentVagval.logiskAdress.hsaId
        String tjanstekomponentFromDB = existentVagval.anropsAdress.tjanstekomponent.hsaId
        String tjanstekontraktFromDB = existentVagval.tjanstekontrakt.namnrymd
        String url = existentVagval.anropsAdress.adress
        String rivTaProfil = existentVagval.anropsAdress.rivTaProfil
        Date genomforandeTidpunkt = new Date(System.currentTimeMillis())


        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        BestallningConstructor2.setDate(bestallning, genomforandeTidpunkt)
        BestallningConstructor2.addVagval(bestallning, logiskAddressFromDB, tjanstekomponentFromDB, tjanstekontraktFromDB, url, rivTaProfil)

        bestallningService.prepareOrder(bestallning);
        bestallningService.executeOrder(bestallning);
        then:
        bestallning.getInkludera().getVagval().get(0).getNewVagval().getId() != 0
        Vagval result = Vagval.get(bestallning.getInkludera().getVagval().get(0).getNewVagval().getId())

        result.logiskAdress.hsaId == logiskAddressFromDB
        result.tjanstekontrakt.namnrymd == tjanstekontraktFromDB
        result.anropsAdress.tjanstekomponent.hsaId == tjanstekomponentFromDB
        result.anropsAdress.rivTaProfil.namn == rivTaProfil
        result.anropsAdress.adress == existentVagval.anropsAdress.adress

        bestallning.getInkludera().getVagval().get(0).getOldVagval().size() != 0
        Vagval oldVagval = Vagval.get(bestallning.getInkludera().getVagval().get(0).getOldVagval().get(0).getId())
        oldVagval.tomTidpunkt < genomforandeTidpunkt
    }

    void "test create anropsbehorighet"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        Date now = new Date(System.currentTimeMillis())
        BestallningConstructor2.setDate(bestallning, now)

        BestallningConstructor2.addLogiskAddress(bestallning, BestallningConstructor2.LOGISK_ADRESS)
        BestallningConstructor2.addTjanstekomponent(bestallning, BestallningConstructor2.TJANSTEKOMPONENT)
        BestallningConstructor2.addTjanstekontrakt(bestallning, BestallningConstructor2.TJANSTEKONTRAKT)

        BestallningConstructor2.addAnropsbehorighet(bestallning, BestallningConstructor2.LOGISK_ADRESS, BestallningConstructor2.TJANSTEKOMPONENT, BestallningConstructor2.TJANSTEKONTRAKT)

        bestallningService.prepareOrder(bestallning);
        bestallningService.executeOrder(bestallning);
        then:
        bestallning.getInkludera().getAnropsbehorigheter().get(0).getNewAnropsbehorighet().getId() != 0
        Anropsbehorighet result = Anropsbehorighet.get(bestallning.getInkludera().getAnropsbehorigheter().get(0).getNewAnropsbehorighet().getId())

        result.logiskAdress.hsaId == BestallningConstructor2.LOGISK_ADRESS
        result.tjanstekontrakt.namnrymd == BestallningConstructor2.TJANSTEKONTRAKT
        result.tjanstekonsument.hsaId == BestallningConstructor2.TJANSTEKOMPONENT

        bestallning.getInkludera().getAnropsbehorigheter().get(0).getOldAnropsbehorighet().size() == 0
    }

    void "test create new anropsbehorighet with overlap"() {
        when:
        Anropsbehorighet existentAnropsbehorighet = Anropsbehorighet.get(2)
        String logiskAddressFromDB = existentAnropsbehorighet.logiskAdress.hsaId
        String tjanstekomponentFromDB = existentAnropsbehorighet.tjanstekonsument.hsaId
        String tjanstekontraktFromDB = existentAnropsbehorighet.tjanstekontrakt.namnrymd


        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        Date now = new Date(System.currentTimeMillis())
        BestallningConstructor2.setDate(bestallning, now)
        BestallningConstructor2.addAnropsbehorighet(bestallning, logiskAddressFromDB, tjanstekomponentFromDB, tjanstekontraktFromDB)


        bestallningService.prepareOrder(bestallning);
        bestallningService.executeOrder(bestallning);
        then:
        bestallning.getInkludera().getAnropsbehorigheter().get(0).getNewAnropsbehorighet().getId() != 0
        Anropsbehorighet result = Anropsbehorighet.get(bestallning.getInkludera().getAnropsbehorigheter().get(0).getNewAnropsbehorighet().getId())

        result.logiskAdress.hsaId == logiskAddressFromDB
        result.tjanstekontrakt.namnrymd == tjanstekontraktFromDB
        result.tjanstekonsument.hsaId == tjanstekomponentFromDB

        bestallning.getInkludera().getAnropsbehorigheter().get(0).getOldAnropsbehorighet().size() != 0
        Anropsbehorighet oldAnropsbehorighet = Anropsbehorighet.get(bestallning.getInkludera().getAnropsbehorigheter().get(0).getOldAnropsbehorighet().get(0).getId())
        oldAnropsbehorighet.tomTidpunkt < now
    }

    void "test delete anropsbehorighet"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        Date now = new Date(System.currentTimeMillis())
        BestallningConstructor2.setDate(bestallning, now)
        Anropsbehorighet anropsbehorighet = Anropsbehorighet.get(1)
        BestallningConstructor2.addAnropsbehorighetForDelete(bestallning, anropsbehorighet.getLogiskAdress().getHsaId(), anropsbehorighet.getTjanstekonsument().getHsaId(), anropsbehorighet.getTjanstekontrakt().getNamnrymd())

        bestallningService.prepareOrder(bestallning);
        bestallningService.executeOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        Anropsbehorighet.get(1).getDeleted() == true
    }

    void "test delete vagval"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        Date now = new Date(System.currentTimeMillis())
        BestallningConstructor2.setDate(bestallning, now)
        Vagval vagval = Vagval.get(4)
        BestallningConstructor2.addVagvalForDelete(bestallning, vagval.getLogiskAdress().getHsaId(), vagval.getAnropsAdress().getTjanstekomponent().hsaId, vagval.getTjanstekontrakt().getNamnrymd(), vagval.getAnropsAdress().getAdress(), vagval.getAnropsAdress().getRivTaProfil().getNamn())
        bestallningService.prepareOrder(bestallning);
        bestallningService.executeOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        Vagval.get(4).getDeleted() == true
        Vagval.get(4).getAnropsAdress().getDeleted() == true
    }

    void "test delete vagval (old)"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        Date now = new Date(System.currentTimeMillis())
        BestallningConstructor2.setDate(bestallning, now)
        Vagval vagval = Vagval.get(11)
        BestallningConstructor2.addVagvalForDelete(bestallning, vagval.getLogiskAdress().getHsaId(), vagval.getAnropsAdress().getTjanstekomponent().hsaId, vagval.getTjanstekontrakt().getNamnrymd(), vagval.getAnropsAdress().getAdress(), vagval.getAnropsAdress().getRivTaProfil().getNamn())
        bestallningService.prepareOrder(bestallning);
        bestallningService.executeOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        Vagval.get(11).getDeleted() == false
        Vagval.get(11).getAnropsAdress().getDeleted() == false
    }

    void "test delete vagval with multiple links to anropAdress"() {
        when:
        JsonBestallning bestallning = BestallningConstructor2.createEmptyBestallning();
        Date now = new Date(System.currentTimeMillis())
        BestallningConstructor2.setDate(bestallning, now)
        Vagval vagval = Vagval.get(1)
        BestallningConstructor2.addVagvalForDelete(bestallning, vagval.getLogiskAdress().getHsaId(), vagval.getAnropsAdress().getTjanstekomponent().hsaId, vagval.getTjanstekontrakt().getNamnrymd(), vagval.getAnropsAdress().getAdress(), vagval.getAnropsAdress().getRivTaProfil().getNamn())
        bestallningService.prepareOrder(bestallning);
        bestallningService.executeOrder(bestallning);
        then:
        bestallning.getBestallningErrors().size() == 0
        Vagval.get(1).getDeleted() == true
        Vagval.get(1).getAnropsAdress().getDeleted() == false
    }*/
}