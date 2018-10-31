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
import grails.converters.JSON
import org.apache.commons.logging.LogFactory
import org.apache.shiro.SecurityUtils
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib
import org.springframework.transaction.annotation.Transactional
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*

import java.sql.Date

@Transactional
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

    @Transactional(readOnly = true)
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
            it.setAnropsbehorighet(exist);
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
            it.setVagval(exist)
        }
    }

    private void validateLogiskAdresser(JsonBestallning bestallning) {
        bestallning.getInkludera().getLogiskadresser().each() { logiskadressBestallning ->
            LogiskAdress logiskAdress = createOrUpdateLogiskAddress(logiskadressBestallning)

            if (logiskAdress.validate()) {
                logiskadressBestallning.setLogiskAdress(logiskAdress)
            } else {
                logiskAdress.errors.allErrors.each() { it ->
                    bestallning.addError(i18nService.msg("bestallning.error.for.logiskAdress") + validationTagLib.message(error: it))
                }
            }
        }
    }

    private LogiskAdress createOrUpdateLogiskAddress(LogiskadressBestallning logiskadressBestallning) {
        LogiskAdress logiskAdress = daoService.getLogiskAdressByHSAId(logiskadressBestallning.getHsaId())
        if (logiskAdress == null) {
            logiskAdress = new LogiskAdress()
            logiskAdress.setHsaId(logiskadressBestallning.getHsaId())
            logiskAdress.setBeskrivning(logiskadressBestallning.getBeskrivning())
        } else {
            if (!logiskAdress.getBeskrivning().equals(logiskadressBestallning.getBeskrivning())) {
                logiskAdress.setBeskrivning(logiskadressBestallning.getBeskrivning())
            }
        }
        return logiskAdress
    }

    private void validateTjanstekomponenter(JsonBestallning bestallning) {
        bestallning.getInkludera().getTjanstekomponenter().each() { tjanstekomponentBestallning ->
            Tjanstekomponent tjanstekomponent = createOrUpdateTjanstekomponent(tjanstekomponentBestallning)
            if (tjanstekomponent.validate()) {
                tjanstekomponentBestallning.setTjanstekomponent(tjanstekomponent)
            } else {
                tjanstekomponent.errors.allErrors.each() { it ->
                    bestallning.addError(i18nService.msg("bestallning.error.for.tjanstekomponent") + validationTagLib.message(error: it))
                }
            }
        }
    }

    private Tjanstekomponent createOrUpdateTjanstekomponent(TjanstekomponentBestallning tjanstekomponentBestallning) {
        Tjanstekomponent tjanstekomponent = daoService.getTjanstekomponentByHSAId(tjanstekomponentBestallning.getHsaId())
        if (tjanstekomponent == null) {
            tjanstekomponent = new Tjanstekomponent()
            tjanstekomponent.setHsaId(tjanstekomponentBestallning.getHsaId())
            tjanstekomponent.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
        } else {
            if (!tjanstekomponent.getBeskrivning().equals(tjanstekomponentBestallning.getBeskrivning())) {
                tjanstekomponent.setBeskrivning(tjanstekomponentBestallning.getBeskrivning())
            }
        }
        return tjanstekomponent
    }

    private void validateTjanstekontrakt(JsonBestallning bestallning) {
        bestallning.getInkludera().getTjanstekontrakt().each() { tjanstekontraktBestallning ->
            Tjanstekontrakt tjanstekontrakt = createOrUpdateTjanstekontrakt(tjanstekontraktBestallning)
            if (tjanstekontrakt.validate()) {
                tjanstekontraktBestallning.setTjanstekontrakt(tjanstekontrakt)
            } else {
                tjanstekontrakt.errors.allErrors.each() { it ->
                    bestallning.addError(i18nService.msg("bestallning.error.for.tjanstekontrakt") + validationTagLib.message(error: it))
                }
            }
        }
    }

    private Tjanstekontrakt createOrUpdateTjanstekontrakt(TjanstekontraktBestallning tjanstekontraktBestallning) {
        Tjanstekontrakt tjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(tjanstekontraktBestallning.getNamnrymd())
        if (tjanstekontrakt == null) {
            tjanstekontrakt = new Tjanstekontrakt()
            tjanstekontrakt.setNamnrymd(tjanstekontraktBestallning.getNamnrymd())
            tjanstekontrakt.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
            tjanstekontrakt.setMajorVersion(tjanstekontraktBestallning.getMajorVersion())
            tjanstekontrakt.setMinorVersion(tjanstekontraktBestallning.getMinorVersion())
        } else {
            if (!tjanstekontrakt.getBeskrivning().equals(tjanstekontraktBestallning.getBeskrivning())) {
                tjanstekontrakt.setBeskrivning(tjanstekontraktBestallning.getBeskrivning())
                tjanstekontrakt.setMajorVersion(tjanstekontraktBestallning.getMajorVersion())
            }
        }
        return tjanstekontrakt
    }


    private validateAddedAnropsbehorigheter(JsonBestallning bestallning) {
        bestallning.inkludera.anropsbehorigheter.each() { anropsbehorighetBestallning ->
            def logiskAdressHSAId = anropsbehorighetBestallning.getLogiskAdress()
            def komponentHSAId = anropsbehorighetBestallning.getTjanstekonsument()
            def kontraktNamnrymd = anropsbehorighetBestallning.getTjanstekontrakt()

            if (logiskAdressHSAId == null || komponentHSAId == null || kontraktNamnrymd == null) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.info.for.anropsbehorighet"))
                return
            }

            Tjanstekomponent tjanstekomponent = findTjanstekomponentInDBorOrder(komponentHSAId, bestallning)
            LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(logiskAdressHSAId, bestallning)
            Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(kontraktNamnrymd, bestallning)


            if (!tjanstekontrakt) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [kontraktNamnrymd]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [kontraktNamnrymd]))
            }

            if (!tjanstekomponent) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [komponentHSAId]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [komponentHSAId]))
            }

            if (!logiskAdress) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.logiskAdress.for.anropsbehorighet", [logiskAdressHSAId]))
                log.error(i18nService.msg("bestallning.error.saknas.logiskAdress.for.anropsbehorighet", [logiskAdressHSAId]))
            }

            if (tjanstekomponent == null || logiskAdress == null || tjanstekontrakt == null) return



            Anropsbehorighet anropsbehorighet = createAnropsbehorighet(logiskAdress, tjanstekontrakt, tjanstekomponent, bestallning.genomforandeTidpunkt)

            anropsbehorighet.setIntegrationsavtal("test")
            if (anropsbehorighet.validate()) {
                anropsbehorighetBestallning.setAnropsbehorighet(anropsbehorighet)
            } else {
                anropsbehorighet.errors.allErrors.each() { it ->
                    bestallning.addError(i18nService.msg("bestallning.error.for.anropsbehorighet") + validationTagLib.message(error: it))
                }
            }
        }
    }

    private validateAddedVagval(JsonBestallning bestallning) {
        bestallning.getInkludera().getVagval().each() { vagvalBestallning ->
            def adressString = vagvalBestallning.getAdress()
            def rivta = vagvalBestallning.getRivtaprofil()
            def komponentHSAId = vagvalBestallning.getTjanstekomponent()
            def logiskAdressHSAId = vagvalBestallning.getLogiskAdress()
            def kontraktNamnrymd = vagvalBestallning.getTjanstekontrakt()

            if (adressString == null || rivta == null || komponentHSAId == null || logiskAdressHSAId == null || kontraktNamnrymd == null) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.info.for.vagval"))
                log.error(i18nService.msg("bestallning.error.saknas.info.for.vagval") + " Adress:" + adressString + " Rivta:" + rivta +
                        " Komponent:" + komponentHSAId + " LogiskAdress:" + logiskAdressHSAId + " Kontrakt:" + kontraktNamnrymd + ".")
                return
            }

            RivTaProfil profil = findRivtaInDB(rivta)
            Tjanstekomponent tjanstekomponent = findTjanstekomponentInDBorOrder(komponentHSAId, bestallning)
            LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(logiskAdressHSAId, bestallning)
            Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(kontraktNamnrymd, bestallning)

            if (!profil) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.rivtaprofil.for.vagval", [rivta]))
                log.error(i18nService.msg("bestallning.error.saknas.rivtaprofil.for.vagval", [rivta]))
            }

            if (!tjanstekomponent) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.vagval", [komponentHSAId]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.vagval", [komponentHSAId]))
            }

            if (!logiskAdress) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.logiskAdress.for.vagval", [logiskAdressHSAId]))
                log.error(i18nService.msg("bestallning.error.saknas.logiskAdress.for.vagval", [logiskAdressHSAId]))
            }

            if (!tjanstekontrakt) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [kontraktNamnrymd]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.vagval", [kontraktNamnrymd]))
            }

            if (profil == null || tjanstekomponent == null || logiskAdress == null || tjanstekontrakt == null) return

            Vagval vagval = new Vagval()
            AnropsAdress anropsAdress = daoService.getAnropsAdress(adressString, rivta, komponentHSAId)
            if (anropsAdress == null) {
                anropsAdress = createAnropsAdress(adressString, profil, tjanstekomponent)
            }

            if (anropsAdress.validate()) {
                vagval.setAnropsAdress(anropsAdress)
            } else {
                anropsAdress.errors.allErrors.each() { it ->
                    bestallning.addError(i18nService.msg("bestallning.error.for.vagval") + validationTagLib.message(error: it))
                }
                return
            }

            vagval.setFromTidpunkt(bestallning.genomforandeTidpunkt)
            vagval.setTomTidpunkt(generateTomDate(bestallning.genomforandeTidpunkt))
            vagval.setLogiskAdress(logiskAdress)
            vagval.setTjanstekontrakt(tjanstekontrakt)

            if (vagval.validate()) {
                vagvalBestallning.setVagval(vagval)
            } else {
                vagval.errors.allErrors.each() { it ->
                    bestallning.addError(i18nService.msg("bestallning.error.for.vagval") + validationTagLib.message(error: it))
                }
            }
        }
    }


    private LogiskAdress findLogiskAdressInDBorOrder(String hsaId, JsonBestallning bestallning) {
        LogiskAdress existLogiskAdress = daoService.getLogiskAdressByHSAId(hsaId)
        if (existLogiskAdress == null) {
            bestallning.getInkludera().getLogiskadresser().each() { iter ->
                if (hsaId.equals(iter.getHsaId())) {
                    existLogiskAdress = iter.getLogiskAdress()
                }
            }
        }
        return existLogiskAdress
    }

    private Tjanstekomponent findTjanstekomponentInDBorOrder(String hsaId, JsonBestallning bestallning) {
        Tjanstekomponent existTjanstekomponent = daoService.getTjanstekomponentByHSAId(hsaId)
        if (existTjanstekomponent == null) {
            bestallning.getInkludera().getTjanstekomponenter().each() { tjanstekomponentBestallning ->
                if (hsaId.equals(tjanstekomponentBestallning.getHsaId())) {
                    existTjanstekomponent = tjanstekomponentBestallning.getTjanstekomponent()
                }
            }
        }
        return existTjanstekomponent
    }

    private Tjanstekontrakt findTjanstekontraktInDBorOrder(String namnrymd, JsonBestallning bestallning) {
        Tjanstekontrakt existTjanstekontrakt = daoService.getTjanstekontraktByNamnrymd(namnrymd)
        if (existTjanstekontrakt == null) {
            bestallning.getInkludera().getTjanstekontrakt().each() { iter ->
                if (namnrymd.equals(iter.getNamnrymd())) {
                    existTjanstekontrakt = iter.getTjanstekontrakt()
                }
            }
        }
        return existTjanstekontrakt
    }

    private RivTaProfil findRivtaInDB(String rivta) {
        return daoService.getRivtaByNamn(rivta)
    }


    def executeOrder(JsonBestallning bestallning) {
        if (bestallning.getBestallningErrors().size() == 0) {
            deleteObjects(bestallning.getExkludera(), bestallning.getGenomforandeTidpunkt());
            createObjects(bestallning.getInkludera());
            return bestallning
        }
    }

    private void deleteObjects(KollektivData deleteData, Date genomforande) {
        //Only Vagval and Anropsbehorighet is to be deleted via json...
        //If matching entity object found in db, set that object to delete..

        deleteVagval(deleteData, genomforande)
        deleteAnropsbehorigheter(deleteData, genomforande)
    }

    private deleteVagval(KollektivData deleteData, Date date) {
        deleteData.getVagval().each() { it ->
            deleteWithCheck(it.getVagval())
        }
    }

    private deleteAnropsbehorigheter(KollektivData deleteData, Date date) {
        deleteData.getAnropsbehorigheter().each() { it ->
            deleteWithCheck(it.getAnropsbehorighet())
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

    private void createObjects(KollektivData newData) {
        saveLogiskaAdresser(newData.getLogiskadresser())
        saveTjanstekomponenter(newData.getTjanstekomponenter())
        saveTjanstekontrakt(newData.getTjanstekontrakt())

        saveAnropsbehorigheter(newData.getAnropsbehorigheter())
        saveVagval(newData.getVagval())
    }

    private saveLogiskaAdresser(List<LogiskadressBestallning> logiskadressBestallningar) {
        logiskadressBestallningar.each() { it ->
            createOrUpdate(it.getLogiskAdress().id, it.getLogiskAdress())
        }
    }


    private saveTjanstekomponenter(List<TjanstekomponentBestallning> tjanstekomponentBestallningar) {
        tjanstekomponentBestallningar.each() { it ->
            createOrUpdate(it.getTjanstekomponent().id, it.getTjanstekomponent())
        }
    }


    private saveTjanstekontrakt(List<TjanstekontraktBestallning> tjanstekontraktBestallningar) {
        tjanstekontraktBestallningar.each() { it ->
            createOrUpdate(it.getTjanstekontrakt().id, it.getTjanstekontrakt())
        }
    }


    private saveAnropsbehorigheter(List<AnropsbehorighetBestallning> anropsbehorighetBestallnings) {
        anropsbehorighetBestallnings.each() { it ->
            createOrUpdate(it.getAnropsbehorighet().id, it.getAnropsbehorighet())
        }
    }

    private saveVagval(List<VagvalBestallning> vagvalBestallnings) {
        vagvalBestallnings.each() { it ->
            createOrUpdate(it.getVagval().getAnropsAdress().id, it.getVagval().getAnropsAdress())
            createOrUpdate(it.getVagval().id, it.getVagval())
        }
    }


    private createOrUpdate(long id, AbstractVersionInfo entityInstance) {
        if (id == 0l) { //create
            setMetaData(entityInstance, false)
            log.info "Entity ${entityInstance.toString()} created by ${entityInstance.getUpdatedBy()}:"
            log.info "${entityInstance as JSON}"
        } else {
            // todo
            setMetaData(entityInstance, false)
            log.info "Entity ${entityInstance.toString()} updated by ${entityInstance.getUpdatedBy()}:"
            log.info "${entityInstance as JSON}"
        }
        entityInstance.save()
    }

    private Anropsbehorighet createAnropsbehorighet(LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt, Tjanstekomponent tjanstekomponent, Date from) {
        Anropsbehorighet anropsbehorighet = new Anropsbehorighet()
        anropsbehorighet.setFromTidpunkt(from)
        anropsbehorighet.setTomTidpunkt(generateTomDate(from))
        anropsbehorighet.setLogiskAdress(logiskAdress)
        anropsbehorighet.setTjanstekontrakt(tjanstekontrakt)
        anropsbehorighet.setTjanstekonsument(tjanstekomponent)
        return anropsbehorighet
    }

    private AnropsAdress createAnropsAdress(String adress, RivTaProfil rivTaProfil, Tjanstekomponent tjanstekomponent) {
        AnropsAdress aa = new AnropsAdress()
        aa.setAdress(adress)
        aa.setRivTaProfil(rivTaProfil)
        aa.setTjanstekomponent(tjanstekomponent)
        return aa
    }


    private void setMetaData(AbstractVersionInfo versionInfo, isDeleted) {
        def principal = SecurityUtils.getSubject()?.getPrincipal()
        versionInfo.setUpdatedTime(new java.util.Date())
        versionInfo.setUpdatedBy(principal)
        versionInfo.setDeleted(isDeleted)
    }

    private Date generateTomDate(Date fromDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.add(Calendar.YEAR, 100);
        Date d = new Date(c.getTime().getTime());
        return d;
    }

}
