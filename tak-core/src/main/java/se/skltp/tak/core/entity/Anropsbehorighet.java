/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

@Entity
public class Anropsbehorighet extends AbstractVersionInfo {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private Date fromTidpunkt;
	private Date tomTidpunkt;
	private String integrationsavtal;
	
	@Version
	private long version;
	
	@ManyToOne (optional = false)
	private Tjanstekomponent tjanstekonsument;

	@ManyToOne(optional = false)
	private Tjanstekontrakt tjanstekontrakt;

	@ManyToOne(optional = false)
	private LogiskAdress logiskAdress;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "anropsbehorighet")
	private List<Filter> filter = new ArrayList<Filter>();
	
	@Override
	public String toString() {
		return Long.toString(id) + "-" + integrationsavtal + "-" + tjanstekonsument +
		       "-" + tjanstekontrakt + "-" + logiskAdress + "(" + fromTidpunkt + " - " + tomTidpunkt + ")";
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

	public Tjanstekomponent getTjanstekonsument() {
		return tjanstekonsument;
	}

	public void setTjanstekonsument(Tjanstekomponent tjanstekonsument) {
		this.tjanstekonsument = tjanstekonsument;
	}

	public Tjanstekontrakt getTjanstekontrakt() {
		return tjanstekontrakt;
	}

	public void setTjanstekontrakt(Tjanstekontrakt tjanstekontrakt) {
		this.tjanstekontrakt = tjanstekontrakt;
	}

	public LogiskAdress getLogiskAdress() {
		return logiskAdress;
	}

	public void setLogiskAdress(LogiskAdress logiskAdress) {
		this.logiskAdress = logiskAdress;
	}

	public String getIntegrationsavtal() {
		return integrationsavtal;
	}

	public void setIntegrationsavtal(String integrationsavtal) {
		this.integrationsavtal = integrationsavtal;
	}

	public List<Filter> getFilter() {
		return filter;
	}

	public void setFilter(List<Filter> filter) {
		this.filter = filter;
	}
	
	public String getPublishInfo() {
		return toString();
	}
}