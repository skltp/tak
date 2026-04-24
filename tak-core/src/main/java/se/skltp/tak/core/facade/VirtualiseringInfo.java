/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.facade;

import java.sql.Date;

public class VirtualiseringInfo {

	// RivProfil
	private String namnRiv;

	// VirtualiseradTjansteproducent
	private long idLogiskAdress;
	private Date fromTidpunkt;
	private Date tomTidpunkt;

	// Tjanstekontrakt
	private String namnrymd;
	
	// LogiskAdressat
	private String hsaIdLogiskAddresat;

	// Tjanstekomponent
	private String hsaIdTjanstekomponent;
	private String adress;

	public String getNamnRiv() {
		return namnRiv;
	}
	public void setNamnRiv(String namnRiv) {
		this.namnRiv = namnRiv;
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
	public String getNamnrymd() {
		return namnrymd;
	}
	public void setNamnrymd(String namnrymd) {
		this.namnrymd = namnrymd;
	}
	public String getHsaIdLogiskAddresat() {
		return hsaIdLogiskAddresat;
	}
	public void setHsaIdLogiskAddresat(String hsaIdLogiskAddresat) {
		this.hsaIdLogiskAddresat = hsaIdLogiskAddresat;
	}
	public String getHsaIdTjanstekomponent() {
		return hsaIdTjanstekomponent;
	}
	public void setHsaIdTjanstekomponent(String hsaIdTjanstekomponent) {
		this.hsaIdTjanstekomponent = hsaIdTjanstekomponent;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public long getIdLogiskAdress() {
		return idLogiskAdress;
	}
	public void setIdLogiskAdress(long idLogiskAdress) {
		this.idLogiskAdress = idLogiskAdress;
	}

}
