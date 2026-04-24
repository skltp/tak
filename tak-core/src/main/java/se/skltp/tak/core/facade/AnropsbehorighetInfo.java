/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.facade;

import java.sql.Date;
import java.util.List;

public class AnropsbehorighetInfo {
	
	// VirtualiseradTjansteproducent
	private long idAnropsbehorighet;
	private Date fromTidpunkt;
	private Date tomTidpunkt;
	private String integrationsavtal;

	// Tjanstekontrakt
	private String tjanstekontraktNamnrymd;
	
	// OrganisatoriskSammanhang
	private String logiskAdressHsaId;
	private String logiskAdressBeskrivning;

	// Tjanstekomponent
	private String hsaIdTjanstekomponent;
	
	private List<FilterInfo> filterInfos;

	public long getIdAnropsbehorighet() {
		return idAnropsbehorighet;
	}

	public void setIdAnropsbehorighet(long idAnropsbehorighet) {
		this.idAnropsbehorighet = idAnropsbehorighet;
	}

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
	
	public String getIntegrationsavtal() {
		return integrationsavtal;
	}

	public void setIntegrationsavtal(String integrationsavtal) {
		this.integrationsavtal = integrationsavtal;
	}

	public String getTjanstekontraktNamnrymd() {
		return tjanstekontraktNamnrymd;
	}

	public void setTjanstekontraktNamnrymd(String tjanstekontraktNamnrymd) {
		this.tjanstekontraktNamnrymd = tjanstekontraktNamnrymd;
	}

	public String getHsaIdTjanstekomponent() {
		return hsaIdTjanstekomponent;
	}

	public void setHsaIdTjanstekomponent(String hsaIdTjanstekomponent) {
		this.hsaIdTjanstekomponent = hsaIdTjanstekomponent;
	}

	public String getLogiskAdressHsaId() {
		return logiskAdressHsaId;
	}

	public void setLogiskAdressHsaId(String hsaIdLogiskAddresat) {
		this.logiskAdressHsaId = hsaIdLogiskAddresat;
	}
	
	public String getLogiskAdressBeskrivning() {
		return logiskAdressBeskrivning;
	}

	public void setLogiskAdressBeskrivning(String logiskAdressBeskrivning) {
		this.logiskAdressBeskrivning = logiskAdressBeskrivning;
	}

	public List<FilterInfo> getFilterInfos() {
		return filterInfos;
	}
	
	public void setFilterInfos(List<FilterInfo> filterInfos) {
		this.filterInfos = filterInfos;
	}

}
