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

package tak.web.alerter

import org.springframework.mail.MailMessage
import se.skltp.tak.web.entity.TAKSettings

abstract class MailAlerterService {
    static final String FROM_MAIL = "alerter.mail.fromAddress"

    def mailService
    def i18nService;

    String fromAddress
    String[] toAddress

    boolean asyncron = true


    void alert(String TO_MAIL, String subjectPropName, String contentPropName, Map data) throws AlerterConfigException {
        checkMailSettings(TO_MAIL, FROM_MAIL)

        def subject = i18nService.message(dbCode: subjectPropName, namedattrs: [date: new Date().format('yyyy-MM-dd')])
        def contents = i18nService.message(dbCode: contentPropName, namedattrs: data)

        sendMail(subject, contents)
    }

    private void checkMailSettings(String TO_MAIL, String FROM_MAIL) throws AlerterConfigException {
        toAddress = TAKSettings.findBySettingName(TO_MAIL)?.settingValue?.split(',')
        fromAddress = TAKSettings.findBySettingName(FROM_MAIL)?.settingValue

        if (toAddress == null || fromAddress == null) {
            def errorMsg = i18nService.message(code: 'pubVersion.mail.installningar.fel')
            log.error(errorMsg)
            throw new AlerterConfigException(errorMsg);
        }
    }

    private MailMessage sendMail(mailSubject, contents) {
        mailService.sendMail {
            async this.asyncron
            to this.toAddress
            from this.fromAddress
            subject mailSubject
            body contents
        }
    }
}
