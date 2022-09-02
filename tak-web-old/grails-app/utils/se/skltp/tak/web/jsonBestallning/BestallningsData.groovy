package se.skltp.tak.web.jsonBestallning

import se.skltp.tak.core.entity.*

import java.sql.Date
import java.text.DateFormat
import java.text.SimpleDateFormat

class BestallningsData {

    BestallningsData(JsonBestallning bestallning) {
        this.bestallning = bestallning
        orderPlatform = bestallning.getPlattform()
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd")
        fromDate = Date.valueOf(df.format(bestallning.genomforandeTidpunkt))
        toDate = generateTomDate(fromDate)
    }

    private Date fromDate
    private Date toDate
    private JsonBestallning bestallning
    private String orderPlatform
    private String currentPlatform

    private transient Set<String> bestallningErrors = new HashSet<String>();

    private Map<LogiskadressBestallning, LogiskAdress> logiskAdressObjects = new HashMap<LogiskadressBestallning, LogiskAdress>();
    private Map<TjanstekontraktBestallning, Tjanstekontrakt> tjanstekontraktObjects = new HashMap<TjanstekontraktBestallning, Tjanstekontrakt>();
    private Map<TjanstekomponentBestallning, Tjanstekomponent> tjanstekomponentObjects = new HashMap<TjanstekomponentBestallning, Tjanstekomponent>();

    private Map<VagvalBestallning, VagvalPair> vagvalObjects = new HashMap<VagvalBestallning, VagvalPair>();
    private Map<AnropsbehorighetBestallning, Anropsbehorighet> anropsbehorighetObjects = new HashMap<AnropsbehorighetBestallning, Anropsbehorighet>();

    private Map<VagvalBestallning, VagvalRelations> vagvalRelations = new HashMap<VagvalBestallning, VagvalRelations>();
    private Map<AnropsbehorighetBestallning, AnropsBehorighetRelations> anropsbehorighetRelations = new HashMap<AnropsbehorighetBestallning, AnropsBehorighetRelations>()

    private Map<String, AnropsAdress> anropsAdress = new HashMap<>()

    Date getFromDate() {
        return fromDate
    }

    Date getToDate() {
        return toDate
    }

    private static Date generateTomDate(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance()
            c.setTime(date)
            c.add(Calendar.YEAR, 100)
            Date d = new Date(c.getTime().getTime())
            return d
        }
        return null;
    }


    public void put(LogiskadressBestallning bestallning, LogiskAdress value) {
        logiskAdressObjects.put(bestallning, value);
    }

    public void put(TjanstekontraktBestallning bestallning, Tjanstekontrakt value) {
        tjanstekontraktObjects.put(bestallning, value);
    }

    public void put(TjanstekomponentBestallning bestallning, Tjanstekomponent value) {
        tjanstekomponentObjects.put(bestallning, value);
    }

    public void putOldVagval(VagvalBestallning bestallning, Vagval vagval) {
        VagvalPair vvl = vagvalObjects.get(bestallning);
        if (vvl == null) {
            vvl = new VagvalPair();
            vagvalObjects.put(bestallning, vvl);
        }
        vvl.oldVagval = vagval;
    }

    public void putNewVagval(VagvalBestallning bestallning, Vagval vagval) {
        VagvalPair vvl = vagvalObjects.get(bestallning);
        if (vvl == null) {
            vvl = new VagvalPair();
            vagvalObjects.put(bestallning, vvl);
        }
        vvl.newVagval = vagval;
    }

    public void put(AnropsbehorighetBestallning bestallning, Anropsbehorighet anropsbehorighet) {
        anropsbehorighetObjects.put(bestallning, anropsbehorighet);

    }

    public putRelations(VagvalBestallning bestallning, AnropsAdress adress, LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt) {
        VagvalRelations relations = new VagvalRelations(adress, logiskAdress, tjanstekontrakt)
        vagvalRelations.put(bestallning, relations)
    }

    public putRelations(AnropsbehorighetBestallning bestallning, LogiskAdress logiskAdress, Tjanstekomponent tjanstekomponent, Tjanstekontrakt tjanstekontrakt) {
        AnropsBehorighetRelations relations = new AnropsBehorighetRelations(logiskAdress, tjanstekontrakt, tjanstekomponent)
        anropsbehorighetRelations.put(bestallning, relations)
    }


    public AnropsAdress getAnropsAdress(RivTaProfil rivTaProfil, Tjanstekomponent tjanstekomponent, String url){
        return anropsAdress.get(createAnropsAdressKey(rivTaProfil, tjanstekomponent, url))
    }

    public AnropsAdress putAnropsAdress(AnropsAdress adress){
        anropsAdress.put(createAnropsAdressKey(adress.rivTaProfil, adress.tjanstekomponent, adress.adress), adress)
    }

    private String createAnropsAdressKey(RivTaProfil rivTaProfil, Tjanstekomponent tjanstekomponent, String url){
        return rivTaProfil.namn + tjanstekomponent.hsaId + url
    }

    public Collection<LogiskAdress> getAllLogiskAdresser() {
        return logiskAdressObjects.values()
    }

    public Collection<VagvalPair> getAllaVagval() {
        return vagvalObjects.values()
    }

    public Collection<Anropsbehorighet> getAllaAnropsbehorighet() {
        return anropsbehorighetObjects.values()
    }


    public Collection<Tjanstekontrakt> getAllTjanstekontrakt() {
        return tjanstekontraktObjects.values()
    }


    public Collection<Tjanstekomponent> getAllTjanstekomponent() {
        return tjanstekomponentObjects.values()
    }

    public Collection<AnropsAdress> getAllAnropsAdress() {
        return anropsAdress.values()
    }

    public Tjanstekontrakt getTjanstekontrakt(TjanstekontraktBestallning bestallning) {
        return tjanstekontraktObjects.get(bestallning)
    }

    public VagvalPair getVagval(VagvalBestallning bestallning) {
        return vagvalObjects.get(bestallning)
    }

    public Anropsbehorighet getAnropsbehorighet(AnropsbehorighetBestallning bestallning) {
        return anropsbehorighetObjects.get(bestallning)
    }

    public LogiskAdress getLogiskAdress(LogiskadressBestallning bestallning) {
        return logiskAdressObjects.get(bestallning)
    }

    public LogiskAdress getLogiskAdress(String hsaId) {
        LogiskAdress address = null
        logiskAdressObjects.keySet().each() {
            if (it.hsaId == hsaId)
                address = logiskAdressObjects.get(it)
        }
        return address
    }

    public Tjanstekontrakt getTjanstekontrakt(String namnrymd) {
        Tjanstekontrakt tjanstekontrakt = null
        tjanstekontraktObjects.keySet().each() {
            if (it.namnrymd == namnrymd) tjanstekontrakt = tjanstekontraktObjects.get(it)
        }
        return tjanstekontrakt
    }


    public Tjanstekomponent getTjanstekomponent(TjanstekomponentBestallning bestallning) {
        return tjanstekomponentObjects.get(bestallning)
    }

    public Tjanstekomponent getTjanstekomponent(String hsaId) {
        Tjanstekomponent tjanstekomponent = null
        tjanstekomponentObjects.keySet().each() {
            if (it.hsaId == hsaId) tjanstekomponent = tjanstekomponentObjects.get(it)
        }
        return tjanstekomponent
    }

    public VagvalRelations getVagvalRelations(VagvalBestallning bestallning) {
        return vagvalRelations.get(bestallning)
    }

    public AnropsBehorighetRelations getAnropsbehorighetRelations(AnropsbehorighetBestallning bestallning) {
        return anropsbehorighetRelations.get(bestallning)
    }


    Set<String> getBestallningErrors() {
        return bestallningErrors
    }

    void addError(String error) {
        this.bestallningErrors.add(error)
    }

    void addError(Set<String> error) {
        this.bestallningErrors.addAll(error)
    }

    boolean hasErrors() {
        return this.bestallningErrors.size() > 0
    }

    JsonBestallning getBestallning() {
        return bestallning
    }

    class AnropsBehorighetRelations {
        AnropsBehorighetRelations(LogiskAdress logiskadress, Tjanstekontrakt tjanstekontrakt, Tjanstekomponent tjanstekomponent) {
            this.logiskadress = logiskadress
            this.tjanstekontrakt = tjanstekontrakt
            this.tjanstekomponent = tjanstekomponent
        }
        LogiskAdress logiskadress;
        Tjanstekontrakt tjanstekontrakt;
        Tjanstekomponent tjanstekomponent;


        LogiskAdress getLogiskadress() {
            return logiskadress
        }

        Tjanstekontrakt getTjanstekontrakt() {
            return tjanstekontrakt
        }

        Tjanstekomponent getTjanstekomponent() {
            return tjanstekomponent
        }

    }

    class VagvalRelations {
        VagvalRelations(AnropsAdress anropsAdress, LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt){
            this.anropsAdress = anropsAdress
            this.tjanstekontrakt = tjanstekontrakt
            this.logiskAdress = logiskAdress
        }
        AnropsAdress anropsAdress;

        LogiskAdress logiskAdress

        Tjanstekontrakt tjanstekontrakt
    }

    class VagvalPair {
        private Vagval newVagval
        private Vagval oldVagval

        Vagval getNewVagval() {
            return newVagval
        }

        Vagval getOldVagval() {
            return oldVagval
        }

        void setNewVagval(Vagval newVagval) {
            this.newVagval = newVagval
        }

        void setOldVagval(Vagval oldVagval) {
            this.oldVagval = oldVagval
        }
    }
}
