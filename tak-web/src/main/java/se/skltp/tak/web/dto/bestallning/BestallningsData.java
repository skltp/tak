package se.skltp.tak.web.dto.bestallning;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import se.skltp.tak.core.entity.*;

public class BestallningsData {

    public BestallningsData(JsonBestallning bestallning) {
        this.bestallning = bestallning;
        orderPlatform = bestallning.getPlattform();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        fromDate = Date.valueOf(df.format(bestallning.getGenomforandeTidpunkt()));
        toDate = generateTomDate(fromDate);
    }

    private final Date fromDate;
    private final Date toDate;
    private final JsonBestallning bestallning;
    private BestallningsRapport bestallningsRapport;
    private final String orderPlatform;

    private Set<String> bestallningErrors = new HashSet<>();

    private final Map<LogiskadressBestallning, LogiskAdress> logiskAdressObjects = new HashMap<>();
    private final Map<TjanstekontraktBestallning, Tjanstekontrakt> tjanstekontraktObjects = new HashMap<>();
    private final Map<TjanstekomponentBestallning, Tjanstekomponent> tjanstekomponentObjects = new HashMap<>();

    private final Map<VagvalBestallning, VagvalPair> vagvalObjects = new HashMap<>();
    private final Map<AnropsbehorighetBestallning, Anropsbehorighet> anropsbehorighetObjects = new HashMap<>();

    private final Map<VagvalBestallning, VagvalRelations> vagvalRelations = new HashMap<>();
    private final Map<AnropsbehorighetBestallning, AnropsBehorighetRelations> anropsbehorighetRelations = new HashMap<>();

    private final Map<AbstractVersionInfo, Boolean> isUpdated = new HashMap<>();

    private Map<String, AnropsAdress> anropsAdress = new HashMap<>();

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    private static Date generateTomDate(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.YEAR, 100);
            return new Date(c.getTime().getTime());
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
        VagvalPair vvl = vagvalObjects.computeIfAbsent(bestallning, k -> new VagvalPair());
        vvl.setOldVagval(vagval);
    }

    public void putNewVagval(VagvalBestallning bestallning, Vagval vagval) {
        VagvalPair vvl = vagvalObjects.computeIfAbsent(bestallning, k -> new VagvalPair());
        vvl.setNewVagval(vagval);
    }

    public void put(AnropsbehorighetBestallning bestallning, Anropsbehorighet anropsbehorighet) {
        anropsbehorighetObjects.put(bestallning, anropsbehorighet);

    }

    public void putRelations(VagvalBestallning bestallning, AnropsAdress adress, LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt) {
        VagvalRelations relations = new VagvalRelations(adress, logiskAdress, tjanstekontrakt);
        vagvalRelations.put(bestallning, relations);
    }

    public void putRelations(AnropsbehorighetBestallning bestallning, LogiskAdress logiskAdress, Tjanstekomponent tjanstekomponent, Tjanstekontrakt tjanstekontrakt) {
        AnropsBehorighetRelations relations = new AnropsBehorighetRelations(logiskAdress, tjanstekontrakt, tjanstekomponent);
        anropsbehorighetRelations.put(bestallning, relations);
    }


    public AnropsAdress getAnropsAdress(RivTaProfil rivTaProfil, Tjanstekomponent tjanstekomponent, String url){
        return anropsAdress.get(createAnropsAdressKey(rivTaProfil, tjanstekomponent, url));
    }

    public void putAnropsAdress(AnropsAdress adress){
        anropsAdress.put(createAnropsAdressKey(adress.getRivTaProfil(), adress.getTjanstekomponent(), adress.getAdress()), adress);
    }

    private String createAnropsAdressKey(RivTaProfil rivTaProfil, Tjanstekomponent tjanstekomponent, String url){
        return rivTaProfil.getNamn() + tjanstekomponent.getHsaId() + url;
    }

    public Collection<LogiskAdress> getAllLogiskAdresser() {
        return logiskAdressObjects.values();
    }

    public Collection<VagvalPair> getAllaVagval() {
        return vagvalObjects.values();
    }

    public Collection<Anropsbehorighet> getAllaAnropsbehorighet() {
        return anropsbehorighetObjects.values();
    }


    public Collection<Tjanstekontrakt> getAllTjanstekontrakt() {
        return tjanstekontraktObjects.values();
    }


    public Collection<Tjanstekomponent> getAllTjanstekomponent() {
        return tjanstekomponentObjects.values();
    }

    public Collection<AnropsAdress> getAllAnropsAdress() {
        return anropsAdress.values();
    }

    public Tjanstekontrakt getTjanstekontrakt(TjanstekontraktBestallning bestallning) {
        return tjanstekontraktObjects.get(bestallning);
    }

    public VagvalPair getVagval(VagvalBestallning bestallning) {
        return vagvalObjects.get(bestallning);
    }

    public Anropsbehorighet getAnropsbehorighet(AnropsbehorighetBestallning bestallning) {
        return anropsbehorighetObjects.get(bestallning);
    }

    public LogiskAdress getLogiskAdress(LogiskadressBestallning bestallning) {
        return logiskAdressObjects.get(bestallning);
    }

    public LogiskAdress getLogiskAdress(String hsaId) {
        for (Map.Entry<LogiskadressBestallning, LogiskAdress> a : logiskAdressObjects.entrySet()) {
            if (a.getKey().getHsaId().equals(hsaId)) return a.getValue();
        }
        return null;
    }

    public Tjanstekontrakt getTjanstekontrakt(String namnrymd) {
        for (Map.Entry<TjanstekontraktBestallning,Tjanstekontrakt> t : tjanstekontraktObjects.entrySet()) {
            if (t.getKey().getNamnrymd().equals(namnrymd)) return t.getValue();
        }
        return null;
    }

    public Tjanstekomponent getTjanstekomponent(TjanstekomponentBestallning bestallning) {
        return tjanstekomponentObjects.get(bestallning);
    }

    public Tjanstekomponent getTjanstekomponent(String hsaId) {
        for (Map.Entry<TjanstekomponentBestallning,Tjanstekomponent> t : tjanstekomponentObjects.entrySet()) {
            if (t.getKey().getHsaId().equals(hsaId)) return t.getValue();
        }
        return null;
    }

    public VagvalRelations getVagvalRelations(VagvalBestallning bestallning) {
        return vagvalRelations.get(bestallning);
    }

    public AnropsBehorighetRelations getAnropsbehorighetRelations(AnropsbehorighetBestallning bestallning) {
        return anropsbehorighetRelations.get(bestallning);
    }


    public Set<String> getBestallningErrors() {
        return bestallningErrors;
    }

    public void addError(String error) {
        this.bestallningErrors.add(error);
    }

    public void addError(Set<String> error) {
        this.bestallningErrors.addAll(error);
    }

    public boolean hasErrors() {
        return !this.bestallningErrors.isEmpty();
    }

    public JsonBestallning getBestallning() {
        return bestallning;
    }

    public String getOrderPlatform() {
        return orderPlatform;
    }

    public void buildBestallningsRapport() {
        bestallningsRapport = new BestallningsRapport(this);
    }
    public BestallningsRapport getBestallningsRapport() {
        return bestallningsRapport;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bestallning);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BestallningsData bd) && bd.hashCode() == hashCode();
    }

    public class AnropsBehorighetRelations {
        AnropsBehorighetRelations(LogiskAdress logiskadress, Tjanstekontrakt tjanstekontrakt, Tjanstekomponent tjanstekomponent) {
            this.logiskadress = logiskadress;
            this.tjanstekontrakt = tjanstekontrakt;
            this.tjanstekomponent = tjanstekomponent;
        }
        LogiskAdress logiskadress;
        Tjanstekontrakt tjanstekontrakt;
        Tjanstekomponent tjanstekomponent;


        public LogiskAdress getLogiskadress() {
            return logiskadress;
        }

        public Tjanstekontrakt getTjanstekontrakt() {
            return tjanstekontrakt;
        }

        public Tjanstekomponent getTjanstekomponent() {
            return tjanstekomponent;
        }

    }

    public class VagvalRelations {
        VagvalRelations(AnropsAdress anropsAdress, LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt){
            this.anropsAdress = anropsAdress;
            this.tjanstekontrakt = tjanstekontrakt;
            this.logiskAdress = logiskAdress;
        }
        AnropsAdress anropsAdress;
        LogiskAdress logiskAdress;
        Tjanstekontrakt tjanstekontrakt;

        public AnropsAdress getAnropsAdress() {
            return anropsAdress;
        }

        public LogiskAdress getLogiskAdress() {
            return logiskAdress;
        }

        public Tjanstekontrakt getTjanstekontrakt() {
            return tjanstekontrakt;
        }
    }

    public class VagvalPair {
        private Vagval newVagval;
        private Vagval oldVagval;

        public Vagval getNewVagval() {
            return newVagval;
        }

        public Vagval getOldVagval() {
            return oldVagval;
        }

        public void setNewVagval(Vagval newVagval) {
            this.newVagval = newVagval;
        }

        public void setOldVagval(Vagval oldVagval) {
            this.oldVagval = oldVagval;
        }
    }
}
