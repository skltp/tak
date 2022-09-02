/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 *                  <http://cehis.se/>
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
package se.skltp.tak.web.command

import java.sql.Date;
import java.util.List;

import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.AnropsAdress
import se.skltp.tak.core.entity.LogiskAdress
import grails.validation.Validateable

@Validateable
class VagvalBulk {

    Date            fromTidpunkt
    Date            tomTidpunkt

    Tjanstekontrakt tjanstekontrakt
    AnropsAdress    anropsAdress
    
    String          logiskAdressBulk
    List            rejectedLogiskAdress = []
    List            logiskaAdresser = []
    
    /*
[ tjanstekontrakt.id:1, tjanstekontrakt:[id:1], logiskAdress.id:[2, 3, 5], logiskAdress:[id:[2, 3, 5]], fromTidpunkt:Thu Mar 26 00:00:00 CET 2015, create:Skapa, anropsAdress.id:1, anropsAdress:[id:1], fromTidpunkt_day:26, tomTidpunkt:Tue Mar 26 00:00:00 CET 2115, action:save, controller:vagval]
     */   
}
