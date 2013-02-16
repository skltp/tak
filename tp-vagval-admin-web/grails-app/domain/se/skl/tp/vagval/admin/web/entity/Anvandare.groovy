/**
 * Copyright 2009 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
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
