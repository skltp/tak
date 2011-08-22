/**
 * Copyright 2009 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
 */
package se.skl.tp.vagval.admin.core.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class LogiskAdress {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	private Date fromTidpunkt;
	private Date tomTidpunkt;

	@Version
	private long version;
	
	@ManyToOne(optional = false)
	private RivVersion rivVersion;

	@ManyToOne(optional = false)
	private Tjanstekontrakt tjanstekontrakt;

	@ManyToOne(optional = false)
	private LogiskAdressat logiskAdressat;

	@ManyToOne(optional = false)
	private Tjanstekomponent tjansteproducent;

	@Override
	public String toString() {
		return Long.toString(id) + "-" + rivVersion + "-" + tjanstekontrakt + 
		       "-" + logiskAdressat + "-" + tjansteproducent; 
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

	public LogiskAdressat getLogiskAdressat() {
		return logiskAdressat;
	}

	public void setLogiskAdressat(LogiskAdressat logiskAdressat) {
		this.logiskAdressat = logiskAdressat;
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

	public RivVersion getRivVersion() {
		return rivVersion;
	}

	public void setRivVersion(RivVersion rivVersion) {
		this.rivVersion = rivVersion;
	}

	public Tjanstekomponent getTjansteproducent() {
		return tjansteproducent;
	}

	public void setTjansteproducent(Tjanstekomponent tjansteproducent) {
		this.tjansteproducent = tjansteproducent;
	}

	public Tjanstekontrakt getTjanstekontrakt() {
		return tjanstekontrakt;
	}

	public void setTjanstekontrakt(Tjanstekontrakt tjanstekontrakt) {
		this.tjanstekontrakt = tjanstekontrakt;
	}

	public LogiskAdressat getLogiskAddresat() {
		return logiskAdressat;
	}

	public void setLogiskAddresat(LogiskAdressat logiskAdressat) {
		this.logiskAdressat = logiskAdressat;
	}

}
