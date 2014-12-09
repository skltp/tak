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
public class LogiskAdress {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	private String hsaId;
	
	private String beskrivning;
	
	private long version;
	
	@OneToMany(mappedBy = "logiskAdress")
	private Set<Vagval> vagval = new HashSet<Vagval>();

	@OneToMany(mappedBy = "logiskAdress")
	private Set<Anropsbehorighet> anropsbehorigheter = new HashSet<Anropsbehorighet>();

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

	public Set<Vagval> getVagval() {
		return vagval;
	}

	public void setVagval(Set<Vagval> vagval) {
		this.vagval = vagval;
	}

	public Set<Anropsbehorighet> getAnropsbehorigheter() {
		return anropsbehorigheter;
	}

	public void setAnropsbehorigheter(Set<Anropsbehorighet> anropsbehorigheter) {
		this.anropsbehorigheter = anropsbehorigheter;
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