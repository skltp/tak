package se.skltp.tak.web.jsonBestallning;

import se.skltp.tak.core.entity.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class BestallningsData {
         Map<LogiskadressBestallning, LogiskAdress> logiskAdressObjects = new HashMap<LogiskadressBestallning, LogiskAdress>();
        Map<TjanstekontraktBestallning, Tjanstekontrakt> tjanstekontraktObjects = new HashMap<TjanstekontraktBestallning, Tjanstekontrakt>();
        Map<TjanstekomponentBestallning, TjanstekomponentBestallning> tjanstekomponentObjects = new HashMap<TjanstekomponentBestallning, TjanstekomponentBestallning>();

        Map<VagvalBestallning, Vagval> vagvalObjects = new HashMap<VagvalBestallning, Vagval>();
        Map<AnropsbehorighetBestallning, Anropsbehorighet> anropsbehorighetObjects = new HashMap<AnropsbehorighetBestallning, Anropsbehorighet>();

        Map<VagvalBestallning, RelationData> vagvalRelations = new HashMap<VagvalBestallning, RelationData>();
        Map<AnropsbehorighetBestallning, RelationData> anropsbehorighetRelations = new HashMap<AnropsbehorighetBestallning, RelationData>();

        private void putVagval(VagvalBestallning bestallning, Vagval vagval) {
            vagvalObjects.put(bestallning, vagval);
        }

        private void putAnropsbehorighet(AnropsbehorighetBestallning bestallning, Anropsbehorighet anropsbehorighet) {
            anropsbehorighetObjects.put(bestallning, anropsbehorighet);
        }

        private void putLogiskAdress(LogiskadressBestallning logiskadressBestallning, LogiskAdress logiskAdress){
            logiskAdressObjects.put(logiskadressBestallning, logiskAdress);
        }



        class RelationData {
            LogiskAdress logiskadress;
            Tjanstekontrakt tjanstekontrakt;
            Tjanstekomponent tjanstekomponent;
            AnropsAdress anropsAdress;
        }

        public Collection<Vagval> getAllaVagval(){
            return vagvalObjects.values();
        }

        public Collection<Anropsbehorighet> getAllaAnropsbehorighet(){
            return anropsbehorighetObjects.values();
        }

}
