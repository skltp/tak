package se.skltp.tak.core.facade;

import java.util.ArrayList;
import java.util.List;

public class TjanstekomponentInfo {

	private String hsaId;
	private String beskrivning;
	private List<AnropsAdressInfo> anropsAdressInfos = new ArrayList<AnropsAdressInfo>();
	private List<AnropsbehorighetInfo> anropsbehorighetInfos = new ArrayList<AnropsbehorighetInfo>();

	public String getHsaId() {
		return hsaId;
	}

	public void setHsaId(String hsaId) {
		this.hsaId = hsaId;
	}

	public String getBeskrivning() {
		return beskrivning;
	}

	public void setBeskrivning(String beskrivning) {
		this.beskrivning = beskrivning;
	}

	public List<AnropsAdressInfo> getAnropsAdressInfos() {
		return anropsAdressInfos;
	}

	public List<AnropsbehorighetInfo> getAnropsbehorighetInfos() {
		return anropsbehorighetInfos;
	}

}
