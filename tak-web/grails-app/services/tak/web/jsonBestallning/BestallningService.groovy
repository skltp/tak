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

package tak.web.jsonBestallning

import com.fasterxml.jackson.databind.ObjectMapper
import grails.converters.JSON
import grails.validation.ValidationException
import org.apache.commons.logging.LogFactory
import org.apache.shiro.SecurityUtils
import org.hibernate.OptimisticLockException
import org.springframework.transaction.annotation.Transactional
import se.skltp.tak.core.entity.AbstractVersionInfo
import se.skltp.tak.web.jsonBestallning.*

@Transactional
class BestallningService {

    private static final log = LogFactory.getLog(this)
    ConstructorService constructorService

    //Below items needed to download json-files by their number, from provider at bestallningUrl.
    String bestallningUrl
    String bestallningPw
    String bestallningCert
    String serverCert
    String serverPw

    JsonBestallning createOrderObject(String jsonBestallningString) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;
    }

    @Transactional(readOnly = true)
    void prepareOrder(JsonBestallning bestallning) {
        constructorService.preparePlainObjects(bestallning)
        if (bestallning.hasErrors()) return

        constructorService.prepareComplexObjectsRelations(bestallning)
        if (bestallning.hasErrors()) return

        constructorService.prepareComplexObjects(bestallning)
    }


    def executeOrder(JsonBestallning bestallning) {
        if (!bestallning.hasErrors()) {
            disableVagval(bestallning.exkludera.vagval)
            disableAnropsbehorigheter(bestallning.exkludera.anropsbehorigheter)

            saveLogiskaAdresser(bestallning.inkludera.logiskadresser)
            saveTjanstekomponenter(bestallning.inkludera.tjanstekomponenter)
            saveTjanstekontrakt(bestallning.inkludera.tjanstekontrakt)

            saveAnropsbehorigheter(bestallning.inkludera.anropsbehorigheter)
            saveVagval(bestallning.inkludera.vagval)
        }
    }

    private disableVagval(List<VagvalBestallning> vagvalBestallningar) {
        vagvalBestallningar.each() { vvBestallning ->
            vvBestallning.vagvalForDelete.each() { vv ->
                createOrUpdate(vv.id, vv)
            }
        }
    }

    private disableAnropsbehorigheter(List<AnropsbehorighetBestallning> vagvalBestallningar) {
        vagvalBestallningar.each() { abBestallning ->
            abBestallning.aropsbehorigheterForDelete.each() { ab ->
                createOrUpdate(ab.id, ab)
            }
        }
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

    private saveAnropsbehorigheter(List<AnropsbehorighetBestallning> anropsbehorighetBestallnings) {
        anropsbehorighetBestallnings.each() { ab ->
            createOrUpdate(ab.oldAnropsbehorighet.id, ab.oldAnropsbehorighet)
            createOrUpdate(ab.newAnropsbehorighet.id, ab.newAnropsbehorighet)
        }
    }

    private saveVagval(List<VagvalBestallning> vagvalBestallnings) {
        vagvalBestallnings.each() { newVagval ->
            newVagval.getOldVagval().each() { oldVagval ->
                createOrUpdate(newVagval.oldVagval.anropsAdress.id, newVagval.oldVagval.anropsAdress)
                createOrUpdate(newVagval.oldVagval.id, newVagval.oldVagval)
                createOrUpdate(newVagval.newVagval.anropsAdress.id, newVagval.newVagval.anropsAdress)
                createOrUpdate(newVagval.newVagval.id, newVagval.newVagval)
            }
        }
    }

    private sendMailAboutNewTjanstekontrakt(TjanstekontraktBestallning bestallning) {
        //todo
    }

    void createOrUpdate(long id, AbstractVersionInfo entityInstance) {
        if (id == 0l) {
            setMetaData(entityInstance)
            entityInstance.save(failOnError: true, flush: true)

            log.info "Entity ${entityInstance.toString()} created by ${entityInstance.getUpdatedBy()}:"
            log.info "${entityInstance as JSON}"
        } else {
            AbstractVersionInfo latestVersionOfEntityInstance = entityInstance.get(id)
            if (latestVersionOfEntityInstance.getVersion() > entityInstance.getVersion()) {
                throw new OptimisticLockException(i18nService.msg("bestallning.error.optimistic.lock.exception", [entityInstance.toString()]))
            }
            setMetaData(entityInstance)
            try {
                entityInstance.merge(failOnError: true, flush: true)
            } catch (ValidationException e) {
                e.fullMessage = "123"
                entityInstance.errors.allErrors.each() { it ->
                    e.fullMessage = i18nService.msg("bestallning.error.for.tjanstekontrakt") + validationTagLib.message(error: it)
                }
                throw e
            }


            log.info "Entity ${entityInstance.toString()} updated by ${entityInstance.getUpdatedBy()}:"
            log.info "${entityInstance as JSON}"
        }
    }

    private void setMetaData(AbstractVersionInfo versionInfo) {
        def principal = SecurityUtils.getSubject()?.getPrincipal()
        versionInfo.setUpdatedTime(new java.util.Date())
        versionInfo.setUpdatedBy((String) principal)
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
