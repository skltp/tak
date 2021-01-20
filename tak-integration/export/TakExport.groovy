/**
 * Export TAK data in the "TAK-published-version" format.
 * Ref: https://skl-tp.atlassian.net/wiki/display/NTJP/TAK+versionshantering
 *
 * Uses data from latest published version.
 *
 * Run script using:
 *  $ groovy TakExport.groovy
 */

@Grapes([
		@GrabConfig(systemClassLoader=true),
		@Grab(group='mysql', module='mysql-connector-java', version='5.1.36'),
		@Grab(group = 'ch.qos.logback', module = 'logback-classic', version = '1.2.3'),
		@Grab(group = 'net.logstash.logback', module = 'logstash-logback-encoder', version='6.4')
])

import groovy.sql.Sql
import groovy.json.*
import java.util.zip.GZIPInputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.transform.Field


@Field
static Logger logger = LoggerFactory.getLogger("scriptLogger")

def unzip(byte [] compressed){
	def inflaterStream = new GZIPInputStream(new ByteArrayInputStream(compressed))
	def uncompressedStr = inflaterStream.getText('UTF-8')
	return uncompressedStr
}

def cli = new CliBuilder(
		usage: 'TakCooperationExport [options]',
		header: '\nAvailable options (use -h for help):\n')
cli.with
		{
			h longOpt: 'help', 'Usage Information', required: false
			u longOpt: 'user', 'Username', args: 1, required: true
			p longOpt: 'password', 'Password', args: 1, required: true
			s longOpt: 'server', 'Database host', args: 1, required: true
			d longOpt: 'database', 'Database name', args:1, required: true
		}

try{

	def opt = cli.parse(args)
	if (!opt) return
	if (opt.h) cli.usage()

	def username = opt.u
	def password = opt.p
	def server = opt.s
	def database = opt.d


	def db = Sql.newInstance("jdbc:mysql://$server/$database", username, password, 'com.mysql.jdbc.Driver')

	def  blob = db.firstRow("Select data from PubVersion order by id desc limit 1")
	def  jsonString = unzip(blob[0])

	createJsonDump(jsonString)
} catch (Exception e) {
	logger.error("Exception i TakExport.groovy ", e)
}


String createJsonDump(String jsonString){
	// Convert to json to object
	def jsonObject = (new JsonSlurper()).parseText(jsonString)

	//Streaming
	def jsonWriter = new StringWriter()
	def jsonBuilder = new StreamingJsonBuilder(jsonWriter)

	jsonBuilder {
		formatVersion jsonObject.formatVersion
		version jsonObject.version
		tidpunkt_version jsonObject.tidpunkt
		tidpunkt new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC"))
		utforare "TakExport script"
		kommentar "Export using TAK version " + jsonObject.version + " published " + jsonObject.tidpunkt
		data {
			rivtaprofil jsonObject.data.rivtaprofil
			tjanstekontrakt jsonObject.data.tjanstekontrakt
			logiskadress jsonObject.data.logiskadress
			tjanstekomponent jsonObject.data.tjanstekomponent
			anropsadress jsonObject.data.anropsadress
			anropsbehorighet jsonObject.data.anropsbehorighet.collect{ row ->
				["id": row.id,
				 "integrationsavtal": row.integrationsavtal,
				 "fromTidpunkt": row.fromTidpunkt,
				 "tomTidpunkt": row.tomTidpunkt,
				 "pubversion" : row.pubVersion,
				 "relationships":
						 ["logiskAdress": row.relationships.logiskAdress,
						  "tjanstekonsument": row.relationships.tjanstekomponent,
						  "tjanstekontrakt": row.relationships.tjanstekontrakt]]
			}
			vagval jsonObject.data.vagval
			filter jsonObject.data.filter
			filtercategorization jsonObject.data.filtercategorization
		}
	}
	// let script invocation decide where to write data (for example using UNIX re-direction)
	println JsonOutput.prettyPrint(jsonWriter.toString())
}