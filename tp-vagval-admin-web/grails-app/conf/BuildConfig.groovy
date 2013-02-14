grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
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
        mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // Dependency specified redundantly here, since maven-managed dependencies are not part of classpath when
		// hibernate is bootstrapped
		compile ("se.skl.tp:tp-vagval-admin-core:${appVersion}") {
			excludes 'aspectjweaver', 'hibernate-entitymanager', 'mysql-connector-java', 'spring-beans', 'spring-core',
				'spring-orm', 'spring-jdbc', 'spring-tx', 'spring-aop', 'spring-context', 'spring-web', 'spring-test',
				'slf4j-api', 'slf4j-log4j12', 'log4j', 'hsqldb', 'dbunit'
		}
    }
}
