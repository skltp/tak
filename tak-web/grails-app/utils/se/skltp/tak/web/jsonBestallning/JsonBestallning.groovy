package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnore

import java.sql.Date;

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

    @JsonIgnore
    private transient List<String> bestallningErrors = new LinkedList<String>();
    @JsonIgnore
    private transient List<String> bestallningInfo = new LinkedList<String>()
    @JsonIgnore
    List<String> getBestallningErrors() {
        return bestallningErrors
    }
    @JsonIgnore
    List<String> getBestallningInfo() {
        return bestallningInfo
    }

    void addError(String error) {
        this.bestallningErrors.add(error)
    }

    boolean hasErrors(){
        return this.bestallningErrors.size() > 0
    }

    void addInfo(String info) {
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
