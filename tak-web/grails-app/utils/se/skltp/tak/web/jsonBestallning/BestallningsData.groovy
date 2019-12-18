package se.skltp.tak.web.jsonBestallning

import se.skltp.tak.core.entity.*

import java.sql.Date
import java.text.DateFormat
import java.text.SimpleDateFormat

class BestallningsData {

    BestallningsData(JsonBestallning bestallning) {
        this.bestallning = bestallning
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd")
        fromDate = Date.valueOf(df.format(bestallning.genomforandeTidpunkt))
        toDate = generateTomDate(fromDate)
    }

    private Date fromDate
    private Date toDate
    private JsonBestallning bestallning

    private transient List<String> bestallningErrors = new LinkedList<String>();

    private Map<LogiskadressBestallning, LogiskAdress> logiskAdressObjects = new HashMap<LogiskadressBestallning, LogiskAdress>();
    private Map<TjanstekontraktBestallning, Tjanstekontrakt> tjanstekontraktObjects = new HashMap<TjanstekontraktBestallning, Tjanstekontrakt>();
    private Map<TjanstekomponentBestallning, Tjanstekomponent> tjanstekomponentObjects = new HashMap<TjanstekomponentBestallning, Tjanstekomponent>();

    private Map<VagvalBestallning, VagvalPair> vagvalObjects = new HashMap<VagvalBestallning, VagvalPair>();
    private Map<AnropsbehorighetBestallning, Anropsbehorighet> anropsbehorighetObjects = new HashMap<AnropsbehorighetBestallning, Anropsbehorighet>();

    private Map<VagvalBestallning, RelationData> vagvalRelations = new HashMap<VagvalBestallning, RelationData>();
    private Map<AnropsbehorighetBestallning, RelationData> anropsbehorighetRelations = new HashMap<AnropsbehorighetBestallning, RelationData>()

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

    public putRelations(VagvalBestallning bestallning, LogiskAdress logiskAdress, Tjanstekomponent tjanstekomponent, Tjanstekontrakt tjanstekontrakt, RivTaProfil profil) {
        RelationData relations = new RelationData(logiskAdress, tjanstekontrakt, tjanstekomponent, profil)
        vagvalRelations.put(bestallning, relations)
    }

    public putRelations(AnropsbehorighetBestallning bestallning, LogiskAdress logiskAdress, Tjanstekomponent tjanstekomponent, Tjanstekontrakt tjanstekontrakt) {
        RelationData relations = new RelationData(logiskAdress, tjanstekontrakt, tjanstekomponent)
        anropsbehorighetRelations.put(bestallning, relations)
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

    public RelationData getVagvalRelations(VagvalBestallning bestallning) {
        return vagvalRelations.get(bestallning)
    }

    public RelationData getAnropsbehorighetRelations(AnropsbehorighetBestallning bestallning) {
        return anropsbehorighetRelations.get(bestallning)
    }


    List<String> getBestallningErrors() {
        return bestallningErrors
    }

    void addError(String error) {
        this.bestallningErrors.add(error)
    }

    void addError(List<String> error) {
        this.bestallningErrors.addAll(error)
    }

    boolean hasErrors() {
        return this.bestallningErrors.size() > 0
    }

    JsonBestallning getBestallning() {
        return bestallning
    }

    class RelationData {
        RelationData(LogiskAdress logiskadress, Tjanstekontrakt tjanstekontrakt, Tjanstekomponent tjanstekomponent, RivTaProfil profil) {
            this.logiskadress = logiskadress
            this.tjanstekontrakt = tjanstekontrakt
            this.tjanstekomponent = tjanstekomponent
            this.profil = profil
        }

        RelationData(LogiskAdress logiskadress, Tjanstekontrakt tjanstekontrakt, Tjanstekomponent tjanstekomponent) {
            this.logiskadress = logiskadress
            this.tjanstekontrakt = tjanstekontrakt
            this.tjanstekomponent = tjanstekomponent
        }
        LogiskAdress logiskadress;
        Tjanstekontrakt tjanstekontrakt;
        Tjanstekomponent tjanstekomponent;
        RivTaProfil profil


        LogiskAdress getLogiskadress() {
            return logiskadress
        }

        Tjanstekontrakt getTjanstekontrakt() {
            return tjanstekontrakt
        }

        Tjanstekomponent getTjanstekomponent() {
            return tjanstekomponent
        }

        RivTaProfil getProfil() {
            return profil
        }


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
