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
import se.skltp.tak.web.jsonBestallning.*
import tak.web.I18nService

class ValidatingService {

    I18nService i18nService
    ValidationTagLib validationTagLib;


    Set<String> validateDataDubletter(JsonBestallning bestallning) {
        Set<String> error = new HashSet<>()

        List<AnropsbehorighetBestallning> ab = new ArrayList<AnropsbehorighetBestallning>()
        ab.addAll(bestallning.inkludera.anropsbehorigheter)
        ab.addAll(bestallning.exkludera.anropsbehorigheter)

        for (int i = 0; i < ab.size(); i++) {
            for (int j = i + 1; j < ab.size(); j++) {
                if(ab.get(i).equals(ab.get(j))){
                    error.add(
                            i18nService.msg("bestallning.error.dubblett.anropsbehorighet.raw",
                                    [ab.get(i).logiskAdress, ab.get(i).tjanstekonsument, ab.get(i).tjanstekontrakt]))
                }
            }
        }

        List<VagvalBestallning> vv = new ArrayList<VagvalBestallning>();
        vv.addAll(bestallning.inkludera.vagval)
        vv.addAll(bestallning.exkludera.vagval)

        for (int i = 0; i < vv.size(); i++) {
            for (int j = i + 1; j < vv.size(); j++) {
                if(vv.get(i).equals(vv.get(j))){
                    error.add(
                            i18nService.msg("bestallning.error.dubblett.vagval.raw",
                                    [vv.get(i).logiskAdress, vv.get(i).tjanstekontrakt]))
                }
            }
        }


        List<TjanstekontraktBestallning> tjK = new ArrayList<TjanstekontraktBestallning>();
        tjK.addAll(bestallning.inkludera.tjanstekontrakt)
        tjK.addAll(bestallning.exkludera.tjanstekontrakt)
        for (int i = 0; i < tjK.size(); i++) {
            for (int j = i + 1; j < tjK.size(); j++) {
                if(tjK.get(i).equals(tjK.get(j))){
                    error.add(
                            i18nService.msg("bestallning.error.dubblett.kontrakt.raw",
                                    [tjK.get(i).namnrymd]))
                }
            }
        }

        List<TjanstekomponentBestallning> tjKomp = new ArrayList<TjanstekomponentBestallning>()
        tjKomp.addAll(bestallning.inkludera.tjanstekomponenter)
        tjKomp.addAll(bestallning.exkludera.tjanstekomponenter)
        for (int i = 0; i < tjKomp.size(); i++) {
            for (int j = i + 1; j < tjKomp.size(); j++) {
                if(tjKomp.get(i).equals(tjKomp.get(j))){
                    error.add(
                            i18nService.msg("bestallning.error.dubblett.komponent.raw",
                                    [tjKomp.get(i).hsaId]))
                }
            }
        }

        List<LogiskadressBestallning> la = new ArrayList<LogiskadressBestallning>()
        la.addAll(bestallning.inkludera.logiskadresser)
        la.addAll(bestallning.exkludera.logiskadresser)
        for (int i = 0; i < la.size(); i++) {
            for (int j = i + 1; j < la.size(); j++) {
                if(la.get(i).equals(la.get(j))){
                    error.add(
                            i18nService.msg("bestallning.error.dubblett.logiskAddress.raw",
                                    [la.get(i).hsaId]))
                }
            }
        }
        return error
    }

    Set<String> validateExists(List<Anropsbehorighet> abList, AnropsbehorighetBestallning bestallning) {
        Set<String> error = new HashSet<>()
        if (abList.size() == 0) {
            error.add(i18nService.msg("bestallning.error.saknas.anropsbehorighet", [bestallning.logiskAdress, bestallning.tjanstekonsument, bestallning.tjanstekontrakt]))
        }
        error
    }

    Set<String> validateExists(List<Vagval> vvList, VagvalBestallning bestallning) {
        Set<String> error = new HashSet<>()
        if (vvList.size() == 0) {
            error.add(i18nService.msg("bestallning.error.saknas.vagval", [bestallning.logiskAdress, bestallning.tjanstekontrakt, bestallning.tjanstekomponent, bestallning.rivtaprofil]))
        }
        error
    }

    Set<String> validateNotNull(VagvalBestallning bestallning) {
        Set<String> error = new HashSet<>()
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

    Set<String> validateNotNull(AnropsbehorighetBestallning bestallning) {
        Set<String> error = new HashSet<>()
        def logiskAdressHSAId = bestallning.getLogiskAdress()
        def komponentHSAId = bestallning.getTjanstekonsument()
        def kontraktNamnrymd = bestallning.getTjanstekontrakt()

        if (logiskAdressHSAId == null || komponentHSAId == null || kontraktNamnrymd == null) {
            error.add(i18nService.msg("bestallning.error.saknas.info.for.anropsbehorighet"))
        }
        error
    }


    Set<String> validate(Tjanstekontrakt tjanstekontrakt) {
        Set<String> error = new HashSet<>()

        if (!tjanstekontrakt.validate()) {
            tjanstekontrakt.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.tjanstekontrakt") + validationTagLib.message(error: it))
            }
        }
        error
    }


    Set<String> validate(Tjanstekomponent tjanstekomponent) {
        Set<String> error = new HashSet<>()

        if (!tjanstekomponent.validate()) {
            tjanstekomponent.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.tjanstekomponent") + validationTagLib.message(error: it))
            }
        }
        error
    }


    Set<String> validate(LogiskAdress logiskAdress) {
        Set<String> error = new HashSet<>()

        if (!logiskAdress.validate()) {
            logiskAdress.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.logiskAdress") + validationTagLib.message(error: it))
            }
        }
        error
    }

    Set<String> validate(VagvalBestallning bestallning, RivTaProfil profil, LogiskAdress logiskAdress, Tjanstekomponent tjanstekonsument, Tjanstekontrakt tjanstekontrakt) {
        Set<String> error = new HashSet<>()

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

    public Set<String> validate(AnropsbehorighetBestallning bestallning, LogiskAdress logiskAdress, Tjanstekomponent tjanstekonsument, Tjanstekontrakt tjanstekontrakt) {
        Set<String> error = new HashSet<>()

        if (!tjanstekonsument) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [bestallning.tjanstekontrakt]))
        }

        if (!tjanstekontrakt) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [bestallning.tjanstekonsument]))
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

    Set<String> validateVagvalForDubblett(List<Vagval> list) {
        Set<String> error = new HashSet<>()
        if (list.size() > 1) {
            error.add(i18nService.msg("bestallning.error.dubblett.vagval", [list.get(0).logiskAdress, list.get(0).tjanstekontrakt]))
        }
        return error
    }

    Set<String> validateAnropsbehorighetForDubblett(List<Anropsbehorighet> list) {
        Set<String> error = new HashSet<>()
        if (list.size() > 1) {
            error.add(i18nService.msg("bestallning.error.dubblett.anropsbehorighet", [list.get(0).logiskAdress, list.get(0).tjanstekontrakt, list.get(0).tjanstekonsument]))
        }
        return error
    }

    Set<String> validateLogiskAdressRelationsForDelete(LogiskAdress logiskAdress) {
        Set<String> error = new HashSet<>()
        if (logiskAdress.vagval.any { it -> !it.getDeleted() })
            error.add(i18nService.msg("bestallning.error.delete.la.vagval", [logiskAdress.hsaId]))
        if((logiskAdress.anropsbehorigheter.any { it -> !it.getDeleted() }))
            error.add(i18nService.msg("bestallning.error.delete.la.ab", [logiskAdress.hsaId]))
        return error
    }

    Set<String> validateTjanstekomponentRelationsForDelete(Tjanstekomponent tjanstekomponent) {
        Set<String> error = new HashSet<>()
        if (tjanstekomponent.anropsAdresser.any { it -> !it.getDeleted() })
            error.add(i18nService.msg("bestallning.error.delete.tjanstekomponent.vagval", [tjanstekomponent.hsaId]))
        if((tjanstekomponent.anropsbehorigheter.any { it -> !it.getDeleted() }))
            error.add(i18nService.msg("bestallning.error.delete.tjanstekomponent.ab", [tjanstekomponent.hsaId]))
        return error
    }

    Set<String> validateTjanstekontraktForDelete(Tjanstekontrakt tjanstekontrakt) {
        Set<String> error = new HashSet<>()
        if (tjanstekontrakt.vagval.any { it -> !it.getDeleted() })
            error.add(i18nService.msg("bestallning.error.delete.tjanstekontrakt.vagval", [tjanstekontrakt.namnrymd]))
        if((tjanstekontrakt.anropsbehorigheter.any { it -> !it.getDeleted() }))
            error.add(i18nService.msg("bestallning.error.delete.tjanstekontrakt.ab", [tjanstekontrakt.namnrymd]))
        return error
    }
}
