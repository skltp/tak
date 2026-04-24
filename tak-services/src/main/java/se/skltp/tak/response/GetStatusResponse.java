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
