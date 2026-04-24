/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

@Entity
public class Filter extends AbstractVersionInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Version
	private long version;
	
	private String servicedomain;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "filter")
	private List<Filtercategorization> categorization = new ArrayList<Filtercategorization>();

	@ManyToOne(optional = false)
	private Anropsbehorighet anropsbehorighet;
	
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

	public String getServicedomain() {
		return servicedomain;
	}

	public void setServicedomain(String servicedomain) {
		this.servicedomain = servicedomain;
	}

	public List<Filtercategorization> getCategorization() {
		return categorization;
	}

	public void setCategorization(List<Filtercategorization> categorization) {
		this.categorization = categorization;
	}

	public Anropsbehorighet getAnropsbehorighet() {
		return anropsbehorighet;
	}

	public void setAnropsbehorighet(Anropsbehorighet anropsbehorighet) {
		this.anropsbehorighet = anropsbehorighet;
	}
	
	@Override
	public String toString() {
		return Long.toString(id) + "-" + servicedomain; 
	}
	
	public String getPublishInfo() {
		return toString();
	}
}
