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

import se.skltp.tak.core.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class Tjanstekomponent extends AbstractVersionInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	private String hsaId;

	private String beskrivning;
	
	@Version
	private long version;
	
	@OneToMany(mappedBy = "tjanstekonsument")
	private List<Anropsbehorighet> anropsbehorigheter = new ArrayList<Anropsbehorighet>();
		
	@OneToMany(mappedBy = "tjanstekomponent")
	private Set<AnropsAdress> anropsAdresser = new HashSet<AnropsAdress>();
	
	@Override
	public String toString() {
		return hsaId; 
	}

	public String getHsaId() {
		return hsaId;
	}
	public void setHsaId(String hsaId) {
		this.hsaId = hsaId;
	}
	public List<Anropsbehorighet> getAnropsbehorigheter() {
		return anropsbehorigheter;
	}
	public void setAnropsbehorigheter(List<Anropsbehorighet> anropsbehorigheter) {
		this.anropsbehorigheter = anropsbehorigheter;
	}
	public Set<AnropsAdress> getAnropsAdresser() {
		return anropsAdresser;
	}
	public void setAnropsAdresser(Set<AnropsAdress> anropsAdresser) {
		this.anropsAdresser = anropsAdresser;
	}
	public String getBeskrivning() {
		return beskrivning;
	}
	public void setBeskrivning(String beskrivning) {
		this.beskrivning = Util.cleanupString(beskrivning);
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
	
	public String getPublishInfo() {
		return toString();
	}
}
