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

import java.sql.Date

class ConstructorService {

    DAOService daoService
    ValidatingService validatingService

    void preparePlainObjects(BestallningsData data) {
        prepareVagvalForDelete(data)
        prepareAnropsbehorighetForDelete(data)
        prepareLogiskAdress(data)
        prepareTjanstekomponent(data)
        prepareTjanstekontrakt(data)
    }

    void prepareComplexObjectsRelations(BestallningsData data) {
        prepareAnropsbehorighet(data)
        prepareVagval(data)
    }

    private prepareAnropsbehorighetForDelete(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()
        if (bestallning.getExkludera() != null) {
            bestallning.getExkludera().getAnropsbehorigheter().each() { anropsbehorighetBestallning ->
                List<Anropsbehorighet> list = findAndDeactivate(anropsbehorighetBestallning, data.fromDate, data.toDate)
                List<String> error = validatingService.validateAnropsbehorighetForDubblett(list)
                if (error.isEmpty()) {
                    List<String> problem = validatingService.validateExists(list, anropsbehorighetBestallning)
                    if (problem.isEmpty()) {
                        data.put(anropsbehorighetBestallning, list.get(0))
                    }
                } else {
                    data.addError(error)
                }
            }
        }
    }

    private findAndDeactivate(AnropsbehorighetBestallning bestallning, Date from, Date tom) {
        List<Anropsbehorighet> anropsbehorigheter = daoService.getAnropsbehorighet(bestallning.logiskAdress, bestallning.tjanstekonsument, bestallning.tjanstekontrakt, from, tom)
        anropsbehorigheter.each() { ab ->
            ab.getFilter().size()
            if (ab.fromTidpunkt >= from) {
                ab.deleted = null
            } else if (ab.fromTidpunkt < from) {
                ab.setTomTidpunkt(generateDateMinusDag(from))
            }
        }
        return anropsbehorigheter
    }


    private prepareVagvalForDelete(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()
        if (bestallning.getExkludera() != null) {
            bestallning.getExkludera().getVagval().each() { vagvalBestallning ->
                List<Vagval> list = findAndDeactivate(vagvalBestallning, data.fromDate, data.toDate)
                List<String> error = validatingService.validateVagvalForDubblett(list)
                if (error.isEmpty()) {
                    List<String> problem = validatingService.validateExists(list, vagvalBestallning)
                    if (problem.isEmpty()) {
                        Vagval vv = list.get(0)
                        vv.anropsAdress.vagVal.size()
                        data.putOldVagval(vagvalBestallning, vv)
                    }
                } else {
                    data.addError(error)
                }
            }
        }
    }

    private prepareTjanstekontrakt(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()
        bestallning.getInkludera().getTjanstekontrakt().each() { tjanstekontraktBestallning ->
            Tjanstekontrakt tjanstekontrakt = createOrUpdate(tjanstekontraktBestallning)
            List<String> error = validatingService.validate(tjanstekontrakt)
            if (error.isEmpty()) {
                data.put(tjanstekontraktBestallning, tjanstekontrakt)
            } else {
                data.addError(error)
            }
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

    private prepareTjanstekomponent(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()
        bestallning.getInkludera().getTjanstekomponenter().each() { tjanstekomponentBestallning ->
            Tjanstekomponent tjanstekomponent = createOrUpdate(tjanstekomponentBestallning)
            List<String> error = validatingService.validate(tjanstekomponent)
            if (error.isEmpty()) {
                data.put(tjanstekomponentBestallning, tjanstekomponent)
            } else {
                data.addError(error)
            }
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

    private void prepareLogiskAdress(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()
        bestallning.getInkludera().getLogiskadresser().each() { logiskadressBestallning ->
            LogiskAdress logiskAdress = createOrUpdate(logiskadressBestallning)
            List<String> error = validatingService.validate(logiskAdress)
            if (error.isEmpty()) {
                data.put(logiskadressBestallning, logiskAdress)
            } else {
                data.addError(error)
            }
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


    private List<Vagval> findAndDeactivate(VagvalBestallning bestallning, Date from, Date tom) {
        List<Vagval> vagvalList = daoService.getVagval(bestallning.logiskAdress, bestallning.tjanstekontrakt, from, tom)
        vagvalList.each() { vv ->
            if (vv.fromTidpunkt >= from) {
                vv.deleted = null
                if (vv.anropsAdress.vagVal.size() == 1) {
                    vv.anropsAdress.deleted = null
                }
            } else if (vv.fromTidpunkt < from) {
                vv.setTomTidpunkt(generateDateMinusDag(from))
            }
        }
        return vagvalList
    }


    private void prepareVagval(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            List<String> errors = validatingService.validateNotNull(vagvalBestallning)
            if (!errors.isEmpty()) {
                data.addError(errors)
                return
            }

            RivTaProfil rivTaProfil = findRivtaInDB(vagvalBestallning.rivtaprofil)
            Tjanstekomponent tjanstekomponent = findTjanstekomponentInDBorOrder(vagvalBestallning.tjanstekomponent, data)
            LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(vagvalBestallning.logiskAdress, data)
            Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(vagvalBestallning.tjanstekontrakt, data)
            errors = validatingService.validate(vagvalBestallning, rivTaProfil, logiskAdress, tjanstekomponent, tjanstekontrakt)
            if (errors.isEmpty()) {
                data.putRelations(vagvalBestallning, logiskAdress, tjanstekomponent, tjanstekontrakt, rivTaProfil)
            } else {
                data.addError(errors)
            }
        }
    }

    private prepareAnropsbehorighet(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()
        bestallning.getInkludera().getAnropsbehorigheter().each() { abBestallning ->
            List<String> errors = validatingService.validateNotNull(abBestallning)
            if (!errors.isEmpty()) {
                data.addError(errors)
                return
            }

            Tjanstekomponent tjanstekonsument = findTjanstekomponentInDBorOrder(abBestallning.tjanstekonsument, data)
            LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(abBestallning.logiskAdress, data)
            Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(abBestallning.tjanstekontrakt, data)

            errors = validatingService.validate(abBestallning, logiskAdress, tjanstekonsument, tjanstekontrakt)
            if (errors.isEmpty()) {
                data.putRelations(abBestallning, logiskAdress, tjanstekonsument, tjanstekontrakt)
            } else {
                data.addError(errors)
            }
        }
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
        List<String> error = validatingService.validateAnropsbehorighetForDubblett(anropsbehorighetList)
        if (!error.isEmpty()) {
            data.addError(error)
            return
        }

        if (anropsbehorighetList.size() == 0) {
            BestallningsData.RelationData abData = data.getAnropsbehorighetRelations(bestallning)
            Anropsbehorighet ab = createAnropsbehorighet(abData.logiskadress, abData.tjanstekontrakt, abData.tjanstekomponent, from, tom)
            data.put(bestallning, ab)
        } else if (anropsbehorighetList.size() == 1) {
            Anropsbehorighet existentAnropsbehorighet = anropsbehorighetList.get(0);
            existentAnropsbehorighet.filter.size()
            if (existentAnropsbehorighet.fromTidpunkt >= from) {
                existentAnropsbehorighet.fromTidpunkt = from
                existentAnropsbehorighet.tomTidpunkt = tom
            } else if (existentAnropsbehorighet.fromTidpunkt < from) {
                existentAnropsbehorighet.setTomTidpunkt(tom)
            }
            data.put(bestallning, existentAnropsbehorighet)
        }
    }

    private prepareVagval(VagvalBestallning bestallning, BestallningsData data, Date from, Date tom) {
        BestallningsData.RelationData vvData = data.getVagvalRelations(bestallning)
        List<Vagval> vagvalList = daoService.getVagval(bestallning.logiskAdress, bestallning.tjanstekontrakt, from, tom)

        List<String> error = validatingService.validateVagvalForDubblett(vagvalList)

        if (!error.isEmpty()) {
            data.addError(error)
            return
        }

        if (vagvalList.size() == 0) {
            AnropsAdress adress = findOrCreateAnropaAdress(bestallning, vvData.profil, vvData.tjanstekomponent)
            validatingService.validateAnropAddress(adress, data)
            if (data.hasErrors()) return

            Vagval newVagval = createVagval(vvData.logiskadress, vvData.tjanstekontrakt, adress, from, tom)
            data.putNewVagval(bestallning, newVagval)
        } else if (vagvalList.size() == 1) {
            Vagval existentVagval = vagvalList.get(0)
            existentVagval.anropsAdress.vagVal.size()
            if (existentVagval.anropsAdress.rivTaProfil == vvData.profil &&
                    existentVagval.anropsAdress.tjanstekomponent == vvData.tjanstekomponent &&
                    existentVagval.anropsAdress.adress == bestallning.adress) {
                if (existentVagval.fromTidpunkt >= from) {
                    existentVagval.fromTidpunkt = from
                    existentVagval.tomTidpunkt = tom
                } else if (existentVagval.fromTidpunkt < from) {
                    existentVagval.setTomTidpunkt(tom)
                }
            } else {
                AnropsAdress adress = findOrCreateAnropaAdress(bestallning, vvData.profil, vvData.tjanstekomponent)
                Vagval newVagval = createVagval(vvData.logiskadress, vvData.tjanstekontrakt, adress, from, tom)
                data.putNewVagval(bestallning, newVagval)
                if (existentVagval.fromTidpunkt >= from) {
                    existentVagval.deleted = null
                    if (existentVagval.anropsAdress.vagVal.size() == 1) {
                        existentVagval.anropsAdress.deleted = null
                    }
                } else if (existentVagval.fromTidpunkt < from) {
                    existentVagval.setTomTidpunkt(generateDateMinusDag(from))
                }
            }
            data.putOldVagval(bestallning, existentVagval)
        }
    }

    private AnropsAdress findOrCreateAnropaAdress(VagvalBestallning bestallning, RivTaProfil profil, Tjanstekomponent tjanstekomponent) {
        AnropsAdress adress = daoService.getAnropsAdress(bestallning.rivtaprofil, bestallning.tjanstekomponent, bestallning.adress)
        if (adress == null) {
            adress = new AnropsAdress()
            adress.setAdress(bestallning.adress)
            adress.setRivTaProfil(profil)
            adress.setTjanstekomponent(tjanstekomponent)
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
