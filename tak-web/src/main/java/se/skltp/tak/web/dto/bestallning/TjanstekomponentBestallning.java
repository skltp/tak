package se.skltp.tak.web.dto.bestallning;

import se.skltp.tak.web.util.JsonUtils;

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
        return  hsaId.equals(tb.hsaId);
    }
}
