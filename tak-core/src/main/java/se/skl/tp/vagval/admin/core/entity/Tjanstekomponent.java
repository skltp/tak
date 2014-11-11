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
package se.skl.tp.vagval.admin.core.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Tjanstekomponent {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	private String hsaId;

	private String adress; // must have adress if component is Service Producer	

	private String beskrivning;

	private long version;
	
	@OneToMany(mappedBy = "tjanstekonsument")
	private List<Anropsbehorighet> anropsbehorigheter = new ArrayList<Anropsbehorighet>();
	
	@OneToMany(mappedBy = "tjansteproducent")
	private List<LogiskAdress> logiskAdresser = new ArrayList<LogiskAdress>();
	
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
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public List<Anropsbehorighet> getAnropsbehorigheter() {
		return anropsbehorigheter;
	}
	public void setAnropsbehorigheter(List<Anropsbehorighet> anropsbehorigheter) {
		this.anropsbehorigheter = anropsbehorigheter;
	}
	public List<LogiskAdress> getLogiskAdresser() {
		return logiskAdresser;
	}
	public void setLogiskAdresser(List<LogiskAdress> logiskAdresser) {
		this.logiskAdresser = logiskAdresser;
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
