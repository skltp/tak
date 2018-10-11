package se.skltp.tak.web.jsonBestallning

import org.apache.shiro.SecurityUtils
import se.skltp.tak.core.entity.AbstractVersionInfo
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.Vagval

class JsonBestallningSave {

    static void saveOrderObjects(JsonBestallning bestallning) {

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
