/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.tak.core.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

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