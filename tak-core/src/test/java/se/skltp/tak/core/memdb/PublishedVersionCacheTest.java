/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
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
package se.skltp.tak.core.memdb;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

public class PublishedVersionCacheTest {
	PublishedVersionCache cache;

	@Before
	public void before() {
		try {
			// read from file, convert it string and initialize cache
			cache = new PublishedVersionCache(new String(readAllBytes(get("./src/test/resources/export.json"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void checkHeaderInfoFromCache()  throws Exception {
		assertEquals(1, cache.getFormatVersion());
		assertEquals(1, cache.getVersion());
		assertEquals(PublishedVersionCache.df.parse("2009-03-10T00:00:00+0100"), cache.getTime());
		assertEquals("Kalle", cache.getUtforare());
		assertEquals("kommentar", cache.getKommentar());
	}
		
	@Test
	public void checkRivTaProfilFromCache()  {
		assertEquals(4, cache.rivTaProfil.size());		
		assertEquals("rivtabp20", cache.rivTaProfil.get(4).getNamn());
		assertEquals("1", cache.rivTaProfil.get(4).getPubVersion());

		assertEquals(1, cache.rivTaProfil.get(4).getAnropsAdresser().size());
	}

	@Test
	public void checkTjanstekontrakt()  {
		assertEquals(6, cache.tjanstekontrakt.size());		
		assertEquals("AAA", cache.tjanstekontrakt.get(4).getNamnrymd());
		assertEquals("ZZZ kontrakt", cache.tjanstekontrakt.get(3).getBeskrivning());
		assertEquals(1, cache.tjanstekontrakt.get(2).getMajorVersion());
		assertEquals(0, cache.tjanstekontrakt.get(1).getMinorVersion());	
		assertEquals("1", cache.tjanstekontrakt.get(1).getPubVersion());

		assertEquals(1, cache.tjanstekontrakt.get(4).getAnropsbehorigheter().size());
		assertEquals(2, cache.tjanstekontrakt.get(1).getVagval().size());			
	}
	
	@Test
	public void checkLogiskAdress()  {
		assertEquals(3, cache.logiskAdress.size());		
		assertEquals("1", cache.logiskAdress.get(3).getHsaId());
		assertEquals("Logisk adress HSAID TEST", cache.logiskAdress.get(22).getBeskrivning());
		assertEquals("1", cache.logiskAdress.get(22).getPubVersion());
		
		assertEquals(6, cache.logiskAdress.get(22).getAnropsbehorigheter().size());
		assertEquals(3, cache.logiskAdress.get(22).getVagval().size());		
	}
	
	@Test
	public void checkTjanstekomponent()  {
		assertEquals(8, cache.tjanstekomponent.size());		
		assertEquals("hsa3", cache.tjanstekomponent.get(3).getHsaId());
		assertEquals("Tjanstekomponent HSAID 7", cache.tjanstekomponent.get(7).getBeskrivning());
		assertEquals("1", cache.tjanstekomponent.get(7).getPubVersion());
		
		assertEquals(1, cache.tjanstekomponent.get(2).getAnropsbehorigheter().size());
		assertEquals(1, cache.tjanstekomponent.get(7).getAnropsAdresser().size());
	}
	
	@Test
	public void checkAnropsadress()  {
		assertEquals(3, cache.anropsAdress.size());		
		assertEquals("http://path/to/some/address/rivtabp21", cache.anropsAdress.get(2).getAdress());
		assertEquals("1", cache.anropsAdress.get(2).getPubVersion());

		assertEquals(4, cache.anropsAdress.get(1).getRivTaProfil().getId());
		assertEquals(7, cache.anropsAdress.get(1).getTjanstekomponent().getId());
		
		assertEquals(3, cache.anropsAdress.get(1).getVagVal().size());		
	}
	
	@Test
	public void checkAnropsbehorighet() throws ParseException  {
		assertEquals(7, cache.anropsbehorighet.size());		
		assertEquals("integrationsavtal_24", cache.anropsbehorighet.get(24).getIntegrationsavtal());
		java.util.Date expectedDateFrom = PublishedVersionCache.df.parse("2009-03-09T23:00:00+0000");
		assertEquals(expectedDateFrom.getTime(), cache.anropsbehorighet.get(22).getFromTidpunkt().getTime());
		java.util.Date expectedDateTom = PublishedVersionCache.df.parse("2010-12-23T23:00:00+0000");
		assertEquals(expectedDateTom.getTime(), cache.anropsbehorighet.get(22).getTomTidpunkt().getTime());
		assertEquals("1", cache.anropsbehorighet.get(22).getPubVersion());

		assertEquals(22, cache.anropsbehorighet.get(22).getLogiskAdress().getId());
		assertEquals(3, cache.anropsbehorighet.get(22).getTjanstekonsument().getId());
		assertEquals(1, cache.anropsbehorighet.get(22).getTjanstekontrakt().getId());
		
		assertEquals(2, cache.anropsbehorighet.get(25).getFilter().size());
	}

	@Test
	public void checkVagval() throws ParseException  {
		assertEquals(4, cache.vagval.size());		
		java.util.Date expectedDateFrom = PublishedVersionCache.df.parse("2010-12-23T23:00:00+0000");
		assertEquals(expectedDateFrom.getTime(), cache.vagval.get(12).getFromTidpunkt().getTime());
		java.util.Date expectedDateTom = PublishedVersionCache.df.parse("2011-12-23T23:00:00+0000");
		assertEquals(expectedDateTom.getTime(), cache.vagval.get(12).getTomTidpunkt().getTime());
		assertEquals("1", cache.vagval.get(12).getPubVersion());

		assertEquals(1, cache.vagval.get(12).getAnropsAdress().getId());
		assertEquals(22, cache.vagval.get(12).getLogiskAdress().getId());
		assertEquals(1, cache.vagval.get(12).getTjanstekontrakt().getId());		
	}

	@Test
	public void checkFilter() throws ParseException  {
		assertEquals(5, cache.filter.size());		
		assertEquals("Scheduling", cache.filter.get(2).getServicedomain());
		assertEquals(25, cache.filter.get(3).getAnropsbehorighet().getId());
		assertEquals("1", cache.filter.get(3).getPubVersion());
		
		assertEquals(2, cache.filter.get(3).getCategorization().size());	
	}

	@Test
	public void checkFilterCategorization() throws ParseException  {
		assertEquals(7, cache.filtercategorization.size());		
		assertEquals("Invitation", cache.filtercategorization.get(4).getCategory());
		assertEquals("1", cache.filtercategorization.get(4).getPubVersion());

		assertEquals(3, cache.filtercategorization.get(5).getFilter().getId());
	}
}
