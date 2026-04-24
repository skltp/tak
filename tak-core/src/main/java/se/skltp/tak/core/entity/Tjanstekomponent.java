/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.entity;

import se.skltp.tak.core.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

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
