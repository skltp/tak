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
package se.skl.tp.vagval.admin.web.entity

import org.apache.shiro.crypto.hash.Sha1Hash

class Anvandare {
    String anvandarnamn
    String losenordHash
	String losenord
	Boolean administrator
	
    static transients = ['losenord']
    
    static constraints = {
    	anvandarnamn(nullable: false, blank: false, unique: true)
		losenord(nullable: false, blank: false)
    }

	static mapping = {
		table "anvandare" //detta direktiv finns p.g.a historiska skäl och bör tas bort när tabellnamnet väl ändras till Anvandare se jira SKLTP-322
		losenordHash column:'losenord_hash'
	}

    String toString() {
    	anvandarnamn
    }
    
    void setLosenord(String losenord) {
    	this.losenord = losenord
    	losenordHash = new Sha1Hash(losenord).toHex()
    }
}
