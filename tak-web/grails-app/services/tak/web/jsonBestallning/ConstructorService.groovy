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
        JsonBestallning bestallning = data.getBestallning()
        bestallning.getExkludera().getVagval().each() { it ->
            List<Vagval> list = createByBestallningForDelete(it, bestallning.genomforandeTidpunkt, generateTomDate(bestallning.genomforandeTidpunkt))
            validatingService.validateVagvalForDubblett(list, data)
            if (data.hasErrors()) return

            if (list.size() == 1) {
                Vagval vv = list.get(0)
                vv.anropsAdress.vagVal.size()
                data.putOldVagval(it, vv)
            }
        }
        bestallning.getExkludera().getAnropsbehorigheter().each() { it ->
            List<Anropsbehorighet> list = createByBestallningForDelete(it, bestallning.genomforandeTidpunkt, generateTomDate(bestallning.genomforandeTidpunkt))
            validatingService.validateAnropsbehorighetForDubblett(list, data)
            if (data.hasErrors()) return

            if (list.size() == 1) {
                data.put(it, list.get(0))
            }
        }
        bestallning.getInkludera().getLogiskadresser().each() { logiskadressBestallning ->
            LogiskAdress logiskAdress = createByBestallning(logiskadressBestallning)
            data.put(logiskadressBestallning, logiskAdress)
        }
        bestallning.getInkludera().getTjanstekomponenter().each() { tjanstekomponentBestallning ->
            Tjanstekomponent tjanstekomponent = createByBestallning(tjanstekomponentBestallning)
            data.put(tjanstekomponentBestallning, tjanstekomponent)
        }
        bestallning.getInkludera().getTjanstekontrakt().each() { tjanstekontraktBestallning ->
            Tjanstekontrakt tjanstekontrakt = createByBestallning(tjanstekontraktBestallning)
            data.put(tjanstekontraktBestallning, tjanstekontrakt)
        }
        validatingService.validatePlainObjects(bestallning, data)
    }

    private LogiskAdress createByBestallning(LogiskadressBestallning bestallning) {
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
        logiskAdress
    }

    private Tjanstekontrakt createByBestallning(TjanstekontraktBestallning bestallning) {
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
        tjanstekontrakt
    }

    private Tjanstekomponent createByBestallning(TjanstekomponentBestallning bestallning) {
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
        tjanstekomponent
    }

    private createByBestallningForDelete(AnropsbehorighetBestallning bestallning, Date from, Date tom) {
        List<Anropsbehorighet> anropsbehorigheter = daoService.getAnropsbehorighet(bestallning.logiskAdress, bestallning.tjanstekonsument, bestallning.tjanstekontrakt, from, tom)
        anropsbehorigheter.each() { ab ->
            ab.getFilter().size()
            ab.tomTidpunkt = generateDateMinusDag(from)
        }
        return anropsbehorigheter
    }

    private List<Vagval> createByBestallningForDelete(VagvalBestallning bestallning, Date from, Date tom) {
        List<Vagval> vagvalList = daoService.getVagval(bestallning.logiskAdress, bestallning.tjanstekontrakt, from, tom)
        vagvalList.each() { vv ->
            vv.tomTidpunkt = generateDateMinusDag(from)
        }
        return vagvalList
    }


    void prepareComplexObjectsRelations(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning()
        bestallning.getInkludera().getAnropsbehorigheter().each() { abBestallning ->
            Tjanstekomponent tjanstekonsumentObject = findTjanstekomponentInDBorOrder(abBestallning.tjanstekonsument, data)
            LogiskAdress logiskAdressObject = findLogiskAdressInDBorOrder(abBestallning.logiskAdress, data)
            Tjanstekontrakt tjanstekontraktObject = findTjanstekontraktInDBorOrder(abBestallning.tjanstekontrakt, data)
            data.putRelations(abBestallning, logiskAdressObject, tjanstekonsumentObject, tjanstekontraktObject)
        }

        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            RivTaProfil rivtaprofilObject = findRivtaInDB(vagvalBestallning.rivtaprofil)
            Tjanstekomponent tjanstekomponentObject = findTjanstekomponentInDBorOrder(vagvalBestallning.tjanstekomponent, data)
            LogiskAdress logiskAdressObject = findLogiskAdressInDBorOrder(vagvalBestallning.logiskAdress, data)
            Tjanstekontrakt tjanstekontraktObject = findTjanstekontraktInDBorOrder(vagvalBestallning.tjanstekontrakt, data)
            data.putRelations(vagvalBestallning, logiskAdressObject, tjanstekomponentObject, tjanstekontraktObject, rivtaprofilObject)
        }

        validatingService.validateComplexObjectRelations(bestallning, data)
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

        Date from = bestallning.genomforandeTidpunkt
        Date tom = generateTomDate(bestallning.genomforandeTidpunkt)

        bestallning.inkludera.anropsbehorigheter.each() { abBestallning ->
            prepareAnropsbehorighet(abBestallning, data, from, tom)
        }

        bestallning.inkludera.vagval.each() { vvBestallning ->
            prepareVagval(vvBestallning, data, from, tom)
        }
    }

    private prepareAnropsbehorighet(AnropsbehorighetBestallning bestallning, BestallningsData data, Date from, Date tom) {
        List<Anropsbehorighet> anropsbehorighetList = daoService.getAnropsbehorighet(bestallning.logiskAdress, bestallning.tjanstekonsument, bestallning.tjanstekontrakt, from, tom)
        validatingService.validateAnropsbehorighetForDubblett(anropsbehorighetList, data)
        if (data.hasErrors()) return
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

        validatingService.validateVagvalForDubblett(vagvalList, data)
        if (data.hasErrors()) return

        if (vagvalList.size() == 0) {
            AnropsAdress adress = findOrCreateAnropaAdress(bestallning, vvData.profil, vvData.tjanstekomponent)
            validatingService.validateAnropAddress(adress, data)
            if (data.hasErrors()) return

            Vagval newVagval = createVagval(vvData.logiskadress, vvData.tjanstekontrakt, adress, from, tom)
            data.putNewVagval(bestallning, newVagval)
        } else if (vagvalList.size() == 1){
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

    private static Date generateTomDate(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.YEAR, 100);
            Date d = new Date(c.getTime().getTime());
            return d;
        }
        return null;
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
