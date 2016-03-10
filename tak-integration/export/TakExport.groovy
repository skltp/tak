/**
 * Export TAK data in the "TAK-published-version" format.
 * Ref: https://skl-tp.atlassian.net/wiki/display/NTJP/TAK+versionshantering
 *
 * Note:
 *  2016-03-09 HD: transitional implementation that queries the underlying
 *      TAK-tables, instead of just grabbing the published version JSON-file
 *      from table PubVersion.
 *      Transitional since not all TAK-instances are upgraded to v2.x that has
 *      the published version feature.
 *      Ref: https://skl-tp.atlassian.net/browse/SKLTP-815
 *
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

//Streaming
def jsonWriter = new StringWriter()
def jsonBuilder = new StreamingJsonBuilder(jsonWriter)


jsonBuilder {
    formatVersion "1"
    version "0"
    tidpunkt new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC"))
    utforare "TakExport script"
    kommentar "Transitional script, not using TAK published version, see SKLTP-815"
    data {
        // Note: pubversion set to "0" for all records since the column does
        // not yet exist in all TAK-databases

        rivtaprofil db.rows('select * from RivTaProfil').collect{ row ->
            ["id": row.id,
             "namn": row.namn,
             "beskrivning": row.beskrivning,
             "pubversion" : "0"]
        }

        tjanstekontrakt db.rows('select * from Tjanstekontrakt').collect{ row ->
            ["id": row.id,
             "namnrymd": row.namnrymd,
             "beskrivning": row.beskrivning,
             "majorVersion": row.majorVersion,
             "minorVersion": row.minorVersion,
             "pubversion" : "0"]
        }

        logiskadress db.rows('select * from LogiskAdress').collect{ row ->
            ["id": row.id,
             "hsaId": row.hsaid,
             "beskrivning": row.beskrivning,
             "pubversion" : "0"]
        }

        tjanstekomponent db.rows('select * from Tjanstekomponent').collect{ row ->
            ["id": row.id,
             "hsaId": row.hsaId,
             "beskrivning": row.beskrivning,
             "pubversion" : "0"]
        }

        anropsadress db.rows('select * from AnropsAdress').collect{ row ->
            ["id": row.id,
             "adress": row.adress,
             "pubversion" : "0",
             "relationships":
                ["rivtaprofil": row.rivTaProfil_id,
                 "tjanstekomponent": row.tjanstekomponent_id]]
        }

        anropsbehorighet db.rows('select * from Anropsbehorighet').collect{ row ->
            ["id": row.id,
             "integrationsavtal": row.integrationsavtal,
             "fromTidpunkt": row.fromTidpunkt,
             "tomTidpunkt": row.tomTidpunkt,
             "pubversion" : "0",
             "relationships":
                ["logiskAdress": row.logiskAdress_id,
                 "tjanstekonsument": row.tjanstekonsument_id,
                 "tjanstekontrakt": row.tjanstekontrakt_id]]
        }

        vagval db.rows('select * from Vagval').collect{ row ->
            ["id": row.id,
             "fromTidpunkt": row.fromTidpunkt,
             "tomTidpunkt": row.tomTidpunkt,
             "pubversion" : "0",
             "relationships":
                ["anropsadress": row.anropsAdress_id,
                 "logiskadress": row.logiskAdress_id,
                 "tjanstekontrakt": row.tjanstekontrakt_id]]
        }

        filter db.rows('select * from Filter').collect{ row ->
            ["id": row.id,
             "servicedomain": row.servicedomain,
             "pubversion" : "0",
             "relationships":
                ["anropsbehorighet": row.anropsbehorighet_id]]
        }

        filtercategorization db.rows('select * from Filtercategorization').collect{ row ->
            ["id": row.id,
             "category": row.category,
             "pubversion" : "0",
             "relationships":
                ["filter": row.filter_id]]
        }
    }
}


// let script invocation decide where to write data (for example using UNIX re-direction)
println JsonOutput.prettyPrint(jsonWriter.toString())
