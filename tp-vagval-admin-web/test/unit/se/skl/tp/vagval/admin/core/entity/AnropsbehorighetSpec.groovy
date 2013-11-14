
package se.skl.tp.vagval.admin.core.entity

import grails.test.mixin.TestFor

import org.joda.time.DateTime

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll;

@TestFor(Anropsbehorighet)
@Mock([Tjanstekomponent, Tjanstekontrakt, LogiskAdressat])
class AnropsbehorighetSpec extends Specification {

	@Shared tjansteKonsument
	@Shared tjansteKontrakt
	@Shared logiskAdressat
	
	def setup() {
		tjansteKontrakt = new Tjanstekontrakt()
		tjansteKontrakt.namnrymd = "http://x.y.z"
		tjansteKontrakt.beskrivning = "Tjänst XYZ"
		tjansteKontrakt.save(flush: true)
		tjansteKonsument = new Tjanstekomponent()
		tjansteKonsument.hsaId = "Komponent 1"
		tjansteKonsument.beskrivning = "Komponent 1"
		tjansteKonsument.save(flush: true)
		logiskAdressat = new LogiskAdressat()
		logiskAdressat.hsaId = "Addressat 1"
		logiskAdressat.beskrivning = "Addressat 1"
		logiskAdressat.save(flush: true)
	}
	
	def "a completely valid Anropsbehorighet"() {
		when:
			def anropsbehorighet = new Anropsbehorighet();
	
			anropsbehorighet.fromTidpunkt = new java.sql.Date(new DateTime().now().millis)
			anropsbehorighet.tomTidpunkt = new java.sql.Date(new DateTime().now().plusYears(1).millis)
			anropsbehorighet.integrationsavtal = "I1"
			anropsbehorighet.tjanstekonsument = tjansteKonsument
			anropsbehorighet.tjanstekontrakt = tjansteKontrakt
			anropsbehorighet.logiskAdressat = logiskAdressat
		then:
			anropsbehorighet.validate()
	}
	
	def "an Anropsbehorighet with a filter"() {
		given:
			def anropsbehorighet = new Anropsbehorighet();
			anropsbehorighet.fromTidpunkt = new java.sql.Date(new DateTime().now().millis)
			anropsbehorighet.tomTidpunkt = new java.sql.Date(new DateTime().now().plusYears(1).millis)
			anropsbehorighet.integrationsavtal = "I1"
			anropsbehorighet.tjanstekonsument = tjansteKonsument
			anropsbehorighet.tjanstekontrakt = tjansteKontrakt
			anropsbehorighet.logiskAdressat = logiskAdressat
			anropsbehorighet.addToFilter(new Filter(servicedomain: 'a_unique_service_domain'))
		when:
			anropsbehorighet.save()	
		then:
			!anropsbehorighet.hasErrors()
		and:
			anropsbehorighet.filter.size() == 1
	}
	
}
