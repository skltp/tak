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

package jsonBestallning

import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.web.jsonBestallning.*
import tak.web.I18nService

class ValidatingService {

    I18nService i18nService
    ValidationTagLib validationTagLib;

    void validatePlainObjects(JsonBestallning bestallning) {
        bestallning.getExkludera().getVagval().each() { it ->
            bestallning.addInfo(validateForDelete(it))
        }
        bestallning.getExkludera().getAnropsbehorigheter().each() { it ->
            bestallning.addInfo(validateForDelete(it))
        }

        bestallning.getInkludera().getLogiskadresser().each() { logiskadressBestallning ->
            bestallning.addError(validate(logiskadressBestallning))
        }
        bestallning.getInkludera().getTjanstekomponenter().each() { tjanstekomponentBestallning ->
            bestallning.addError(validate(tjanstekomponentBestallning))
        }
        bestallning.getInkludera().getTjanstekontrakt().each() { tjanstekontraktBestallning ->
            bestallning.addError(validate(tjanstekontraktBestallning))
        }

        bestallning.getInkludera().getAnropsbehorigheter().each() { anropsbehorighetBestallning ->
            bestallning.addError(validateNotNull(anropsbehorighetBestallning))
        }
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            bestallning.addError(validateNotNull(vagvalBestallning))
        }

    }

    private List<String> validateForDelete(AnropsbehorighetBestallning bestallning) {
        List<String> error = new LinkedList<>()
        if (bestallning.aropsbehorigheterForDelete.size() == 0) {
            error.add(i18nService.msg("bestallning.error.saknas.anropsbehorighet", [bestallning.logiskAdress, bestallning.tjanstekonsument, bestallning.tjanstekontrakt]))
        }
        error
    }

    private List<String> validateForDelete(VagvalBestallning bestallning) {
        List<String> error = new LinkedList<>()
        if (bestallning.vagvalForDelete.size() == 0) {
            error.add(i18nService.msg("bestallning.error.saknas.vagval", [bestallning.logiskAdress, bestallning.tjanstekontrakt, bestallning.tjanstekomponent, bestallning.rivtaprofil]))
        }
        error
    }

    private List<String> validateNotNull(VagvalBestallning bestallning) {
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

    private List<String> validateNotNull(AnropsbehorighetBestallning bestallning) {
        List<String> error = new LinkedList<>()
        def logiskAdressHSAId = bestallning.getLogiskAdress()
        def komponentHSAId = bestallning.getTjanstekonsument()
        def kontraktNamnrymd = bestallning.getTjanstekontrakt()

        if (logiskAdressHSAId == null || komponentHSAId == null || kontraktNamnrymd == null) {
            error.add(i18nService.msg("bestallning.error.saknas.info.for.anropsbehorighet"))
        }
        error
    }

    private List<String> validate(TjanstekontraktBestallning bestallning) {
        List<String> error = new LinkedList<>()
        Tjanstekontrakt tjanstekontrakt = bestallning.tjanstekontrakt
        if (!tjanstekontrakt.validate()) {
            tjanstekontrakt.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.tjanstekontrakt") + validationTagLib.message(error: it))
            }
        }
        error
    }

    private List<String> validate(TjanstekomponentBestallning bestallning) {
        List<String> error = new LinkedList<>()
        Tjanstekomponent tjanstekomponent = bestallning.tjanstekomponent
        if (!tjanstekomponent.validate()) {
            tjanstekomponent.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.tjanstekomponent") + validationTagLib.message(error: it))
            }
        }
        error
    }

    private List<String> validate(LogiskadressBestallning logiskadressBestallning) {
        List<String> error = new LinkedList<>()
        LogiskAdress logiskAdress = logiskadressBestallning.logiskAdress

        if (!logiskAdress.validate()) {
            logiskAdress.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.logiskAdress") + validationTagLib.message(error: it))
            }
        }
        error
    }

    void validateComplexObjectRelations(JsonBestallning bestallning){
        bestallning.getInkludera().getAnropsbehorigheter().each() { anropsbehorighetBestallning ->
            bestallning.addError(validate(anropsbehorighetBestallning))
        }
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            bestallning.addError(validate(vagvalBestallning))
        }
    }

    private List<String> validate(VagvalBestallning bestallning) {
        List<String> error = new LinkedList<>()
        if (!bestallning.rivtaprofilObject) {
            error.add(i18nService.msg("bkestallning.error.saknas.rivtaprofil.for.vagval", [bestallning.rivtaprofil]))
        }

        if (!bestallning.tjanstekomponentObject) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.vagval", [bestallning.tjanstekomponent]))
        }

        if (!bestallning.logiskAdressObject) {
            error.add(i18nService.msg("bestallning.error.saknas.logiskAdress.for.vagval", [bestallning.logiskAdress]))
        }

        if (!bestallning.tjanstekontraktObject) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [bestallning.tjanstekontrakt]))
        }
        error
    }

    private List<String> validate(AnropsbehorighetBestallning bestallning){
        List<String> error = new LinkedList<>()
        if (!bestallning.getTjanstekontraktObject()) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [bestallning.tjanstekontrakt]))
        }

        if (!bestallning.getTjanstekonsumentObject()) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [bestallning.tjanstekonsument]))
        }

        if (!bestallning.getLogiskAdressObject()) {
            error.add(i18nService.msg("bestallning.error.saknas.logiskAdress.for.anropsbehorighet", [bestallning.logiskAdress]))
        }
        error
    }

    void validateAnropAddress(JsonBestallning bestallning ){
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            if (!vagvalBestallning.anropsAdressObject.validate()) {
                vagvalBestallning.anropsAdressObject.errors.allErrors.each() { it ->
                    bestallning.addError(i18nService.msg("bestallning.error.for.logiskAdress") + validationTagLib.message(error: it))
                }
            }
        }
    }
}
