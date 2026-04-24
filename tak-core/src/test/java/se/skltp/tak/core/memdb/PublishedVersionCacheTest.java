/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.memdb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublishedVersionCacheTest {
	PublishedVersionCache cache;

	@BeforeEach
	void before() {
		try {
			// read from file, convert it string and initialize cache
			FileInputStream fis = new FileInputStream("./src/test/resources/export.json");
			cache = new PublishedVersionCache(fis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void checkHeaderInfoFromCache()  throws Exception {
		assertEquals(1, cache.getFormatVersion());
		assertEquals(1, cache.getVersion());
		assertEquals(PublishedVersionCache.df.parse("2009-03-10T00:00:00+0100"), cache.getTime());
		assertEquals("Kalle", cache.getUtforare());
		assertEquals("kommentar", cache.getKommentar());
	}
		
	@Test
	void checkRivTaProfilFromCache()  {
		assertEquals(2, cache.rivTaProfil.size());		
		assertEquals("RIVTABP20", cache.rivTaProfil.get(1).getNamn());
		assertEquals("1", cache.rivTaProfil.get(1).getPubVersion());

		assertEquals(1, cache.rivTaProfil.get(1).getAnropsAdresser().size());
	}

	@Test
	void checkTjanstekontrakt()  {
		assertEquals(7, cache.tjanstekontrakt.size());		
		assertEquals("urn:riv:itinfra:tp:PingResponder:1", cache.tjanstekontrakt.get(11).getNamnrymd());
		assertEquals("urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:1", cache.tjanstekontrakt.get(12).getNamnrymd());
		assertEquals(1, cache.tjanstekontrakt.get(13).getMajorVersion());
		assertEquals(0, cache.tjanstekontrakt.get(14).getMinorVersion());	
		assertEquals("1", cache.tjanstekontrakt.get(15).getPubVersion());

		assertEquals(2, cache.tjanstekontrakt.get(11).getAnropsbehorigheter().size());
		assertEquals(1, cache.tjanstekontrakt.get(15).getVagval().size());			
	}
	
	@Test
	void checkLogiskAdress()  {
		assertEquals(5, cache.logiskAdress.size());		
		assertEquals("HSA-VKY567", cache.logiskAdress.get(3).getHsaId());
		assertEquals("VPs egna ping-tjanst", cache.logiskAdress.get(4).getBeskrivning());
		assertEquals("1", cache.logiskAdress.get(5).getPubVersion());
		
		assertEquals(1, cache.logiskAdress.get(2).getAnropsbehorigheter().size());
		assertEquals(1, cache.logiskAdress.get(3).getVagval().size());		
	}
	
	@Test
	void checkTjanstekomponent()  {
		assertEquals(7, cache.tjanstekomponent.size());		
		assertEquals("PING-HSAID", cache.tjanstekomponent.get(3).getHsaId());
		assertEquals("VP-Cachad-GetLogicalAddresseesByServiceContract", cache.tjanstekomponent.get(5).getBeskrivning());
		assertEquals("1", cache.tjanstekomponent.get(7).getPubVersion());
		
		assertEquals(8, cache.tjanstekomponent.get(2).getAnropsbehorigheter().size());
		assertEquals(1, cache.tjanstekomponent.get(7).getAnropsAdresser().size());
	}
	
	@Test
	void checkAnropsadress()  {
		assertEquals(7, cache.anropsAdress.size());		
		assertEquals("http://localhost:10000/test/Ping_Service", cache.anropsAdress.get(2).getAdress());
		assertEquals("1", cache.anropsAdress.get(2).getPubVersion());

		assertEquals(2, cache.anropsAdress.get(1).getRivTaProfil().getId());
		assertEquals(1, cache.anropsAdress.get(1).getTjanstekomponent().getId());
		
		assertEquals(3, cache.anropsAdress.get(1).getVagVal().size());		
	}
	
	@Test
	void checkAnropsbehorighet() throws ParseException  {
		assertEquals(8, cache.anropsbehorighet.size());		
		assertEquals("I1", cache.anropsbehorighet.get(1).getIntegrationsavtal());
		java.util.Date expectedDateFrom = PublishedVersionCache.df.parse("2009-03-09T23:00:00+0000");
		assertEquals(expectedDateFrom.getTime(), cache.anropsbehorighet.get(3).getFromTidpunkt().getTime());
		java.util.Date expectedDateTom = PublishedVersionCache.df.parse("2113-12-23T23:00:00+0000");
		assertEquals(expectedDateTom.getTime(), cache.anropsbehorighet.get(4).getTomTidpunkt().getTime());
		assertEquals("1", cache.anropsbehorighet.get(7).getPubVersion());

		assertEquals(2, cache.anropsbehorighet.get(2).getLogiskAdress().getId());
		assertEquals(2, cache.anropsbehorighet.get(3).getTjanstekonsument().getId());
		assertEquals(11, cache.anropsbehorighet.get(4).getTjanstekontrakt().getId());
		
		assertEquals(2, cache.anropsbehorighet.get(8).getFilter().size());
	}

	@Test
	void checkVagval() throws ParseException  {
		assertEquals(9, cache.vagval.size());		
		java.util.Date expectedDateFrom = PublishedVersionCache.df.parse("2010-12-23T23:00:00+0000");
		assertEquals(expectedDateFrom.getTime(), cache.vagval.get(2).getFromTidpunkt().getTime());
		java.util.Date expectedDateTom = PublishedVersionCache.df.parse("2113-12-23T23:00:00+0000");
		assertEquals(expectedDateTom.getTime(), cache.vagval.get(3).getTomTidpunkt().getTime());
		assertEquals("1", cache.vagval.get(4).getPubVersion());

		assertEquals(7, cache.vagval.get(9).getAnropsAdress().getId());
		assertEquals(4, cache.vagval.get(4).getLogiskAdress().getId());
		assertEquals(15, cache.vagval.get(8).getTjanstekontrakt().getId());		
	}

	@Test
	void checkFilter()  {
		assertEquals(4, cache.filter.size());		
		assertEquals("urn:riv:itintegration:registry:GetItems", cache.filter.get(1).getServicedomain());
		assertEquals(5, cache.filter.get(1).getAnropsbehorighet().getId());
		assertEquals("1", cache.filter.get(1).getPubVersion());
		
		assertEquals(2, cache.filter.get(2).getCategorization().size());	
	}

	@Test
	void checkFilterCategorization()  {
		assertEquals(3, cache.filtercategorization.size());		
		assertEquals("Category c1", cache.filtercategorization.get(1).getCategory());
		assertEquals("1", cache.filtercategorization.get(1).getPubVersion());

		assertEquals(2, cache.filtercategorization.get(1).getFilter().getId());
	}
}
