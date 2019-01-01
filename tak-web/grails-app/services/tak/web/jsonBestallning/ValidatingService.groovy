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
import se.skltp.tak.core.entity.AnropsAdress
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.Vagval
import se.skltp.tak.web.jsonBestallning.*
import tak.web.I18nService

class ValidatingService {

    I18nService i18nService
    ValidationTagLib validationTagLib;

    void validatePlainObjects(JsonBestallning bestallning, BestallningsData data) {
        bestallning.getExkludera().getVagval().each() { it ->
            data.addInfo(validateForDelete(it, data))
        }
        bestallning.getExkludera().getAnropsbehorigheter().each() { it ->
            data.addInfo(validateForDelete(it, data))
        }

        bestallning.getInkludera().getLogiskadresser().each() { logiskadressBestallning ->
            data.addError(validate(logiskadressBestallning, data))
        }
        bestallning.getInkludera().getTjanstekomponenter().each() { tjanstekomponentBestallning ->
            data.addError(validate(tjanstekomponentBestallning, data))
        }
        bestallning.getInkludera().getTjanstekontrakt().each() { tjanstekontraktBestallning ->
            data.addError(validate(tjanstekontraktBestallning, data))
        }

        bestallning.getInkludera().getAnropsbehorigheter().each() { anropsbehorighetBestallning ->
            data.addError(validateNotNull(anropsbehorighetBestallning))
        }
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            data.addError(validateNotNull(vagvalBestallning))
        }

    }

    private List<String> validateForDelete(AnropsbehorighetBestallning bestallning, BestallningsData data) {
        List<String> error = new LinkedList<>()
        if (data.getAnropsbehorighet(bestallning)== null) {
            error.add(i18nService.msg("bestallning.error.saknas.anropsbehorighet", [bestallning.logiskAdress, bestallning.tjanstekonsument, bestallning.tjanstekontrakt]))
        }
        error
    }

    private List<String> validateForDelete(VagvalBestallning bestallning, BestallningsData data) {
        List<String> error = new LinkedList<>()
        if (data.getVagval(bestallning) == null) {
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

    private List<String> validate(TjanstekontraktBestallning bestallning, BestallningsData data) {
        List<String> error = new LinkedList<>()
        Tjanstekontrakt tjanstekontrakt = data.getTjanstekontrakt(bestallning)
        if (!tjanstekontrakt.validate()) {
            tjanstekontrakt.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.tjanstekontrakt") + validationTagLib.message(error: it))
            }
        }
        error
    }

    private List<String> validate(TjanstekomponentBestallning bestallning, BestallningsData data) {
        List<String> error = new LinkedList<>()
        Tjanstekomponent tjanstekomponent = data.getTjanstekomponent(bestallning)
        if (!tjanstekomponent.validate()) {
            tjanstekomponent.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.tjanstekomponent") + validationTagLib.message(error: it))
            }
        }
        error
    }

    private List<String> validate(LogiskadressBestallning logiskadressBestallning, BestallningsData data) {
        List<String> error = new LinkedList<>()
        LogiskAdress logiskAdress = data.getLogiskAdress(logiskadressBestallning)

        if (!logiskAdress.validate()) {
            logiskAdress.errors.allErrors.each() { it ->
                error.add(i18nService.msg("bestallning.error.for.logiskAdress") + validationTagLib.message(error: it))
            }
        }
        error
    }

    void validateComplexObjectRelations(JsonBestallning bestallning, BestallningsData data) {
        bestallning.getInkludera().getAnropsbehorigheter().each() { anropsbehorighetBestallning ->
            data.addError(validate(anropsbehorighetBestallning, data))
        }
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            data.addError(validate(vagvalBestallning, data))
        }
    }

    private List<String> validate(VagvalBestallning bestallning, BestallningsData data) {
        List<String> error = new LinkedList<>()
        BestallningsData.RelationData relationData = data.getVagvalRelations(bestallning)
        if (!relationData.profil) {
            error.add(i18nService.msg("bkestallning.error.saknas.rivtaprofil.for.vagval", [bestallning.rivtaprofil]))
        }

        if (!relationData.tjanstekomponent) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.vagval", [bestallning.tjanstekomponent]))
        }

        if (!relationData.logiskadress) {
            error.add(i18nService.msg("bestallning.error.saknas.logiskAdress.for.vagval", [bestallning.logiskAdress]))
        }

        if (!relationData.tjanstekontrakt) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [bestallning.tjanstekontrakt]))
        }
        error
    }

    private List<String> validate(AnropsbehorighetBestallning bestallning, BestallningsData data) {
        List<String> error = new LinkedList<>()
        BestallningsData.RelationData relationData = data.getAnropsbehorighetRelations(bestallning)
        if (!relationData.getTjanstekontrakt()) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [bestallning.tjanstekontrakt]))
        }

        if (!relationData.getTjanstekontrakt()) {
            error.add(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [bestallning.tjanstekonsument]))
        }

        if (!relationData.getLogiskadress()) {
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

    void validateVagvalForDubblett(List<Vagval> list, BestallningsData data) {
        if(list.size() > 1){
            data.addError(i18nService.msg("bestallning.error.dubblett.vagval", [list.get(0).logiskAdress, list.get(0).tjanstekontrakt]))
        }

    }

    void validateAnropsbehorighetForDubblett(List<Anropsbehorighet> list, BestallningsData data) {
        if(list.size() > 1){
            data.addError(i18nService.msg("bestallning.error.dubblett.anropsbehorighet", [list.get(0).logiskAdress, list.get(0).tjanstekontrakt, list.get(0).tjanstekonsument]))
        }

    }
}
