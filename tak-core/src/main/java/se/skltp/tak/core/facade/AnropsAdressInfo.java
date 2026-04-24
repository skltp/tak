/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
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
