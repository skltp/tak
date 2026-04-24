/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
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
