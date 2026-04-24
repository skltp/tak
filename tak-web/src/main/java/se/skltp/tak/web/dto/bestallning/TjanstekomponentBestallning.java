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

public class TjanstekomponentBestallning {
    private String hsaId;
    private String beskrivning;

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = JsonUtils.cleanupString(hsaId);
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = JsonUtils.cleanupString(beskrivning);
    }

    @Override
    public String toString() {
        return hsaId;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof TjanstekomponentBestallning)) return false;
        TjanstekomponentBestallning tb = (TjanstekomponentBestallning) obj;
        return  hsaId.equalsIgnoreCase(tb.hsaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hsaId);
    }
}
