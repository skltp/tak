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

import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.context.MessageSource
import se.skltp.tak.web.entity.TAKSettings

class I18nService {

    boolean transactional = false

    SessionLocaleResolver localeResolver
    MessageSource messageSource

    def msg(String msgKey, List objs = null, String defaultMessage = null) {
        messageSource.getMessage(msgKey,objs?.toArray(),defaultMessage,localeResolver.defaultLocale)
    }

    def msgFromDB(String msgKey){
        TAKSettings.findBySettingName(msgKey)?.settingValue
    }

    def message(Map args) {
        def msgTemplate

        if (args.dbCode) {
            msgTemplate = msgFromDB(args.dbCode)
        } else {
            msgTemplate = msg(args.code, args.attrs, args.default)
        }

        if (args.namedattrs != null) {
            return format(msgTemplate, args.namedattrs)
        }
        return msgTemplate;
    }

    def format(String message, Map namedattrs) {
        for (myattr in namedattrs) {
            String placeholder = '\\$\\{'+myattr.key+'\\}';
            message = message.replaceAll(placeholder, myattr.value.toString());
        }
        return message;
    }
}