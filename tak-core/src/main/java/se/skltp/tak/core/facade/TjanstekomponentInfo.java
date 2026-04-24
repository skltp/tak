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
