/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.facade;

import java.util.List;

public class FilterInfo {

	private String servicedomain;

	private List<String> filterCategorizations;
	
	public String getServicedomain() {
		return servicedomain;
	}

	public void setServicedomain(String servicedomain) {
		this.servicedomain = servicedomain;
	}

	public List<String> getFilterCategorizations() {
		return filterCategorizations;
	}
	
	public void setFilterCategorizations(List<String> filterCategorizations) {
		this.filterCategorizations = filterCategorizations;
	}
	
}
