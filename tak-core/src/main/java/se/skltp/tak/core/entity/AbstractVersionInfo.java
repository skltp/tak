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
/**
 * 
 */
package se.skltp.tak.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author muqkha
 *
 */
@MappedSuperclass
public abstract class AbstractVersionInfo {
	
	@Column(nullable=true, length=255, updatable=true)
	private String pubVersion;
	
	@Column(nullable=true, updatable=true)
	private Date updatedTime;
	
	@Column(nullable=true, length=255, updatable=true)
	private String updatedBy;
	
	@Column(nullable=false, updatable=true, columnDefinition = "boolean default false")
	private Boolean deleted;
	
	public String getPubVersion() {
		return pubVersion;
	}
	public void setPubVersion(String pubVersion) {
		this.pubVersion = pubVersion;
	}
	
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public boolean isNewlyCreated() {
		return (!deleted && pubVersion == null && updatedTime != null);
	}
	
	public boolean isUpdated() {
		return (!deleted && pubVersion != null && updatedTime != null);
	}
	
	public boolean isDeleted() {
		return (deleted && pubVersion != null && updatedTime != null);
	}
	
	public boolean isModified() {
		return (updatedTime != null && updatedBy != null);
	}
}
