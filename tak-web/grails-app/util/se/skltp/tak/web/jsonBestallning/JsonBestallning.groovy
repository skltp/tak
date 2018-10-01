package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class JsonBestallning {
    private String plattform;
    private float formatVersion;
    private int version;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD hh:mm:ss")
    private Date tidpunkt;

    private String utforare;
    private String kommentar;

    @JsonProperty("ensure-data")
    private KollektivData ensureData;

    @JsonProperty("extrude-data")
    private KollektivData extrudeData;

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

    public Date getTidpunkt() {
        return tidpunkt;
    }

    public void setTidpunkt(Date tidpunkt) {
        this.tidpunkt = tidpunkt;
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


    public KollektivData getEnsureData() {
        return ensureData;
    }

    public void setEnsureData(KollektivData ensureData) {
        this.ensureData = ensureData;
    }


    public KollektivData getExtrudeData() {
        return extrudeData;
    }


    public void setExtrudeData(KollektivData extrudeData) {
        this.extrudeData = extrudeData;
    }
}
