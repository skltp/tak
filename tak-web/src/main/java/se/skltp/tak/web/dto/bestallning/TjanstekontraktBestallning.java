/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.dto.bestallning;

import se.skltp.tak.web.util.JsonUtils;

import java.util.Objects;

public class TjanstekontraktBestallning {
    private String namnrymd;
    private String beskrivning;
    private long majorVersion;

    public String getNamnrymd() {
        return namnrymd;
    }

    public void setNamnrymd(String namnrymd) {
        this.namnrymd = JsonUtils.cleanupString(namnrymd);
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = JsonUtils.cleanupString(beskrivning);
    }

    public long getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(long majorVersion) {
        this.majorVersion = majorVersion;
    }

    @Override
    public String toString() {
        return namnrymd;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof TjanstekontraktBestallning)) return false;
        TjanstekontraktBestallning tk = (TjanstekontraktBestallning) obj;
        return  namnrymd.equalsIgnoreCase(tk.namnrymd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namnrymd, majorVersion);
    }
}
