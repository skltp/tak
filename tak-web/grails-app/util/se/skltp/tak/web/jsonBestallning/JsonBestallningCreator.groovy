package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.databind.ObjectMapper
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.Vagval
import tak.web.IncorrectBestallningException



class JsonBestallningCreator {

    static String errorString = ""

    static String getErrorString() {
        return errorString
    }

    static JsonBestallning createBestallningObject(String jsonBestallningString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;

    }

    static void findAllOrderObjects(JsonBestallning bestallning) throws IncorrectBestallningException {
        IncorrectBestallningException bestallningException = new IncorrectBestallningException();

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
                //errorString += it.toString() + ": " + bestallning.vagval.missing + "\n"
                bestallningException.addNewProblem("fins inget vagval ...")
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
                bestallningException.addNewProblem("fins inget anropsbehorighet ...")
                //errorString += it.toString() + ": " + bestallning.anropsbehorighet.missing + "\n"
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
            if (results.size() > 0) {
                it.setVagval(results.get(0))
            }
        }

        bestallning.getEnsureData().getAnropsbehorighet().each() { it ->
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            def results = Anropsbehorighet.findAll(" from Anropsbehorighet as db where db.deleted != 1 and db.logiskAdress.id = " +
                    "(select id from LogiskAdress where hsaId = '" + logisk +
                    "') and db.tjanstekonsument.id = (select id from Tjanstekomponent where hsaId = '" + konsument + "') " +
                    "and db.tjanstekontrakt.id = (select id from Tjanstekontrakt where namnrymd = '" + kontrakt + "')")
            if (results.size() > 0) {
                it.setAnropsbehorighet(results.get(0))
            }

            if(bestallningException.bestallningIncorrect){
                throw bestallningException;
            }
        }
    }
}
