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
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

final String localMavenRepo = System.getenv('JENKINS_HOME') ? "file://" + new File(System.getProperty('user.home'), '.m2/repository').absolutePath :
		"${System.getProperty('user.home')}/.m2/repository"

//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
	// include dependencies from maven pom
	pom true
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
//        mavenLocal(System.getenv('GRAILS_MAVEN_LOCAL'))
        mavenCentral()
		mavenRepo "file://${localMavenRepo}"

		//mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		// Dependency specified redundantly here, since maven-managed dependencies are not part of classpath when
		// hibernate is bootstrapped
		compile ("se.skltp.tak:tak-core:${appVersion}") {
			excludes 'aspectjweaver', 'hibernate-entitymanager', 'mysql-connector-java', 'spring-beans', 'spring-core',
				'spring-orm', 'spring-jdbc', 'spring-tx', 'spring-aop', 'spring-context', 'spring-web', 'spring-test',
				'slf4j-api', 'slf4j-log4j12', 'log4j', 'hsqldb', 'dbunit'
		}
    }

	plugins {
		compile ":mail:1.0.7"

		runtime ":hibernate:$grailsVersion"
		// Uncomment these (or add new ones) to enable additional resources capabilities
		//runtime ":zipped-resources:1.0"
		//runtime ":cached-resources:1.0"
		//runtime ":yui-minify-resources:0.1.4"

		build ":tomcat:$grailsVersion"

		runtime (":shiro:1.1.4") {
			excludes 'servlet-api'
		}
	}
}
