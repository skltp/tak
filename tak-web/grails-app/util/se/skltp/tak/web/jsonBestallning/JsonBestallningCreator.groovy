package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.databind.ObjectMapper
import se.skltp.tak.core.entity.AnropsAdress
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.Vagval
import tak.web.IncorrectBestallningException



class JsonBestallningCreator {

    static JsonBestallning createBestallningObject(String jsonBestallningString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;

    }

    static void findAllOrderObjects(JsonBestallning bestallning) throws IncorrectBestallningException {
        IncorrectBestallningException bestallningException = new IncorrectBestallningException();

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
                bestallningException.addNewProblem("Vagval som ska tas bort finns inte i databasen.")
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
                bestallningException.addNewProblem("Anropsbehorighet som ska tas bort finns inte i databasen.")
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
                bestallningException.addNewProblem("Det saknas information i json-filen för att kunna skapa Vagval.")
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
                    bestallning.getInkludera().getLogiskadresser().each() { iter->
                        if (iter.getHsaId().equals(logisk)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No LogiskAdress in db and not in json file.. fill exception with message
                        bestallningException.addNewProblem("Skapa Vagval: Det saknas LogiskAdress.")
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
                    bestallning.getInkludera().getTjanstekontrakt().each() { iter->
                        if (iter.getNamnrymd().equals(kontrakt)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No Tjanstekontrakt in db and not in json file.. fill exception with message
                        bestallningException.addNewProblem("Skapa Vagval: Det saknas Tjanstekontrakt.")
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
                bestallningException.addNewProblem("Det saknas information i json-filen för att kunna skapa Anropsbehorighet.")
            } else {
                LogiskAdress existLogiskAdress
                def results1 = LogiskAdress.findAll(" from LogiskAdress where deleted != 1 and hsaId = '" + logisk + "'")
                if (results1.size() > 0) {
                    existLogiskAdress = results1.get(0)
                } else {
                    //Is the needed LogiskAdress included in json file?
                    def foundItem
                    foundItem = false
                    bestallning.getInkludera().getLogiskadresser().each() { iter->
                        if (iter.getHsaId().equals(logisk)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No LogiskAdress in db and not in json file.. fill exception with message
                        bestallningException.addNewProblem("Skapa Anropsbehorighet: LogiskAdress:en finns inte.")
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
                    bestallning.getInkludera().getTjanstekomponenter().each() { iter->
                        if (iter.getHsaId().equals(konsument)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No Tjanstekomponent in db and not in json file.. fill exception with message
                        bestallningException.addNewProblem("Skapa Anropsbehorighet: Tjanstekomponent:en finns inte.")
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
                    bestallning.getInkludera().getTjanstekontrakt().each() { iter->
                        if (iter.getNamnrymd().equals(kontrakt)) {
                            foundItem = true
                        }
                    }
                    if (foundItem == false) {
                        //No Tjanstekontrakt in db and not in json file.. fill exception with message
                        bestallningException.addNewProblem("Skapa Anropsbehorighet: Tjanstekontrakt:et finns inte.")
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

        if(bestallningException.bestallningIncorrect){
            throw bestallningException;
        }
    }
}
