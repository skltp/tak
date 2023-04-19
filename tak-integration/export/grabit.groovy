#!/usr/bin/env groovy

/**
 *  The purpose of this scripts is to grab dependencies before you run the import/export script in a
 *  locked down environment i.e no internet connection.
 *
 *  When running a Groovy script that uses Grab for dependencies Grape is used as the dependency manager.
 *  Grape is using Ivy for dependency management. Grape uses the ~/.groovy/grapes directory for
 *  downloading libraries so if you don't have internet access then you have to populate this directory manually.
 *
 *  To download all dependency jars you can run this script with the following system property
 *  $ groovy -Dgrape.root=./grape_repo grabit.groovy
 *
 *  The directory ./grape_repo contains all dependency jars that you have to copy to the
 *  ~/.groovy/grapes directory on the server.
 *
 *
 */


@Grapes([
	@GrabConfig(systemClassLoader=true),
	@Grab(group='mysql', module='mysql-connector-java', version='8.0.29'),
	@Grab(group = 'ch.qos.logback', module = 'logback-classic', version = '1.2.10'),
	@Grab(group = 'net.logstash.logback', module = 'logstash-logback-encoder', version='6.4')
])

import groovy.sql.Sql

println 'Done grabbing'
