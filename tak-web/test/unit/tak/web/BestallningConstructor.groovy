package tak.web

import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*

import java.sql.Date

public class BestallningConstructor {
    public static final String RIVTA_PROFIL = "RIVTABP21";
    public static final String LOGISK_ADRESS = "SE2321000131-F000000000264";
    public static final String ADRESS = "https://min-fina-url.se";
    public static final String TJANSTEKONTRAKT = "urn:riv:ehr:blocking:administration:GetPatientIdsResponder:2";
    public static final String TJANSTEKOMPONENT = "SE2321000016-8VT4";
    public static final String SOME_STRING = "Identificator";

    public static final String RIVTA_PROFIL2 = "RIVTABP22";
    public static final String LOGISK_ADRESS2 = "SE2321000131-F000000000265";
    public static final String ADRESS2 = "https://min-fula-url.se";
    public static final String TJANSTEKONTRAKT2 = "urn:riv:ehr:blocking:administration:GetPatientIdsResponder:4";
    public static final String TJANSTEKOMPONENT2 = "SE2321000016-8VT2";
    public static final String SOME_STRING2 = "Identificator2";


    public static JsonBestallning createBestallning() {
        JsonBestallning jsonBestallning = createEmptyBestallning();
        fillBestallning(jsonBestallning);
        return jsonBestallning;
    }


    public static JsonBestallning createEmptyBestallning() {
        JsonBestallning jsonBestallning = new JsonBestallning();
        setDate(jsonBestallning, generateDateGreaterThan(new Date(System.currentTimeMillis())));

        //inkludera
        BestallningsAvsnitt kollektivData = new BestallningsAvsnitt();
        jsonBestallning.setInkludera(kollektivData);

        List<TjanstekontraktBestallning> tjanstekontrakt = new LinkedList<TjanstekontraktBestallning>();
        List<LogiskadressBestallning> logiskadresser = new LinkedList<LogiskadressBestallning>();
        List<TjanstekomponentBestallning> tjanstekomponenter = new LinkedList<TjanstekomponentBestallning>();
        List<AnropsbehorighetBestallning> anropsbehorigheter = new LinkedList<AnropsbehorighetBestallning>();
        List<VagvalBestallning> vagval = new LinkedList<VagvalBestallning>();

        kollektivData.setAnropsbehorigheter(anropsbehorigheter);
        kollektivData.setLogiskadresser(logiskadresser);
        kollektivData.setTjanstekomponenter(tjanstekomponenter);
        kollektivData.setTjanstekontrakt(tjanstekontrakt);
        kollektivData.setVagval(vagval);

        //exkludera
        BestallningsAvsnitt exkluderaKollektivData = new BestallningsAvsnitt();
        jsonBestallning.setExkludera(exkluderaKollektivData);

        List<AnropsbehorighetBestallning> anropsbehorigheter2 = new LinkedList<AnropsbehorighetBestallning>();
        List<VagvalBestallning> vagval2 = new LinkedList<VagvalBestallning>();

        exkluderaKollektivData.setAnropsbehorigheter(anropsbehorigheter2);
        exkluderaKollektivData.setVagval(vagval2);

        return jsonBestallning;
    }

    public static void setDate(JsonBestallning jsonBestallning, java.util.Date genomforandeTidpunkt) {
        jsonBestallning.setGenomforandeTidpunkt(new Date(genomforandeTidpunkt.getTime()));
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

    public
    static void addAnropsbehorighet(JsonBestallning jsonBestallning, String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd) {
        jsonBestallning.getInkludera().getAnropsbehorigheter().add(createAnropsbehorighetBestallning(laHsaId, tKomponentHsaId, tKontraktNamnrymd));
    }

    public
    static void addVagval(JsonBestallning jsonBestallning, String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd, String url, String rivTaProfil) {
        VagvalBestallning vagvalBestallning = createVagvalBestallning(laHsaId, tKomponentHsaId, tKontraktNamnrymd, url, rivTaProfil)
        jsonBestallning.getInkludera().getVagval().add(vagvalBestallning)
    }

    static VagvalBestallning createVagvalBestallning(String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd, String url, String rivTaProfil) {
        VagvalBestallning vagvalBestallning = new VagvalBestallning()
        vagvalBestallning.setRivtaprofil(rivTaProfil)
        vagvalBestallning.setAdress(url)
        vagvalBestallning.setLogiskadress(laHsaId)
        vagvalBestallning.setTjanstekomponent(tKomponentHsaId)
        vagvalBestallning.setTjanstekontrakt(tKontraktNamnrymd)
        vagvalBestallning
    }

    public
    static void addAnropsbehorighetForDelete(JsonBestallning jsonBestallning, String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd) {
        AnropsbehorighetBestallning anropsbehorighetBestallning = createAnropsbehorighetBestallning(laHsaId, tKomponentHsaId, tKontraktNamnrymd)
        jsonBestallning.getExkludera().getAnropsbehorigheter().add(anropsbehorighetBestallning);
    }

    public
    static AnropsbehorighetBestallning createAnropsbehorighetBestallning(String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd) {
        AnropsbehorighetBestallning anropsbehorighetBestallning = new AnropsbehorighetBestallning();
        anropsbehorighetBestallning.setLogiskAdress(laHsaId);
        anropsbehorighetBestallning.setTjanstekonsument(tKomponentHsaId);
        anropsbehorighetBestallning.setTjanstekontrakt(tKontraktNamnrymd);
        anropsbehorighetBestallning
    }


    public
    static void addVagvalForDelete(JsonBestallning jsonBestallning, String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd, String url, String rivTaProfil) {
        VagvalBestallning vagvalBestallning = new VagvalBestallning();
        vagvalBestallning.setRivtaprofil(rivTaProfil);
        vagvalBestallning.setAdress(url);
        vagvalBestallning.setLogiskadress(laHsaId);
        vagvalBestallning.setTjanstekomponent(tKomponentHsaId);
        vagvalBestallning.setTjanstekontrakt(tKontraktNamnrymd);

        jsonBestallning.getExkludera().getVagval().add(vagvalBestallning);
    }


    public static Date generateDateGreaterThan(Date genamforandeDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(genamforandeDate);
        c.add(Calendar.YEAR, 1);
        return new Date(c.getTime().getTime());
    }

    public static Date generateDateLowerThan(Date genamforandeDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(genamforandeDate);
        c.add(Calendar.YEAR, -1);
        return new Date(c.getTime().getTime());
    }

    public static Date generateTomDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, 100);
        Date d = new Date(c.getTime().getTime());
        return d;
    }

    public static  BestallningsData  createBestallningsData(JsonBestallning bestallning){
        BestallningsData data = new BestallningsData(bestallning)
        data
    }
}
