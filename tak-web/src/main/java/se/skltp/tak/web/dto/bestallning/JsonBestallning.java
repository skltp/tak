/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.dto.bestallning;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import se.skltp.tak.web.util.JsonUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

public class JsonBestallning {
    private String plattform;
    private float formatVersion;
    private int version;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date bestallningsTidpunkt;

    @JsonDeserialize(using = LenientLocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate genomforandeTidpunkt;

    private String utforare;
    private String kommentar;

    @JsonProperty("inkludera")
    private BestallningsAvsnitt inkludera;

    @JsonProperty("exkludera")
    private BestallningsAvsnitt exkludera;

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

    public Date getBestallningsTidpunkt() {
        return bestallningsTidpunkt;
    }

    public void setBestallningsTidpunkt(Date bestallningsTidpunkt) {
        this.bestallningsTidpunkt = bestallningsTidpunkt;
    }

    public LocalDate getGenomforandeTidpunkt() {
        return genomforandeTidpunkt;
    }

    public void setGenomforandeTidpunkt(LocalDate genomforandeTidpunkt) {
        this.genomforandeTidpunkt = genomforandeTidpunkt;
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
        this.kommentar = JsonUtils.cleanupString(kommentar);
    }

    public BestallningsAvsnitt getInkludera() {
        if (inkludera == null) {
            return new BestallningsAvsnitt();
        } else {
            return inkludera;
        }
    }

    public void setInkludera(BestallningsAvsnitt inkludera) {
        if (inkludera == null) {
            this.inkludera = new BestallningsAvsnitt();
        } else {
            this.inkludera = inkludera;
        }
    }

    public BestallningsAvsnitt getExkludera() {
        if (exkludera == null) {
            return new BestallningsAvsnitt();
        } else {
            return exkludera;
        }
    }

    void setExkludera(BestallningsAvsnitt exkludera) {
        if (exkludera == null) {
            this.exkludera = new BestallningsAvsnitt();
        } else {
            this.exkludera = exkludera;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                plattform,
                formatVersion,
                version, utforare,
                bestallningsTidpunkt,
                genomforandeTidpunkt,
                inkludera, exkludera);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof JsonBestallning jb) && jb.hashCode() == hashCode();
    }

}
