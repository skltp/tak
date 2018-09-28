package se.skltp.tak.web.jsonBestallning;

public class TjanstekontraktBestallning {
    private String namnrymd;
    private String beskrivning;
    private long majorVersion;
    private long minorVersion;

    public String getNamnrymd() {
        return namnrymd;
    }

    public void setNamnrymd(String namnrymd) {
        this.namnrymd = namnrymd;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }

    public long getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(long majorVersion) {
        this.majorVersion = majorVersion;
    }

    public long getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(long minorVersion) {
        this.minorVersion = minorVersion;
    }
}
