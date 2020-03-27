package tak.web

import se.skltp.tak.core.entity.*

import java.sql.Date

/**
 * Created by mtuliakova on 2018-12-14.
 */
class ObjectsConstructor {

    static  Vagval createVagval(Date from){
        return createVagval(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL,
                from)
    }
    static Vagval createVagval(String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd, String url, String profil, Date fromDate) {
        LogiskAdress la = new LogiskAdress()
        la.hsaId = laHsaId

        Tjanstekomponent tjanstekomponent = new Tjanstekomponent()
        tjanstekomponent.hsaId = tKomponentHsaId

        Tjanstekontrakt tjanstekontrakt = new Tjanstekontrakt()
        tjanstekontrakt.namnrymd = tKontraktNamnrymd

        RivTaProfil rivTaProfil = new RivTaProfil()
        rivTaProfil.namn = profil

        AnropsAdress anropsAdress = new AnropsAdress()
        anropsAdress.tjanstekomponent = tjanstekomponent
        anropsAdress.rivTaProfil = rivTaProfil
        anropsAdress.adress = url

        Vagval vv = new Vagval()
        vv.setLogiskAdress(la)
        vv.setTjanstekontrakt(tjanstekontrakt)
        vv.setAnropsAdress(anropsAdress)

        vv.setFromTidpunkt(fromDate)
        vv.setTomTidpunkt(generateTomDate(fromDate))
        return vv
    }

    static Vagval createVagval(LogiskAdress la, Tjanstekomponent tjanstekomponent, Tjanstekontrakt tjanstekontrakt, RivTaProfil rivTaProfil, String url, Date fromDate) {
        AnropsAdress anropsAdress = new AnropsAdress()
        anropsAdress.tjanstekomponent = tjanstekomponent
        anropsAdress.rivTaProfil = rivTaProfil
        anropsAdress.adress = url

        Vagval vv = new Vagval()
        vv.setLogiskAdress(la)
        vv.setTjanstekontrakt(tjanstekontrakt)
        vv.setAnropsAdress(anropsAdress)

        vv.setFromTidpunkt(fromDate)
        vv.setTomTidpunkt(generateTomDate(fromDate))
        return vv
    }


    static  Anropsbehorighet createAnropsbehorighet(Date from){
        return createAnropsbehorighet(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                from)
    }

    static Anropsbehorighet createAnropsbehorighet(String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd, Date fromDate) {
        LogiskAdress la = createLogiskAdress(laHsaId)
        Tjanstekomponent tjanstekomponent = createTjanstekomponent(tKomponentHsaId)
        Tjanstekontrakt tjanstekontrakt = createTjanstekontrakt(tKontraktNamnrymd)

        Anropsbehorighet anropsbehorighet = new Anropsbehorighet()
        anropsbehorighet.logiskAdress = la
        anropsbehorighet.tjanstekontrakt = tjanstekontrakt
        anropsbehorighet.tjanstekonsument = tjanstekomponent

        anropsbehorighet.setFromTidpunkt(fromDate)
        anropsbehorighet.setTomTidpunkt(generateTomDate(fromDate))
        return anropsbehorighet
    }

    static LogiskAdress createLogiskAdress(String hsaId) {
        LogiskAdress la = new LogiskAdress()
        la.hsaId = hsaId
        la
    }

    static Tjanstekomponent createTjanstekomponent(String hsaId) {
        Tjanstekomponent tk = new Tjanstekomponent()
        tk.hsaId = hsaId
        tk
    }

    static Tjanstekontrakt createTjanstekontrakt(String namnrymd) {
        Tjanstekontrakt tk = new Tjanstekontrakt()
        tk.namnrymd = namnrymd
        tk
    }

    static RivTaProfil createRivTaProfil(String name) {
        RivTaProfil profil = new RivTaProfil()
        profil.namn = name
        profil
    }

    static AnropsAdress createAnropsAdress(String profil, String tkomponent, String url){
        RivTaProfil rivTaProfil = createRivTaProfil(profil)
        Tjanstekomponent tjanstekomponent = createTjanstekomponent(tkomponent)
        AnropsAdress anropsAdress = new AnropsAdress()
        anropsAdress.tjanstekomponent = tjanstekomponent
        anropsAdress.rivTaProfil = rivTaProfil
        anropsAdress.adress = url
        anropsAdress
    }

    public static Date generateTomDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, 100);
        Date d = new Date(c.getTime().getTime());
        return d;
    }

}
