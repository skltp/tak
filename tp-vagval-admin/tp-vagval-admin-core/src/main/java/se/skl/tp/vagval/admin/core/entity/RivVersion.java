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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class RivVersion {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;	
	
	private String namn;

	private String beskrivning;
	
	private long version;

	@OneToMany(mappedBy = "rivVersion")
	private Set<LogiskAdress> virtualiseradTjanstekomponenter = new HashSet<LogiskAdress>();

	@Override
	public String toString() {
		return namn; 
	}

	public String getNamn() {
		return namn;
	}

	public void setNamn(String namnrymd) {
		this.namn = namnrymd;
	}

	public Set<LogiskAdress> getVirtualiseradTjanstekomponenter() {
		return virtualiseradTjanstekomponenter;
	}

	public void setVirtualiseradTjanstekomponenter(
			Set<LogiskAdress> virtualiseradTjanstekomponenter) {
		this.virtualiseradTjanstekomponenter = virtualiseradTjanstekomponenter;
	}

	public String getBeskrivning() {
		return beskrivning;
	}

	public void setBeskrivning(String beskrivning) {
		this.beskrivning = beskrivning;
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

}
