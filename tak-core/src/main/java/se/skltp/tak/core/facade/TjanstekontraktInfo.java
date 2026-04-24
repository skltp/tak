package se.skltp.tak.core.facade;


public class TjanstekontraktInfo {
	
	private String minorVersion;
	private String majorVersion;
	private String namnrymd;
	private String beskrivning;

	public String toString() {
		return namnrymd; 
	}

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

	public String getMinorVersion() {
		return minorVersion;
	}
	public void setMinorVersion(String minorVersion) {
		this.minorVersion = minorVersion;
	}

	public String getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(String majorVersion) {
		this.majorVersion = majorVersion;
	}
}
