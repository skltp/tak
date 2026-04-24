/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.response;

import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.*;


@XmlRootElement
public class GetStatusResponse {
	
	private String message;
	private Map<String, String> appInfoList = new HashMap<>();

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@XmlElementWrapper(name="appinfo")
	public Map<String, String> getAppInfoList() {
		return appInfoList;
	}	
	public void setAppInfoList(String app, String info) {
		this.appInfoList.put(app, info);
	}
}
