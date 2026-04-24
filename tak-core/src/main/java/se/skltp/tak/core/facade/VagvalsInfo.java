package se.skltp.tak.core.facade;

import java.sql.Date;

public class VagvalsInfo {

	private Date fromTidpunkt;
	private Date tomTidpunkt;
	private String tjanstekontraktNamnrymd;
	private String logiskAdressHsaId;
	private String logiskAdressBeskrivning;

	public Date getFromTidpunkt() {
		return fromTidpunkt;
	}

	public void setFromTidpunkt(Date fromTidpunkt) {
		this.fromTidpunkt = fromTidpunkt;
	}

	public Date getTomTidpunkt() {
		return tomTidpunkt;
	}

	public void setTomTidpunkt(Date tomTidpunkt) {
		this.tomTidpunkt = tomTidpunkt;
	}

	public String getTjanstekontraktNamnrymd() {
		return tjanstekontraktNamnrymd;
	}

	public void setTjanstekontraktNamnrymd(String tjanstekontraktNamnrymd) {
		this.tjanstekontraktNamnrymd = tjanstekontraktNamnrymd;
	}

	public String getLogiskAdressHsaId() {
		return logiskAdressHsaId;
	}

	public void setLogiskAdressHsaId(String logiskAdressHsaId) {
		this.logiskAdressHsaId = logiskAdressHsaId;
	}

	public String getLogiskAdressBeskrivning() {
		return logiskAdressBeskrivning;
	}

	public void setLogiskAdressBeskrivning(String logiskAdressBeskrivning) {
		this.logiskAdressBeskrivning = logiskAdressBeskrivning;
	}

}
