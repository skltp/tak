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

import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.AnropsbehorighetBestallning
import se.skltp.tak.web.jsonBestallning.BestallningsData
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import se.skltp.tak.web.jsonBestallning.VagvalBestallning
import tak.web.I18nService

class ValidatingService {

    I18nService i18nService
    ValidationTagLib validationTagLib;


    List<String> validateExcludeData(JsonBestallning bestallning) {
        List<String> error = new LinkedList<>()
        if (bestallning.getExkludera() != null) {
            if (bestallning.getExkludera().getLogiskadresser() != null ||
                bestallning.getExkludera().getTjanstekomponenter() != null ||
                bestallning.getExkludera().getTjanstekontrakt() != null)
                error.add(i18nService.msg("bestallning.error.faulty.members"))
            }
     return error
    }

    List<String> validateExists(List<Anropsbehorighet> abList, AnropsbehorighetBestallning bestallning) {
        List<String> error = new LinkedList<>()
        if (abList.size() == 0) {
            error.add(i18nService.msg("bestallning.error.saknas.anropsbehorighet", [bestallning.logiskAdress, bestallning.tjanstekonsument, bestallning.tjanstekontrakt]))
        }
        error
    }

    List<String> validateExists(List<Vagval> vvList, VagvalBestallning bestallning) {
        List<String> error = new LinkedList<>()
        if (vvList.size() == 0) {
            error.add(i18nService.msg("bestallning.error.saknas.vagval", [bestallning.logiskAdress, bestallning.tjanstekontrakt, bestallning.tjanstekomponent, bestallning.rivtaprofil]))
        }
        error
    }

    List<String> validateNotNull(VagvalBestallning bestallning) {
        List<String> error = new LinkedList<>()
        def adressString = bestallning.getAdress()
        def rivta = bestallning.getRivtaprofil()
        def komponentHSAId = bestallning.getTjanstekomponent()
        def logiskAdressHSAId = bestallning.getLogiskAdress()
        def kontraktNamnrymd = bestallning.getTjanstekontrakt()

        if (adressString == null || rivta == null || komponentHSAId == null || logiskAdressHSAId == null || kontraktNamnrymd == null) {
            error.add(i18nService.msg("bestallning.error.saknas.info.for.vagval") + " Adress:" + adressString + " Rivta:" + rivta +
                    " Komponent:" + komponentHSAId + " LogiskAdress:" + logiskAdressHSAId + " Kontrakt:" + kontraktNamnrymd + ".")
        }

        error
    }

    List<String> validateNotNull(AnropsbehorighetBestallning bestallning) {
        List<String> error = new LinkedList<>()
        def logiskAdressHSAId = bestallning.getLogiskAdress()
        def komponentHSAId = bestallning.getTjanstekonsument()
        def kontraktNamnrymd = bestallning.getTjanstekontrakt()

        if (logiskAdressHSAId == null || komponentHSAId == null || kontraktNamnrymd == null) {
            error.add(i18nService.msg("bestallning.error.saknas.info.for.anropsbehorighet"))
        }
        error
    }


    List<String> validate(Tjanstekontrakt tjanstekontrakt) {
        List<String> error = new LinkedList<>()

        if (!tjanstekontrakt.validate()) {
            tjanstekontrakt.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.tjanstekontrakt") + validationTagLib.message(error: it))
            }
        }
        error
    }


    List<String> validate(Tjanstekomponent tjanstekomponent) {
        List<String> error = new LinkedList<>()

        if (!tjanstekomponent.validate()) {
            tjanstekomponent.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.tjanstekomponent") + validationTagLib.message(error: it))
            }
        }
        error
    }


    List<String> validate(LogiskAdress logiskAdress) {
        List<String> error = new LinkedList<>()

        if (!logiskAdress.validate()) {
            logiskAdress.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.logiskAdress") + validationTagLib.message(error: it))
            }
        }
        error
    }

    List<String> validate(VagvalBestallning bestallning, RivTaProfil profil, LogiskAdress logiskAdress, Tjanstekomponent tjanstekonsument, Tjanstekontrakt tjanstekontrakt) {
        List<String> error = new LinkedList<>()

        if (!profil) {
            error.add(i18nService.msg("bestallning.error.saknas.rivtaprofil.for.vagval", [bestallning.rivtaprofil]))
        }

        if (!tjanstekonsument) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.vagval", [bestallning.tjanstekomponent]))
        }

        if (!logiskAdress) {
            error.add(i18nService.msg("bestallning.error.saknas.logiskAdress.for.vagval", [bestallning.logiskAdress]))
        }

        if (!tjanstekontrakt) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [bestallning.tjanstekontrakt]))
        }
        error
    }

    public List<String> validate(AnropsbehorighetBestallning bestallning, LogiskAdress logiskAdress, Tjanstekomponent tjanstekonsument, Tjanstekontrakt tjanstekontrakt) {
        List<String> error = new LinkedList<>()

        if (!tjanstekonsument) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [bestallning.tjanstekontrakt]))
        }

        if (!tjanstekontrakt) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [bestallning.tjanstekonsument]))
        }

        if (!logiskAdress) {
            error.add(i18nService.msg("bestallning.error.saknas.logiskAdress.for.anropsbehorighet", [bestallning.logiskAdress]))
        }
        error
    }

    void validateAnropAddress(AnropsAdress adress, BestallningsData data) {
        if (!adress.validate()) {
            adress.errors.allErrors.each() { it ->
                data.addError(i18nService.msg("bestallning.error.for.vagval") + validationTagLib.message(error: it))
            }
        }
    }

    List<String> validateVagvalForDubblett(List<Vagval> list) {
        List<String> error = new LinkedList<>()
        if (list.size() > 1) {
            error.add(i18nService.msg("bestallning.error.dubblett.vagval", [list.get(0).logiskAdress, list.get(0).tjanstekontrakt]))
        }
        return error
    }

    List<String> validateAnropsbehorighetForDubblett(List<Anropsbehorighet> list) {
        List<String> error = new LinkedList<>()
        if (list.size() > 1) {
            error.add(i18nService.msg("bestallning.error.dubblett.anropsbehorighet", [list.get(0).logiskAdress, list.get(0).tjanstekontrakt, list.get(0).tjanstekonsument]))
        }
        return error
    }
}
