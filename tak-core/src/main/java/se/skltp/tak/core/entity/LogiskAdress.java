/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.entity;

import se.skltp.tak.core.util.Util;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

@Entity
public class LogiskAdress extends AbstractVersionInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	private String hsaId;

	private String beskrivning;

	@Version
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
