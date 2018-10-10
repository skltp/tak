package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.databind.ObjectMapper
import se.skltp.tak.core.entity.AnropsAdress
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.Vagval;



class JsonBestallningCreator {

    static JsonBestallning createBestallningObject(String jsonBestallningString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;

    }

    static void findAllOrderObjects(JsonBestallning bestallning) {
        //TODO: Not clear what criteria to use for finding correspondent objects in db
        bestallning.getExtrudeData().getVagval().each() { it ->
            def Vagval existingVagval
            def adress = it.getAdress()
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def getAnropsadress = "(select id from AnropsAdress where deleted != 1 and adress = '" + adress + "' and rivTaProfil.id = " +
                    "(select id from RivTaProfil where deleted != 1 and namn = '" + rivta + "') and tjanstekomponent.id = " +
                    "(select id from Tjanstekomponent where deleted != 1 and hsaId = '" + komponent + "')) "
            def logisk = it.getLogiskadress()
            def getLogiskAdress = "(select id from LogiskAdress where deleted != 1 and hsaId = '" + logisk + "')"
            def kontrakt = it.getTjanstekontrakt()
            def getTjanstekontrakt = "(select id from Tjanstekontrakt where deleted != 1 and namnrymd = '" + kontrakt + "')"
            //Final query...
            def results = Vagval.findAll(" from Vagval as db where db.deleted != 1 and db.anropsAdress.id = " + getAnropsadress +
                    " and db.logiskAdress.id = " + getLogiskAdress + " and tjanstekontrakt.id = " + getTjanstekontrakt)
            if (results.size() > 0 && results.size() == 1) {
                it.setVagval(results.get(0))
                existingVagval = it.getVagval()
                existingVagval.setDeleted(true)
            } else {
                //No object found OR more than one found, so we can't delete..?
                errorString += it.toString() + ": " + bestallning.vagval.missing + "\n"
            }
        }

        bestallning.getExtrudeData().getAnropsbehorighet().each() { it ->
            def Anropsbehorighet anropsbehorighet
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            def results = Anropsbehorighet.findAll(" from Anropsbehorighet as db where db.deleted != 1 and db.logiskAdress.id = " +
                    "(select id from LogiskAdress where hsaId = '" + logisk +
                    "') and db.tjanstekonsument.id = (select id from Tjanstekomponent where hsaId = '" + konsument + "') " +
                    "and db.tjanstekontrakt.id = (select id from Tjanstekontrakt where namnrymd = '" + kontrakt + "')")
            if (results.size() > 0 && results.size() == 1) {
                it.setAnropsbehorighet(results.get(0))
                anropsbehorighet = it.getAnropsbehorighet()
                anropsbehorighet.setDeleted(true)
            } else {
                //No object found OR more than one found, so we can't delete..?
                errorString += it.toString() + ": " + bestallning.anropsbehorighet.missing + "\n"
            }
        }

        //These below should NOT be deleted via json!!!
        /*bestallning.getExtrudeData().getLogiskadress().each() { it ->
            String hsaId = it.getHsaId()
            def results = LogiskAdress.findAll(" from LogiskAdress db where db.deleted != 1 and db.hsaId = '" + hsaId + "'")
            if (results.size() > 0) {
                it.setLogiskAdress(results.get(0))
            }
        }

        bestallning.getExtrudeData().getTjanstekomponent().each() { it ->
            String hsaId = it.getHsaId()
            def results = Tjanstekomponent.findAll(" from Tjanstekomponent as db where db.deleted != 1 and db.hsaId = '" + hsaId + "'")
            if (results.size() > 0) {
                it.setTjanstekomponent(results.get(0))
            }
        }

        bestallning.getExtrudeData().getTjanstekontrakt().each() { it ->
            String namespace = it.getNamnrymd()
            def results = Tjanstekontrakt.findAll(" from Tjanstekontrakt as db where db.deleted != 1 and db.namnrymd = '" + namespace + "'")
            if (results.size() > 0) {
                it.setTjanstekontrakt(results.get(0))
            }
        }*/

        bestallning.getEnsureData().getLogiskadress().each() { it ->
            String hsaId = it.getHsaId()
            def results = LogiskAdress.findAll(" from LogiskAdress db where db.deleted != 1 and db.hsaId = '" + hsaId + "'")
            if (results.size() > 0) {
                LogiskAdress existing = results.get(0)
                //LogiskAdress already exists, but maybe update description?
                it.setLogiskAdress(existing)
            }
        }

        bestallning.getEnsureData().getTjanstekomponent().each() { it ->
            String hsaId = it.getHsaId()
            def results = Tjanstekomponent.findAll(" from Tjanstekomponent as db where db.deleted != 1 and db.hsaId = '" + hsaId + "'")
            if (results.size() > 0) {
                it.setTjanstekomponent(results.get(0))
            }
        }

        bestallning.getEnsureData().getTjanstekontrakt().each() { it ->
            String namespace = it.getNamnrymd()
            def results = Tjanstekontrakt.findAll(" from Tjanstekontrakt as db where db.deleted != 1 and db.namnrymd = '" + namespace + "'")
            if (results.size() > 0) {
                it.setTjanstekontrakt(results.get(0))
            }
        }

        bestallning.getEnsureData().getVagval().each() { it ->
            def adress = it.getAdress()
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def logisk = it.getLogiskadress()
            def kontrakt = it.getTjanstekontrakt()

            if (adress == null || rivta == null || komponent == null || logisk == null || kontrakt == null) {
                //Abort fill exception with message
            } else {
                AnropsAdress existAnropsAdress
                def results1 = AnropsAdress.findAll("select from AnropsAdress where deleted != 1 and adress = '" +
                        adress + "' and rivTaProfil.id = (select id from RivTaProfil where deleted != 1 and namn = '" + rivta + "') and "
                        + "tjanstekomponent.id = (select id from Tjanstekomponent where deleted != 1 and hsaId = '" + komponent + "')")
                if (results1.size() != 0) {
                    existAnropsAdress = results1.get(0)
                } else {
                    //If the AnropsAdress not is found in db, it should be created with the values adress + rivta + komponent? Yes...
                }
                LogiskAdress existLogiskAdress
                def results2 = LogiskAdress.findAll("select from LogiskAdress where deleted != 1 and hsaId = '" + logisk + "'")
                if (results2.size() > 0) {
                    existLogiskAdress = results2.get(0)
                } else {
                    //Is the needed LogiskAdress included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getEnsureData().getLogiskadress().each() { iter->
                        if (iter.getHsaId().equals(logisk)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No LogiskAdress in db and not in json file.. fill exception with message
                    }
                }
                Tjanstekontrakt existTjanstekontrakt
                def results3 = Tjanstekontrakt.findAll("select from Tjanstekontrakt where deleted != 1 and namnrymd = '" + kontrakt + "'")
                if (results3.size() > 0) {
                    existTjanstekontrakt = results3.get(0)
                } else {
                    //Is the needed LogiskAdress included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getEnsureData().getTjanstekontrakt().each() { iter->
                        if (iter.getNamnrymd().equals(kontrakt)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No LogiskAdress in db and not in json file.. fill exception with message
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

        bestallning.getEnsureData().getAnropsbehorighet().each() { it ->
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            if (logisk == null || konsument == null || kontrakt == null) {
                //Abort fill exception with message
            } else {
                LogiskAdress existLogiskAdress
                def results1 = LogiskAdress.findAll("select from LogiskAdress where deleted != 1 and hsaId = '" + logisk + "'")
                if (results1.size() > 0) {
                    existLogiskAdress = results1.get(0)
                } else {
                    //Is the needed LogiskAdress included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getEnsureData().getLogiskadress().each() { iter->
                        if (iter.getHsaId().equals(logisk)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No LogiskAdress in db and not in json file.. fill exception with message
                    }
                }
                Tjanstekomponent existTjanstekonsument
                def results2 = Tjanstekomponent.findAll("select from Tjanstekomponent where deleted != 1 and hsaId = '" + konsument + "'")
                if (results2.size() > 0) {
                    existTjanstekonsument = results2.get(0)
                } else {
                    //Is the needed Tjanstekomponent included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getEnsureData().getTjanstekomponent().each() { iter->
                        if (iter.getHsaId().equals(konsument)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No Tjanstekomponent in db and not in json file.. fill exception with message
                    }
                }
                Tjanstekontrakt existTjanstekontrakt
                def results3 = Tjanstekontrakt.findAll("select from Tjanstekontrakt where deleted != 1 and namnrymd = '" + kontrakt + "'")
                if (results3.size() > 0) {
                    existTjanstekontrakt = results3.get(0)
                } else {
                    //Is the needed Tjanstekontrakt included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getEnsureData().getTjanstekontrakt().each() { iter->
                        if (iter.getNamnrymd().equals(kontrakt)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No LogiskAdress in db and not in json file.. fill exception with message
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
        //if (Ex.getMessages > 0) {
        //Throw ex
    }
}
