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

package tak.web

import se.skltp.tak.core.entity.Anropsbehorighet

import java.sql.Date

class DAOService {

    List<Anropsbehorighet> getAnropsbehorighet(String logiskAddressId, String konsumentId, String kontraktId) {

        List<Anropsbehorighet> anropsbehorighetList = Anropsbehorighet.findAll("from Anropsbehorighet as ab where " +
                "ab.deleted=0 and " +
                "ab.logiskAdress.id=:logisk and ab.logiskAdress.deleted=0 and " +
                "ab.tjanstekontrakt.id=:kontrakt and ab.tjanstekontrakt.deleted=0 and " +
                "ab.tjanstekonsument.id=:komponent and ab.tjanstekonsument.deleted=0"
                , [logisk: Long.parseLong(logiskAddressId), kontrakt: Long.parseLong(kontraktId), komponent: Long.parseLong(konsumentId)])

            Date nu = new Date(System.currentTimeMillis())

            List<Anropsbehorighet> anropsbehorighet = anropsbehorighetList.findAll { it ->
                (nu >= it.fromTidpunkt) && (nu <= it.tomTidpunkt)
            }

            return anropsbehorighet
    }
}