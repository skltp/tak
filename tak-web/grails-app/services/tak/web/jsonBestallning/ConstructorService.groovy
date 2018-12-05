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

    void preparePlainObjects(JsonBestallning bestallning) {
        bestallning.getExkludera().getVagval().each() { it ->
            createByBestallningForDelete(it, bestallning.genomforandeTidpunkt, generateTomDate(bestallning.genomforandeTidpunkt))
        }
        bestallning.getExkludera().getAnropsbehorigheter().each() { it ->
            createByBestallningForDelete(it, bestallning.genomforandeTidpunkt, generateTomDate(bestallning.genomforandeTidpunkt))
        }
        bestallning.getInkludera().getLogiskadresser().each() { logiskadressBestallning ->
            createByBestallning(logiskadressBestallning)
        }
        bestallning.getInkludera().getTjanstekomponenter().each() { tjanstekomponentBestallning ->
            createByBestallning(tjanstekomponentBestallning)
        }
        bestallning.getInkludera().getTjanstekontrakt().each() { tjanstekontraktBestallning ->
            createByBestallning(tjanstekontraktBestallning)
        }
        validatingService.validatePlainObjects(bestallning)
    }

    private createByBestallning(LogiskadressBestallning bestallning) {
        LogiskAdress logiskAdress = daoService.getLogiskAdressByHSAId(bestallning.getHsaId())
        if (logiskAdress == null) {
            logiskAdress = new LogiskAdress()
            logiskAdress.setHsaId(bestallning.getHsaId())
            logiskAdress.setBeskrivning(bestallning.getBeskrivning())
        } else {
            if (!logiskAdress.getBeskrivning().equals(bestallning.getBeskrivning())) {
                logiskAdress.setBeskrivning(bestallning.getBeskrivning())
            }
        }
        bestallning.logiskAdress = logiskAdress
    }

    private createByBestallning(VagvalBestallning bestallning) {
        AnropsAdress adress = daoService.getAnropsAdress(bestallning.rivtaprofilObject.namn, bestallning.tjanstekomponentObject.hsaId, bestallning.adress)
        if (adress == null) {
            adress = new AnropsAdress()
            adress.setAdress(bestallning.adress)
            adress.setRivTaProfil(bestallning.rivtaprofilObject)
            adress.setTjanstekomponent(bestallning.tjanstekomponentObject)
        }
        bestallning.anropsAdressObject = adress
    }

    private createByBestallning(TjanstekontraktBestallning bestallning) {
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
        bestallning.tjanstekontrakt = tjanstekontrakt
    }

    private createByBestallning(TjanstekomponentBestallning bestallning) {
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
        bestallning.tjanstekomponent = tjanstekomponent
    }

    private createByBestallningForDelete(AnropsbehorighetBestallning bestallning, Date from, Date tom) {
        List<Anropsbehorighet> anropsbehorigheter = daoService.getAnropsbehorighet(bestallning.logiskAdress, bestallning.tjanstekonsument, bestallning.tjanstekontrakt, from, tom)
        anropsbehorigheter.each() { ab ->
            ab.getFilter().size()
            ab.tomTidpunkt = from
        }
        bestallning.aropsbehorigheterForDelete = anropsbehorigheter
    }

    private createByBestallningForDelete(VagvalBestallning bestallning, Date from, Date tom) {
        List<Vagval> vagvalList = daoService.getVagval(bestallning.logiskAdress, bestallning.tjanstekontrakt, bestallning.rivtaprofil, bestallning.tjanstekomponent, from, tom)
        vagvalList.each() { vv ->
            vv.tomTidpunkt = from
        }
        bestallning.vagvalForDelete = vagvalList
    }

    void prepareComplexObjectsRelations(JsonBestallning bestallning) {
        bestallning.getInkludera().getAnropsbehorigheter().each() { anropsbehorighetBestallning ->
            anropsbehorighetBestallning.tjanstekonsumentObject = findTjanstekomponentInDBorOrder(anropsbehorighetBestallning.tjanstekonsument, bestallning)
            anropsbehorighetBestallning.logiskAdressObject = findLogiskAdressInDBorOrder(anropsbehorighetBestallning.logiskAdress, bestallning)
            anropsbehorighetBestallning.tjanstekontraktObject = findTjanstekontraktInDBorOrder(anropsbehorighetBestallning.tjanstekontrakt, bestallning)
        }

        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            vagvalBestallning.rivtaprofilObject = findRivtaInDB(vagvalBestallning.rivtaprofil)
            vagvalBestallning.tjanstekomponentObject = findTjanstekomponentInDBorOrder(vagvalBestallning.tjanstekomponent, bestallning)
            vagvalBestallning.logiskAdressObject = findLogiskAdressInDBorOrder(vagvalBestallning.logiskAdress, bestallning)
            vagvalBestallning.tjanstekontraktObject = findTjanstekontraktInDBorOrder(vagvalBestallning.tjanstekontrakt, bestallning)
        }

        validatingService.validateComplexObjectRelations(bestallning)
        if (bestallning.hasErrors()) return
    }

    private LogiskAdress findLogiskAdressInDBorOrder(String hsaId, JsonBestallning bestallning) {
        LogiskAdress existLogiskAdress = daoService.getLogiskAdressByHSAId(hsaId)
        if (existLogiskAdress == null) {
            bestallning.getInkludera().getLogiskadresser().each() { iter ->
                if (hsaId.equals(iter.getHsaId())) {
                    existLogiskAdress = iter.getLogiskAdress()
                }
            }
        }
        return existLogiskAdress
    }

    private Tjanstekomponent findTjanstekomponentInDBorOrder(String hsaId, JsonBestallning bestallning) {
        Tjanstekomponent existTjanstekomponent = daoService.getTjanstekomponentByHSAId(hsaId)
        if (existTjanstekomponent == null) {
            bestallning.getInkludera().getTjanstekomponenter().each() { tjanstekomponentBestallning ->
                if (hsaId.equals(tjanstekomponentBestallning.getHsaId())) {
                    existTjanstekomponent = tjanstekomponentBestallning.getTjanstekomponent()
                }
            }
        }
        return existTjanstekomponent
    }

    private Tjanstekontrakt findTjanstekontraktInDBorOrder(String namnrymd, JsonBestallning bestallning) {
        Tjanstekontrakt existTjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(namnrymd)
        if (existTjanstekontrakt == null) {
            bestallning.getInkludera().getTjanstekontrakt().each() { iter ->
                if (namnrymd.equals(iter.getNamnrymd())) {
                    existTjanstekontrakt = iter.getTjanstekontrakt()
                }
            }
        }
        return existTjanstekontrakt
    }

    private RivTaProfil findRivtaInDB(String rivta) {
        return daoService.getRivtaByNamn(rivta)
    }

    void prepareComplexObjects(JsonBestallning bestallning) {
        bestallning.inkludera.anropsbehorigheter.each() { abBestallning ->
            prepareAnropsbehorighet(abBestallning, bestallning.genomforandeTidpunkt, generateTomDate(bestallning.genomforandeTidpunkt))
        }

        bestallning.inkludera.vagval.each() { vvBestallning ->
            prepareVagval(vvBestallning, bestallning.genomforandeTidpunkt, generateTomDate(bestallning.genomforandeTidpunkt))

        }
    }

    private prepareAnropsbehorighet(AnropsbehorighetBestallning bestallning, Date from, Date tom) {
        List<Anropsbehorighet> anropsbehorighetList = daoService.getAnropsbehorighet(bestallning.logiskAdressObject.hsaId, bestallning.tjanstekonsumentObject.hsaId, bestallning.tjanstekontraktObject.namnrymd, from, tom)
        if (anropsbehorighetList.size() > 1) {
            //todo error
        } else if (anropsbehorighetList.size() == 0) { //нет такого  - просто создаю новый
            Anropsbehorighet ab = createAnropsbehorighet(bestallning.logiskAdressObject, bestallning.tjanstekontraktObject, bestallning.tjanstekonsumentObject, from, tom)
            bestallning.setNewAnropsbehorighet(ab)
        } else if (anropsbehorighetList.size() == 1) { //есть - трансформирую че есть
            Anropsbehorighet existentAnropsbehorighet = anropsbehorighetList.get(0);
            existentAnropsbehorighet.filter.size()
            if (existentAnropsbehorighet.fromTidpunkt >= from) {
                existentAnropsbehorighet.fromTidpunkt = from
                existentAnropsbehorighet.tomTidpunkt = tom
            } else if (existentAnropsbehorighet.fromTidpunkt < from) {
                existentAnropsbehorighet.setTomTidpunkt(tom)
            }
            bestallning.oldAnropsbehorighet = existentAnropsbehorighet
        }
    }

    private prepareVagval(VagvalBestallning bestallning, Date from, Date tom) {
        List<Vagval> vagvalList = daoService.getVagval(bestallning.logiskAdressObject.hsaId, bestallning.tjanstekontraktObject.namnrymd)
        if (vagvalList.size() > 1) {
            //todo error
        } else if (vagvalList.size() == 0) { //нет такого  - просто создаю новый
            createByBestallning(bestallning)
            Vagval newVagval = createVagval(bestallning.logiskAdressObject, bestallning.tjanstekontraktObject, bestallning.anropsAdressObject, from, tom)
            bestallning.setNewVagval(newVagval)
        } else if (vagvalList.size() == 1) {
            Vagval existentVagval = vagvalList.get(0)
            existentVagval.anropsAdress.vagVal.size()
            if (existentVagval.anropsAdress.rivTaProfil == bestallning.rivtaprofilObject && existentVagval.anropsAdress.tjanstekomponent == bestallning.tjanstekomponentObject &&
                    existentVagval.anropsAdress.adress == bestallning.adress) {
                if (existentVagval.fromTidpunkt >= from) {
                    existentVagval.fromTidpunkt = from
                    existentVagval.tomTidpunkt = tom
                } else if (existentVagval.fromTidpunkt < from) {
                    existentVagval.setTomTidpunkt(tom)
                }
            } else {
                createByBestallning(bestallning)
                Vagval newVagval = createVagval(bestallning.logiskAdressObject, bestallning.tjanstekontraktObject, bestallning.anropsAdressObject, from, tom)
                bestallning.setNewVagval(newVagval)
                if (existentVagval.fromTidpunkt >= from) {
                    existentVagval.deleted = true
                    if(existentVagval.anropsAdress.vagVal.size() == 1){
                        existentVagval.anropsAdress.deleted = true
                    }
                } else if (existentVagval.fromTidpunkt < from) {
                    existentVagval.setTomTidpunkt(generateDateMinusDag(from))
                }
                bestallning.oldVagval = existentVagval
            }
        }
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

    private static Date generateTomDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, 100);
        Date d = new Date(c.getTime().getTime());
        return d;
    }

    private Date generateDateMinusDag(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date d = new Date(c.getTime().getTime());
        return d;
    }
}
