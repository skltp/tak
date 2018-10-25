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
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib
import org.springframework.context.ApplicationContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.FieldError
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*

class BestallningService {

    private static final log = LogFactory.getLog(this)
    DAOService daoService;
    I18nService i18nService;

    ValidationTagLib validationTagLib;

    public JsonBestallning createOrderObject(String jsonBestallningString) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;
    }

    public validateOrderObjects(JsonBestallning bestallning) {
        validateDeletedVagval(bestallning)
        validateDeletedAnropsbehorigheter(bestallning)

        validateLogiskAdresser(bestallning)
        validateTjanstekomponenter(bestallning)
        validateTjanstekontrakt(bestallning)

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


    private void validateLogiskAdresser(JsonBestallning bestallning) {
        bestallning.getInkludera().getLogiskadresser().each() { logiskadressBestallning ->
            LogiskAdress logiskAdress = daoService.getLogiskAdressByHSAId(logiskadressBestallning.getHsaId())
            if (logiskAdress == null) {
                logiskAdress = new LogiskAdress()
                logiskAdress.setHsaId(logiskadressBestallning.getHsaId())
                logiskAdress.setBeskrivning(logiskadressBestallning.getBeskrivning())
                setMetaData(logiskAdress, false)
            } else {
                if (!logiskAdress.getBeskrivning().equals(logiskadressBestallning.getBeskrivning())) {
                    logiskAdress.setBeskrivning(logiskadressBestallning.getBeskrivning())
                    setMetaData(logiskAdress, false)
                }
            }

            if (logiskAdress.validate()) {
                logiskadressBestallning.setLogiskAdress(logiskAdress)
            } else {
                logiskAdress.errors.allErrors.each() { it ->
                    bestallning.addError(validationTagLib.message(error:it))
                }
            }
        }
    }



    private void validateTjanstekomponenter(JsonBestallning bestallning) {
        bestallning.getInkludera().getTjanstekomponenter().each() { tjanstekomponentBestallning ->
            Tjanstekomponent tjanstekomponent = daoService.getTjanstekomponentByHSAId(tjanstekomponentBestallning.getHsaId())
            if (tjanstekomponent == null) {
                tjanstekomponent = new Tjanstekomponent()
                tjanstekomponent.setHsaId(tjanstekomponentBestallning.getHsaId())
                tjanstekomponent.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
                setMetaData(tjanstekomponent, false)
            } else {
                if (!tjanstekomponent.getBeskrivning().equals(tjanstekomponentBestallning.getBeskrivning())) {
                    setMetaData(tjanstekomponent, false)
                    tjanstekomponent.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
                }
            }
            if (tjanstekomponent.validate()) {
                tjanstekomponentBestallning.setTjanstekomponent(tjanstekomponent)
            } else {
                tjanstekomponent.errors.allErrors.each() { it ->
                    bestallning.addError(validationTagLib.message(error:it))
                }
            }
        }
    }

    private void validateTjanstekontrakt(JsonBestallning bestallning) {
        bestallning.getInkludera().getTjanstekontrakt().each() { tjanstekontraktBestallning ->
            Tjanstekontrakt tjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(tjanstekontraktBestallning.getNamnrymd())

            if (tjanstekontraktBestallning.getTjanstekontrakt() == null) {
                tjanstekontrakt = new Tjanstekontrakt()
                tjanstekontrakt.setNamnrymd(tjanstekontraktBestallning.getNamnrymd())
                tjanstekontrakt.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                tjanstekontrakt.setMajorVersion(tjanstekontraktBestallning.getMajorVersion())
                tjanstekontrakt.setMinorVersion(tjanstekontraktBestallning.getMinorVersion())
                setMetaData(tjanstekontrakt, false)
            } else {
                if (!tjanstekontrakt.getBeskrivning().equals(tjanstekontraktBestallning.getBeskrivning())) {
                    tjanstekontrakt.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                    tjanstekontrakt.setMajorVersion(tjanstekontraktBestallning.getMajorVersion())
                    setMetaData(tjanstekontrakt, false)
                }
            }
            if (tjanstekontrakt.validate()) {
                tjanstekontraktBestallning.setTjanstekontrakt(tjanstekontrakt)
            } else {
                tjanstekontrakt.errors.allErrors.each() {it ->
                    bestallning.addError(validationTagLib.message(error:it))
                }
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

        saveLogiskaAdresser(newData.getLogiskadresser())
        saveTjanstekomponenter(newData.getTjanstekomponenter())
        saveTjanstekontrakt(newData.getTjanstekontrakt())

        createAnropsbehorigheter(newData.getAnropsbehorigheter(), from)
        createVagval(newData.getVagval(), from)
    }

    private saveLogiskaAdresser(List<LogiskadressBestallning> logiskadressBestallningar) {
        logiskadressBestallningar.each() { logiskadressBestallning ->
            logiskadressBestallning.logiskAdress.save()
        }
    }

    private saveTjanstekomponenter(List<TjanstekomponentBestallning> tjanstekomponentBestallningar) {
        tjanstekomponentBestallningar.each() { tjanstekomponentBestallning ->
            tjanstekomponentBestallning.getTjanstekomponent().save()
        }
    }

    private saveTjanstekontrakt(List<TjanstekontraktBestallning> tjanstekontraktBestallningar) {
        tjanstekontraktBestallningar.each() { tjanstekontraktBestallning ->
            tjanstekontraktBestallning.getBeskrivning().save()
        }
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
                def result = a.save(validate: false)
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
