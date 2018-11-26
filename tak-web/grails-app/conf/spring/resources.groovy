/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 					<http://cehis.se/>
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
import org.apache.shiro.authc.credential.Sha1CredentialsMatcher
import org.springframework.context.MessageSource
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import tak.web.DAOService
import tak.web.alerter.LogAlerterService
import tak.web.alerter.MailAlerterService
import tak.web.BestallningService
import se.skltp.tak.core.entity.PubVersionController
import org.springframework.beans.factory.config.ListFactoryBean
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

// Place your Spring DSL code here
beans = {
    credentialMatcher(Sha1CredentialsMatcher) {
        storedCredentialsHexEncoded = true
    }

    bestallningService(BestallningService) {
        daoService = ref('daoService')
        i18nService = ref('i18nService')
        validationTagLib = ref('validationTagLib')
    }

    mailAlerter(MailAlerterService) {
        mailService = ref('mailService')
        i18nService = ref('i18nService')
    }

    validationTagLib(ValidationTagLib)
    logAlerter(LogAlerterService)
    daoService(DAOService)

    bestallningService(BestallningService) {
        bestallningUrl = application.config.tak.bestallning.url
        bestallningPw = application.config.tak.bestallning.pw
        bestallningCert = application.config.tak.bestallning.cert
    }

    if (application.config.tak.alert.on.publicera.size() == 0 || !Boolean.parseBoolean(application.config.tak.alert.on.publicera)) {
        pubVersionControllerBean(PubVersionController) {
            alerters(ListFactoryBean) {
                sourceList = []
            }
        }
    } else {
        pubVersionControllerBean(PubVersionController) {
            alerters(ListFactoryBean) {
                sourceList = [ref('mailAlerter'), ref('logAlerter')]
            }
        }
    }
}