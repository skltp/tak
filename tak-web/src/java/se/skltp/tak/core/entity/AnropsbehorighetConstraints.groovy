/**
 * Copyright (c) 2013 Center för eHälsa i samverkan (CeHis).
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
package se.skltp.tak.core.entity;

constraints = {
	integrationsavtal(blank:false, nullable:false, maxSize: 255, validator: { val, obj ->
		
		if (val?.startsWith(" ")) {
			return 'invalid.leadingspace'
		}
		if (val?.startsWith("\t")) {
			return 'invalid.leadingtab'
		}
		if (val?.endsWith(" ")) {
			return 'invalid.trailingspace'
		}
		if (val?.endsWith("\t")) {
			return 'invalid.trailingtab'
		}
        
        return true
    })
	
	tjanstekonsument(nullable:false, unique:['tjanstekontrakt', 'logiskAdress', 'fromTidpunkt', 'tomTidpunkt', 'deleted'])
	tjanstekontrakt(nullable:false)
	logiskAdress(nullable:false)

    
    // --- cloned from VagvalContraints ----------
    
    fromTidpunkt(precision:"day", validator: { val, obj ->


		if (obj.fromTidpunkt > obj.tomTidpunkt) {
			return 'anropsbehorighet.tom.innan.from'
		}

		Anropsbehorighet.withNewSession {
			if (obj.logiskAdress.id != 0 && obj.tjanstekontrakt.id != 0 && obj.tjanstekonsument.id != 0) {
				def all = Anropsbehorighet.findAll("from Anropsbehorighet as a where " +
						"a.deleted=:deleted and " +
						"a.logiskAdress.id =:logiskadressid and " +
						"a.tjanstekontrakt.id =:tjanstekontraktid and " +
						"a.tjanstekonsument.id =:tjanstekonsumentId"
						, [deleted: false, logiskadressid: obj.logiskAdress.id, tjanstekontraktid: obj.tjanstekontrakt.id, tjanstekonsumentId: obj.tjanstekonsument.id])

				List<Anropsbehorighet> anropsbehorighetList = all.findAll { (it.id != obj.id) }
				List<Anropsbehorighet> anropsbehorighet_Without_TidOverlap = anropsbehorighetList.findAll {
					(obj.fromTidpunkt >= it.tomTidpunkt) && (obj.tomTidpunkt >= it.fromTidpunkt)
				}
				List<Anropsbehorighet> anropsbehorighet_With_TidOverlap = anropsbehorighetList.minus(anropsbehorighet_Without_TidOverlap)

				if (anropsbehorighet_With_TidOverlap.size() > 0) {
					return 'anropsbehorighet.overlappar'
				} else {
					return true
				}
			}
			return true
		}
    })
}
