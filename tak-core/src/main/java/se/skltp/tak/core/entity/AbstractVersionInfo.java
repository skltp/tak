/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class AbstractVersionInfo {
	
	@Column(nullable=true, length=255, updatable=true)
	private String pubVersion;
	
	@Column(nullable=true, updatable=true)
	private Date updatedTime;
	
	@Column(nullable=true, length=255, updatable=true)
	private String updatedBy;
	
	@Column(nullable=true, updatable=true)
	private Boolean deleted = Boolean.valueOf(false);
	
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
	
	//To allow multiple deleted items, deleted=null is equal to true
	public Boolean getDeleted() {
		return ((deleted == null) ? true : false); //false;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	/**
	 * Only for unpublished row
	 * @return
	 */
	public boolean isNewlyCreated() {
		return (!getDeleted() && !isPublished() && isModified());
	}
	
	/**
	 * Only for unpublished row
	 * @return
	 */
	public boolean isUpdatedAfterPublishedVersion() {
		return (!getDeleted() && isPublished() && isModified());
	}
	
	/**
	 * Only for unpublished row
	 * @return
	 */
	public boolean isDeletedAfterPublishedVersion() {
		return (getDeleted() && isPublished() && isModified());
	}
	
	/**
	 * Checks if a row is deleted and published, or deleted by a specific user
	 * after it was published.
	 * 
	 * @param username
	 * @return
	 */
	public boolean isDeletedInPublishedVersionOrByUser(String username) {
		return (getDeleted() && isPublished() && (updatedBy == null || updatedBy.equals(username)));
	}
	
	public boolean isDeletedInPublishedVersion() {
		return (getDeleted() && isPublished() && !isModified());
	}

    public boolean isModified() {
        return updatedBy != null;
    }

	public boolean isPublished(){
		return pubVersion != null;
	}

	public abstract String getPublishInfo();
}
