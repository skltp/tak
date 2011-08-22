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
import se.skl.tp.vagval.admin.web.entity.*

class BootStrap {

     def init = { servletContext ->
        switch(grails.util.GrailsUtil.environment) {
        case "development":
        case "test":
        	setupTestData()
			setupUsers()
			break
        }
     }
     def destroy = {
     }
     
     def setupTestData() {
     	def rivVersion = new se.skl.tp.vagval.admin.core.entity.RivVersion()
     	rivVersion.beskrivning = "Riv 1.0"
		rivVersion.namn = "1.0"
		rivVersion.save()
		def kontrakt = new se.skl.tp.vagval.admin.core.entity.Tjanstekontrakt()
     	kontrakt.namnrymd = "http://x.y.z"
		kontrakt.beskrivning = "Tjänst XYZ"
		kontrakt.save()
		def tjansteKomponent = new se.skl.tp.vagval.admin.core.entity.Tjanstekomponent()
     	tjansteKomponent.hsaId = "Komponent 1"
     	tjansteKomponent.adress = "Address 1"
     	tjansteKomponent.beskrivning = "Komponent 1"
     	tjansteKomponent.save()
		def logiskAdressat = new se.skl.tp.vagval.admin.core.entity.LogiskAdressat()
     	logiskAdressat.hsaId = "Addressat 1"
     	logiskAdressat.beskrivning = "Addressat 1"
     	logiskAdressat.save()     	
		def logiskAdress = new se.skl.tp.vagval.admin.core.entity.LogiskAdress()
     	logiskAdress.fromTidpunkt = new java.sql.Date(System.currentTimeMillis())
     	logiskAdress.tomTidpunkt = new java.sql.Date(System.currentTimeMillis())
     	logiskAdress.rivVersion = rivVersion
     	logiskAdress.tjanstekontrakt = kontrakt
     	logiskAdress.logiskAdressat = logiskAdressat
     	logiskAdress.tjansteproducent = tjansteKomponent
     	logiskAdress.save()     	
		def anropsbehorighet = new se.skl.tp.vagval.admin.core.entity.Anropsbehorighet()
     	anropsbehorighet.fromTidpunkt = new java.sql.Date(System.currentTimeMillis())
     	anropsbehorighet.tomTidpunkt = new java.sql.Date(System.currentTimeMillis())
     	anropsbehorighet.integrationsavtal = "Avtal 1"
     	anropsbehorighet.tjanstekontrakt = kontrakt
     	anropsbehorighet.logiskAdressat = logiskAdressat
     	anropsbehorighet.tjanstekonsument = tjansteKomponent
     	anropsbehorighet.save()     	
     }
     
     def setupUsers() {
    	 // Administrators
    	 def adminUser = new Anvandare(anvandarnamn: "admin", losenord: "admin", administrator: true).save()
		 //	Users.
		 def normalUser = new Anvandare(anvandarnamn: "bbe", losenord: "bbe", administrator: false).save()
     }
}