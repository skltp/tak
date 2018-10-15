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

import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt

class DAOService {

    LogiskAdress getLogiskAdressByHSAId(String hsaId){
        LogiskAdress adress = LogiskAdress.findByHsaId(hsaId.toUpperCase());
        if (adress == null || adress.getDeleted()) return null
        return adress
    }

    Tjanstekomponent getTjanstekomponentByHSAId(String hsaId){
        Tjanstekomponent tjanstekomponent = Tjanstekomponent.findByHsaId(hsaId.toUpperCase());
        if (tjanstekomponent == null || tjanstekomponent.getDeleted()) return null
        return tjanstekomponent
    }

    Tjanstekontrakt getTjanstekontraktByNamnrymd(String namnrymd){
        Tjanstekontrakt tjanstekontrakt = Tjanstekontrakt.findByNamnrymd(namnrymd);
        if (tjanstekontrakt == null || tjanstekontrakt.getDeleted()) return null
        return tjanstekontrakt
    }
}
