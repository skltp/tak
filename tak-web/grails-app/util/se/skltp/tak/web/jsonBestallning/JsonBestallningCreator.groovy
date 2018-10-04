package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt;

 class JsonBestallningCreator {

     static JsonBestallning createBestallningObject(String jsonBestallningString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;

    }

     static void hittaAllBestallningObjeckter(JsonBestallning bestallning) {
        bestallning.getEnsureData().getLogiskadress().each() { it ->
            String hsaId = it.getHsaId()
            LogiskAdress adress = LogiskAdress.findByHsaId(hsaId)
            it.setLogiskAdress(adress)
        }

        bestallning.getEnsureData().getTjanstekomponent().each() { it ->
             String hsaId = it.getHsaId()
             it.setTjanstekomponent(Tjanstekomponent.findByHsaId(hsaId))
         }

         bestallning.getEnsureData().getTjanstekontrakt().each() { it ->
             String namnrymd = it.getNamnrymd()
             it.setTjanstekontrakt(Tjanstekontrakt.findByNamnrymd(namnrymd))
         }

        // hitta alla andra objekter
    }

}
