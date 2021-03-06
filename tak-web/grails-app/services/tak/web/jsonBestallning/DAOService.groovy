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

package tak.web.jsonBestallning

import se.skltp.tak.core.entity.*
import java.sql.Date

class DAOService {

    LogiskAdress getLogiskAdressByHSAId(String hsaId) {
        LogiskAdress adress = LogiskAdress.findByHsaIdAndDeleted(hsaId.toUpperCase(), false);
        return adress
    }

    Tjanstekomponent getTjanstekomponentByHSAId(String hsaId) {
        Tjanstekomponent tjanstekomponent = Tjanstekomponent.findByHsaIdAndDeleted(hsaId.toUpperCase(), false);
        return tjanstekomponent
    }

    Tjanstekontrakt getTjanstekontraktByNamnrymd(String namnrymd) {
        Tjanstekontrakt tjanstekontrakt = Tjanstekontrakt.findByNamnrymdAndDeleted(namnrymd, false);
        return tjanstekontrakt
    }

    RivTaProfil getRivtaByNamn(String namn) {
        RivTaProfil rivTaProfil = RivTaProfil.findByNamnAndDeleted(namn, false);
        return rivTaProfil
    }

    List<Vagval> getVagval(String logisk, String kontrakt, String rivta, String komponent, Date from, Date tom) {
        def vagvalList = getVagval(logisk, kontrakt, rivta, komponent)
        List<Vagval> vagvals_Without_TidOverlap = vagvalList.findAll { it ->
            (from > it.tomTidpunkt) || (tom < it.fromTidpunkt)
        }
        List<Vagval> vagvals_With_TidOverlap = vagvalList.minus(vagvals_Without_TidOverlap)
        return vagvals_With_TidOverlap
    }

    List<Vagval> getVagval(String logisk, String kontrakt, String rivta, String komponent) {
        def vagvalList = Vagval.findAll("from Vagval as vv where " +
                "vv.deleted=0 and " +
                "vv.logiskAdress.hsaId=:logisk and vv.logiskAdress.deleted=0 and " +
                "vv.tjanstekontrakt.namnrymd=:kontrakt and vv.tjanstekontrakt.deleted=0 and " +
                "vv.anropsAdress.deleted=0 and " +
                "vv.anropsAdress.rivTaProfil.namn=:rivta and vv.anropsAdress.rivTaProfil.deleted=0 and " +
                "vv.anropsAdress.tjanstekomponent.hsaId=:komponent and vv.anropsAdress.tjanstekomponent.deleted=0"
                , [logisk: logisk, kontrakt: kontrakt, rivta: rivta, komponent: komponent])
        return vagvalList
    }

    List<Vagval> getVagval(String logisk, String kontrakt, Date from, Date tom) {
        def vagvalList = getVagval(logisk, kontrakt)
        List<Vagval> vagvals_Without_TidOverlap = vagvalList.findAll { it ->
            (from > it.tomTidpunkt) || (tom < it.fromTidpunkt)
        }
        List<Vagval> vagvals_With_TidOverlap = vagvalList.minus(vagvals_Without_TidOverlap)
        return vagvals_With_TidOverlap
    }

    List<Vagval> getVagval(String logisk, String kontrakt) {
        def vagvalList = Vagval.findAll("from Vagval as vv where " +
                "vv.deleted=0 and " +
                "vv.logiskAdress.hsaId=:logisk and vv.logiskAdress.deleted=0 and " +
                "vv.tjanstekontrakt.namnrymd=:kontrakt and vv.tjanstekontrakt.deleted=0"
                , [logisk: logisk, kontrakt: kontrakt])
        return vagvalList
    }

    List<Anropsbehorighet> getAnropsbehorighet(String logisk, String konsument, String kontrakt, Date from, Date tom) {
        def anropsbehorighetList = getAnropsbehorighet(logisk, konsument, kontrakt)

        List<Anropsbehorighet> anropsbehorighet_Without_TidOverlap = anropsbehorighetList.findAll { it ->
            (from > it.tomTidpunkt) || (tom < it.fromTidpunkt)
        }
        List<Anropsbehorighet> anropsbehorighet_With_TidOverlap = anropsbehorighetList.minus(anropsbehorighet_Without_TidOverlap)
        return anropsbehorighet_With_TidOverlap
    }

    List<Anropsbehorighet> getAnropsbehorighet(String logisk, String konsument, String kontrakt) {
        def anropsbehorighetList = Anropsbehorighet.findAll("from Anropsbehorighet as ab where " +
                "ab.deleted=0 and " +
                "ab.logiskAdress.hsaId=:logisk and ab.logiskAdress.deleted=0 and " +
                "ab.tjanstekontrakt.namnrymd=:kontrakt and ab.tjanstekontrakt.deleted=0 and " +
                "ab.tjanstekonsument.hsaId=:komponent and ab.tjanstekonsument.deleted=0"
                , [logisk: logisk, kontrakt: kontrakt, komponent: konsument])
        return anropsbehorighetList
    }


    List<Anropsbehorighet> getAktuellaAnropsbehorigheter(String logiskAddressId, String konsumentId, String kontraktId) {

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


    AnropsAdress getAnropsAdress(String rivta, String komponent, String url) {
        List<AnropsAdress> adresses = AnropsAdress.findAll("from AnropsAdress as aa where " +
                "aa.deleted=0 and " +
                "aa.adress =:adress and " +
                "aa.rivTaProfil.namn=:rivta and aa.rivTaProfil.deleted=0 and " +
                "aa.tjanstekomponent.hsaId=:komponent and aa.tjanstekomponent.deleted=0"
                , [rivta: rivta, komponent: komponent, adress: url])

        if (adresses.size() == 0) return null;
        return adresses.get(0)
    }
}
