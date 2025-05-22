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

import java.io.FileInputStream;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

public class PublishedVersionCacheTest {
	PublishedVersionCache cache;

	@Before
	public void before() {
		try {
			// read from file, convert it string and initialize cache
			FileInputStream fis = new FileInputStream("./src/test/resources/export.json");
			cache = new PublishedVersionCache(fis);
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
		assertEquals(2, cache.rivTaProfil.size());		
		assertEquals("RIVTABP20", cache.rivTaProfil.get(1).getNamn());
		assertEquals("1", cache.rivTaProfil.get(1).getPubVersion());

		assertEquals(1, cache.rivTaProfil.get(1).getAnropsAdresser().size());
	}

	@Test
	public void checkTjanstekontrakt()  {
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
	public void checkLogiskAdress()  {
		assertEquals(5, cache.logiskAdress.size());		
		assertEquals("HSA-VKY567", cache.logiskAdress.get(3).getHsaId());
		assertEquals("VPs egna ping-tjanst", cache.logiskAdress.get(4).getBeskrivning());
		assertEquals("1", cache.logiskAdress.get(5).getPubVersion());
		
		assertEquals(1, cache.logiskAdress.get(2).getAnropsbehorigheter().size());
		assertEquals(1, cache.logiskAdress.get(3).getVagval().size());		
	}
	
	@Test
	public void checkTjanstekomponent()  {
		assertEquals(7, cache.tjanstekomponent.size());		
		assertEquals("PING-HSAID", cache.tjanstekomponent.get(3).getHsaId());
		assertEquals("VP-Cachad-GetLogicalAddresseesByServiceContract", cache.tjanstekomponent.get(5).getBeskrivning());
		assertEquals("1", cache.tjanstekomponent.get(7).getPubVersion());
		
		assertEquals(8, cache.tjanstekomponent.get(2).getAnropsbehorigheter().size());
		assertEquals(1, cache.tjanstekomponent.get(7).getAnropsAdresser().size());
	}
	
	@Test
	public void checkAnropsadress()  {
		assertEquals(7, cache.anropsAdress.size());		
		assertEquals("http://localhost:10000/test/Ping_Service", cache.anropsAdress.get(2).getAdress());
		assertEquals("1", cache.anropsAdress.get(2).getPubVersion());

		assertEquals(2, cache.anropsAdress.get(1).getRivTaProfil().getId());
		assertEquals(1, cache.anropsAdress.get(1).getTjanstekomponent().getId());
		
		assertEquals(3, cache.anropsAdress.get(1).getVagVal().size());		
	}
	
	@Test
	public void checkAnropsbehorighet() throws ParseException  {
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
	public void checkVagval() throws ParseException  {
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
	public void checkFilter() throws ParseException  {
		assertEquals(4, cache.filter.size());		
		assertEquals("urn:riv:itintegration:registry:GetItems", cache.filter.get(1).getServicedomain());
		assertEquals(5, cache.filter.get(1).getAnropsbehorighet().getId());
		assertEquals("1", cache.filter.get(1).getPubVersion());
		
		assertEquals(2, cache.filter.get(2).getCategorization().size());	
	}

	@Test
	public void checkFilterCategorization() throws ParseException  {
		assertEquals(3, cache.filtercategorization.size());		
		assertEquals("Category c1", cache.filtercategorization.get(1).getCategory());
		assertEquals("1", cache.filtercategorization.get(1).getPubVersion());

		assertEquals(2, cache.filtercategorization.get(1).getFilter().getId());
	}
}
