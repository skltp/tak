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
// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

 grails.config.locations = []
 
if(System.getenv('TAK_HOME')) {
	grails.config.locations << "file:${System.getenv('TAK_HOME')}/${appName}-config.properties"
}

grails.project.groupId = se.skl.tp
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        // grails.serverURL = "http://www.changeme.com"

        def catalinaBase = System.properties.getProperty('catalina.base')
		def logDirectory = catalinaBase ? "${catalinaBase}/logs" : "."

        log4j = {
            appenders {
                // set up a log file in the standard tomcat area; be sure to use .toString() with ${}
                rollingFile name:'tomcatLog', file:"${logDirectory}/${appName}.log".toString()
            	rollingFile name:"audit", file:"${logDirectory}/${appName}-audit.log".toString(), layout:pattern(conversionPattern: '%d: %m%n')
                'null' name:'stacktrace'
            }

            root {
                // change the root logger to my tomcatLog file
                error 'tomcatLog'
                additivity = true
            }

            error tomcatLog:'stackTrace'

            // set level for my messages; this uses the root logger (and thus the tomcatLog file)
            info 'grails.app'

			info audit:'grails.app.controller.se.skl.tp.vagval.admin.core.entity'
		}
    }
    development {
        // grails.serverURL = "http://localhost:8080/${appName}"
    }
    test {
        // grails.serverURL = "http://localhost:8080/${appName}"
    }

}

// log4j configuration
log4j = {
    error  'org.codehaus.groovy.grails.web.servlet', //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
    info 'grails.app', 'se.skl.tp.vagval.admin'
}
