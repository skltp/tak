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
import org.hibernate.OptimisticLockException
import org.springframework.transaction.annotation.Transactional
import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*

import java.sql.Date

@Transactional
class BestallningService {

    private static final log = LogFactory.getLog(this)
    DAOService daoService;
    I18nService i18nService;
    String bestallningUrl
    String bestallningPw
    String bestallningCert
    String serverCert
    String serverPw

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
            List<Anropsbehorighet> exist = daoService.getAnropsbehorighet(logisk, konsument, kontrakt, bestallning.getGenomforandeTidpunkt(), generateTomDate(bestallning.getGenomforandeTidpunkt()))
            if (exist.size() == 0) {
                bestallning.addInfo(i18nService.msg("bestallning.error.saknas.anropsbehorighet", [logisk, konsument, kontrakt]))
            }
            it.setAropsbehorigheterForDelete(exist);
        }
    }

    private validateDeletedVagval(JsonBestallning bestallning) {
        bestallning.getExkludera().getVagval().each() { it ->
            def rivta = it.getRivtaprofil()
            def komponent = it.getTjanstekomponent()
            def logisk = it.getLogiskAdress()
            def kontrakt = it.getTjanstekontrakt()
            List<Vagval> exist = daoService.getVagval(logisk, kontrakt, rivta, komponent, bestallning.genomforandeTidpunkt, generateTomDate(bestallning.genomforandeTidpunkt))
            if (exist.size() == 0) {
                bestallning.addInfo(i18nService.msg("bestallning.error.saknas.vagval", [logisk, kontrakt, rivta, komponent]))
            }
            it.setVagvalForDelete(exist)
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
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [kontraktNamnrymd]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekontrakt.for.anropsbehorighet", [kontraktNamnrymd]))
            }

            if (!tjanstekomponent) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [komponentHSAId]))
                log.error(i18nService.msg("bestallning.error.saknas.tjanstekomponent.for.anropsbehorighet", [komponentHSAId]))
            }

            if (!logiskAdress) {
                bestallning.addError(i18nService.msg("bestallning.error.saknas.logiskAdress.for.anropsbehorighet", [logiskAdressHSAId]))
                log.error(i18nService.msg("bestallning.error.saknas.logiskAdress.for.anropsbehorighet", [logiskAdressHSAId]))
            }

            if (bestallning.hasErrors()) return

            Anropsbehorighet anropsbehorighet = createAnropsbehorighet(logiskAdress, tjanstekontrakt, tjanstekomponent, bestallning.genomforandeTidpunkt)

            List<Anropsbehorighet> liveAnropsbehorighet = daoService.getAnropsbehorighet(logiskAdressHSAId, komponentHSAId, kontraktNamnrymd, bestallning.getGenomforandeTidpunkt(), generateTomDate(bestallning.getGenomforandeTidpunkt()))
            liveAnropsbehorighet.each() { ab ->
                ab.setTomTidpunkt(generateDateMinusDag(bestallning.genomforandeTidpunkt))
            }

            anropsbehorighetBestallning.setNewAnropsbehorighet(anropsbehorighet)
            anropsbehorighetBestallning.setOldAnropsbehorighet(liveAnropsbehorighet)

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

            if (bestallning.hasErrors()) return

            Vagval newVagval = createOrUpdateVagval(bestallning, logiskAdress, tjanstekomponent, tjanstekontrakt, profil, adressString)
            if (bestallning.hasErrors()) return

            List<Vagval> liveVagval = daoService.getVagval(logiskAdressHSAId, kontraktNamnrymd, rivta, komponentHSAId, bestallning.genomforandeTidpunkt, generateTomDate(bestallning.genomforandeTidpunkt))
            liveVagval.each() { vv ->
                vv.setTomTidpunkt(generateDateMinusDag(bestallning.genomforandeTidpunkt))
            }

            vagvalBestallning.setNewVagval(newVagval)
            vagvalBestallning.setOldVagval(liveVagval)
        }
    }

    private Vagval createOrUpdateVagval(JsonBestallning bestallning, LogiskAdress logiskAdress, Tjanstekomponent tjanstekomponent, Tjanstekontrakt tjanstekontrakt, RivTaProfil profil, String adressString) {
        Vagval vagval = new Vagval()
        vagval.setFromTidpunkt(bestallning.genomforandeTidpunkt)
        vagval.setTomTidpunkt(generateTomDate(bestallning.genomforandeTidpunkt))
        vagval.setLogiskAdress(logiskAdress)
        vagval.setTjanstekontrakt(tjanstekontrakt)

        AnropsAdress anropsAdress = daoService.getAnropsAdress(profil.getNamn(), tjanstekomponent.getHsaId())

        if (anropsAdress != null) {
            anropsAdress.setAdress(adressString)
        } else {
            anropsAdress = createAnropsAdress(adressString, profil, tjanstekomponent)
        }

        if (anropsAdress.validate()) {
            vagval.setAnropsAdress(anropsAdress)
        } else {
            anropsAdress.errors.allErrors.each() { it ->
                bestallning.addError(i18nService.msg("bestallning.error.for.vagval") + validationTagLib.message(error: it))
            }
        }
        return vagval
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
        if (!bestallning.hasErrors()) {
            deleteObjects(bestallning.getExkludera());
            saveNewObjects(bestallning.getInkludera());
            return bestallning
        }
    }

    private void deleteObjects(KollektivData deleteData) {
        //Only Vagval and Anropsbehorighet is to be deleted via json...
        //If matching entity object found in db, set that object to delete..

        deleteVagval(deleteData)
        deleteAnropsbehorigheter(deleteData)
    }

    private deleteVagval(KollektivData deleteData) {
        deleteData.getVagval().each() { it ->
            it.getVagvalForDelete().each() { vagval ->
                deleteWithCheck(vagval)
                Set<Vagval> addressVagval = vagval.getAnropsAdress().getVagVal()

                //if 1:1 vagval:anropAdress
                if (addressVagval.size() == 1 && addressVagval.contains(vagval)) {
                    deleteWithCheck(vagval.getAnropsAdress())
                }

                //if all connected vagval.delete = true
                boolean vagvalDeleted = true
                addressVagval.each { vv ->
                    if (!vv.getDeleted()) vagvalDeleted = false
                }
                if (vagvalDeleted) deleteWithCheck(vagval.getAnropsAdress())
            }
        }
    }

    private deleteAnropsbehorigheter(KollektivData deleteData) {
        deleteData.getAnropsbehorigheter().each() { it ->
            it.getAropsbehorigheterForDelete().each() { anropsbehorighet ->
                deleteWithCheck(anropsbehorighet)
            }
        }
    }


    private void saveNewObjects(KollektivData newData) {
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
            sendMailAboutNewTjanstekontrakt(it)
        }
    }

    private sendMailAboutNewTjanstekontrakt(TjanstekontraktBestallning bestallning) {
        //todo
    }

    private saveAnropsbehorigheter(List<AnropsbehorighetBestallning> anropsbehorighetBestallnings) {
        anropsbehorighetBestallnings.each() { ab ->
            ab.getOldAnropsbehorighet().each { oldAb ->
                createOrUpdate(oldAb.getId(), oldAb)
            }
            createOrUpdate(ab.getNewAnropsbehorighet().id, ab.getNewAnropsbehorighet())
        }
    }

    private saveVagval(List<VagvalBestallning> vagvalBestallnings) {
        vagvalBestallnings.each() { newVagval ->
            newVagval.getOldVagval().each() { oldVagval ->
                createOrUpdate(oldVagval.getId(), oldVagval)
            }
            createOrUpdate(newVagval.getNewVagval().getAnropsAdress().id, newVagval.getNewVagval().getAnropsAdress())
            createOrUpdate(newVagval.getNewVagval().id, newVagval.getNewVagval())
        }
    }

    private deleteWithCheck(AbstractVersionInfo entityInstance) {
        if (entityInstance != null) {
            if (entityInstance.getPubVersion()) {
                setMetaData(entityInstance, null)
                entityInstance.save()
                log.info "Entity ${entityInstance.toString()} was set to deleted by ${entityInstance.getUpdatedBy()}:"
                log.info "${entityInstance as JSON}"
            } else {
                entityInstance.delete()
                log.info "Entity ${entityInstance.toString()} was deleted by ${entityInstance.getUpdatedBy()}:"
                log.info "${entityInstance as JSON}"
            }
        }
    }

    private createOrUpdate(long id, AbstractVersionInfo entityInstance) {
        if (id == 0l) {
            setMetaData(entityInstance, false)
            entityInstance.save(failOnError: true,flush: true)

            log.info "Entity ${entityInstance.toString()} created by ${entityInstance.getUpdatedBy()}:"
            log.info "${entityInstance as JSON}"
        } else {
            AbstractVersionInfo latestVersionOfEntityInstance = entityInstance.get(id)
            if (latestVersionOfEntityInstance.getVersion() > entityInstance.getVersion()) {
                throw new OptimisticLockException(i18nService.msg("bestallning.error.optimistic.lock.exception", [entityInstance.toString()]))

            }
            setMetaData(entityInstance, false)
            entityInstance.merge(failOnError: true, flush: true)

            log.info "Entity ${entityInstance.toString()} updated by ${entityInstance.getUpdatedBy()}:"
            log.info "${entityInstance as JSON}"
        }
    }

    private Anropsbehorighet createAnropsbehorighet(LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt, Tjanstekomponent tjanstekomponent, Date from) {
        Anropsbehorighet anropsbehorighet = new Anropsbehorighet()
        anropsbehorighet.setFromTidpunkt(from)
        anropsbehorighet.setTomTidpunkt(generateTomDate(from))
        anropsbehorighet.setLogiskAdress(logiskAdress)
        anropsbehorighet.setTjanstekontrakt(tjanstekontrakt)
        anropsbehorighet.setTjanstekonsument(tjanstekomponent)
        anropsbehorighet.setIntegrationsavtal("AUTOTAKNING")
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
        versionInfo.setUpdatedBy((String) principal)
        versionInfo.setDeleted(isDeleted)
    }

    public static Date generateTomDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, 100);
        Date d = new Date(c.getTime().getTime());
        return d;
    }

    private Date generateDateMinusDag(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date d = new Date(c.getTime().getTime());
        return d;
    }

    String createTextReport(JsonBestallning bestallning) {
        LinkedList<String> newObjects = new LinkedList<String>();
        LinkedList<String> updatedObjects = new LinkedList<String>();
        List<String> deletedObjects = new LinkedList<String>();
        LinkedList<String> nonDeletedObjects = new LinkedList<String>();

        fillListsWithDeletedObjects(bestallning, deletedObjects, nonDeletedObjects);
        fillListsWithCreatedOrUpdatedObjects(bestallning, newObjects, updatedObjects);

        StringBuffer report = new StringBuffer();
        report.
                append("Platform: ").append(bestallning.plattform).append("\n").
                append("Format Version: ").append(bestallning.formatVersion).append("\n").
                append("Version: ").append(bestallning.version).append("\n").
                append("BestallningsTidpunkt: ").append(bestallning.bestallningsTidpunkt).append("\n").
                append("GenomforandeTidpunkt: ").append(bestallning.genomforandeTidpunkt).append("\n").
                append("Utforare: ").append(bestallning.utforare).append("\n").
                append("Kommentar: ").append(bestallning.kommentar).append("\n");

        report.append("\n").append("Nyligen skapad: \n");
        for (String text : newObjects) {
            report.append(text).append("\n");
        }

        if (newObjects.size() == 0) report.append("-").append("\n");

        report.append("\n").append("Nyligen uppdaterad: \n");
        for (String text : updatedObjects) {
            report.append(text).append("\n");
        }
        if (updatedObjects.size() == 0) report.append("-").append("\n");

        report.append("\n").append("Nyligen borttagen: \n");
        for (String text : deletedObjects) {
            report.append(text).append("\n");
        }
        if (deletedObjects.size() == 0) report.append("-").append("\n");

        report.append("\n").append("Ej existerande för borttagning: \n");
        for (String text : nonDeletedObjects) {
            report.append(text).append("\n");
        }
        if (nonDeletedObjects.size() == 0) report.append("-").append("\n");

        return report.toString()
    }

    private void fillListsWithDeletedObjects(JsonBestallning bestallning, List deletedObjects, List nonDeletedObjects) {
        for (AnropsbehorighetBestallning element : bestallning.exkludera.getAnropsbehorigheter()) {
            if (element.getNewAnropsbehorighet() != null) {
                deletedObjects.add("Anropsbehorighet: " + element.toString())
            } else {
                nonDeletedObjects.add("Anropsbehorighet: " + element.toString())
            }
        }

        for (VagvalBestallning element : bestallning.exkludera.getVagval()) {
            if (element.newVagval != null) {
                deletedObjects.add("Vagval: " + element.toString())
            } else {
                nonDeletedObjects.add("Vagval: " + element.toString())
            }
        }
    }

    private void fillListsWithCreatedOrUpdatedObjects(JsonBestallning bestallning, List newObjects, List updatedObjects) {
        for (LogiskadressBestallning element : bestallning.inkludera.getLogiskadresser()) {
            if (element.getLogiskAdress().getVersion() == 0) {
                newObjects.add("Logiskadress: " + element.toString())
            } else {
                updatedObjects.add("Logiskadress: " + element.toString())
            }
        }
        for (TjanstekontraktBestallning element : bestallning.inkludera.getTjanstekontrakt()) {
            if (element.getTjanstekontrakt().getVersion() == 0) {
                newObjects.add("Tjanstekontrakt: " + element.toString())
            } else {
                updatedObjects.add("Tjanstekontrakt: " + element.toString())
            }
        }
        for (TjanstekomponentBestallning element : bestallning.inkludera.getTjanstekomponenter()) {
            if (element.getTjanstekomponent().getVersion() == 0) {
                newObjects.add("Tjanstekomponent: " + element.toString())
            } else {
                updatedObjects.add("Tjanstekomponent: " + element.toString())
            }
        }

        for (AnropsbehorighetBestallning element : bestallning.inkludera.getAnropsbehorigheter()) {
            newObjects.add("Anropsbehorighet: " + element.toString())
        }

        for (VagvalBestallning element : bestallning.inkludera.getVagval()) {
            newObjects.add("Vagval: " + element.toString())
        }
    }

    String getBestallningUrl() {
        return bestallningUrl
    }

    void setBestallningUrl(String bestallningUrl) {
        this.bestallningUrl = bestallningUrl
    }

    String getBestallningPw() {
        return bestallningPw
    }

    void setBestallningPw(String bestallningPw) {
        this.bestallningPw = bestallningPw
    }

    String getBestallningCert() {
        return bestallningCert
    }

    void setBestallningCert(String bestallningCert) {
        this.bestallningCert = bestallningCert
    }

    String getServerCert() {
        return serverCert
    }

    void setServerCert(String serverCert) {
        this.serverCert = serverCert
    }

    String getServerPw() {
        return serverPw
    }

    void setServerPw(String serverPw) {
        this.serverPw = serverPw
    }

}
