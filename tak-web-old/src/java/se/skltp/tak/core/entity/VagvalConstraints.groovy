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
package se.skltp.tak.core.entity
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
    tjanstekontrakt(nullable: false)
    logiskAdress(nullable: false)
    anropsAdress(nullable: false, unique: ['tjanstekontrakt', 'logiskAdress', 'fromTidpunkt', 'tomTidpunkt', 'deleted'])

    fromTidpunkt(precision: "day", validator: { val, obj ->

        if (obj.fromTidpunkt > obj.tomTidpunkt) {
            return 'vagval.tom.innan.from'
        }

        Vagval.withNewSession {
            if (obj.logiskAdress.id != 0 && obj.tjanstekontrakt.id != 0 && obj.anropsAdress.id != 0) {
                def all = Vagval.findAll("from Vagval as v where " +
                        "v.deleted=:deleted and " +
                        "v.logiskAdress.id =:logiskadressid and " +
                        "v.tjanstekontrakt.id =:tjanstekontraktid"
                        , [deleted: false, logiskadressid: obj.logiskAdress.id, tjanstekontraktid: obj.tjanstekontrakt.id])

                List<Vagval> vagvalList = all.findAll { (it.id != obj.id) }
                List<Vagval> vagvals_Without_TidOverlap = vagvalList.findAll {
                    (obj.fromTidpunkt > it.tomTidpunkt) || (obj.tomTidpunkt < it.fromTidpunkt)
                }
                List<Vagval> vagvals_With_TidOverlap = vagvalList.minus(vagvals_Without_TidOverlap)

                if (vagvals_With_TidOverlap.size() > 0) {
                    return 'vagval.overlappar'
                } else {
                    return true
                }
            }
            return true
        }
    })
}
