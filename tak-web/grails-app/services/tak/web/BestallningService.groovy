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

class BestallningService {

    @Autowired
    def DAOService daoService;

    public def JsonBestallning createBestallningObject(String jsonBestallningString) {
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
            Vagval existingVagval = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt)

            if (existingVagval) {
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

        validateAddedVagval(bestallning)
        validateDeletedAnropsbehorigheter(bestallning);
    }

    private validateDeletedAnropsbehorigheter(JsonBestallning bestallning) {
        bestallning.inkludera.anropsbehorigheter.each() { anropsbehorighetBestallning ->
            def logisk = anropsbehorighetBestallning.getLogiskAdress()
            def konsument = anropsbehorighetBestallning.getTjanstekonsument()
            def kontrakt = anropsbehorighetBestallning.getTjanstekontrakt()

            if (logisk == null || konsument == null || kontrakt == null) {
                bestallning.addError("Det saknas information i json-filen för att kunna skapa Anropsbehorighet.")
                return
            }

            if (!findLogiskAdressInDBorInOrder(logisk, bestallning)) {
                bestallning.addError("Skapa Anropsbehorighet: LogiskAdress:en med HSAId = %s finns inte.", logisk)
            }

            if (!findTjanstekomponentInDBorInOrder(konsument, bestallning)) {
                bestallning.addError("Skapa Anropsbehorighet: Tjanstekomponent:en med HSAId = %s finns inte.", konsument)
            }

            if (!findTjanstekontraktInDBorInOrder(kontrakt, bestallning)) {
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

            if (!findLogiskAdressInDBorInOrder(logisk, bestallning)) {
                bestallning.addError("Skapa Vagval: LogiskAdress:en med HSAId = %s finns inte.", logisk)
            }


            if (!findTjanstekontraktInDBorInOrder(kontrakt, bestallning)) {
                bestallning.addError("Skapa Vagval: Tjanstekontrakt:et med HSAId = %s finns inte.", kontrakt)
            }

            def vagval = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt)
            if (vagval.size() > 0) {
                bestallning.addError("Vagval[%s, %s, %s, %s, %s]  redan innns i databasen.", adress, rivta, komponent, logisk, kontrakt)
            }
        }
    }

    private boolean findLogiskAdressInDBorInOrder(String hsaId, JsonBestallning bestallning) {
        LogiskAdress existLogiskAdress = daoService.getLogiskAdressByHSAId(hsaId)
        if (existLogiskAdress != null) {
            return true
        } else {
            bestallning.getInkludera().getLogiskadresser().each() { it ->
                if (it.getHsaId().equals(hsaId)) return true
            }
        }
        return false
    }

    private boolean findTjanstekomponentInDBorInOrder(String hsaId, JsonBestallning bestallning) {
        Tjanstekomponent existTjanstekomponent = daoService.getTjanstekomponentByHSAId(hsaId)
        if (existTjanstekomponent != null) {
            return true
        } else {
            bestallning.getInkludera().getTjanstekomponenter().each() { iter ->
                if (iter.getHsaId().equals(hsaId)) return true
            }
        }
        return false
    }

    boolean findTjanstekontraktInDBorInOrder(String namnrymd, JsonBestallning bestallning) {
        Tjanstekontrakt existTjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(namnrymd)
        if (existTjanstekontrakt != null) {
            return true
        } else {
            bestallning.getInkludera().getTjanstekontrakt().each() { iter ->
                if (namnrymd.equals(iter.getNamnrymd())) return true
            }
        }
        return false
    }

    def executeOrder(JsonBestallning bestallning) {
        if (bestallning.isValidBestallning()) {
            deleteObjects(bestallning.getExkludera());
            createObjects(bestallning.getInkludera(), bestallning.genomforandeTidpunkt);
        }
    }

    private void deleteObjects(KollektivData deleteData) {
        //Only Vagval and Anropsbehorighet is to be deleted via json...
        //If matching entity object found in db (set in bestallning-> it), set that object to delete..

        deleteData.getVagval().each() { it ->
            if (it.getVagval() != null) {
                Vagval vagval = it.getVagval()
                setMetaData(vagval, true)
                def result = vagval.save(validate: false)
                System.out.println("What is the result ? " + result)
            }
        }

        deleteData.getAnropsbehorigheter().each() { it ->
            if (it.getAnropsbehorighet() != null) {
                Anropsbehorighet anropsbehorighet = it.getAnropsbehorighet()
                setMetaData(anropsbehorighet, true)
                def result = anropsbehorighet.save(validate: false)
                System.out.println("What is the result ? " + result)
            }
        }
    }

    private void createObjects(KollektivData newData, Date fromTidpunkt) {
        try {
            HashMap<String, LogiskAdress> newLogiskadresser = createLogiskAddresser(newData.getLogiskadresser())

            HashMap<String, Tjanstekomponent> newTjanstekomponenter = createTjanstekomponenter(newData.getTjanstekomponenter())

            HashMap<String, Tjanstekontrakt> newTjanstekontrakt = createTjanstekontrakt(newData.getTjanstekontrakt())

            newData.getAnropsbehorigheter().each() { it ->
                if (it.getAnropsbehorighet() == null) {
                    Anropsbehorighet a = new Anropsbehorighet()
                    setMetaData(a, false)
                    a.setFromTidpunkt(fromTidpunkt)
                    a.setTomTidpunkt(generateTomDate(fromTidpunkt))
                    a.setLogiskAdress(it.getLogiskadress())  //Must find id from db?
                    a.setTjanstekontrakt(it.getTjanstekontrakt()) //Must find id from db?
                    a.setTjanstekonsument(it.getTjanstekonsument()) //Must find id from db
                    a.setVersion() //  ??
                    a.setPubVersion() //  ??
                    a.setIntegrationsavtal()  // ??
                    def result = a.save(validate: false)
                    System.out.println("What is the result ? " + result)
                }
            }

            //If no matching object found in db, so ok to save ? Depending on search criteria...
            newData.getVagval().each() { it ->
                if (it.getVagval() == null) {
                    //Must create a new object  I presume..
                    Vagval v = new Vagval()
                    setMetaData(v, false)
                    v.setAnropsAdress(it.adress)  //Must find id from db?
                    v.setFromTidpunkt(fromTidpunkt)
                    v.setTomTidpunkt(generateTomDate(fromTidpunkt))
                    v.setLogiskAdress(it.getLogiskadress())  //Must find id from db?
                    v.setTjanstekontrakt(it.getTjanstekontrakt()) //Must find id from db?
                    v.setVersion() //  ??
                    v.setPubVersion() //  ??
                    def result = v.save(flush: true)
                    System.out.println("What is the result ? " + result)
                }
            }

        } catch (Exception e) {
            //Something bad happened during save to db.. how rollback?
            def String errorString = "Det gick fel när beställningen sparades till databasen!"
            return
        }
    }

    private HashMap<String, LogiskAdress> createLogiskAddresser(List<LogiskadressBestallning> logiskadressBestallningar) {
        HashMap<String, LogiskAdress> newLogiskAddresser = new HashMap<>()

        logiskadressBestallningar.each() { logiskadressBestallning ->
            //if non-existing in db, we create the object and save in hashmap for use later..
            if (logiskadressBestallning.getLogiskAdress() == null) {
                LogiskAdress logiskAdress = new LogiskAdress()
                logiskAdress.setHsaId(logiskadressBestallning.getHsaId())
                logiskAdress.setBeskrivning(logiskadressBestallning.getBeskrivning())
                setMetaData(logiskAdress, false)
                def result = logiskAdress.save(validate: false)
                newLogiskAddresser.put(logiskAdress.getHsaId(), logiskAdress)
            } else {
                //Object already existed in db, so don't create if identical, but can still be used later
                LogiskAdress logiskAdress = logiskadressBestallning.getLogiskAdress()
                if (!logiskAdress.getBeskrivning().equals(logiskadressBestallning.getBeskrivning())) {
                    logiskAdress.setBeskrivning(logiskadressBestallning.getBeskrivning())
                    def result = logiskAdress.save(validate: false)
                }
                //                newLogiskAddresser.put(logiskAdress.getHsaId(), logiskAdress)
            }
        }
        return newLogiskAddresser
    }

    private HashMap<String, Tjanstekomponent> createTjanstekomponenter(List<TjanstekomponentBestallning> tjanstekomponentBestallningar) {
        HashMap<String, Tjanstekomponent> newTjanstekomponenter = new HashMap<>()

        tjanstekomponentBestallningar.each() { tjanstekomponentBestallning ->
            if (tjanstekomponentBestallning.getTjanstekomponent() == null) {
                Tjanstekomponent tjanstekomponent = new Tjanstekomponent()
                tjanstekomponent.setHsaId(tjanstekomponentBestallning.getHsaId())
                tjanstekomponent.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
                setMetaData(tjanstekomponent, false)
                def result = tjanstekomponent.save(validate: false)
                newTjanstekomponenter.put(tjanstekomponent.getHsaId(), tjanstekomponent)
            } else {
                //Object already existed in db, so don't create, but can still be used later
                Tjanstekomponent existing = tjanstekomponentBestallning.getTjanstekomponent()
                if (!existing.getBeskrivning().equals(tjanstekomponentBestallning.getBeskrivning())) {
                    existing.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
                    def result = existing.save(validate: false)
                }
                //                newTjanstekomponenter.put(existing.getHsaId(), existing)
            }
        }
        return newTjanstekomponenter
    }

    private HashMap<String, Tjanstekontrakt> createTjanstekontrakt(List<TjanstekontraktBestallning> tjanstekontraktBestallningar) {
        HashMap<String, Tjanstekontrakt> newTjanstekontrakt = new HashMap<>()
        tjanstekontraktBestallningar.each() { tjanstekontraktBestallning ->
            if (tjanstekontraktBestallning.getTjanstekontrakt() == null) {
                Tjanstekontrakt tjanstekontrakt = new Tjanstekontrakt()
                tjanstekontrakt.setNamnrymd(tjanstekontraktBestallning.getNamnrymd())
                tjanstekontrakt.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                tjanstekontrakt.setMajorVersion(tjanstekontraktBestallning.getMajorVersion())
                tjanstekontrakt.setMinorVersion(tjanstekontraktBestallning.getMinorVersion())
                setMetaData(tjanstekontrakt, false)
                def result = tjanstekontrakt.save(validate: false)
                newTjanstekontrakt.put(tjanstekontrakt.getNamnrymd(), tjanstekontrakt)
            } else {
                //Object already existed in db, so don't create, but can still be used later
                Tjanstekontrakt tjanstekontrakt = tjanstekontraktBestallning.getTjanstekontrakt()
                if (!tjanstekontrakt.getBeskrivning().equals(tjanstekontraktBestallning.getBeskrivning())) {
                    tjanstekontrakt.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                    def result = tjanstekontrakt.save(validate: false)
                }
                //                newTjanstekontrakt.put(tjanstekontrakt.getNamnrymd(), tjanstekontrakt)
            }
        }
        return newTjanstekontrakt
    }

    private void setMetaData(AbstractVersionInfo versionInfo, isDeleted) {
        def principal = SecurityUtils.getSubject()?.getPrincipal()
        versionInfo.setUpdatedTime(new Date())
        versionInfo.setUpdatedBy(principal)
        versionInfo.setDeleted(isDeleted)
    }

    private Date generateTomDate(Date fromDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.add(Calendar.YEAR, 100);
        return c.getTime();
    }

}
