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
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*

class BestallningService {

    def DAOService daoService;

    def JsonBestallning createBestallningObject(String jsonBestallningString) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;
    }

    def findAllOrderObjects(JsonBestallning bestallning) {

        //TODO: Not clear what criteria to use for finding correspondent objects in db
        bestallning.getExkludera().getVagval().each() { it ->
            def Vagval existingVagval
            def adress = it.getAdress()
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def getAnropsadress = "(select id from AnropsAdress where deleted != 1 and adress = '" + adress + "' and rivTaProfil.id = " +
                    "(select id from RivTaProfil where deleted != 1 and namn = '" + rivta + "') and tjanstekomponent.id = " +
                    "(select id from Tjanstekomponent where deleted != 1 and hsaId = '" + komponent + "')) "
            def logisk = it.getLogiskAdress()
            def getLogiskAdress = "(select id from LogiskAdress where deleted != 1 and hsaId = '" + logisk + "')"
            def kontrakt = it.getTjanstekontrakt()
            def getTjanstekontrakt = "(select id from Tjanstekontrakt where deleted != 1 and namnrymd = '" + kontrakt + "')"
            //Final query...
            def results = Vagval.findAll(" from Vagval as db where db.deleted != 1 and db.anropsAdress.id = " + getAnropsadress +
                    " and db.logiskAdress.id = " + getLogiskAdress + " and tjanstekontrakt.id = " + getTjanstekontrakt)
            if (results.size() > 0) {
                it.setVagval(results.get(0))
                existingVagval = it.getVagval()
                existingVagval.setDeleted(true)
            } else {
                //No object found OR more than one found, so we can't delete..?
                bestallning.addError("Vagval som ska tas bort finns inte i databasen.")
            }
        }

        bestallning.getExkludera().getAnropsbehorigheter().each() { it ->
            Anropsbehorighet anropsbehorighet
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            def results = Anropsbehorighet.findAll(" from Anropsbehorighet as db where db.deleted != 1 and db.logiskAdress.id = " +
                    "(select id from LogiskAdress where hsaId = '" + logisk +
                    "') and db.tjanstekonsument.id = (select id from Tjanstekomponent where hsaId = '" + konsument + "') " +
                    "and db.tjanstekontrakt.id = (select id from Tjanstekontrakt where namnrymd = '" + kontrakt + "')")
            if (results.size() > 0) {
                it.setAnropsbehorighet(results.get(0))
                anropsbehorighet = it.getAnropsbehorighet()
                anropsbehorighet.setDeleted(true)
            } else {
                //No object found OR more than one found, so we can't delete..?
                bestallning.addError("Anropsbehorighet som ska tas bort finns inte i databasen.")
            }
        }

        //Adding objects
        bestallning.getInkludera().getLogiskadresser().each() { it ->
            String hsaId = it.getHsaId()
            def results = LogiskAdress.findAll(" from LogiskAdress db where db.deleted != 1 and db.hsaId = '" + hsaId + "'")
            if (results.size() > 0) {
                LogiskAdress existing = results.get(0)
                //LogiskAdress already exists, but maybe update description? Take care of that in JsonBestallningSave
                it.setLogiskAdress(existing)
            }
        }

        bestallning.getInkludera().getTjanstekomponenter().each() { it ->
            String hsaId = it.getHsaId()
            def results = Tjanstekomponent.findAll(" from Tjanstekomponent as db where db.deleted != 1 and db.hsaId = '" + hsaId + "'")
            if (results.size() > 0) {
                it.setTjanstekomponent(results.get(0))
            }
        }


        bestallning.getInkludera().getTjanstekontrakt().each() { it ->
            String namespace = it.getNamnrymd()
            def results = Tjanstekontrakt.findAll(" from Tjanstekontrakt as db where db.deleted != 1 and db.namnrymd = '" + namespace + "'")
            if (results.size() > 0) {
                it.setTjanstekontrakt(results.get(0))
            }
        }

        validVagval(bestallning)
        validAnropsbehorigheter(bestallning);
    }

    def validAnropsbehorigheter(JsonBestallning bestallning) {
        bestallning.inkludera.anropsbehorigheter.each() { anropsbehorighetBestallning ->
            def logisk = anropsbehorighetBestallning.getLogiskAdress()
            def konsument = anropsbehorighetBestallning.getTjanstekonsument()
            def kontrakt = anropsbehorighetBestallning.getTjanstekontrakt()

            if (logisk == null || konsument == null || kontrakt == null) {
                bestallning.addError("Det saknas information i json-filen för att kunna skapa Anropsbehorighet.")
                return
            }

            if (!findLogiskAdressInDBorInOrder(logisk)) {
                bestallning.addError("Skapa Anropsbehorighet: LogiskAdress:en med HSAId = " + logisk + " finns inte.")
            }

            if (findTjanstekomponentInDBorInOrder(konsument)) {
                bestallning.addError("Skapa Anropsbehorighet: Tjanstekomponent:en finns inte.")
            }

            if (findTjanstekontraktInDBorInOrder(kontrakt)) {
                bestallning.addError("Skapa Anropsbehorighet: Tjanstekontrakt:et finns inte.")
            }

//            if (existLogiskAdress != null && existTjanstekonsument != null && existTjanstekontrakt != null) {
//                //Check if Anropsbehorighet already exist in db..
//                def results = Anropsbehorighet.findAll(" from Anropsbehorighet as db where db.deleted != 1 and db.logiskAdress.id = " +
//                        existLogiskAdress.getId() + " and db.tjanstekonsument.id = " + existTjanstekonsument.getId() +
//                        " and db.tjanstekontrakt.id = " + existTjanstekontrakt.getId())
//                if (results.size() > 0) {
//                    anropsbehorighetBestallning.setAnropsbehorighet(results.get(0))
//                }


        }
    }

    def validVagval(JsonBestallning bestallning) {
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            def adressvv = vagvalBestallning.getAdress()
            def rivta = vagvalBestallning.getRivtaprofil()
            def komponent = vagvalBestallning.getTjanstekomponent()
            def logisk = vagvalBestallning.getLogiskAdress()
            def kontrakt = vagvalBestallning.getTjanstekontrakt()

            if (adressvv == null || rivta == null || komponent == null || logisk == null || kontrakt == null) {
                bestallning.addError("Det saknas information i json-filen för att kunna skapa Vagval.")
                return
            }

            AnropsAdress existAnropsAdress
            def results1 = AnropsAdress.findAll(" from AnropsAdress where deleted != 1 and adress = '" +
                    adressvv + "' and rivTaProfil.id = (select id from RivTaProfil where deleted != 1 and namn = '" + rivta + "') and "
                    + "tjanstekomponent.id = (select id from Tjanstekomponent where deleted != 1 and hsaId = '" + komponent + "')")
            if (results1.size() != 0) {
                existAnropsAdress = results1.get(0)
            } else {
                //If the AnropsAdress not is found in db, it should be created with the values adress + rivta + komponent? Yes...
            }

            if (!findLogiskAdressInDBorInOrder(logisk)) {
                bestallning.addError("Skapa Anropsbehorighet: LogiskAdress:en med HSAId = " + logisk + " finns inte.")
            }


            if (findTjanstekontraktInDBorInOrder(kontrakt)) {
                bestallning.addError("Skapa Anropsbehorighet: Tjanstekontrakt:et finns inte.")
            }

            if (existAnropsAdress != null && existLogiskAdress != null && existTjanstekontrakt != null) {
                //Check if Vagval already exist in db..
                def results = Vagval.findAll(" from Vagval as db where db.deleted != 1 and db.anropsAdress.id = " + existAnropsAdress.getId() +
                        " and db.logiskAdress.id = " + existLogiskAdress.getId() + " and tjanstekontrakt.id = " + existTjanstekontrakt.getId())
                if (results.size() > 0) {
                    vagvalBestallning.setVagval(results.get(0))

                }
            }
        }
    }

    boolean findLogiskAdressInDBorInOrder(String hsaId, JsonBestallning bestallning) {
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

    boolean findTjanstekomponentInDBorInOrder(String hsaId, JsonBestallning bestallning) {
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
                if (iter.namnrymd().equals(namnrymd)) return true
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
