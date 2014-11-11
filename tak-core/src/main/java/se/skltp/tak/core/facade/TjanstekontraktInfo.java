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
package se.skltp.tak.core.facade;

import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.LogiskAdress;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

public class TjanstekontraktInfo {
	
	private String version;
	private String namnrymd;
	private String beskrivning;

	public String toString() {
		return namnrymd; 
	}

	public String getNamnrymd() {
		return namnrymd;
	}
	public void setNamnrymd(String namnrymd) {
		this.namnrymd = namnrymd;
	}


	public String getBeskrivning() {
		return beskrivning;
	}
	public void setBeskrivning(String beskrivning) {
		this.beskrivning = beskrivning;
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
