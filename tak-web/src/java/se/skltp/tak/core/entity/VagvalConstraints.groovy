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

import org.apache.log4j.*

import groovy.util.logging.*

/**
 *  Grails guide
 *  19.3 Adding Constraints
 *
 * You can still use GORM validation even if you use a Java domain model. Grails lets you define constraints through 
 * separate scripts in the src/java directory. The script must be in a directory that matches the package of the corresponding 
 * domain class and its name must have a Constraints suffix. 
 * For example, if you had a domain class 
 *   org.example.Book, 
 * then you would create the script 
 *   src/java/org/example/BookConstraints.groovy. 
 *   
 *   
 * @see http://spring.io/blog/2010/08/26/reuse-your-hibernate-jpa-domain-model-with-grails  
 */

constraints = {
    tjanstekontrakt(nullable:false)
    logiskAdress(nullable:false)
    anropsAdress(nullable:false, unique:['tjanstekontrakt', 'logiskAdress', 'fromTidpunkt', 'tomTidpunkt', 'deleted'])

    fromTidpunkt(precision:"day", validator: { val, obj ->

        /*
         TODO - don't know why this doesn't work - always returns all Vagval  
         int numberOfOverlaps = Vagval.where {
            tjanstekontrakt == obj.tjanstekontrakt && logiskAdress == obj.logiskAdress && anropsAdress == obj.anropsAdress 
         }.count()
         */

        // However, this works instead
        Vagval.withNewSession {
			def all = Vagval.findAllByDeleted(false)
	        def v = all.findAll {
	            (it.tjanstekontrakt.id == obj.tjanstekontrakt.id) && (it.logiskAdress.id == obj.logiskAdress.id) && 
					(it.fromTidpunkt <= val && val <= it.tomTidpunkt) && (it.id != obj.id)
	        }
	
	        if (v.size() > 0) {
	            return 'vagval.överlappar'
	        } else {
	            return true
	        }
        }
    })

    tomTidpunkt(precision:"day", validator: { val, obj ->
        // Don't duplicate 'overlap' error message on the scree n
		Vagval.withNewSession {
		    if (!obj.errors.hasFieldErrors('fromTidpunkt')) {
				def all = Vagval.findAllByDeleted(false)
	            def v = all.findAll {
	                (it.tjanstekontrakt.id == obj.tjanstekontrakt.id) && (it.logiskAdress.id == obj.logiskAdress.id) && (it.id != obj.id) && 
						((obj.fromTidpunkt <= it.fromTidpunkt && it.tomTidpunkt <= val) || (it.fromTidpunkt <= val && val <= it.tomTidpunkt))
	            }
				
	            if (v.size() > 0) {
	                return 'vagval.överlappar'
	            }
	        }
	        
	        if (obj.fromTidpunkt > val) {
	            return 'vagval.tom.innan.from'
	        }
	        return true
		}
    })
}
