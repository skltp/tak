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
import org.apache.commons.logging.LogFactory
import org.apache.shiro.SecurityUtils
import org.springframework.transaction.annotation.Transactional
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*

class BestallningService {

    private static final log = LogFactory.getLog(this)
    DAOService daoService;
    I18nService i18nService;

    public JsonBestallning createOrderObject(String jsonBestallningString) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;
    }

    public validateOrderObjects(JsonBestallning bestallning) {
        validateDeletedVagval(bestallning)
        validateDeletedAnropsbehorigheter(bestallning)

        saveLogiskaAdresserToOrder(bestallning)
        saveTjanstekomponenterToOrder(bestallning)
        saveTjanstekontraktToOrder(bestallning)

        validateAddedVagval(bestallning)
        validateAddedAnropsbehorigheter(bestallning);
    }

    private validateDeletedAnropsbehorigheter(JsonBestallning bestallning) {
        bestallning.getExkludera().getAnropsbehorigheter().each() { it ->
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            Anropsbehorighet exist = daoService.getAnropsbehorighet(logisk, konsument, kontrakt, bestallning.getGenomforandeTidpunkt())
            if (exist == null) {
                bestallning.addInfo(i18nService.msg("bestallning.error.saknas.anropsbehorighet", [logisk, konsument, kontrakt]))
            }
        }
    }

    private validateDeletedVagval(JsonBestallning bestallning) {
        bestallning.getExkludera().getVagval().each() { it ->
            def adress = it.getAdress()
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def logisk = it.getLogiskAdress()
            def kontrakt = it.getTjanstekontrakt()
            Vagval exist = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt, bestallning.genomforandeTidpunkt)
            if (exist == null) {
                bestallning.addInfo(i18nService.msg("bestallning.error.saknas.vagval", [logisk, kontrakt, adress]))
            }
        }
    }

    private validateAddedAnropsbehorigheter(JsonBestallning bestallning) {
        bestallning.inkludera.anropsbehorigheter.each() { anropsbehorighetBestallning ->
            def logisk = anropsbehorighetBestallning.getLogiskAdress()
            def konsument = anropsbehorighetBestallning.getTjanstekonsument()
            def kontrakt = anropsbehorighetBestallning.getTjanstekontrakt()

            if (logisk == null || konsument == null || kontrakt == null) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.info.for.anropsbehorighet"))
                return
            }

            if (!existsLogiskAdressInDBorInOrder(logisk, bestallning)) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.logiskAdress.for.anropsbehorighet", [logisk]))
                log.error(i18nService.msg("bestallning.error.saknas.logiskAdress.for.anropsbehorighet", [logisk]))
            }

            if (!existsTjanstekomponentInDBorInOrder(konsument, bestallning)) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [konsument]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [konsument]))
            }

            if (!existsTjanstekontraktInDBorInOrder(kontrakt, bestallning)) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [kontrakt]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [kontrakt]))
            }
        }
    }

    private validateAddedVagval(JsonBestallning bestallning) {
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            def adress = vagvalBestallning.getAdress()
            def rivta = vagvalBestallning.getRivtaprofil()
            def komponent = vagvalBestallning.getTjanstekomponent()
            def logisk = vagvalBestallning.getLogiskAdress()
            def kontrakt = vagvalBestallning.getTjanstekontrakt()

            if (adress == null || rivta == null || komponent == null || logisk == null || kontrakt == null) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.info.for.vagval"))
                log.error(i18nService.msg("bestallning.error.saknas.info.for.vagval") + " Adress:" + adress + " Rivta:" + rivta +
                        " Komponent:" + komponent + " LogiskAdress:" + logisk + " Kontrakt:" + kontrakt + ".")
                return
            }

            if (!existsRivtaInDB(rivta)) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.rivtaprofil.for.vagval", [rivta]))
                log.error(i18nService.msg("bestallning.error.saknas.rivtaprofil.for.vagval", [rivta]))
            }

            if (!existsTjanstekomponentInDBorInOrder(komponent, bestallning)) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.vagval", [komponent]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.vagval", [komponent]))
            }

            if (!existsLogiskAdressInDBorInOrder(logisk, bestallning)) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.logiskAdress.for.vagval", [logisk]))
                log.error(i18nService.msg("bestallning.error.saknas.logiskAdress.for.vagval", [logisk]))
            }

            if (!existsTjanstekontraktInDBorInOrder(kontrakt, bestallning)) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [kontrakt]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [kontrakt]))
            }
            Vagval exist = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt, bestallning.getGenomforandeTidpunkt())
            if (exist != null) {
                bestallning.addError(i18nService.msg("bestallning.error.vagval.redan.finns", [adress, rivta, komponent, logisk, kontrakt]))
                log.error(i18nService.msg("bestallning.error.vagval.redan.finns", [adress, rivta, komponent, logisk, kontrakt]))
            }
        }
    }

    private saveLogiskaAdresserToOrder(JsonBestallning bestallning) {
        bestallning.getInkludera().getLogiskadresser().each() { it ->
            LogiskAdress existLogiskAdress = daoService.getLogiskAdressByHSAId(it.getHsaId())
            if (existLogiskAdress != null && !existLogiskAdress.getDeleted()) {
                it.setLogiskAdress(existLogiskAdress)
            }
        }
    }

    private saveTjanstekomponenterToOrder(JsonBestallning bestallning) {
        bestallning.getInkludera().getTjanstekomponenter().each() { it ->
            Tjanstekomponent existTjanstekomponent = daoService.getTjanstekomponentByHSAId(it.getHsaId())
            if (existTjanstekomponent != null && !existTjanstekomponent.getDeleted()) {
                it.setTjanstekomponent(existTjanstekomponent)
            }
        }
    }

    private saveTjanstekontraktToOrder(JsonBestallning bestallning) {
        bestallning.getInkludera().getTjanstekontrakt().each() { it ->
            Tjanstekontrakt existTjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(it.getNamnrymd())
            if (existTjanstekontrakt && !existTjanstekontrakt.getDeleted()) {
                it.setTjanstekontrakt(existTjanstekontrakt)
            }
        }
    }

    private boolean existsLogiskAdressInDBorInOrder(String hsaId, JsonBestallning bestallning) {
        LogiskAdress existLogiskAdress = daoService.getLogiskAdressByHSAId(hsaId)
        if (existLogiskAdress != null) {
            return true
        } else {
            boolean found
            found = false
            bestallning.getInkludera().getLogiskadresser().each() { it ->
                if (it.getHsaId().equals(hsaId)) {
                    found = true
                }
            }
            return found
        }
    }

    private boolean existsTjanstekomponentInDBorInOrder(String hsaId, JsonBestallning bestallning) {
        Tjanstekomponent existTjanstekomponent = daoService.getTjanstekomponentByHSAId(hsaId)
        if (existTjanstekomponent != null) {
            return true
        } else {
            boolean found
            found = false
            bestallning.getInkludera().getTjanstekomponenter().each() { iter ->
                if (iter.getHsaId().equals(hsaId)) {
                    found = true
                }
            }
            return found
        }
    }

    private boolean existsTjanstekontraktInDBorInOrder(String namnrymd, JsonBestallning bestallning) {
        Tjanstekontrakt existTjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(namnrymd)
        if (existTjanstekontrakt != null) {
            return true
        } else {
            boolean found
            found = false
            bestallning.getInkludera().getTjanstekontrakt().each() { iter ->
                if (namnrymd.equals(iter.getNamnrymd())) {
                    found = true
                }
            }
            return found
        }
    }

    private boolean existsRivtaInDB(String rivta) {
        RivTaProfil existRivta = daoService.getRivtaByNamn(rivta)
        return existRivta != null
    }

    @Transactional
    def executeOrder(JsonBestallning bestallning) {
        if (bestallning.getBestallningErrors().size() == 0) {
            try {
                deleteObjects(bestallning.getExkludera(), bestallning.getGenomforandeTidpunkt());
                createObjects(bestallning.getInkludera(), bestallning.getGenomforandeTidpunkt());
                return bestallning
            } catch (Exception e) {
                log.error("Database error: Trying to rollback: " + e.getMessage())
                transactionStatus.setRollbackOnly()
                throw e
            }
        }
    }

    private void deleteObjects(KollektivData deleteData, Date genomforande) {
        //Only Vagval and Anropsbehorighet is to be deleted via json...
        //If matching entity object found in db, set that object to delete..
        java.sql.Date date = new java.sql.Date(genomforande.getTime())
        deleteVagval(deleteData, date)
        deleteAnropsbehorigheter(deleteData, date)
    }

    private deleteVagval(KollektivData deleteData, java.sql.Date date) {
        deleteData.getVagval().each() { it ->
            def adress = it.getAdress()
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def logisk = it.getLogiskAdress()
            def kontrakt = it.getTjanstekontrakt()
            Vagval exist = daoService.getVagval(adress, rivta, komponent, logisk, kontrakt, date)
            deleteWithCheck(exist)
        }
    }

    private deleteAnropsbehorigheter(KollektivData deleteData, java.sql.Date date) {
        deleteData.getAnropsbehorigheter().each() { it ->
            def logisk = it.getLogiskAdress()
            def konsument = it.getTjanstekonsument()
            def kontrakt = it.getTjanstekontrakt()
            Anropsbehorighet exist = daoService.getAnropsbehorighet(logisk, konsument, kontrakt, date)
            deleteWithCheck(exist)
        }
    }

    private deleteWithCheck(AbstractVersionInfo exist) {
        if (exist != null) {
            if (exist.getPubVersion()) {
                setMetaData(exist, null)
                exist.save()
            } else {
                exist.delete()
            }
        }
    }

    private void createObjects(KollektivData newData, Date fromTidpunkt) {
        java.sql.Date from = new java.sql.Date(fromTidpunkt.getTime())

        createLogiskAddresser(newData.getLogiskadresser())
        createTjanstekomponenter(newData.getTjanstekomponenter())
        createTjanstekontrakt(newData.getTjanstekontrakt())

        createAnropsbehorigheter(newData.getAnropsbehorigheter(), from)
        createVagval(newData.getVagval(), from)
    }

    private createVagval(List<VagvalBestallning> newData, java.sql.Date from) {
        newData.each() { it ->
            if (it.getVagval() == null) {
                Vagval v = new Vagval()
                AnropsAdress aa = daoService.getAnropsAdress(it.getAdress(), it.getRivtaprofil(), it.getTjanstekomponent())
                if (aa == null) {
                    aa = createAnropsAdress(it.getAdress(), it.getRivtaprofil(), it.getTjanstekomponent())
                }
                v.setAnropsAdress(aa)
                v.setFromTidpunkt(from)
                v.setTomTidpunkt(generateTomDate(from))
                //Since the Bestallning has been validated, we assume that components below exist.
                v.setLogiskAdress(daoService.getLogiskAdressByHSAId(it.getLogiskAdress()))
                v.setTjanstekontrakt(daoService.getTjanstekontraktByNamnrymd(it.getTjanstekontrakt()))
                setMetaData(v, false)
                v.save()
            }
        }
    }

    private createAnropsbehorigheter(List<AnropsbehorighetBestallning> newData, java.sql.Date from) {
        newData.each() { it ->
            if (it.getAnropsbehorighet() == null) {
                Anropsbehorighet a = new Anropsbehorighet()
                a.setFromTidpunkt(from)
                a.setTomTidpunkt(generateTomDate(from))
                a.setLogiskAdress(daoService.getLogiskAdressByHSAId(it.getLogiskAdress()))
                a.setTjanstekontrakt(daoService.getTjanstekontraktByNamnrymd(it.getTjanstekontrakt()))
                a.setTjanstekonsument(daoService.getTjanstekomponentByHSAId(it.getTjanstekonsument()))
                setMetaData(a, false)
                //a.setIntegrationsavtal()  // A text string not used in any conditions, as it seems.
                def result = a.save()
            }
        }
    }

    private AnropsAdress createAnropsAdress(String adress, String rivta, String komponent) {
        AnropsAdress aa = new AnropsAdress()
        RivTaProfil rivTaProfil = daoService.getRivtaByNamn(rivta)
        Tjanstekomponent tjanstekomponent = daoService.getTjanstekomponentByHSAId(komponent)
        setMetaData(aa, false)
        aa.setAdress(adress)
        aa.setRivTaProfil(rivTaProfil)
        aa.setTjanstekomponent(tjanstekomponent)
        def result = aa.save()
        return aa
    }

    private void createLogiskAddresser(List<LogiskadressBestallning> logiskadressBestallningar) {
        logiskadressBestallningar.each() { logiskadressBestallning ->
            if (logiskadressBestallning.getLogiskAdress() == null) {
                LogiskAdress logiskAdress = new LogiskAdress()
                logiskAdress.setHsaId(logiskadressBestallning.getHsaId())
                logiskAdress.setBeskrivning(logiskadressBestallning.getBeskrivning())
                setMetaData(logiskAdress, false)
                def result = logiskAdress.save()
            } else {
                //Object already existed in db, so don't create, but maybe update
                LogiskAdress existing = logiskadressBestallning.getLogiskAdress()
                if (!existing.getBeskrivning().equals(logiskadressBestallning.getBeskrivning())) {
                    setMetaData(existing, false)
                    existing.setBeskrivning(logiskadressBestallning.getBeskrivning())
                    def result = existing.save()
                }
            }
        }
    }

    private void createTjanstekomponenter(List<TjanstekomponentBestallning> tjanstekomponentBestallningar) {
        tjanstekomponentBestallningar.each() { tjanstekomponentBestallning ->
            if (tjanstekomponentBestallning.getTjanstekomponent() == null) {
                Tjanstekomponent tjanstekomponent = new Tjanstekomponent()
                tjanstekomponent.setHsaId(tjanstekomponentBestallning.getHsaId())
                tjanstekomponent.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
                setMetaData(tjanstekomponent, false)
                def result = tjanstekomponent.save()
            } else {
                //Object already existed in db, so don't create, but maybe update
                Tjanstekomponent existing = tjanstekomponentBestallning.getTjanstekomponent()
                if (!existing.getBeskrivning().equals(tjanstekomponentBestallning.getBeskrivning())) {
                    setMetaData(existing, false)
                    existing.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
                    def result = existing.save()
                }
            }
        }
    }

    private void createTjanstekontrakt(List<TjanstekontraktBestallning> tjanstekontraktBestallningar) {
        tjanstekontraktBestallningar.each() { tjanstekontraktBestallning ->
            if (tjanstekontraktBestallning.getTjanstekontrakt() == null) {
                Tjanstekontrakt tjanstekontrakt = new Tjanstekontrakt()
                tjanstekontrakt.setNamnrymd(tjanstekontraktBestallning.getNamnrymd())
                tjanstekontrakt.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                tjanstekontrakt.setMajorVersion(tjanstekontraktBestallning.getMajorVersion())
                tjanstekontrakt.setMinorVersion(tjanstekontraktBestallning.getMinorVersion())
                setMetaData(tjanstekontrakt, false)
                def result = tjanstekontrakt.save()
            } else {
                //Object already existed in db, so don't create, but maybe update
                Tjanstekontrakt existing = tjanstekontraktBestallning.getTjanstekontrakt()
                if (!existing.getBeskrivning().equals(tjanstekontraktBestallning.getBeskrivning())) {
                    setMetaData(existing, false)
                    existing.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                    existing.setMajorVersion(tjanstekontraktBestallning.getMajorVersion())
                    def result = existing.save()
                }
            }
        }
    }

    private void setMetaData(AbstractVersionInfo versionInfo, isDeleted) {
        def principal = SecurityUtils.getSubject()?.getPrincipal()
        versionInfo.setUpdatedTime(new Date())
        versionInfo.setUpdatedBy(principal)
        versionInfo.setDeleted(isDeleted)
    }

    private java.sql.Date generateTomDate(java.sql.Date fromDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.add(Calendar.YEAR, 100);
        java.sql.Date d = new java.sql.Date(c.getTime().getTime());
        return d;
    }
}
