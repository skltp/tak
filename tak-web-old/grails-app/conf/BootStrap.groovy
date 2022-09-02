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
import groovy.sql.Sql


import se.skltp.tak.web.entity.*
class BootStrap {
	
	def grailsApplication
     def init = { servletContext ->
		 
		 if(System.getenv('TAK_HOME')) {
			 def logo = grailsApplication.config.tak.image.logo
			 if(logo != null && !logo.equals("") && !"inera-logo.png".equals(logo)) {
				 
				 def source = new File(System.getenv('TAK_HOME') + "/" + logo)
				 def target = new File(servletContext.getRealPath("/images") + "/" + logo);
				 target << source.bytes
			 }
		 }
		 
        switch(grails.util.GrailsUtil.environment) {
        case "development":
        case "test":
			println 'Using H2 in-memory database with test data'
			def config = grailsApplication.config
			String h2InitSqlFilePath = './grails-app/conf/testdata/h2init.sql'
			String initSqlFilePath = './grails-app/conf/testdata/init.sql'
			String testDataSqlFilePath = './grails-app/conf/testdata/testdata.sql'
			String h2InitSql = new File(h2InitSqlFilePath).text
			String initSql = new File(initSqlFilePath).text
			String testDataSql = new File(testDataSqlFilePath).text
			def sql = Sql.newInstance(config.dataSource.url,
				config.dataSource.username,
				config.dataSource.password,
				config.dataSource.driverClassName)
			sql.execute(h2InitSql)
			sql.execute(initSql)
			sql.execute(testDataSql)
			break
        }
     }
     def destroy = {
     }
}
