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
import org.apache.shiro.SecurityUtils
import org.springframework.beans.factory.annotation.Autowired
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*
import java.text.SimpleDateFormat

class BestallningService {

    @Autowired
    def DAOService daoService;

    //2018-10-09T10:23:10+0200 Format to date 2018-10-09
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd")

    public def JsonBestallning createOrderObject(String jsonBestallningString) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;
    }

    public validateOrderObjects(JsonBestallning bestallning) {
        //validate deleted Vagval and Anropsbehorighet

        bestallning.getExkludera().getVagval().each() { it ->
            def adress = it.getAdress()
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def logisk = it.getLogiskAdress()
            def kontrakt = it.getTjanstekontrakt()
            def existingVagval = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt)
            if (existingVagval.size() == 0) {
                bestallning.addError("Vagval[%s, %s, %s, %s, %s] som ska tas bort finns inte i databasen.", adress, rivta, komponent, logisk, kontrakt)
            }
        }

        bestallning.getExkludera().getAnropsbehorigheter().each() { it ->
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            def existingAnropsbehorighet = daoService.getAnropsbehorighet(logisk, konsument, kontrakt)
            if (existingAnropsbehorighet.size() == 0) {
                bestallning.addError("Anropsbehorighet[%s, %s, %s] som ska tas bort finns inte i databasen.", logisk, konsument, kontrakt)
            }
        }
        validateLogiskaAdresser(bestallning)
        validateTjanstekomponenter(bestallning)
        validateTjanstekontrakt(bestallning)
        validateAddedVagval(bestallning)
        validateAnropsbehorigheter(bestallning);
    }

    private validateAnropsbehorigheter(JsonBestallning bestallning) {
        bestallning.inkludera.anropsbehorigheter.each() { anropsbehorighetBestallning ->
            def logisk = anropsbehorighetBestallning.getLogiskAdress()
            def konsument = anropsbehorighetBestallning.getTjanstekonsument()
            def kontrakt = anropsbehorighetBestallning.getTjanstekontrakt()

            if (logisk == null || konsument == null || kontrakt == null) {
                bestallning.addError("Det saknas information i json-filen för att kunna skapa Anropsbehorighet.")
                return
            }

            if (!existsLogiskAdressInDBorInOrder(logisk, bestallning)) {
                bestallning.addError("Skapa Anropsbehorighet: LogiskAdress:en med HSAId = %s finns inte.", logisk)
            }

            if (!existsTjanstekomponentInDBorInOrder(konsument, bestallning)) {
                bestallning.addError("Skapa Anropsbehorighet: Tjanstekomponent:en med HSAId = %s finns inte.", konsument)
            }

            if (!existsTjanstekontraktInDBorInOrder(kontrakt, bestallning)) {
                bestallning.addError("Skapa Anropsbehorighet: Tjanstekontrakt:et med namnrymd = %s finns inte.", kontrakt)
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
                bestallning.addError("Det saknas information i json-filen för att kunna skapa Vagval.")
                return
            }

            if (!existsRivtaInDB(rivta, bestallning)) {
                bestallning.addError("Skapa Vagval: RivTaProfil:en med namn = %s finns inte.", rivta)
            }

            if (!existsTjanstekomponentInDBorInOrder(komponent, bestallning)) {
                bestallning.addError("Skapa Vagval: Tjanstekomponent:en med HSAId = %s finns inte.", komponent)
            }

            if (!existsLogiskAdressInDBorInOrder(logisk, bestallning)) {
                bestallning.addError("Skapa Vagval: LogiskAdress:en med HSAId = %s finns inte.", logisk)
            }

            if (!existsTjanstekontraktInDBorInOrder(kontrakt, bestallning)) {
                bestallning.addError("Skapa Vagval: Tjanstekontrakt:et med HSAId = %s finns inte.", kontrakt)
            }

            def vagval = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt)
            if (vagval.size() > 0) {
                bestallning.addError("Vagval[%s, %s, %s, %s, %s]  redan innns i databasen.", adress, rivta, komponent, logisk, kontrakt)
            }
        }
    }

    private validateLogiskaAdresser(JsonBestallning bestallning) {
        bestallning.getInkludera().getLogiskadresser().each() { it ->
            LogiskAdress existLogiskAdress = daoService.getLogiskAdressByHSAId(it.getHsaId())
            if (existLogiskAdress != null && !existLogiskAdress.isDeletedInPublishedVersion()) {
                it.setLogiskAdress(existLogiskAdress)
            }
        }
    }

    private validateTjanstekomponenter(JsonBestallning bestallning) {
        bestallning.getInkludera().getTjanstekomponenter().each() { it ->
            Tjanstekomponent existTjanstekomponent = daoService.getTjanstekomponentByHSAId(it.getHsaId())
            if (existTjanstekomponent != null && !existTjanstekomponent.isDeletedInPublishedVersion()) {
                it.setTjanstekomponent(existTjanstekomponent)
            }
        }
    }

    private validateTjanstekontrakt(JsonBestallning bestallning) {
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

    private boolean existsRivtaInDB(String rivta, JsonBestallning bestallning) {
        RivTaProfil existRivta = daoService.getRivtaByNamn(rivta)
        if (existRivta != null) {
            return true
        } else {
            return false
        }
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
            setMetaData(vagval, true)
            vagval.save(validate: false)
        }

        deleteData.getAnropsbehorigheter().each() { it ->
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            def anropsbehorighet = daoService.getAnropsbehorighet(logisk, konsument, kontrakt)
            setMetaData(anropsbehorighet, true)
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
                    setMetaData(a, false)
                    a.setFromTidpunkt(from)
                    a.setTomTidpunkt(generateTomDate(from))
                    a.setLogiskAdress(newLogiskadresser.get(it.getLogiskadress()))
                    a.setTjanstekontrakt(newTjanstekontrakt.get(it.getTjanstekontrakt()))
                    a.setTjanstekonsument(newTjanstekomponenter.get(it.getTjanstekonsument()))
                    setMetaData(a, false)
                    a.setVersion(0) //  Since we create new, set to 0
                    a.setPubVersion(0) // Same here?
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
                    setMetaData(v, false)
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
                    v.setPubVersion(0) // Same here?
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
        setMetaData(aa, false)
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
                setMetaData(logiskAdress, false)
                def result = logiskAdress.save(validate: false)
            } else {
                //Object already existed in db, so don't create, but maybe update
                LogiskAdress existing = logiskadressBestallning.getLogiskAdress()
                if (!existing.getBeskrivning().equals(logiskadressBestallning.getBeskrivning())) {
                    setMetaData(existing, false)
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
                setMetaData(tjanstekomponent, false)
                def result = tjanstekomponent.save(validate: false)
            } else {
                //Object already existed in db, so don't create, but maybe update
                Tjanstekomponent existing = tjanstekomponentBestallning.getTjanstekomponent()
                if (!existing.getBeskrivning().equals(tjanstekomponentBestallning.getBeskrivning())) {
                    setMetaData(existing, false)
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
                setMetaData(tjanstekontrakt, false)
                def result = tjanstekontrakt.save(validate: false)
            } else {
                //Object already existed in db, so don't create, but maybe update
                Tjanstekontrakt existing = tjanstekontraktBestallning.getTjanstekontrakt()
                if (!existing.getBeskrivning().equals(tjanstekontraktBestallning.getBeskrivning())) {
                    setMetaData(existing, false)
                    existing.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                    def result = existing.save(validate: false)
                }
            }
        }
    }

    private void setMetaData(AbstractVersionInfo versionInfo, isDeleted) {
        def principal = SecurityUtils.getSubject()?.getPrincipal()
        versionInfo.setUpdatedTime(new Date())
        versionInfo.setUpdatedBy(principal)
        versionInfo.setDeleted(isDeleted)
    }

    private java.sql.Date generateTomDate(java.sql.Date fromDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.add(Calendar.YEAR, 100);
        java.sql.Date d = new java.sql.Date(c.getTime().getTime());
        return d;
    }

}
