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

import jakarta.persistence.*;
@Entity
public class AnropsAdress extends AbstractVersionInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Version
	private long version;

	private String adress;
	
	@OneToMany(mappedBy = "anropsAdress")
	private Set<Vagval> vagVal = new HashSet<Vagval>();
	
	@ManyToOne(optional = false)
	private Tjanstekomponent tjanstekomponent;
	
	@ManyToOne(optional = false)
	private RivTaProfil rivTaProfil;
	

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

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}
	
	@Override
	public String toString() {
		return Long.toString(id) + "-" + adress + 
		       "-" + tjanstekomponent; 
	}
	

	public Set<Vagval> getVagVal() {
		return vagVal;
	}

	public void setVagVal(Set<Vagval> vagVal) {
		this.vagVal = vagVal;
	}
	
	public Tjanstekomponent getTjanstekomponent() {
		return tjanstekomponent;
	}
	
	public void setTjanstekomponent(Tjanstekomponent tjanstekomponent) {
		this.tjanstekomponent = tjanstekomponent;
	}

	public RivTaProfil getRivTaProfil() {
		return rivTaProfil;
	}

	public void setRivTaProfil(RivTaProfil rivTaProfil) {
		this.rivTaProfil = rivTaProfil;
	}
	
	public String getPublishInfo() {
		return toString();
	}
}
