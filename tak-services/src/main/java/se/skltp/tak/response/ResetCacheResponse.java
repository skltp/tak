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
package se.skltp.tak.response;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author muqkha
 *
 */
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
