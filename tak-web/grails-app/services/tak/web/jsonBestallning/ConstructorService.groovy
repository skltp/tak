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
import se.skltp.tak.web.jsonBestallning.*
import tak.web.I18nService

import java.sql.Date

class ConstructorService {

    DAOService daoService
    ValidatingService validatingService
    I18nService i18nService

    void preparePlainObjects(BestallningsData data) {
        validateRawDataIntegrity(data)
        JsonBestallning bestallning = data.getBestallning()

        if (data.getBestallning().getExkludera() != null) {
            bestallning.exkludera?.vagval?.each() { vagvalBestallning ->
                prepareVagvalForDelete(vagvalBestallning, data)
            }
            bestallning.exkludera?.anropsbehorigheter?.each() { anropsbehorighetBestallning ->
                prepareAnropsbehorighetForDelete(anropsbehorighetBestallning, data)
            }
        }

        bestallning.exkludera?.logiskadresser?.each { logiskadressBestallning ->
            prepareLogiskAdressForDelete(logiskadressBestallning, data)
        }

        bestallning.exkludera?.tjanstekomponenter?.each { tjanstekomponentBestallning ->
            prepareTjanstekomponentForDelete(tjanstekomponentBestallning, data)
        }

        bestallning.exkludera?.tjanstekontrakt?.each { tjanstekontraktBestallning ->
            prepareTjanstekontraktForDelete(tjanstekontraktBestallning, data)
        }

        bestallning.inkludera?.logiskadresser?.each { logiskadressBestallning ->
            prepareLogiskAdress(logiskadressBestallning, data)
        }

        bestallning.inkludera?.tjanstekomponenter?.each { tjanstekomponentBestallning ->
            prepareTjanstekomponent(tjanstekomponentBestallning, data)
        }

        bestallning.inkludera?.tjanstekontrakt?.each { tjanstekontraktBestallning ->
            prepareTjanstekontrakt(tjanstekontraktBestallning, data)
        }
    }

    void validateRawDataIntegrity(BestallningsData data) {
        data.addError(validatingService.validateDataDubletter(data.bestallning))
    }

    void prepareComplexObjectsRelations(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()

        bestallning.inkludera?.anropsbehorigheter?.each { abBestallning ->
            prepareAnropsbehorighetRelations(abBestallning, data)
        }

        bestallning.inkludera?.vagval?.each { vagvalBestallning ->
            prepareVagvalRelations(vagvalBestallning, data)
        }
    }

    private prepareAnropsbehorighetForDelete(AnropsbehorighetBestallning anropsbehorighetBestallning, BestallningsData data) {
        List<Anropsbehorighet> list = daoService.getAnropsbehorighet(anropsbehorighetBestallning.logiskAdress,
                anropsbehorighetBestallning.tjanstekonsument, anropsbehorighetBestallning.tjanstekontrakt, data.fromDate, data.toDate)

        Set<String> error = validatingService.validateAnropsbehorighetForDubblett(list)
        if (!error.isEmpty()) {
            data.addError(error)
            return
        }

        Set<String> problem = validatingService.validateExists(list, anropsbehorighetBestallning)
        if (problem.isEmpty()) {
            Anropsbehorighet ab = list.get(0)
            ab.getFilter().size()
            if (ab.fromTidpunkt >= data.fromDate) {
                ab.setDeleted(null)
            } else if (ab.fromTidpunkt < data.fromDate) {
                ab.setTomTidpunkt(generateDateMinusDag(data.fromDate))
            }
            data.put(anropsbehorighetBestallning, list.get(0))
        }
    }


    private prepareVagvalForDelete(VagvalBestallning bestallning, BestallningsData data) {
        List<Vagval> list = daoService.getVagval(bestallning.logiskAdress, bestallning.tjanstekontrakt, data.fromDate, data.toDate)

        Set<String> error = validatingService.validateVagvalForDubblett(list)
        if (!error.isEmpty()) {
            data.addError(error)
            return
        }

        Set<String> problem = validatingService.validateExists(list, bestallning)
        if (problem.isEmpty()) {
            Vagval existingVagval = list.get(0)
            existingVagval.anropsAdress.vagVal.size()
            data.putOldVagval(bestallning, existingVagval)

            deactivateVagval(existingVagval, data)
        }
    }


    private prepareLogiskAdressForDelete(LogiskadressBestallning logiskadressBestallning, BestallningsData data) {
        LogiskAdress logiskAdress = daoService.getLogiskAdressByHSAId(logiskadressBestallning.hsaId)
        if (logiskAdress != null) {
            logiskAdress.setDeleted(null)
            Set<String> error = validatingService.validateLogiskAdressRelationsForDelete(logiskAdress)
            if (error.isEmpty()) {
                data.put(logiskadressBestallning, logiskAdress)
            } else {
                data.addError(error)
            }
        }
    }

    private prepareTjanstekomponentForDelete(TjanstekomponentBestallning tjanstekomponentBestallning, BestallningsData data) {
        Tjanstekomponent tjanstekomponent = daoService.getTjanstekomponentByHSAId(tjanstekomponentBestallning.hsaId)
        if (tjanstekomponent != null) {
            tjanstekomponent.setDeleted(null)
            Set<String> error = validatingService.validateTjanstekomponentRelationsForDelete(tjanstekomponent)
            if (error.empty) {
                data.put(tjanstekomponentBestallning, tjanstekomponent)
            } else {
                data.addError(error)
            }
        }
    }

    private prepareTjanstekontraktForDelete(TjanstekontraktBestallning tjanstekontraktBestallning, BestallningsData data) {
        Tjanstekontrakt tjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(tjanstekontraktBestallning.namnrymd)
        if (tjanstekontrakt != null) {
            tjanstekontrakt.setDeleted(null)
            Set<String> error = validatingService.validateTjanstekontraktForDelete(tjanstekontrakt)
            if (error.empty) {
                data.put(tjanstekontraktBestallning, tjanstekontrakt)
            } else {
                data.addError(error)
            }
        }
    }


    private prepareTjanstekontrakt(TjanstekontraktBestallning tjanstekontraktBestallning, BestallningsData data) {
        Tjanstekontrakt tjanstekontrakt = createOrUpdate(tjanstekontraktBestallning)
        Set<String> error = validatingService.validate(tjanstekontrakt)
        if (error.isEmpty()) {
            data.put(tjanstekontraktBestallning, tjanstekontrakt)
        } else {
            data.addError(error)
        }
    }

    private Tjanstekontrakt createOrUpdate(TjanstekontraktBestallning bestallning) {
        Tjanstekontrakt tjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(bestallning.getNamnrymd())
        if (tjanstekontrakt == null) {
            tjanstekontrakt = new Tjanstekontrakt()
            tjanstekontrakt.setNamnrymd(bestallning.getNamnrymd())
            tjanstekontrakt.setBeskrivning(bestallning.getBeskrivning())
            tjanstekontrakt.setMajorVersion(bestallning.getMajorVersion())
        } else {
            if (!tjanstekontrakt.getBeskrivning().equals(bestallning.getBeskrivning())) {
                tjanstekontrakt.setBeskrivning(bestallning.getBeskrivning())
            }
            if (tjanstekontrakt.getMajorVersion() != bestallning.getMajorVersion()) {
                tjanstekontrakt.setMajorVersion(bestallning.getMajorVersion())
            }
        }
        return tjanstekontrakt
    }

    private prepareTjanstekomponent(TjanstekomponentBestallning tjanstekomponentBestallning, BestallningsData data) {
        Tjanstekomponent tjanstekomponent = createOrUpdate(tjanstekomponentBestallning)
        Set<String> error = validatingService.validate(tjanstekomponent)
        if (error.isEmpty()) {
            data.put(tjanstekomponentBestallning, tjanstekomponent)
        } else {
            data.addError(error)
        }
    }

    private Tjanstekomponent createOrUpdate(TjanstekomponentBestallning bestallning) {
        Tjanstekomponent tjanstekomponent = daoService.getTjanstekomponentByHSAId(bestallning.getHsaId())
        if (tjanstekomponent == null) {
            tjanstekomponent = new Tjanstekomponent()
            tjanstekomponent.setHsaId(bestallning.getHsaId())
            tjanstekomponent.setBeskrivning(bestallning.getBeskrivning())
        } else {
            if (!tjanstekomponent.getBeskrivning().equals(bestallning.getBeskrivning())) {
                tjanstekomponent.setBeskrivning(bestallning.getBeskrivning())
            }
        }
        return tjanstekomponent
    }

    private void prepareLogiskAdress(LogiskadressBestallning logiskadressBestallning, BestallningsData data) {
        LogiskAdress logiskAdress = createOrUpdate(logiskadressBestallning)
        Set<String> error = validatingService.validate(logiskAdress)
        if (error.isEmpty()) {
            data.put(logiskadressBestallning, logiskAdress)
        } else {
            data.addError(error)
        }
    }

    private LogiskAdress createOrUpdate(LogiskadressBestallning bestallning) {
        LogiskAdress logiskAdress = daoService.getLogiskAdressByHSAId(bestallning.hsaId)
        if (logiskAdress == null) {
            logiskAdress = new LogiskAdress()
            logiskAdress.hsaId = bestallning.hsaId
            logiskAdress.beskrivning = bestallning.beskrivning
        } else {
            if (!logiskAdress.beskrivning?.equals(bestallning.beskrivning)) {
                logiskAdress.beskrivning = bestallning.beskrivning
            }
        }
        return logiskAdress
    }


    private void prepareVagvalRelations(VagvalBestallning vagvalBestallning, BestallningsData data) {
        Set<String> errors = validatingService.validateNotEmpty(vagvalBestallning)
        if (!errors.isEmpty()) {
            data.addError(errors)
            return
        }

        RivTaProfil rivTaProfil = findRivtaInDB(vagvalBestallning.rivtaprofil)
        Tjanstekomponent tjanstekomponent = findTjanstekomponentInDBorOrder(vagvalBestallning.tjanstekomponent, data)
        LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(vagvalBestallning.logiskAdress, data)
        Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(vagvalBestallning.tjanstekontrakt, data)

        errors = validatingService.validateRelatedObjects(vagvalBestallning, rivTaProfil, logiskAdress, tjanstekomponent, tjanstekontrakt)
        if (!errors.isEmpty()) {
            data.addError(errors)
            return
        }

        AnropsAdress adress = findOrCreateAnropsAdress(vagvalBestallning, data, rivTaProfil, tjanstekomponent)
        errors = validatingService.validateAnropAddress(adress, data)
        if (!errors.isEmpty()) {
            data.addError(errors)
            return
        }

        data.putRelations(vagvalBestallning, adress, logiskAdress, tjanstekontrakt)
    }

    private prepareAnropsbehorighetRelations(AnropsbehorighetBestallning abBestallning, BestallningsData data) {
        Set<String> errors = validatingService.validateNotEmpty(abBestallning)
        if (!errors.isEmpty()) {
            data.addError(errors)
            return
        }

        Tjanstekomponent tjanstekonsument = findTjanstekomponentInDBorOrder(abBestallning.tjanstekonsument, data)
        LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(abBestallning.logiskAdress, data)
        Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(abBestallning.tjanstekontrakt, data)

        errors = validatingService.validateRelatedObjects(abBestallning, logiskAdress, tjanstekonsument, tjanstekontrakt)
        if (!errors.isEmpty()) {
            data.addError(errors)
            return
        }

        data.putRelations(abBestallning, logiskAdress, tjanstekonsument, tjanstekontrakt)
    }

    private LogiskAdress findLogiskAdressInDBorOrder(String hsaId, BestallningsData data) {
        LogiskAdress existLogiskAdress = daoService.getLogiskAdressByHSAId(hsaId)
        if (existLogiskAdress == null) {
            existLogiskAdress = data.getLogiskAdress(hsaId)

        }
        return existLogiskAdress
    }

    private Tjanstekomponent findTjanstekomponentInDBorOrder(String hsaId, BestallningsData data) {
        Tjanstekomponent existTjanstekomponent = daoService.getTjanstekomponentByHSAId(hsaId)
        if (existTjanstekomponent == null) {
            existTjanstekomponent = data.getTjanstekomponent(hsaId)
        }
        return existTjanstekomponent
    }

    private Tjanstekontrakt findTjanstekontraktInDBorOrder(String namnrymd, BestallningsData data) {
        Tjanstekontrakt existTjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(namnrymd)
        if (existTjanstekontrakt == null) {
            existTjanstekontrakt = data.getTjanstekontrakt(namnrymd)
        }
        return existTjanstekontrakt
    }

    private RivTaProfil findRivtaInDB(String rivta) {
        return daoService.getRivtaByNamn(rivta)
    }


    void prepareComplexObjects(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()

        bestallning.inkludera.anropsbehorigheter.each() { abBestallning ->
            prepareAnropsbehorighet(abBestallning, data, data.fromDate, data.toDate)
        }

        bestallning.inkludera.vagval.each() { vvBestallning ->
            prepareVagval(vvBestallning, data, data.fromDate, data.toDate)
        }
    }

    private prepareAnropsbehorighet(AnropsbehorighetBestallning bestallning, BestallningsData data, Date from, Date tom) {
        List<Anropsbehorighet> anropsbehorighetList = daoService.getAnropsbehorighet(bestallning.logiskAdress, bestallning.tjanstekonsument, bestallning.tjanstekontrakt, from, tom)
        Set<String> error = validatingService.validateAnropsbehorighetForDubblett(anropsbehorighetList)
        if (!error.isEmpty()) {
            data.addError(error)
            return
        }

        if (anropsbehorighetList.size() == 0) {
            BestallningsData.AnropsBehorighetRelations abData = data.getAnropsbehorighetRelations(bestallning)
            Anropsbehorighet ab = createAnropsbehorighet(abData.logiskadress, abData.tjanstekontrakt, abData.tjanstekomponent, from, tom)
            data.put(bestallning, ab)
        } else if (anropsbehorighetList.size() == 1) {
            Anropsbehorighet existingAnropsbehorighet = anropsbehorighetList.get(0);
            existingAnropsbehorighet.filter.size()
            if (existingAnropsbehorighet.fromTidpunkt >= from) {
                existingAnropsbehorighet.fromTidpunkt = from
                existingAnropsbehorighet.tomTidpunkt = tom
            } else if (existingAnropsbehorighet.fromTidpunkt < from) {
                existingAnropsbehorighet.setTomTidpunkt(tom)
            }
            data.put(bestallning, existingAnropsbehorighet)
        }
    }

    private prepareVagval(VagvalBestallning bestallning, BestallningsData data, Date from, Date tom) {
        List<Vagval> vagvalList = daoService.getVagval(bestallning.logiskAdress, bestallning.tjanstekontrakt, from, tom)
        Set<String> error = validatingService.validateVagvalForDubblett(vagvalList)
        if (!error.isEmpty()) {
            data.addError(error)
            return
        }

        BestallningsData.VagvalRelations newVagvalData = data.getVagvalRelations(bestallning)
        if (vagvalList.size() == 0) {
            Vagval newVagval = createVagval(newVagvalData.logiskAdress, newVagvalData.tjanstekontrakt, newVagvalData.anropsAdress, from, tom)
            data.putNewVagval(bestallning, newVagval)
            return
        }

        Vagval existingVagval = vagvalList.get(0)
        existingVagval.anropsAdress.vagVal.size()
        //samma vägval
        if (existingVagval.anropsAdress.rivTaProfil == newVagvalData.anropsAdress.rivTaProfil &&
                existingVagval.anropsAdress.tjanstekomponent == newVagvalData.anropsAdress.tjanstekomponent &&
                existingVagval.anropsAdress.adress == bestallning.adress) {
            if (existingVagval.fromTidpunkt >= from) {
                existingVagval.fromTidpunkt = from
                existingVagval.tomTidpunkt = tom
            } else if (existingVagval.fromTidpunkt < from) {
                existingVagval.setTomTidpunkt(tom)
            }
        } else { //vägval med annan anropsAdress
            Vagval newVagval = createVagval(newVagvalData.logiskAdress, newVagvalData.tjanstekontrakt, newVagvalData.anropsAdress, from, tom)
            data.putNewVagval(bestallning, newVagval)

            deactivateVagval(existingVagval, data)
        }
        data.putOldVagval(bestallning, existingVagval)
    }

    private deactivateVagval(Vagval existingVagval, BestallningsData data){
        if (existingVagval.fromTidpunkt >= data.fromDate) {
            existingVagval.setDeleted(null)
            if (existingVagval.anropsAdress.vagVal.size() == 1 || existingVagval.anropsAdress.vagVal.every { avv -> avv.getDeleted() })  {
                existingVagval.anropsAdress.setDeleted(null)
                data.putAnropsAdress(existingVagval.anropsAdress)
            }
        } else if (existingVagval.fromTidpunkt < data.fromDate) {
            existingVagval.setTomTidpunkt(generateDateMinusDag(data.fromDate))
        }
    }

    private AnropsAdress findOrCreateAnropsAdress(VagvalBestallning bestallning, BestallningsData data, RivTaProfil profil, Tjanstekomponent tjanstekomponent) {
        AnropsAdress adress = data.getAnropsAdress(profil, tjanstekomponent, bestallning.adress);

        if (adress == null) {
            adress = daoService.getAnropsAdress(bestallning.rivtaprofil, bestallning.tjanstekomponent, bestallning.adress)
            if (adress == null) {
                adress = new AnropsAdress()
                adress.setAdress(bestallning.adress)
                adress.setRivTaProfil(profil)
                adress.setTjanstekomponent(tjanstekomponent)
            }
            data.putAnropsAdress(adress)
        }
        return adress
    }

    private Anropsbehorighet createAnropsbehorighet(LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt, Tjanstekomponent tjanstekomponent, Date from, Date tom) {
        Anropsbehorighet anropsbehorighet = new Anropsbehorighet()
        anropsbehorighet.setLogiskAdress(logiskAdress)
        anropsbehorighet.setTjanstekontrakt(tjanstekontrakt)
        anropsbehorighet.setTjanstekonsument(tjanstekomponent)
        anropsbehorighet.setIntegrationsavtal("AUTOTAKNING")
        anropsbehorighet.setFromTidpunkt(from)
        anropsbehorighet.setTomTidpunkt(tom)
        return anropsbehorighet
    }

    private Vagval createVagval(LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt, AnropsAdress anropsAdress, Date from, Date tom) {
        Vagval vagval = new Vagval()
        vagval.setLogiskAdress(logiskAdress)
        vagval.setTjanstekontrakt(tjanstekontrakt)
        vagval.setAnropsAdress(anropsAdress)
        vagval.setFromTidpunkt(from)
        vagval.setTomTidpunkt(tom)
        return vagval
    }


    private Date generateDateMinusDag(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -1);
            Date d = new Date(c.getTime().getTime());
            return d;
        }
        return null;
    }
}
