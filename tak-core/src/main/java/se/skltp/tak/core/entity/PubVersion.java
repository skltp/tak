/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.entity;

import jakarta.persistence.*;

import java.sql.Blob;
import java.util.Date;

@Entity
public class PubVersion {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;	
		
	private long formatVersion;
	
	private Date time;
	
	private String utforare;
	
	private String kommentar;

	@Version
	private long version;

	@Lob
	private Blob data;
	
	private long storlek;
	
	@Override
	public String toString() {
		return Long.toString(id, 10); 
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFormatVersion() {
		return formatVersion;
	}

	public void setFormatVersion(long formatVersion) {
		this.formatVersion = formatVersion;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getUtforare() {
		return utforare;
	}

	public void setUtforare(String utforare) {
		this.utforare = utforare;
	}

	public String getKommentar() {
		return kommentar;
	}

	public void setKommentar(String kommentar) {
		this.kommentar = kommentar;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

	public long getStorlek() {
		return storlek;
	}

	public void setStorlek(long storlek) {
		this.storlek = storlek;
	}
	
}
