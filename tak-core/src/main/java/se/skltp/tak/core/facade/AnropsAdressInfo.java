package se.skltp.tak.core.facade;

import java.util.ArrayList;
import java.util.List;

public class AnropsAdressInfo {

	private String adress;
	private String rivtaProfilNamn;
	private List<VagvalsInfo> vagvalsInfos = new ArrayList<VagvalsInfo>();

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getRivtaProfilNamn() {
		return rivtaProfilNamn;
	}

	public void setRivtaProfilNamn(String rivtaProfilNamn) {
		this.rivtaProfilNamn = rivtaProfilNamn;
	}

	public List<VagvalsInfo> getVagvalsInfos() {
		return vagvalsInfos;
	}

}
