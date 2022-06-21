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

import java.util.function.Function
import org.apache.shiro.SecurityUtils
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*
import tak.web.I18nService

class ValidatingService {

    I18nService i18nService
    ValidationTagLib validationTagLib;


    Set<String> validateDataDubletter(JsonBestallning bestallning) {
        Set<String> errors = new HashSet<>()

        detectDuplicates(
                bestallning.inkludera.anropsbehorigheter,
                bestallning.exkludera.anropsbehorigheter,
                errors,
                "bestallning.error.dubblett.anropsbehorighet.raw",
                ({ duplicatedItem -> [duplicatedItem.logiskAdress, duplicatedItem.tjanstekonsument, duplicatedItem.tjanstekontrakt] }))

        detectDuplicates(
                bestallning.inkludera.vagval,
                bestallning.exkludera.vagval,
                errors,
                "bestallning.error.dubblett.vagval.raw",
                ({ duplicatedItem -> [duplicatedItem.logiskAdress, duplicatedItem.tjanstekontrakt] }))

        detectDuplicates(
                bestallning.inkludera.tjanstekontrakt,
                bestallning.exkludera.tjanstekontrakt,
                errors,
                "bestallning.error.dubblett.kontrakt.raw",
                ({ duplicatedItem -> [duplicatedItem.namnrymd] }))

        detectDuplicates(
                bestallning.inkludera.tjanstekomponenter,
                bestallning.exkludera.tjanstekomponenter,
                errors,
                "bestallning.error.dubblett.komponent.raw",
                ({ duplicatedItem -> [duplicatedItem.hsaId] }))

        detectDuplicates(
                bestallning.inkludera.logiskadresser,
                bestallning.exkludera.logiskadresser,
                errors,
                "bestallning.error.dubblett.logiskAddress.raw",
                ({ duplicatedItem -> [duplicatedItem.hsaId] }))

        return errors
    }

    private <T> void detectDuplicates(
            List<T> includes,
            List<T> excludes,
            Set<String> errors,
            String rawErrorMsg,
            Function<T,List<String>> msgPartsFunc) {

        List<T> allItems = new ArrayList<T>()
        addAllUnlessNull(allItems, includes)
        addAllUnlessNull(allItems, excludes)

        // Note: It does not work to check duplicates by using a hashSet and check for failed inserts
        // (Because the used classes overrides equals() but not hashCode(), so all items must be compared
        // using equals())

        for (int itemIteration = 0; itemIteration < allItems.size(); itemIteration++) {
            for (int cmpIteration = itemIteration + 1; cmpIteration < allItems.size(); cmpIteration) {
                T item = allItems.get(itemIteration)
                T cmpItem = allItems.get(cmpIteration)
                if (item.equals(cmpItem)) {
                    errors.add(i18nService.msg(rawErrorMsg, msgPartsFunc.apply(item)))
                    break;
                }
            }
        }
    }

    private static <T> void addAllUnlessNull(List<T> target, Collection<? extends T> input) {
        if (input != null) {
            target.addAll(input)
        }
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

    Set<String> validateNotEmpty(VagvalBestallning bestallning) {
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

    Set<String> validateNotEmpty(AnropsbehorighetBestallning bestallning) {
        Set<String> error = new HashSet<>()
        def logiskAdressHSAId = bestallning.getLogiskAdress()
        def komponentHSAId = bestallning.getTjanstekonsument()
        def kontraktNamnrymd = bestallning.getTjanstekontrakt()

        if (logiskAdressHSAId == null || komponentHSAId == null || kontraktNamnrymd == null) {
            error.add(i18nService.msg("bestallning.error.saknas.info.for.anropsbehorighet") +
                    " Komponent:" + komponentHSAId + " LogiskAdress:" + logiskAdressHSAId + " Kontrakt:" + kontraktNamnrymd + ".")
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

    Set<String> validateRelatedObjects(VagvalBestallning bestallning, RivTaProfil profil, LogiskAdress logiskAdress, Tjanstekomponent tjanstekonsument, Tjanstekontrakt tjanstekontrakt) {
        def principal = SecurityUtils.getSubject()?.getPrincipal()
        Set<String> error = new HashSet<>()

        if (!profil) {
            error.add(i18nService.msg("bestallning.error.saknas.rivtaprofil.for.vagval", [bestallning.rivtaprofil]))
        } else {
            if (profil.id != 0l && !profil.isPublished() && profil.getUpdatedBy() != principal) {
                error.add(i18nService.msg("bestallning.error.rivtaprofil.not.fit.for.vagval", [profil.namn, profil.getUpdatedBy()]))
            }
        }

        if (!tjanstekonsument) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.vagval", [bestallning.tjanstekomponent]))
        } else {
            if (tjanstekonsument.id != 0l && !tjanstekonsument.isPublished() && tjanstekonsument.getUpdatedBy() != principal) {
                error.add(i18nService.msg("bestallning.error.tjanstekomponent.not.fit.for.vagval", [tjanstekonsument.hsaId, tjanstekonsument.getUpdatedBy()]))
            }
        }

        if (!logiskAdress) {
            error.add(i18nService.msg("bestallning.error.saknas.logiskAdress.for.vagval", [bestallning.logiskAdress]))
        } else {
            if (logiskAdress.id != 0l && !logiskAdress.isPublished() && logiskAdress.getUpdatedBy() != principal) {
                error.add(i18nService.msg("bestallning.error.logiskAdress.not.fit.for.vagval", [logiskAdress.hsaId, logiskAdress.getUpdatedBy()]))
            }
        }

        if (!tjanstekontrakt) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [bestallning.tjanstekontrakt]))
        } else {
            if (tjanstekontrakt.id != 0l && !tjanstekontrakt.isPublished() && tjanstekontrakt.getUpdatedBy() != principal) {
                error.add(i18nService.msg("bestallning.error.tjanstekontrakt.not.fit.for.vagval", [tjanstekontrakt.namnrymd, tjanstekontrakt.getUpdatedBy()]))
            }
        }

        error
    }

     public Set<String> validateRelatedObjects(AnropsbehorighetBestallning bestallning, LogiskAdress logiskAdress, Tjanstekomponent tjanstekonsument, Tjanstekontrakt tjanstekontrakt) {
         def principal = SecurityUtils.getSubject()?.getPrincipal()
        Set<String> error = new HashSet<>()

        if (!tjanstekonsument) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [bestallning.tjanstekonsument]))
        } else {
            if (tjanstekonsument.id != 0l && !tjanstekonsument.isPublished() && tjanstekonsument.getUpdatedBy() != principal) {
                error.add(i18nService.msg("bestallning.error.tjanstekomponent.not.fit.for.anropsbehorighet", [tjanstekonsument.hsaId, tjanstekonsument.getUpdatedBy()]))
            }
        }

        if (!tjanstekontrakt) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [bestallning.tjanstekontrakt]))
        } else {
            if (tjanstekontrakt.id != 0l && !tjanstekontrakt.isPublished() && tjanstekontrakt.getUpdatedBy() != principal) {
                error.add(i18nService.msg("bestallning.error.tjanstekontrakt.not.fit.for.anropsbehorighet", [tjanstekontrakt.namnrymd, tjanstekontrakt.getUpdatedBy()]))
            }
        }

        if (!logiskAdress) {
            error.add(i18nService.msg("bestallning.error.saknas.logiskAdress.for.anropsbehorighet", [bestallning.logiskAdress]))
        }else {
            if (logiskAdress.id != 0l && !logiskAdress.isPublished() && logiskAdress.getUpdatedBy() != principal) {
                error.add(i18nService.msg("bestallning.error.logiskAdress.not.fit.for.anropsbehorighet", [logiskAdress.hsaId, logiskAdress.getUpdatedBy()]))
            }
        }

        error
    }


    Set<String> validateAnropAddress(AnropsAdress adress, BestallningsData data) {
        Set<String> errors = new HashSet<>()

        if (!adress.validate()) {
            adress.errors.allErrors.each() { it ->
                errors.add(i18nService.msg("bestallning.error.for.vagval") + validationTagLib.message(error: it))
            }
        }

        def principal = SecurityUtils.getSubject()?.getPrincipal()
        if (adress.id != 0l && !adress.isPublished() && adress.getUpdatedBy() != principal) {
            errors.add(i18nService.msg("bestallning.error.apropsAdress.not.fit.for.vagval",
                    ["(" + adress.adress + ", " + adress.rivTaProfil + ", " + adress.tjanstekomponent + ")", adress.getUpdatedBy()]))
        }
        return errors
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
        if ((logiskAdress.anropsbehorigheter.any { it -> !it.getDeleted() }))
            error.add(i18nService.msg("bestallning.error.delete.la.ab", [logiskAdress.hsaId]))
        return error
    }

    Set<String> validateTjanstekomponentRelationsForDelete(Tjanstekomponent tjanstekomponent) {
        Set<String> error = new HashSet<>()
        if (tjanstekomponent.anropsAdresser.any { it -> !it.getDeleted() })
            error.add(i18nService.msg("bestallning.error.delete.tjanstekomponent.vagval", [tjanstekomponent.hsaId]))
        if ((tjanstekomponent.anropsbehorigheter.any { it -> !it.getDeleted() }))
            error.add(i18nService.msg("bestallning.error.delete.tjanstekomponent.ab", [tjanstekomponent.hsaId]))
        return error
    }

    Set<String> validateTjanstekontraktForDelete(Tjanstekontrakt tjanstekontrakt) {
        Set<String> error = new HashSet<>()
        if (tjanstekontrakt.vagval.any { it -> !it.getDeleted() })
            error.add(i18nService.msg("bestallning.error.delete.tjanstekontrakt.vagval", [tjanstekontrakt.namnrymd]))
        if ((tjanstekontrakt.anropsbehorigheter.any { it -> !it.getDeleted() }))
            error.add(i18nService.msg("bestallning.error.delete.tjanstekontrakt.ab", [tjanstekontrakt.namnrymd]))
        return error
    }

}
