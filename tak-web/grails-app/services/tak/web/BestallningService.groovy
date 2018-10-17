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

package tak.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.logging.LogFactory
import org.apache.shiro.SecurityUtils
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*
import java.text.SimpleDateFormat

class BestallningService {

    private static final log = LogFactory.getLog(this)
    DAOService daoService;
    def i18nService;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd")

    public def JsonBestallning createOrderObject(String jsonBestallningString) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;
    }

    public validateOrderObjects(JsonBestallning bestallning) {
        validateDeletedVagval(bestallning)
        validateDeletedAnropsbehorigheter(bestallning)

        saveLogiskaAdresserToOrder(bestallning)
        saveTjanstekomponenterToOrder(bestallning)
        saveTjanstekontraktToOrder(bestallning)

        validateAddedVagval(bestallning)
        validateAddedAnropsbehorigheter(bestallning);
    }

    private List<AnropsbehorighetBestallning> validateDeletedAnropsbehorigheter(JsonBestallning bestallning) {
        bestallning.getExkludera().getAnropsbehorigheter().each() { it ->
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            def existingAnropsbehorighet = daoService.getAnropsbehorighet(logisk, konsument, kontrakt)
            if (existingAnropsbehorighet.size() == 0) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.anropsbehorighet", [logisk, konsument, kontrakt]))
            }
        }
    }

    private List<VagvalBestallning> validateDeletedVagval(JsonBestallning bestallning) {
        bestallning.getExkludera().getVagval().each() { it ->
            def adress = it.getAdress()
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def logisk = it.getLogiskAdress()
            def kontrakt = it.getTjanstekontrakt()
            def existingVagval = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt)
            if (existingVagval.size() == 0) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.vagval", [adress, rivta, komponent, logisk, kontrakt]))
            }
        }
    }

    private validateAddedAnropsbehorigheter(JsonBestallning bestallning) {
        bestallning.inkludera.anropsbehorigheter.each() { anropsbehorighetBestallning ->
            def logisk = anropsbehorighetBestallning.getLogiskAdress()
            def konsument = anropsbehorighetBestallning.getTjanstekonsument()
            def kontrakt = anropsbehorighetBestallning.getTjanstekontrakt()

            if (logisk == null || konsument == null || kontrakt == null) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.info.for.anropsbehorighet"))
                return
            }

            if (!existsLogiskAdressInDBorInOrder(logisk, bestallning)) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.logiskAdress.for.anropsbehorighet", [logisk]))
            }

            if (!existsTjanstekomponentInDBorInOrder(konsument, bestallning)) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.tjanstekomponent.for.anropsbehorighet", [konsument]))
            }

            if (!existsTjanstekontraktInDBorInOrder(kontrakt, bestallning)) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [kontrakt]))
            }
        }
    }

    private validateAddedVagval(JsonBestallning bestallning) {
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            def adress = vagvalBestallning.getAdress()
            def rivta = vagvalBestallning.getRivtaprofil()
            def komponent = vagvalBestallning.getTjanstekomponent()
            def logisk = vagvalBestallning.getLogiskAdress()
            def kontrakt = vagvalBestallning.getTjanstekontrakt()

            if (adress == null || rivta == null || komponent == null || logisk == null || kontrakt == null) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.info.for.vagval"))
                return
            }

            if (!existsRivtaInDB(rivta)) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.rivtaprofil.for.vagval", [rivta]))
            }

            if (!existsTjanstekomponentInDBorInOrder(komponent, bestallning)) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.tjanstekomponent.for.vagval", [komponent]))
            }

            if (!existsLogiskAdressInDBorInOrder(logisk, bestallning)) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.logiskAdress.for.vagval", [logisk]))
            }

            if (!existsTjanstekontraktInDBorInOrder(kontrakt, bestallning)) {
                bestallning.addError(i18nService.msg("beställning.error.saknas.tjanstekontrakt.for.vagval", [kontrakt]))
            }

            def vagval = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt)
            if (vagval.size() > 0) {
                bestallning.addError(i18nService.msg("beställning.error.vagval.refan.finns", [adress, rivta, komponent, logisk, kontrakt]))
            }
        }
    }

    private saveLogiskaAdresserToOrder(JsonBestallning bestallning) {
        bestallning.getInkludera().getLogiskadresser().each() { it ->
            LogiskAdress existLogiskAdress = daoService.getLogiskAdressByHSAId(it.getHsaId())
            if (existLogiskAdress != null && !existLogiskAdress.isDeletedInPublishedVersion()) {
                it.setLogiskAdress(existLogiskAdress)
            }
        }
    }

    private saveTjanstekomponenterToOrder(JsonBestallning bestallning) {
        bestallning.getInkludera().getTjanstekomponenter().each() { it ->
            Tjanstekomponent existTjanstekomponent = daoService.getTjanstekomponentByHSAId(it.getHsaId())
            if (existTjanstekomponent != null && !existTjanstekomponent.isDeletedInPublishedVersion()) {
                it.setTjanstekomponent(existTjanstekomponent)
            }
        }
    }

    private saveTjanstekontraktToOrder(JsonBestallning bestallning) {
        bestallning.getInkludera().getTjanstekontrakt().each() { it ->
            Tjanstekontrakt existTjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(it.getNamnrymd())
            if (existTjanstekontrakt && !existTjanstekontrakt.isDeletedInPublishedVersion()) {
                it.setTjanstekontrakt(existTjanstekontrakt)
            }
        }
    }

    private boolean existsLogiskAdressInDBorInOrder(String hsaId, JsonBestallning bestallning) {
        LogiskAdress existLogiskAdress = daoService.getLogiskAdressByHSAId(hsaId)
        if (existLogiskAdress != null) {
            return true
        } else {
            boolean found = false
            bestallning.getInkludera().getLogiskadresser().each() { it ->
                if (it.getHsaId().equals(hsaId)) {
                    found = true
                }
            }
            return found
        }
    }

    private boolean existsTjanstekomponentInDBorInOrder(String hsaId, JsonBestallning bestallning) {
        Tjanstekomponent existTjanstekomponent = daoService.getTjanstekomponentByHSAId(hsaId)
        if (existTjanstekomponent != null) {
            return true
        } else {
            boolean found = false
            bestallning.getInkludera().getTjanstekomponenter().each() { iter ->
                if (iter.getHsaId().equals(hsaId)) {
                    found = true
                }
            }
            return found
        }
    }

    private boolean existsTjanstekontraktInDBorInOrder(String namnrymd, JsonBestallning bestallning) {
        Tjanstekontrakt existTjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(namnrymd)
        if (existTjanstekontrakt != null) {
            return true
        } else {
            boolean found = false
            bestallning.getInkludera().getTjanstekontrakt().each() { iter ->
                if (namnrymd.equals(iter.getNamnrymd())) {
                    found =  true
                }
            }
            return found
        }
    }

    private boolean existsRivtaInDB(String rivta) {
        RivTaProfil existRivta = daoService.getRivtaByNamn(rivta)
        return existRivta != null
    }

    def executeOrder(JsonBestallning bestallning) {
        if (bestallning.isValidBestallning()) {
            deleteObjects(bestallning.getExkludera());
            createObjects(bestallning, bestallning.genomforandeTidpunkt);
        }
    }

    private void deleteObjects(KollektivData deleteData) {
        //Only Vagval and Anropsbehorighet is to be deleted via json...
        //If matching entity object found in db (set in bestallning-> it), set that object to delete..

        deleteData.getVagval().each() { it ->
            def adress = it.getAdress()
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def logisk = it.getLogiskAdress()
            def kontrakt = it.getTjanstekontrakt()
            Vagval vagval = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt).get(0)
            setMetaData(vagval, true, vagval.getPubVersion())
            vagval.save(validate: false)
        }

        deleteData.getAnropsbehorigheter().each() { it ->
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            def anropsbehorighet = daoService.getAnropsbehorighet(logisk, konsument, kontrakt).get(0)
            setMetaData(anropsbehorighet, true, anropsbehorighet.getPubVersion())
            anropsbehorighet.save(validate: false)
        }
    }

    private void createObjects(JsonBestallning bestallning, Date fromTidpunkt) {
        KollektivData newData = bestallning.getInkludera()
        String s = format.format(fromTidpunkt)
        java.sql.Date from = java.sql.Date.valueOf(s)
        try {
            createLogiskAddresser(newData.getLogiskadresser())
            createTjanstekomponenter(newData.getTjanstekomponenter())
            createTjanstekontrakt(newData.getTjanstekontrakt())

            newData.getAnropsbehorigheter().each() { it ->
                if (it.getAnropsbehorighet() == null) {
                    Anropsbehorighet a = new Anropsbehorighet()
                    a.setFromTidpunkt(from)
                    a.setTomTidpunkt(generateTomDate(from))
                    a.setLogiskAdress(newLogiskadresser.get(it.getLogiskadress()))
                    a.setTjanstekontrakt(newTjanstekontrakt.get(it.getTjanstekontrakt()))
                    a.setTjanstekonsument(newTjanstekomponenter.get(it.getTjanstekonsument()))
                    setMetaData(a, false, null)
                    a.setVersion(0) //  Since we create new, set to 0
                    //a.setIntegrationsavtal()  // ??
                    def result = a.save(validate: false)
                    System.out.println("What is the result ? " + result)
                }
            }

            int numberOfVagval
            numberOfVagval = newData.getVagval().size()
            //If no matching object found in db, so ok to save...
            newData.getVagval().each() { it ->
                if (it.getVagval() == null) {
                    Vagval v = new Vagval()
                    setMetaData(v, false, null)
                    AnropsAdress aa
                    List<AnropsAdress> anropsAdressList = daoService.getAnropsAdress(it.getAdress(), it.getRivtaprofil(), it.getTjanstekomponent())
                    if (anropsAdressList.size() > 0) {
                        aa = anropsAdressList.get(0)
                    } else {
                        aa = createAnropsAdress(it.getAdress(), it.getRivtaprofil(), it.getTjanstekomponent())
                    }
                    v.setAnropsAdress(aa)
                    v.setFromTidpunkt(from)
                    v.setTomTidpunkt(generateTomDate(from))
                    LogiskAdress la = daoService.getLogiskAdressByHSAId(it.getLogiskAdress())
                    v.setLogiskAdress(la)
                    Tjanstekontrakt tk = daoService.getTjanstekontraktByNamnrymd(it.getTjanstekontrakt())
                    v.setTjanstekontrakt(tk)
                    v.setVersion(0) // Since we create new, set to 0
                    numberOfVagval--  // Clumsy, but only testing what to expect when saving different ways..
                    if (numberOfVagval == 0) {
                        def result = v.save(flush: true)
                        System.out.println("What is the result ? " + result)
                    } else {
                        def result = v.save(validate:false)
                        System.out.println("What is the result ? " + result)
                    }
                }
            }

        } catch (Exception e) {
            //Something bad happened during save to db.. how rollback?
            bestallning.addError("Det gick fel när beställningen sparades till databasen!")
            return
        }
    }

    private AnropsAdress createAnropsAdress(String adress, String rivta, String komponent) {
        AnropsAdress aa = new AnropsAdress()
        RivTaProfil rivTaProfil = daoService.getRivtaByNamn(rivta)
        Tjanstekomponent tjanstekomponent = daoService.getTjanstekomponentByHSAId(komponent)
        setMetaData(aa, false, null)
        aa.setAdress(adress)
        aa.setRivTaProfil(rivTaProfil)
        aa.setTjanstekomponent(tjanstekomponent)
        aa.setVersion(0)  // Since we create new, set to 0
        aa.setPubVersion(0) //  Same here?
        def result = aa.save(validate: false)
        System.out.println("What is the result ? " + result)
        return aa
    }

    private void createLogiskAddresser(List<LogiskadressBestallning> logiskadressBestallningar) {
        HashMap<String, LogiskAdress> newLogiskAddresser = new HashMap<>()

        logiskadressBestallningar.each() { logiskadressBestallning ->
            //if non-existing in db, we create the object and save in hashmap for use later..
            if (logiskadressBestallning.getLogiskAdress() == null) {
                LogiskAdress logiskAdress = new LogiskAdress()
                logiskAdress.setHsaId(logiskadressBestallning.getHsaId())
                logiskAdress.setBeskrivning(logiskadressBestallning.getBeskrivning())
                setMetaData(logiskAdress, false, null)
                def result = logiskAdress.save(validate: false)
            } else {
                //Object already existed in db, so don't create, but maybe update
                LogiskAdress existing = logiskadressBestallning.getLogiskAdress()
                if (!existing.getBeskrivning().equals(logiskadressBestallning.getBeskrivning())) {
                    setMetaData(existing, false, existing.getPubVersion())
                    existing.setBeskrivning(logiskadressBestallning.getBeskrivning())
                    def result = existing.save(validate: false)
                }
            }
        }
    }

    private void createTjanstekomponenter(List<TjanstekomponentBestallning> tjanstekomponentBestallningar) {

        tjanstekomponentBestallningar.each() { tjanstekomponentBestallning ->
            if (tjanstekomponentBestallning.getTjanstekomponent() == null) {
                Tjanstekomponent tjanstekomponent = new Tjanstekomponent()
                tjanstekomponent.setHsaId(tjanstekomponentBestallning.getHsaId())
                tjanstekomponent.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
                setMetaData(tjanstekomponent, false, null)
                def result = tjanstekomponent.save(validate: false)
            } else {
                //Object already existed in db, so don't create, but maybe update
                Tjanstekomponent existing = tjanstekomponentBestallning.getTjanstekomponent()
                if (!existing.getBeskrivning().equals(tjanstekomponentBestallning.getBeskrivning())) {
                    setMetaData(existing, false, existing.getPubVersion())
                    existing.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
                    def result = existing.save(validate: false)
                }
            }
        }
    }

    private void createTjanstekontrakt(List<TjanstekontraktBestallning> tjanstekontraktBestallningar) {

        tjanstekontraktBestallningar.each() { tjanstekontraktBestallning ->
            if (tjanstekontraktBestallning.getTjanstekontrakt() == null) {
                Tjanstekontrakt tjanstekontrakt = new Tjanstekontrakt()
                tjanstekontrakt.setNamnrymd(tjanstekontraktBestallning.getNamnrymd())
                tjanstekontrakt.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                tjanstekontrakt.setMajorVersion(tjanstekontraktBestallning.getMajorVersion())
                tjanstekontrakt.setMinorVersion(tjanstekontraktBestallning.getMinorVersion())
                setMetaData(tjanstekontrakt, false, null)
                def result = tjanstekontrakt.save(validate: false)
            } else {
                //Object already existed in db, so don't create, but maybe update
                Tjanstekontrakt existing = tjanstekontraktBestallning.getTjanstekontrakt()
                if (!existing.getBeskrivning().equals(tjanstekontraktBestallning.getBeskrivning())) {
                    setMetaData(existing, false, existing.getPubVersion())
                    existing.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                    def result = existing.save(validate: false)
                }
            }
        }
    }

    private void setMetaData(AbstractVersionInfo versionInfo, isDeleted, String pubVersion) {
        def principal = SecurityUtils.getSubject()?.getPrincipal()
        versionInfo.setUpdatedTime(new Date())
        versionInfo.setUpdatedBy(principal)
        versionInfo.setDeleted(isDeleted)
        versionInfo.setPubVersion(generatePubversion(pubVersion))
    }

    private java.sql.Date generateTomDate(java.sql.Date fromDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.add(Calendar.YEAR, 100);
        java.sql.Date d = new java.sql.Date(c.getTime().getTime());
        return d;
    }

    private static String generatePubversion(String existing) {
        if (existing == null) {
            return "0";
        } else {
            try {
                Long l;
                l = Long.parseLong(existing);
                return "" + ++l;
            } catch (Exception e) {
                return "0";
            }
        }
    }

}
