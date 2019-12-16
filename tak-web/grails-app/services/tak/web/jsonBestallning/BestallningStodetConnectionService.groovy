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

import tak.web.I18nService

class BestallningStodetConnectionService {

    def grailsApplication
    I18nService i18nService


    Boolean isJsonBestallningOn(){
        return grailsApplication.config.tak.bestallning.on.size() != 0 && Boolean.parseBoolean(grailsApplication.config.tak.bestallning.on);
    }

    String validateConnectionConfig() {
        String configErrors = "";
        if (grailsApplication.config.tak.bestallning.url?.isEmpty()) {
            configErrors += i18nService.msg("bestallning.error.url") + "<br/>"
        }
        if (grailsApplication.config.tak.bestallning.cert?.isEmpty()) {
            configErrors += i18nService.msg("bestallning.error.cert") + "<br/>"
        } else {
            String cert = grailsApplication.config.tak.bestallning.cert
            File f = new File(System.getenv("TAK_HOME") + "/security/" + cert)
            if (!f.exists()) {
                i18nService.msg("bestallning.error.certNotFound") + "\n"
            }
        }
        if (grailsApplication.config.tak.bestallning.pw?.isEmpty()) {
            configErrors += i18nService.msg("bestallning.error.pw") + "<br/>"
        }
        if (grailsApplication.config.tak.bestallning.serverCert?.isEmpty()) {
            configErrors += i18nService.msg("bestallning.error.servercert") + "<br/>"
        } else {
            String serverCert = grailsApplication.config.tak.bestallning.serverCert
            File f = new File(System.getenv("TAK_HOME") + "/security/" + serverCert)
            if (!f.exists()) {
                i18nService.msg("bestallning.error.serverCertNotFound") + "\n"
            }
        }
        if (grailsApplication.config.tak.bestallning.serverPw?.isEmpty()) {
            configErrors += i18nService.msg("bestallning.error.serverpw") + "<br/>"
        }
    return configErrors
    }

}
