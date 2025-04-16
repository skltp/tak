package se.skltp.tak.web.dto.bestallning;

import se.skltp.tak.web.util.JsonUtils;

import java.util.Objects;

public class LogiskadressBestallning {
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
        if(!(obj instanceof LogiskadressBestallning)) return false;
        LogiskadressBestallning la = (LogiskadressBestallning) obj;
        return  hsaId.equalsIgnoreCase(la.hsaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hsaId);
    }
}
