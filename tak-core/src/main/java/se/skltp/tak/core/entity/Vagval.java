/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

@Entity
public class Vagval extends AbstractVersionInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	private Date fromTidpunkt;
	private Date tomTidpunkt;

	@Version
	private long version;

	@ManyToOne(optional = false)
	private Tjanstekontrakt tjanstekontrakt;

	@ManyToOne(optional = false)
	private LogiskAdress logiskAdress;
	
	@ManyToOne(optional = false)
	private AnropsAdress anropsAdress;

	@Override
	public String toString() {
		return Long.toString(id) + "-" + tjanstekontrakt + 
		       "-" + logiskAdress + "-" + anropsAdress+ "(" + fromTidpunkt + " - " + tomTidpunkt + ")";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public LogiskAdress getLogiskAdress() {
		return logiskAdress;
	}

	public void setLogiskAdress(LogiskAdress logiskAdress) {
		this.logiskAdress = logiskAdress;
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

	public Tjanstekontrakt getTjanstekontrakt() {
		return tjanstekontrakt;
	}

	public void setTjanstekontrakt(Tjanstekontrakt tjanstekontrakt) {
		this.tjanstekontrakt = tjanstekontrakt;
	}
	
	public AnropsAdress getAnropsAdress() {
		return anropsAdress;
	}

	public void setAnropsAdress(AnropsAdress anropsAdress) {
		this.anropsAdress = anropsAdress;
	}
	
	public String getPublishInfo() {
		return toString();
	}
}
