package tak.web;


import org.apache.commons.lang.RandomStringUtils;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.web.command.LogiskaAdresserBulk;
import se.skltp.tak.web.jsonBestallning.*;

import java.util.LinkedList;
import java.util.List;

public class BestallningConstructor {
    private static final String RIVTA_PROFIL="RIVTABP21";
    private static final String LOGISK_ADRESS="SE2321000131-F000000000264";
    private static final String ADRESS="https://min-fina-url.se";
    private static final String TJANSTEKONTRAKT="urn:riv:ehr:blocking:administration:GetPatientIdsResponder:2";
    private static final String TJANSTEKOMPONENT="SE2321000016-8VT4";


    public static JsonBestallning createBestallning(){
        JsonBestallning jsonBestallning = new JsonBestallning();
        jsonBestallning.setExkludera(createKollektivData());
        jsonBestallning.setInkludera(createKollektivData());
        return jsonBestallning;
    }


    private static KollektivData createKollektivData(){
        KollektivData kollektivData = new KollektivData();

        List<TjanstekontraktBestallning> tjanstekontrakt = new LinkedList<TjanstekontraktBestallning>();
        List<LogiskadressBestallning> logiskadresser = new LinkedList<LogiskadressBestallning>();
        logiskadresser.add(createLogiskaAdress());
        List<TjanstekomponentBestallning> tjanstekomponenter = new LinkedList<TjanstekomponentBestallning>();
        List<AnropsbehorighetBestallning> anropsbehorigheter = new LinkedList<AnropsbehorighetBestallning>();
        anropsbehorigheter.add(createAnropsbehorighet());
        List<VagvalBestallning> vagval = new LinkedList<VagvalBestallning>();
        vagval.add(createVagvalBestallning());

        kollektivData.setAnropsbehorigheter(anropsbehorigheter);


        kollektivData.setLogiskadresser(logiskadresser);
        kollektivData.setTjanstekomponenter(tjanstekomponenter);
        kollektivData.setTjanstekontrakt(tjanstekontrakt);
        kollektivData.setVagval(vagval);

        return kollektivData;
    }

    private  static VagvalBestallning createVagvalBestallning() {
        VagvalBestallning vagvalBestallning = new VagvalBestallning();
        vagvalBestallning.setAdress(ADRESS);
        vagvalBestallning.setLogiskadress(LOGISK_ADRESS);
        vagvalBestallning.setTjanstekontrakt(TJANSTEKONTRAKT);
        vagvalBestallning.setTjanstekomponent(TJANSTEKOMPONENT);
        vagvalBestallning.setRivtaprofil(RIVTA_PROFIL);
        return vagvalBestallning;
    }

    private static AnropsbehorighetBestallning createAnropsbehorighet(){
        AnropsbehorighetBestallning anropsbehorighetBestallning = new AnropsbehorighetBestallning();
        anropsbehorighetBestallning.setLogiskAdress(LOGISK_ADRESS);
        anropsbehorighetBestallning.setTjanstekonsument(TJANSTEKOMPONENT);
        anropsbehorighetBestallning.setTjanstekontrakt(TJANSTEKONTRAKT);
        return anropsbehorighetBestallning;
    }

    private static LogiskadressBestallning createLogiskaAdress(){
        LogiskadressBestallning logiskadressBestallning = new LogiskadressBestallning();
        logiskadressBestallning.setHsaId("123");
        return logiskadressBestallning;

    }
}
