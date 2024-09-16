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
