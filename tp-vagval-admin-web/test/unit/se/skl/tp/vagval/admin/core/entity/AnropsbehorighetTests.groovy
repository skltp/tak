package se.skl.tp.vagval.admin.core.entity

import grails.test.mixin.*

import org.joda.time.DateTime
import se.skl.tp.vagval.admin.core.entity.Anropsbehorighet
import se.skl.tp.vagval.admin.core.entity.Tjanstekomponent
import se.skl.tp.vagval.admin.core.entity.Tjanstekontrakt
import se.skl.tp.vagval.admin.core.entity.LogiskAdressat
@TestFor(Anropsbehorighet)
@Mock([Tjanstekomponent, Tjanstekontrakt, LogiskAdressat])
class AnropsbehorighetTests {

	private tjansteKonsument
	private tjansteKontrakt
	private logiskAdressat

	@Before
	def void setup() {
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

	@Test
	def  void "a completely valid Anropsbehorighet"() {
		def anropsbehorighet = new Anropsbehorighet();

		anropsbehorighet.fromTidpunkt = new java.sql.Date(new DateTime().now().millis)
		anropsbehorighet.tomTidpunkt = new java.sql.Date(new DateTime().now().plusYears(1).millis)
		anropsbehorighet.integrationsavtal = "I1"
		anropsbehorighet.tjanstekonsument = tjansteKonsument
		anropsbehorighet.tjanstekontrakt = tjansteKontrakt
		anropsbehorighet.logiskAdressat = logiskAdressat
		assert anropsbehorighet.validate()
	}

	@Test
	def void "an Anropsbehorighet with a filter"() {
		def anropsbehorighet = new Anropsbehorighet();
		anropsbehorighet.fromTidpunkt = new java.sql.Date(new DateTime().now().millis)
		anropsbehorighet.tomTidpunkt = new java.sql.Date(new DateTime().now().plusYears(1).millis)
		anropsbehorighet.integrationsavtal = "I1"
		anropsbehorighet.tjanstekonsument = tjansteKonsument
		anropsbehorighet.tjanstekontrakt = tjansteKontrakt
		anropsbehorighet.logiskAdressat = logiskAdressat
		anropsbehorighet.addToFilter(new Filter(servicedomain: 'a_unique_service_domain'))

		anropsbehorighet.save()

		assert !anropsbehorighet.hasErrors()
		assert anropsbehorighet.filter.size() == 1
	}
}
