/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 					<http://cehis.se/>
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
package se.skltp.tak.web.entity;

import org.apache.shiro.crypto.hash.Sha1Hash;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Anvandare {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	@NotBlank
	@Column(unique = true, nullable = false)
    private String anvandarnamn;

	@Column(name = "losenord_hash")
    private String losenordHash;

	@NotBlank
	@NotNull
	@Transient
	private String losenord;

	private Boolean administrator;

	@Version
	private long version;

	@Override
    public String toString() {
    	return anvandarnamn;
    }

    public void setLosenord(String losenord) {
    	this.losenord = losenord;
    	losenordHash = new Sha1Hash(losenord).toHex();
    }
	public String getLosenordHash() { return losenordHash; }

	public String getAnvandarnamn() { return anvandarnamn; }
	public void setAnvandarnamn(String anvandarnamn) { this.anvandarnamn = anvandarnamn;	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
}
