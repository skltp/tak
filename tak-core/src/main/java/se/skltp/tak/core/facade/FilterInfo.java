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
