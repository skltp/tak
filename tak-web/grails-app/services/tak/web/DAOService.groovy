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

import se.skltp.tak.core.entity.*

class DAOService {

    LogiskAdress getLogiskAdressByHSAId(String hsaId) {
        LogiskAdress adress = LogiskAdress.findByHsaId(hsaId.toUpperCase());
        if (adress == null || adress.getDeleted()) return null
        return adress
    }

    Tjanstekomponent getTjanstekomponentByHSAId(String hsaId) {
        Tjanstekomponent tjanstekomponent = Tjanstekomponent.findByHsaId(hsaId.toUpperCase());
        if (tjanstekomponent == null || tjanstekomponent.getDeleted()) return null
        return tjanstekomponent
    }

    Tjanstekontrakt getTjanstekontraktByNamnrymd(String namnrymd) {
        Tjanstekontrakt tjanstekontrakt = Tjanstekontrakt.findByNamnrymd(namnrymd);
        if (tjanstekontrakt == null || tjanstekontrakt.getDeleted()) return null
        return tjanstekontrakt
    }

    RivTaProfil getRivtaByNamn(String namn) {
        RivTaProfil rivTaProfil = RivTaProfil.findByNamn(namn);
        if (rivTaProfil == null || rivTaProfil.getDeleted()) return null
        return rivTaProfil
    }

    List<Vagval> getVagval(String logisk, String kontrakt, String rivta, String komponent, Date from, Date tom) {
        def vagvalList = Vagval.findAll("from Vagval as vv where " +
                "vv.deleted!=1 and " +
                "vv.logiskAdress.hsaId=:logisk and vv.logiskAdress.deleted!=1 and " +
                "vv.tjanstekontrakt.namnrymd=:kontrakt and vv.tjanstekontrakt.deleted!=1 and " +
                "vv.anropsAdress.deleted!=1 and " +
                "vv.anropsAdress.rivTaProfil.namn=:rivta and vv.anropsAdress.rivTaProfil.deleted != 1 and " +
                "vv.anropsAdress.tjanstekomponent.hsaId=:komponent and vv.anropsAdress.tjanstekomponent.deleted != 1"
                , [logisk: logisk, kontrakt: kontrakt, rivta: rivta, komponent: komponent])


        List<Vagval> vagvals_Without_TidOverlap = vagvalList.findAll { it->
            (from > it.tomTidpunkt) || (tom < it.fromTidpunkt)
        }
        List<Vagval> vagvals_With_TidOverlap = vagvalList.minus(vagvals_Without_TidOverlap)
        return vagvals_With_TidOverlap
    }

    List<Anropsbehorighet> getAnropsbehorighet(String logisk, String konsument, String kontrakt, Date from, Date tom) {
        def anropsbehorighetList = Anropsbehorighet.findAll("from Anropsbehorighet as ab where " +
                "ab.deleted != 1 and " +
                "ab.logiskAdress.hsaId=:logisk and ab.logiskAdress.deleted!=1 and " +
                "ab.tjanstekontrakt.namnrymd=:kontrakt and ab.tjanstekontrakt.deleted!=1 and " +
                "ab.tjanstekonsument.hsaId=:komponent and ab.tjanstekonsument.deleted != 1"
                , [logisk: logisk, kontrakt: kontrakt, komponent: konsument])

        List<Anropsbehorighet> anropsbehorighet_Without_TidOverlap = anropsbehorighetList.findAll { it->
            (from > it.tomTidpunkt) || (tom < it.fromTidpunkt)
        }
        List<Anropsbehorighet> anropsbehorighet_With_TidOverlap = anropsbehorighetList.minus(anropsbehorighet_Without_TidOverlap)

        return anropsbehorighet_With_TidOverlap
    }

    AnropsAdress getAnropsAdress(String rivta, String komponent) {
        List<AnropsAdress> adresses = AnropsAdress.findAll("from AnropsAdress as aa where " +
                "aa.deleted != 1 and " +
                "aa.rivTaProfil.namn=:rivta and aa.rivTaProfil.deleted != 1 and " +
                "aa.tjanstekomponent.hsaId=:komponent and aa.tjanstekomponent.deleted != 1"
                , [rivta: rivta, komponent: komponent])

        if (adresses.size() == 0) return null;
        return adresses.get(0)
    }
}
