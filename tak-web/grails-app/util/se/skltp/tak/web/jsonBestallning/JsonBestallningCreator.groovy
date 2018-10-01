package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent;

 class JsonBestallningCreator {

     static JsonBestallning createBestallningObject(String jsonBestallningString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;

    }

     static void hittaAllBestallningObjeckter(JsonBestallning bestallning) {
        bestallning.getEnsureData().getLogiskadress().each() { it ->
            String hsaId = it.getHsaId()
            it.setLogiskAdress(LogiskAdress.findAllByHsaId(hsaId))
        }

        bestallning.getEnsureData().getTjanstekomponent().each() { it ->
            String hsaId = it.getHsaId()
            it.setTjanstekomponent(Tjanstekomponent.findAllByHsaId(hsaId))
        }

        // hitta alla andra objekter
    }

}
