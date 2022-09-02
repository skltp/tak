package se.skltp.tak.web.jsonBestallning

class TjanstekontraktBestallning {
    private String namnrymd
    private String beskrivning
    private long majorVersion

    String getNamnrymd() {
        return namnrymd
    }

    void setNamnrymd(String namnrymd) {
        this.namnrymd = JsonUtils.cleanupString(namnrymd)
    }

    String getBeskrivning() {
        return beskrivning
    }

    void setBeskrivning(String beskrivning) {
        this.beskrivning = JsonUtils.cleanupString(beskrivning)
    }

    long getMajorVersion() {
        return majorVersion
    }

    void setMajorVersion(long majorVersion) {
        this.majorVersion = majorVersion
    }

    @Override
    public String toString() {
        return namnrymd;
    }

    @Override
    boolean equals(Object obj) {
        if(!Objects instanceof TjanstekontraktBestallning) return false
        TjanstekontraktBestallning tk = (TjanstekontraktBestallning) obj;
        return  namnrymd.equals(tk.namnrymd)
    }
}
