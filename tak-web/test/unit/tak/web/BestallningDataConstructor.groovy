package tak.web

import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.RivTaProfil
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.web.jsonBestallning.AnropsbehorighetBestallning
import se.skltp.tak.web.jsonBestallning.BestallningsData
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import se.skltp.tak.web.jsonBestallning.VagvalBestallning

class BestallningDataConstructor {

    static BestallningsData createBestallningDataForAnropsbehorighetAndRelations() {
        return createBestallningDataForAnropsbehorighetAndRelations(BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT)
    }

    static BestallningsData createBestallningDataForAnropsbehorighetAndRelations(String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd) {
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        AnropsbehorighetBestallning bestallning = BestallningConstructor.createAnropsbehorighetBestallning(laHsaId, tKomponentHsaId, tKontraktNamnrymd)
        jsonBestallning.inkludera.anropsbehorigheter.add(bestallning)

        BestallningsData bestallningsData = new BestallningsData(jsonBestallning)

        LogiskAdress la = ObjectsConstructor.createLogiskAdress(laHsaId)
        Tjanstekomponent tjanstekomponent = ObjectsConstructor.createTjanstekomponent(tKomponentHsaId)
        Tjanstekontrakt tjanstekontrakt = ObjectsConstructor.createTjanstekontrakt(tKontraktNamnrymd)

        bestallningsData.putRelations(bestallning, la, tjanstekomponent, tjanstekontrakt)

        return bestallningsData
    }

    static BestallningsData createBestallningDataForVagvalAndRelations(){
        createBestallningDataForVagvalAndRelations(BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT, BestallningConstructor.RIVTA_PROFIL)

    }

    static BestallningsData createBestallningDataForVagvalAndRelations(String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd, String rivTaProfil) {
        JsonBestallning jsonBestallning = BestallningConstructor.createEmptyBestallning()
        VagvalBestallning bestallning = BestallningConstructor.createVagvalBestallning(laHsaId, tKomponentHsaId, tKontraktNamnrymd,
                BestallningConstructor.ADRESS, rivTaProfil)
        jsonBestallning.inkludera.vagval.add(bestallning)

        BestallningsData bestallningsData = new BestallningsData(jsonBestallning)

        LogiskAdress la = ObjectsConstructor.createLogiskAdress(laHsaId)
        Tjanstekomponent tjanstekomponent = ObjectsConstructor.createTjanstekomponent(tKomponentHsaId)
        Tjanstekontrakt tjanstekontrakt = ObjectsConstructor.createTjanstekontrakt(tKontraktNamnrymd)
        RivTaProfil profil = ObjectsConstructor.createRivTaProfil(rivTaProfil)

        bestallningsData.putRelations(bestallning, la, tjanstekomponent, tjanstekontrakt, profil)

        return bestallningsData
    }

    static void fillDataAboutRelations(BestallningsData bestallningsData, VagvalBestallning bestallning, String laHsaId, String tKomponentHsaId, String tKontraktNamnrymd, String rivTaProfil) {
        LogiskAdress la = ObjectsConstructor.createLogiskAdress(laHsaId)
        Tjanstekomponent tjanstekomponent = ObjectsConstructor.createTjanstekomponent(tKomponentHsaId)
        Tjanstekontrakt tjanstekontrakt = ObjectsConstructor.createTjanstekontrakt(tKontraktNamnrymd)
        RivTaProfil profil = ObjectsConstructor.createRivTaProfil(rivTaProfil)

        bestallningsData.putRelations(bestallning, la, tjanstekomponent, tjanstekontrakt, profil)
    }

}
