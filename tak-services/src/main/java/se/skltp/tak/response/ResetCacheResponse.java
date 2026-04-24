/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
/**
 * 
 */
package se.skltp.tak.response;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ResetCacheResponse {
	
	private STATUS status;
	private String message;
	private long currentVersion;
	
	public enum STATUS {
		OK, 
		ERROR
	};
	
	public enum SERVICES {
		ANROPSBEHORIGHT,
		VIRTUALISERING,
		TJANSTEKONTRAKT,
		TJANSTEKOMPONENT
	};
	
	@XmlElement(name="SERVICES")
	public SERVICES[] getEnumServices() {
		return SERVICES.values();
	}
	
	private Map<SERVICES, Integer> servicesList = new HashMap<SERVICES, Integer>();
	
	@XmlElement(name="STATUS")
	public STATUS[] getEnumStatus() {
		return STATUS.values();
	}
	
	public STATUS getStatus() {
		return status;
	}
	public void setStatus(STATUS status) {
		this.status = status;
	}
	
	public long getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(long currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@XmlElementWrapper(name="services")
	public Map<SERVICES, Integer> getServicesList() {
		return servicesList;
	}	
	public void setServicesList(SERVICES service, int size) {
		this.servicesList.put(service, size);
	}
	
}
