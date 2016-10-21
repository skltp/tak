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
])

import groovy.sql.Sql
import groovy.json.*
import java.util.zip.GZIPInputStream

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
		anropsbehorighet jsonObject.data.anropsbehorighet
		vagval jsonObject.data.vagval
		filter jsonObject.data.filter
		filtercategorization jsonObject.data.filtercategorization
	}
}

// let script invocation decide where to write data (for example using UNIX re-direction)
println JsonOutput.prettyPrint(jsonWriter.toString())
