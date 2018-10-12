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
import se.skltp.tak.web.jsonBestallning.JsonBestallning

class BestallningService {

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

        bestallning.getInkludera().getVagval().each() { it ->
            def adressvv = it.getAdress()
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def logisk = it.getLogiskAdress()
            def kontrakt = it.getTjanstekontrakt()

            if (adressvv == null || rivta == null || komponent == null || logisk == null || kontrakt == null) {
                //Abort fill exception with message
                bestallning.addError("Det saknas information i json-filen för att kunna skapa Vagval.")
            } else {
                AnropsAdress existAnropsAdress
                def results1 = AnropsAdress.findAll(" from AnropsAdress where deleted != 1 and adress = '" +
                        adressvv + "' and rivTaProfil.id = (select id from RivTaProfil where deleted != 1 and namn = '" + rivta + "') and "
                        + "tjanstekomponent.id = (select id from Tjanstekomponent where deleted != 1 and hsaId = '" + komponent + "')")
                if (results1.size() != 0) {
                    existAnropsAdress = results1.get(0)
                } else {
                    //If the AnropsAdress not is found in db, it should be created with the values adress + rivta + komponent? Yes...
                }
                LogiskAdress existLogiskAdress
                def results2 = LogiskAdress.findAll(" from LogiskAdress where deleted != 1 and hsaId = '" + logisk + "'")
                if (results2.size() > 0) {
                    existLogiskAdress = results2.get(0)
                } else {
                    //Is the needed LogiskAdress included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getInkludera().getLogiskadresser().each() { iter ->
                        if (iter.getHsaId().equals(logisk)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No LogiskAdress in db and not in json file.. fill exception with message
                        bestallning.addError("Skapa Vagval: Det saknas LogiskAdress.")
                    }
                }
                Tjanstekontrakt existTjanstekontrakt
                def results3 = Tjanstekontrakt.findAll(" from Tjanstekontrakt where deleted != 1 and namnrymd = '" + kontrakt + "'")
                if (results3.size() > 0) {
                    existTjanstekontrakt = results3.get(0)
                } else {
                    //Is the needed Tjanstekontrakt included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getInkludera().getTjanstekontrakt().each() { iter ->
                        if (iter.getNamnrymd().equals(kontrakt)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No Tjanstekontrakt in db and not in json file.. fill exception with message
                        bestallning.addError("Skapa Vagval: Det saknas Tjanstekontrakt.")
                    }
                }
                if (existAnropsAdress != null && existLogiskAdress != null && existTjanstekontrakt != null) {
                    //Check if Vagval already exist in db..
                    def results = Vagval.findAll(" from Vagval as db where db.deleted != 1 and db.anropsAdress.id = " + existAnropsAdress.getId() +
                            " and db.logiskAdress.id = " + existLogiskAdress.getId() + " and tjanstekontrakt.id = " + existTjanstekontrakt.getId())
                    if (results.size() > 0) {
                        it.setVagval(results.get(0))
                    }
                }
            }
        }

        bestallning.getInkludera().getAnropsbehorigheter().each() { it ->
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            if (logisk == null || konsument == null || kontrakt == null) {
                //Abort fill exception with message
                bestallning.addError("Det saknas information i json-filen för att kunna skapa Anropsbehorighet.")
            } else {
                LogiskAdress existLogiskAdress
                def results1 = LogiskAdress.findAll(" from LogiskAdress where deleted != 1 and hsaId = '" + logisk + "'")
                if (results1.size() > 0) {
                    existLogiskAdress = results1.get(0)
                } else {
                    //Is the needed LogiskAdress included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getInkludera().getLogiskadresser().each() { iter ->
                        if (iter.getHsaId().equals(logisk)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No LogiskAdress in db and not in json file.. fill exception with message
                        bestallning.addError("Skapa Anropsbehorighet: LogiskAdress:en finns inte.")
                    }
                }
                Tjanstekomponent existTjanstekonsument
                def results2 = Tjanstekomponent.findAll(" from Tjanstekomponent where deleted != 1 and hsaId = '" + konsument + "'")
                if (results2.size() > 0) {
                    existTjanstekonsument = results2.get(0)
                } else {
                    //Is the needed Tjanstekomponent included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getInkludera().getTjanstekomponenter().each() { iter ->
                        if (iter.getHsaId().equals(konsument)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No Tjanstekomponent in db and not in json file.. fill exception with message
                        bestallning.addError("Skapa Anropsbehorighet: Tjanstekomponent:en finns inte.")
                    }
                }
                Tjanstekontrakt existTjanstekontrakt
                def results3 = Tjanstekontrakt.findAll(" from Tjanstekontrakt where deleted != 1 and namnrymd = '" + kontrakt + "'")
                if (results3.size() > 0) {
                    existTjanstekontrakt = results3.get(0)
                } else {
                    //Is the needed Tjanstekontrakt included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getInkludera().getTjanstekontrakt().each() { iter ->
                        if (iter.getNamnrymd().equals(kontrakt)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No Tjanstekontrakt in db and not in json file.. fill exception with message
                        bestallning.addError("Skapa Anropsbehorighet: Tjanstekontrakt:et finns inte.")
                    }
                }
                if (existLogiskAdress != null && existTjanstekonsument != null && existTjanstekontrakt != null) {
                    //Check if Anropsbehorighet already exist in db..
                    def results = Anropsbehorighet.findAll(" from Anropsbehorighet as db where db.deleted != 1 and db.logiskAdress.id = " +
                            existLogiskAdress.getId() + " and db.tjanstekonsument.id = " + existTjanstekonsument.getId() +
                            " and db.tjanstekontrakt.id = " + existTjanstekontrakt.getId())
                    if (results.size() > 0) {
                        it.setAnropsbehorighet(results.get(0))
                    }
                }
            }
        }
    }

    def saveOrderObjects(JsonBestallning bestallning) {

        //Only if no errors happened during validation in JsonBestallningCreator.groovy we save..
        if (bestallning.isValidBestallning()) {

            HashMap<String, LogiskAdress> las = new HashMap<>()
            HashMap<String, Tjanstekomponent> tkms = new HashMap<>()
            HashMap<String, Tjanstekontrakt> tks = new HashMap<>()

            //Only Vagval and Anropsbehorighet is to be deleted via json...
            //If matching entity object found in db (set in bestallning-> it), set that object to delete..
            try {
                bestallning.getExkludera().getVagval().each() { it ->
                    if (it.getVagval() != null) {
                        Vagval existing = it.getVagval()
                        setMetaData(existing, true)
                        def result = existing.save(validate: false)
                        System.out.println("What is the result ? " + result)
                    }
                }

                bestallning.getExkludera().getAnropsbehorigheter().each() { it ->
                    if (it.getAnropsbehorighet() != null) {
                        Anropsbehorighet existing = it.getAnropsbehorighet()
                        setMetaData(existing, true)
                        def result = existing.save(validate: false)
                        System.out.println("What is the result ? " + result)
                    }
                }

                //And the additions, objects to be saved..
                //A bit unclear here, waiting for specification.

                bestallning.getInkludera().getLogiskadresser().each() { it ->
                    //if non-existing in db, we create the object and save in hashmap for use later..
                    if (it.getLogiskAdress() == null) {
                        LogiskAdress la = new LogiskAdress()
                        la.setHsaId(it.getHsaId())
                        la.setBeskrivning(it.getBeskrivning())
                        setMetaData(la, false)
                        def result = la.save(validate: false)
                        System.out.println("What is the result ? " + result)
                        las.put(la.getHsaId(), la)
                    } else {
                        //Object already existed in db, so don't create if identical, but can still be used later
                        LogiskAdress existing = it.getLogiskAdress()
                        if (!existing.getBeskrivning().equals(it.getBeskrivning())) {
                            existing.setBeskrivning(it.getBeskrivning())
                            def result = existing.save(validate: false)
                        }
                        las.put(existing.getHsaId(), existing)
                    }
                }

                bestallning.getInkludera().getTjanstekomponenter().each() { it ->
                    if (it.getTjanstekomponent() == null) {
                        Tjanstekomponent tk = new Tjanstekomponent()
                        tk.setHsaId(it.getHsaId())
                        tk.setBeskrivning(it.getBeskrivning())
                        setMetaData(tk, false)
                        def result = tk.save(validate: false)
                        System.out.println("What is the result ? " + result)
                        las.put(tk.getHsaId(), tk)
                    } else {
                        //Object already existed in db, so don't create, but can still be used later
                        Tjanstekomponent existing = it.getTjanstekomponent()
                        if (!existing.getBeskrivning().equals(it.getBeskrivning())) {
                            existing.setBeskrivning(it.getBeskrivning())
                            def result = existing.save(validate: false)
                        }
                        las.put(existing.getHsaId(), existing)
                    }
                }

                bestallning.getInkludera().getTjanstekontrakt().each() { it ->
                    if (it.getTjanstekontrakt() == null) {
                        Tjanstekontrakt tk = new Tjanstekontrakt()
                        tk.setNamnrymd(it.getNamnrymd())
                        tk.setBeskrivning(it.getBeskrivning())
                        tk.setMajorVersion(it.getMajorVersion())
                        tk.setMinorVersion(it.getMinorVersion())
                        setMetaData(tk, false)
                        def result = tk.save(validate: false)
                        System.out.println("What is the result ? " + result)
                        las.put(tk.getNamnrymd(), tk)
                    } else {
                        //Object already existed in db, so don't create, but can still be used later
                        Tjanstekontrakt existing = it.getTjanstekontrakt()
                        if (!existing.getBeskrivning().equals(it.getBeskrivning())) {
                            existing.setBeskrivning(it.getBeskrivning())
                            def result = existing.save(validate: false)
                        }
                        las.put(existing.getNamnrymd(), existing)
                    }
                }

                bestallning.getInkludera().getAnropsbehorigheter().each() { it ->
                    if (it.getAnropsbehorighet() == null) {
                        Anropsbehorighet a = new Anropsbehorighet()
                        setMetaData(a, false)
                        a.setFromTidpunkt(it.fromTidpunkt) //If not present, now?
                        a.setTomTidpunkt(it.getTomTidpunkt()) //If not present, WHAT???
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
                bestallning.getInkludera().getVagval().each() { it ->
                    if (it.getVagval() == null) {
                        //Must create a new object  I presume..
                        Vagval v = new Vagval()
                        setMetaData(v, false)
                        v.setAnropsAdress(it.adress)  //Must find id from db?
                        v.setFromTidpunkt(it.fromTidpunkt) //If not present, now?
                        v.setTomTidpunkt(it.getTomTidpunkt()) //If not present, WHAT???
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
    }

    protected void setMetaData(AbstractVersionInfo versionInfo, isDeleted) {
        def principal = SecurityUtils.getSubject()?.getPrincipal()
        versionInfo.setUpdatedTime(new Date())
        versionInfo.setUpdatedBy(principal)
        versionInfo.setDeleted(isDeleted)
    }
}
