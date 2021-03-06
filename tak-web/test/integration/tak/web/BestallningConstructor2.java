package tak.web;


import se.skltp.tak.web.jsonBestallning.*;

import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class BestallningConstructor2 {
    public static final String RIVTA_PROFIL = "RIVTABP21";
    public static final String LOGISK_ADRESS = "SE2321000131-F000000000264";
    public static final String ADRESS = "https://min-fina-url.se";
    public static final String TJANSTEKONTRAKT = "urn:riv:ehr:blocking:administration:GetPatientIdsResponder:2";
    public static final String TJANSTEKOMPONENT = "SE2321000016-8VT4";
    public static final String SOME_STRING = "Identificator";


    public static JsonBestallning createBestallning() {
        JsonBestallning jsonBestallning = createEmptyBestallning();
        fillBestallning(jsonBestallning);
        setDate(jsonBestallning, new Date(System.currentTimeMillis()));
        return jsonBestallning;
    }

    public static Date generateAfterDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return new Date(c.getTime().getTime());
    }

    public static Date generateBeforeDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, -100);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return new Date(c.getTime().getTime());
    }


    public static JsonBestallning createEmptyBestallning() {
        JsonBestallning jsonBestallning = new JsonBestallning();

        //inkludera
        BestallningsAvsnitt bestallningsAvsnitt = new BestallningsAvsnitt();
        jsonBestallning.setInkludera(bestallningsAvsnitt);

        List<TjanstekontraktBestallning> tjanstekontrakt = new LinkedList<TjanstekontraktBestallning>();
        List<LogiskadressBestallning> logiskadresser = new LinkedList<LogiskadressBestallning>();
        List<TjanstekomponentBestallning> tjanstekomponenter = new LinkedList<TjanstekomponentBestallning>();
        List<AnropsbehorighetBestallning> anropsbehorigheter = new LinkedList<AnropsbehorighetBestallning>();
        List<VagvalBestallning> vagval = new LinkedList<VagvalBestallning>();

        bestallningsAvsnitt.setAnropsbehorigheter(anropsbehorigheter);
        bestallningsAvsnitt.setLogiskadresser(logiskadresser);
        bestallningsAvsnitt.setTjanstekomponenter(tjanstekomponenter);
        bestallningsAvsnitt.setTjanstekontrakt(tjanstekontrakt);
        bestallningsAvsnitt.setVagval(vagval);


        //exkludera
        BestallningsAvsnitt exkluderaBestallningsAvsnitt = new BestallningsAvsnitt();
        jsonBestallning.setExkludera(exkluderaBestallningsAvsnitt);

        List<AnropsbehorighetBestallning> anropsbehorigheter2 = new LinkedList<AnropsbehorighetBestallning>();
        List<VagvalBestallning> vagval2 = new LinkedList<VagvalBestallning>();

        exkluderaBestallningsAvsnitt.setAnropsbehorigheter(anropsbehorigheter2);
        exkluderaBestallningsAvsnitt.setVagval(vagval2);

        return jsonBestallning;
    }

    public static void setDate(JsonBestallning jsonBestallning, Date genomforandeTidpunkt) {
        jsonBestallning.setGenomforandeTidpunkt(genomforandeTidpunkt);
    }

    private static void fillBestallning(JsonBestallning jsonBestallning) {
        addLogiskAddress(jsonBestallning, LOGISK_ADRESS);
        addTjanstekomponent(jsonBestallning, TJANSTEKOMPONENT);
        addTjanstekontrakt(jsonBestallning, TJANSTEKONTRAKT);

        addAnropsbehorighet(jsonBestallning, LOGISK_ADRESS, TJANSTEKOMPONENT, TJANSTEKONTRAKT);
        addVagval(jsonBestallning, LOGISK_ADRESS, TJANSTEKOMPONENT, TJANSTEKONTRAKT, ADRESS, RIVTA_PROFIL);

        addAnropsbehorighetForDelete(jsonBestallning, LOGISK_ADRESS, TJANSTEKOMPONENT, TJANSTEKONTRAKT);
        addVagvalForDelete(jsonBestallning, LOGISK_ADRESS, TJANSTEKOMPONENT, TJANSTEKONTRAKT, ADRESS, RIVTA_PROFIL);
    }


    public static void addLogiskAddress(JsonBestallning jsonBestallning, String logiskAddressHsaId) {
        LogiskadressBestallning logiskadressBestallning = new LogiskadressBestallning();
        logiskadressBestallning.setHsaId(logiskAddressHsaId);
        jsonBestallning.getInkludera().getLogiskadresser().add(logiskadressBestallning);
    }

    public static void addTjanstekomponent(JsonBestallning jsonBestallning, String tjanstekomponentHsaId) {
        TjanstekomponentBestallning tjanstekomponentBestallning = new TjanstekomponentBestallning();
        tjanstekomponentBestallning.setHsaId(tjanstekomponentHsaId);
        jsonBestallning.getInkludera().getTjanstekomponenter().add(tjanstekomponentBestallning);
    }

    public static void addTjanstekontrakt(JsonBestallning jsonBestallning, String namnrymd) {
        TjanstekontraktBestallning tjanstekontraktBestallning = new TjanstekontraktBestallning();
        tjanstekontraktBestallning.setNamnrymd(namnrymd);
        jsonBestallning.getInkludera().getTjanstekontrakt().add(tjanstekontraktBestallning);

    }

    public static void addAnropsbehorighet(JsonBestallning jsonBestallning, String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd) {
        AnropsbehorighetBestallning anropsbehorighetBestallning = new AnropsbehorighetBestallning();
        anropsbehorighetBestallning.setLogiskAdress(laHsaId);
        anropsbehorighetBestallning.setTjanstekonsument(tKomponentHsaId);
        anropsbehorighetBestallning.setTjanstekontrakt(tKontraktNamnrymd);
        jsonBestallning.getInkludera().getAnropsbehorigheter().add(anropsbehorighetBestallning);
    }

    public static void addVagval(JsonBestallning jsonBestallning, String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd, String url, String rivTaProfil) {
        VagvalBestallning vagvalBestallning = new VagvalBestallning();
        vagvalBestallning.setRivtaprofil(rivTaProfil);
        vagvalBestallning.setAdress(url);
        vagvalBestallning.setLogiskadress(laHsaId);
        vagvalBestallning.setTjanstekomponent(tKomponentHsaId);
        vagvalBestallning.setTjanstekontrakt(tKontraktNamnrymd);

        jsonBestallning.getInkludera().getVagval().add(vagvalBestallning);
    }

    public static void addAnropsbehorighetForDelete(JsonBestallning jsonBestallning, String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd) {
        AnropsbehorighetBestallning anropsbehorighetBestallning = new AnropsbehorighetBestallning();
        anropsbehorighetBestallning.setLogiskAdress(laHsaId);
        anropsbehorighetBestallning.setTjanstekonsument(tKomponentHsaId);
        anropsbehorighetBestallning.setTjanstekontrakt(tKontraktNamnrymd);
        jsonBestallning.getExkludera().getAnropsbehorigheter().add(anropsbehorighetBestallning);
    }

    public static void addVagvalForDelete(JsonBestallning jsonBestallning, String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd, String url, String rivTaProfil) {
        VagvalBestallning vagvalBestallning = new VagvalBestallning();
        vagvalBestallning.setRivtaprofil(rivTaProfil);
        vagvalBestallning.setAdress(url);
        vagvalBestallning.setLogiskadress(laHsaId);
        vagvalBestallning.setTjanstekomponent(tKomponentHsaId);
        vagvalBestallning.setTjanstekontrakt(tKontraktNamnrymd);

        jsonBestallning.getExkludera().getVagval().add(vagvalBestallning);
    }


}
