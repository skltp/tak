package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty

class JsonBestallning {
    private String plattform;
    private float formatVersion;
    private int version;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZ")
    private Date bestallningsTidpunkt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZ")
    private Date genomforandeTidpunkt

    private String utforare;
    private String kommentar;

    @JsonProperty("inkludera")
    private KollektivData inkludera;

    @JsonProperty("exkludera")
    private KollektivData exkludera;


    private List<String> bestallningErrors = new LinkedList<String>();

    private List<String> bestallningInfo = new LinkedList<String>()

    List<String> getBestallningErrors() {
        return bestallningErrors
    }

    boolean isValidBestallning() {
        bestallningErrors.isEmpty()
    }

    List<String> getBestallningInfo() {
        return bestallningInfo
    }

    void addError(String error) {
        this.bestallningErrors.add(error)
    }

    void addIInfo(String info) {
        this.bestallningInfo.add(info)
    }

    public String getPlattform() {
        return plattform;
    }

    public void setPlattform(String plattform) {
        this.plattform = plattform;
    }

    public float getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(float formatVersion) {
        this.formatVersion = formatVersion;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    Date getBestallningsTidpunkt() {
        return bestallningsTidpunkt
    }

    void setBestallningsTidpunkt(Date bestallningsTidpunkt) {
        this.bestallningsTidpunkt = bestallningsTidpunkt
    }

    Date getGenomforandeTidpunkt() {
        return genomforandeTidpunkt
    }

    void setGenomforandeTidpunkt(Date genomforandeTidpunkt) {
        this.genomforandeTidpunkt = genomforandeTidpunkt
    }

    public String getUtforare() {
        return utforare;
    }

    public void setUtforare(String utforare) {
        this.utforare = utforare;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    KollektivData getInkludera() {
        return inkludera
    }

    void setInkludera(KollektivData inkludera) {
        this.inkludera = inkludera
    }

    KollektivData getExkludera() {
        return exkludera
    }

    void setExkludera(KollektivData exkludera) {
        this.exkludera = exkludera
    }
}
