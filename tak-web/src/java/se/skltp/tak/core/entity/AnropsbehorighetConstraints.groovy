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
	integrationsavtal(blank:false, nullable:false, maxSize: 255)
	tjanstekonsument(nullable:false, unique:['tjanstekontrakt', 'logiskAdress', 'fromTidpunkt', 'tomTidpunkt'])
	tjanstekontrakt(nullable:false)
	logiskAdress(nullable:false)

    
    // --- cloned from VagvalContraints ----------
    
    fromTidpunkt(precision:"day", validator: { val, obj ->
        
        def all = Anropsbehorighet.findAll()
        def a = all.findAll {
            it.tjanstekonsument == obj.tjanstekonsument && it.tjanstekontrakt == obj.tjanstekontrakt && it.logiskAdress == obj.logiskAdress && (
                    (it.fromTidpunkt <= val && val <= it.tomTidpunkt) && (it.id != obj.id)
            )
        }

        if (a.size() > 0) {
            return 'anropsbehorighet.överlappar'
        } else {
            return true
        }
    })
        
    tomTidpunkt(precision:"day", validator: { val, obj ->
        // Don't duplicate 'overlap' error message on the screen
        if (!obj.errors.hasFieldErrors('fromTidpunkt')) {
            def all = Anropsbehorighet.findAll()
            def a = all.findAll {
                it.tjanstekonsument == obj.tjanstekonsument && it.tjanstekontrakt == obj.tjanstekontrakt && it.logiskAdress == obj.logiskAdress && (it.id != obj.id) && (
                    (obj.fromTidpunkt <= it.fromTidpunkt && it.tomTidpunkt <= val)
                     ||
                    (it.fromTidpunkt <= val && val <= it.tomTidpunkt)
                )
            }
            if (a.size() > 0) {
                return 'anropsbehorighet.överlappar'
            }
        }
        
        if (obj.fromTidpunkt > val) {
            return 'anropsbehorighet.tom.innan.from'
        }
        return true
    })
        
}
