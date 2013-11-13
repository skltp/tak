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
package se.skl.tp.vagval.admin.core.facade;

import java.sql.Date;
import java.util.List;

public class AnropsbehorighetInfo {
	
	// VirtualiseradTjansteproducent
	private long idAnropsbehorighet;
	private Date fromTidpunkt;
	private Date tomTidpunkt;

	// Tjanstekontrakt
	private String namnrymd;
	
	// OrganisatoriskSammanhang
	private String hsaIdLogiskAddresat;

	// Tjanstekomponent
	private String hsaIdTjanstekomponent;
	
	private List<FilterInfo> filterInfos;

	public long getIdAnropsbehorighet() {
		return idAnropsbehorighet;
	}

	public void setIdAnropsbehorighet(long idAnropsbehorighet) {
		this.idAnropsbehorighet = idAnropsbehorighet;
	}

	public Date getFromTidpunkt() {
		return fromTidpunkt;
	}

	public void setFromTidpunkt(Date fromTidpunkt) {
		this.fromTidpunkt = fromTidpunkt;
	}

	public Date getTomTidpunkt() {
		return tomTidpunkt;
	}

	public void setTomTidpunkt(Date tomTidpunkt) {
		this.tomTidpunkt = tomTidpunkt;
	}

	public String getNamnrymd() {
		return namnrymd;
	}

	public void setNamnrymd(String namnrymd) {
		this.namnrymd = namnrymd;
	}

	public String getHsaIdTjanstekomponent() {
		return hsaIdTjanstekomponent;
	}

	public void setHsaIdTjanstekomponent(String hsaIdTjanstekomponent) {
		this.hsaIdTjanstekomponent = hsaIdTjanstekomponent;
	}

	public String getHsaIdLogiskAddresat() {
		return hsaIdLogiskAddresat;
	}

	public void setHsaIdLogiskAddresat(String hsaIdLogiskAddresat) {
		this.hsaIdLogiskAddresat = hsaIdLogiskAddresat;
	}

	public List<FilterInfo> getFilters() {
		return filterInfos;
	}
	
	public void setFilters(List<FilterInfo> filterInfos) {
		this.filterInfos = filterInfos;
	}

}
