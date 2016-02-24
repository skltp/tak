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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class RivTaProfil extends AbstractVersionInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;	
	
	private String namn;

	private String beskrivning;
	
	private long version;
	
	@OneToMany(mappedBy = "rivTaProfil")
	private Set<AnropsAdress> AnropsAdresser = new HashSet<AnropsAdress>();

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

	public Set<AnropsAdress> getAnropsAdresser() {
		return AnropsAdresser;
	}

	public void setAnropsAdresser(Set<AnropsAdress> anropsAdresser) {
		AnropsAdresser = anropsAdresser;
	}
	
	public String getPublishInfo() {
		return toString();
	}
}
