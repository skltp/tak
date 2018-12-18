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
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib
import org.hibernate.OptimisticLockException
import org.springframework.transaction.annotation.Transactional
import se.skltp.tak.core.entity.AbstractVersionInfo
import se.skltp.tak.web.jsonBestallning.BestallningsData
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import tak.web.I18nService
import tak.web.alerter.AlerterConfigException
import tak.web.alerter.JsonBetallningMailAlerterService

@Transactional
class BestallningService {

    private static final log = LogFactory.getLog(this)
    ConstructorService constructorService
    I18nService i18nService
    ValidationTagLib validationTagLib;
    JsonBetallningMailAlerterService jsonBestallningMailAlerter

    JsonBestallning createOrderObject(String jsonBestallningString) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonBestallning bestallning = objectMapper.readValue(jsonBestallningString, JsonBestallning.class);
        return bestallning;
    }

    @Transactional(readOnly = true)
    BestallningsData prepareOrder(JsonBestallning bestallning) {
        BestallningsData data = new BestallningsData(bestallning)
        constructorService.preparePlainObjects(data)
        if (data.hasErrors()) return data

        constructorService.prepareComplexObjectsRelations(data)
        if (data.hasErrors()) return data

        constructorService.prepareComplexObjects(data)
        return data
    }


    def executeOrder(BestallningsData data) {
        if (!data.hasErrors()) {
            data.getAllLogiskAdresser().each {
                createOrUpdate(it.id, it)
            }

            data.getAllTjanstekomponent().each {
                createOrUpdate(it.id, it)
            }

            data.getAllTjanstekontrakt().each {
                sendMailAboutNewTjanstekontrakt(it.namnrymd, data.bestallning.genomforandeTidpunkt)
                createOrUpdate(it.id, it)
            }

            data.getAllaVagval().each {
                if (it.oldVagval != null) {
                    createOrUpdate(it.oldVagval.anropsAdress.id, it.oldVagval.anropsAdress)
                    createOrUpdate(it.oldVagval.id, it.oldVagval)
                }

                if (it.newVagval != null) {
                    createOrUpdate(it.newVagval.anropsAdress.id, it.newVagval.anropsAdress)
                    createOrUpdate(it.newVagval.id, it.newVagval)
                }
            }

            data.getAllaAnropsbehorighet().each {
                createOrUpdate(it.id, it)
            }
        }
    }

    private sendMailAboutNewTjanstekontrakt(String contractName, Date genomforandeTidpunkt) {
        try {
            jsonBestallningMailAlerter.alertOnNewContract(contractName, genomforandeTidpunkt)
        } catch (AlerterConfigException e) {
            log.error(e.message)
        }
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
                entityInstance.errors.allErrors.each() { it ->
                    e.fullMessage = validationTagLib.message(error: it)
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
}
