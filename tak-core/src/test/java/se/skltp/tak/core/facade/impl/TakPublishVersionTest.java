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
package se.skltp.tak.core.facade.impl;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.sql.Blob;
import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;

import se.skltp.tak.core.dao.PubVersionDao;
import se.skltp.tak.core.dao.PublishDao;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;

//Test logic should be moved to tak-web!

public class TakPublishVersionTest extends AbstractCoreTest {

	@Autowired
	TakPublishVersion takPublishVersion;
	
	@Autowired
	PubVersionDao pubVersionDao;

	@Autowired
	PublishDao publishDao;

	@PersistenceContext
	private EntityManager em;

	// Problem to make String diffs from File and db ...
	public void testJSONFromDB() throws Exception {
		// Create JSON string from DB entities
		String jsonFromDBEntities = takPublishVersion.getJSONFromDb();
		String jsonFromCache = new String(readAllBytes(get("./src/test/resources/export.json")), "utf-8");
		assertTrue(compareJson(jsonFromDBEntities, jsonFromCache));
	}
	
	public void testPublishAndReadFromDB() throws Exception {
		// Read DB and create a PV
		PubVersion pubVersion = new PubVersion();
		pubVersion.setFormatVersion(1L);
		pubVersion.setKommentar("Comment");
		pubVersion.setTime(new java.sql.Date(System.currentTimeMillis()));
		pubVersion.setUtforare("test");

		PublishedVersionCache newPVFromDataRows = Util.getPublishedVersionCache( pubVersion,
				publishDao.getRivTaProfil(), 
				publishDao.getTjanstekontrakt(),
				publishDao.getTjanstekomponent(),
				publishDao.getLogiskAdress(),
				publishDao.getAnropsadress(),
				publishDao.getVagval(),
				publishDao.getAnropsbehorighet(),
				publishDao.getFilter(),
				publishDao.getFiltercategorization() );

		
		// Save a new PV as gzipped JSON to DB
		String newPVFromDataRowsJSON = Util.fromPublishedVersionToJSON(newPVFromDataRows);
		Blob blob = new SerialBlob(Util.compress(newPVFromDataRowsJSON));
		pubVersion.setData(blob);
		pubVersion.setStorlek(2);
		
		em.persist(pubVersion);
		em.flush();
		
		// Read gzipped JSON as PV from DB
		String jsonFromDBPV = takPublishVersion.getJSONFromDb();
		PublishedVersionCache readFromPBDB = pubVersionDao.getLatestPublishedVersionCache();
		
		// Check values!
		assertEquals("Comment", readFromPBDB.getKommentar());
	}
	
	
	
	
//	private String insertAnropsAdressData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<anropsAdress id='3' adress='http://path/from/some/address/rivtabp20' version='1' rivTaProfil_id='4' tjanstekomponent_id='7' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String updateAnropsAdressData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<anropsAdress id='1' adress='http://path/to/another/address/rivtabp20' version='1' rivTaProfil_id='4' tjanstekomponent_id='7' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<anropsAdress id='2' adress='http://path/to/some/address/rivtabp21' version='1' rivTaProfil_id='5' tjanstekomponent_id='8' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String insertFiltercategorizationData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<filtercategorization id='7' category='Testing' filter_id='3' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String updateFiltercategorizationData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<filtercategorization id='1' category='BookingChanged' filter_id='1' version='1' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<filtercategorization id='2' category='Invitation' filter_id='1' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String insertFilterData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<filter id='5' servicedomain='Servicedomain_NEW' anropsbehorighet_id='25' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String updateFilterData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<filter id='3' servicedomain='Servicedomain_CHANGED' anropsbehorighet_id='25' version='1' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<filter id='4' servicedomain='Scheduling' anropsbehorighet_id='26' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String insertAnropsbehorighetData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<anropsBehorighet id='27' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' integrationsavtal='integrationsavtal_27' tjanstekonsument_id='2' logiskadress_id='22' tjanstekontrakt_id='1' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String updateAnropsbehorighetData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<anropsBehorighet id='21' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' integrationsavtal='integrationsavtal_21_Updated' tjanstekonsument_id='2' logiskadress_id='22' tjanstekontrakt_id='1' version='1' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<anropsBehorighet id='22' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' integrationsavtal='integrationsavtal_22' tjanstekonsument_id='3' logiskadress_id='22' tjanstekontrakt_id='1' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String insertTjanstekontraktData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<tjanstekontrakt id='6' namnrymd='QWERTY' version='1' beskrivning='QWERTY kontrakt' majorVersion='1' minorVersion='0' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String updateTjanstekontraktData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<tjanstekontrakt id='4' namnrymd='AAA Updated' version='1' beskrivning='AAA kontrakt' majorVersion='1' minorVersion='0' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<tjanstekontrakt id='5' namnrymd='BBB' version='1' beskrivning='BBB kontrakt' majorVersion='1' minorVersion='0' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String insertVagvalData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<vagval id='14' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' logiskadress_id='22' tjanstekontrakt_id='3' anropsAdress_id='1' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String updateVagvalData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<vagval id='12' fromTidpunkt='2010-12-24' tomTidpunkt='2011-12-24' logiskadress_id='22' tjanstekontrakt_id='4' anropsAdress_id='1' version='1' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"			
//			+ "<vagval id='13' fromTidpunkt='2010-12-24' tomTidpunkt='2011-12-24' logiskadress_id='22' tjanstekontrakt_id='2' anropsAdress_id='1' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String insertTjanstekomponentData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<tjanstekomponent id='9' hsaId='hsa9' beskrivning='Tjanstekomponent HSAID 9' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String updateTjanstekomponentData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<tjanstekomponent id='3' hsaId='hsa3' beskrivning='Tjanstekomponent HSAID 3 Updated' version='1' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<tjanstekomponent id='5' hsaId='hsa4' beskrivning='Tjanstekomponent HSAID 5' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String insertLogiskAdressData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<logiskadress id='10' hsaId='1' version='1' beskrivning='Logisk adress HSAID 10' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"		
//			+ "</dataset>";
//
//	private String updateLogiskAdressData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<logiskadress id='22' hsaId='test-hsa' version='1' beskrivning='Logisk adress HSAID TEST Updated' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<logiskadress id='3' hsaId='1' version='1' beskrivning='Logisk adress HSAID 1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"		
//			+ "</dataset>";
//
//	private String insertRivTaProfilData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<rivTaProfil id='6' namn='rivtabp30' version='1' beskrivning='RIV TA Profil 3.0' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String updateRivTaProfilData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<rivTaProfil id='4' namn='rivtabp20' version='1' beskrivning='RIV TA Profil 2.0 Updated' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<rivTaProfil id='3' namn='rivtabp10' version='1' beskrivning='RIV TA Profil 1.0' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//
//	private String complexInsertData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<rivTaProfil id='100' namn='rivtabp100' version='1' beskrivning='RIV TA Profil 100.0' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<tjanstekontrakt id='101' namnrymd='special:new' version='1' beskrivning='special new kontrakt' majorVersion='1' minorVersion='0' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<logiskadress id='102' hsaId='100' version='1' beskrivning='Logisk adress HSAID 102' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"		
//			+ "<tjanstekomponent id='103' hsaId='hsa-103' beskrivning='Tjanstekonsument HSA-103' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<tjanstekomponent id='104' hsaId='hsa-104' beskrivning='Tjansteproducent HSA-104' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<anropsAdress id='105' adress='http://path/from/some/address/rivtabp20' version='1' rivTaProfil_id='100' tjanstekomponent_id='104' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<vagval id='106' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' logiskadress_id='102' tjanstekontrakt_id='101' anropsAdress_id='105' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<anropsBehorighet id='107' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' integrationsavtal='integrationsavtal_107' tjanstekonsument_id='103' logiskadress_id='102' tjanstekontrakt_id='101' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"			
//			+ "<filter id='108' servicedomain='Servicedomain_special' anropsbehorighet_id='107' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<filtercategorization id='109' category='Testing 109' filter_id='108' version='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";
//	
//	private String complexDeleteData = "<?xml version='1.0' encoding='UTF-8'?> "
//			+ "<dataset>"
//			+ "<rivTaProfil id='50' namn='rivtabp50' beskrivning='RIV TA Profil 50.0' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<tjanstekontrakt id='51' namnrymd='special:new' beskrivning='special new kontrakt' majorVersion='1' minorVersion='0' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<logiskadress id='52' hsaId='52' beskrivning='Logisk adress HSAID 52' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"		
//			+ "<tjanstekomponent id='53' hsaId='hsa-53' beskrivning='Tjanstekonsument HSA-53' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<tjanstekomponent id='54' hsaId='hsa-54' beskrivning='Tjansteproducent HSA-54' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<anropsAdress id='55' adress='http://path/from/some/address/rivtabp20' rivTaProfil_id='50' tjanstekomponent_id='54' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<vagval id='56' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' logiskadress_id='52' tjanstekontrakt_id='51' anropsAdress_id='55' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<anropsBehorighet id='57' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' integrationsavtal='integrationsavtal_57' tjanstekonsument_id='53' logiskadress_id='52' tjanstekontrakt_id='51' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<filter id='58' servicedomain='Servicedomain_special' anropsbehorighet_id='57' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "<filtercategorization id='59' category='Testing 59' filter_id='58' version='1' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
//			+ "</dataset>";

	
	
//	public void testAnropsAdressToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(updateAnropsAdressData.getBytes()));
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(insertAnropsAdressData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted value
//	    assertNotNull(newCache.anropsAdress.get(3)); 
//	    assertEquals("http://path/from/some/address/rivtabp20", newCache.anropsAdress.get(3).getAdress());	    
//	    
//	    // Check updated values
//	    assertEquals("http://path/to/another/address/rivtabp20", newCache.anropsAdress.get(1).getAdress());	    
//	    
//	    // Check deleted values, should be absent in cache
//	    assertNull(newCache.anropsAdress.get(2));
//	}
//
//	public void testFiltercategorizationToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(updateFiltercategorizationData.getBytes()));
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(insertFiltercategorizationData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted value
//	    assertNotNull(newCache.filtercategorization.get(7)); 
//	    assertEquals("Testing", newCache.filtercategorization.get(7).getCategory());	    
//	    
//	    // Check updated values
//	    assertEquals("BookingChanged", newCache.filtercategorization.get(1).getCategory());	    
//	    
//	    // Check deleted values, should be absent in cache
//	    assertNull(newCache.filtercategorization.get(2));
//	}
//
//	public void testFilterToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(updateFilterData.getBytes()));
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(insertFilterData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted value
//	    assertNotNull(newCache.filter.get(5)); 
//	    assertEquals("Servicedomain_NEW", newCache.filter.get(5).getServicedomain());	    
//	    
//	    // Check updated values
//	    assertEquals("Servicedomain_CHANGED", newCache.filter.get(3).getServicedomain());	    
//	    
//	    // Check deleted values, should be absent in cache
//	    assertNull(newCache.filter.get(4));
//	}
//
//	
//	public void testTjanstekontraktToPubVersion() throws Exception {
//		// Insert updated data to mimic a users change
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(updateTjanstekontraktData.getBytes()));
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(insertTjanstekontraktData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted value
//	    assertNotNull(newCache.tjanstekontrakt.get(6)); 
//	    assertEquals("QWERTY", newCache.tjanstekontrakt.get(6).getNamnrymd());	 
//	    
//	    // Check updated values
//	    assertEquals("AAA Updated", newCache.tjanstekontrakt.get(4).getNamnrymd());
//	    
//	    // Check deleted values, should be absent in cache
//	    assertNull(newCache.tjanstekontrakt.get(5));
//	}
//
//	public void testVagvalToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(updateVagvalData.getBytes()));
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(insertVagvalData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted value
//	    assertNotNull(newCache.vagval.get(14)); 
//	    assertEquals("ZZZ", newCache.vagval.get(14).getTjanstekontrakt().getNamnrymd());	    
//	    
//	    // Check updated values
//	    assertEquals("AAA", newCache.vagval.get(12).getTjanstekontrakt().getNamnrymd());	    
//	    
//	    // Check deleted values, should be absent in cache
//	    assertNull(newCache.vagval.get(13));
//	}
//	
//	public void testAnropsbehorighetToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(updateAnropsbehorighetData.getBytes()));
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(insertAnropsbehorighetData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted value
//	    assertNotNull(newCache.anropsbehorighet.get(27)); 
//	    assertEquals("integrationsavtal_27", newCache.anropsbehorighet.get(27).getIntegrationsavtal());	    
//	    
//	    // Check updated values
//	    assertEquals("integrationsavtal_21_Updated", newCache.anropsbehorighet.get(21).getIntegrationsavtal());	    
//	    
//	    // Check deleted values, should be absent in cache
//	    assertNull(newCache.anropsbehorighet.get(22));
//	}
//
//	public void testTjanstekomponentToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(updateTjanstekomponentData.getBytes()));
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(insertTjanstekomponentData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted value
//	    assertNotNull(newCache.tjanstekomponent.get(9)); 
//	    assertEquals("Tjanstekomponent HSAID 9", newCache.tjanstekomponent.get(9).getBeskrivning());	    
//	    
//	    // Check updated values
//	    assertEquals("Tjanstekomponent HSAID 3 Updated", newCache.tjanstekomponent.get(3).getBeskrivning());	    
//	    
//	    // Check deleted values, should be absent in cache
//	    assertNull(newCache.tjanstekomponent.get(5));
//	}
//
//	public void testLogiskAdressToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(updateLogiskAdressData.getBytes()));
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(insertLogiskAdressData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted value
//	    assertNotNull(newCache.logiskAdress.get(10)); 
//	    assertEquals("Logisk adress HSAID 10", newCache.logiskAdress.get(10).getBeskrivning());	    
//	    
//	    // Check updated values
//	    assertEquals("Logisk adress HSAID TEST Updated", newCache.logiskAdress.get(22).getBeskrivning());	    
//	    
//	    // Check deleted values, should be absent in cache
//	    assertNull(newCache.logiskAdress.get(3));
//	}
//
//	public void testRivTaProfilToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(updateRivTaProfilData.getBytes()));
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(insertRivTaProfilData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted value
//	    assertNotNull(newCache.rivTaProfil.get(6)); 
//	    assertEquals("RIV TA Profil 3.0", newCache.rivTaProfil.get(6).getBeskrivning());	    
//	    
//	    // Check updated values
//	    assertEquals("RIV TA Profil 2.0 Updated", newCache.rivTaProfil.get(4).getBeskrivning());	    
//	    
//	    // Check deleted values, should be absent in cache
//	    assertNull(newCache.rivTaProfil.get(3));
//	}
//
//	public void testComplexInsertToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsInsert = new FlatXmlDataSet(new ByteArrayInputStream(complexInsertData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check inserted values
//	    assertNotNull(newCache.rivTaProfil.get(100)); 
//	    assertEquals("RIV TA Profil 100.0", newCache.rivTaProfil.get(100).getBeskrivning());
//	    
//	    assertNotNull(newCache.tjanstekontrakt.get(101)); 
//	    assertEquals("special new kontrakt", newCache.tjanstekontrakt.get(101).getBeskrivning());
//
//	    assertNotNull(newCache.logiskAdress.get(102)); 
//	    assertEquals("Logisk adress HSAID 102", newCache.logiskAdress.get(102).getBeskrivning());
//	    
//	    assertNotNull(newCache.tjanstekomponent.get(103)); 
//	    assertEquals("Tjanstekonsument HSA-103", newCache.tjanstekomponent.get(103).getBeskrivning());
//
//	    assertNotNull(newCache.tjanstekomponent.get(104)); 
//	    assertEquals("Tjansteproducent HSA-104", newCache.tjanstekomponent.get(104).getBeskrivning());
//
//	    assertNotNull(newCache.anropsAdress.get(105)); 
//	    assertEquals("http://path/from/some/address/rivtabp20", newCache.anropsAdress.get(105).getAdress());
//	    assertEquals(100, newCache.anropsAdress.get(105).getRivTaProfil().getId());
//	    assertEquals(104, newCache.anropsAdress.get(105).getTjanstekomponent().getId());
//	    
//	    assertNotNull(newCache.vagval.get(106)); 
//	    assertEquals(102, newCache.vagval.get(106).getLogiskAdress().getId());
//	    assertEquals(101, newCache.vagval.get(106).getTjanstekontrakt().getId());
//	    assertEquals(105, newCache.vagval.get(106).getAnropsAdress().getId());
//
//	    assertNotNull(newCache.anropsbehorighet.get(107)); 
//	    assertEquals(103, newCache.anropsbehorighet.get(107).getTjanstekonsument().getId());
//	    assertEquals(102, newCache.anropsbehorighet.get(107).getLogiskAdress().getId());
//	    assertEquals(101, newCache.anropsbehorighet.get(107).getTjanstekontrakt().getId());
//
//	    assertNotNull(newCache.filter.get(108)); 
//	    assertEquals("Servicedomain_special", newCache.filter.get(108).getServicedomain());
//	    assertEquals(107, newCache.filter.get(108).getAnropsbehorighet().getId());
//
//	    assertNotNull(newCache.filtercategorization.get(109)); 
//	    assertEquals("Testing 109", newCache.filtercategorization.get(109).getCategory());
//	    assertEquals(108, newCache.filtercategorization.get(109).getFilter().getId());
//	}	
//	
//	public void testComplexDeleteToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//	    IDataSet dsUpdate = new FlatXmlDataSet(new ByteArrayInputStream(complexDeleteData.getBytes()));
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//
//	    // Check deleted values
//	    assertNull(newCache.rivTaProfil.get(50));
//	    assertNull(newCache.tjanstekontrakt.get(51));
//	    assertNull(newCache.logiskAdress.get(52));
//	    assertNull(newCache.tjanstekomponent.get(53));
//	    assertNull(newCache.tjanstekomponent.get(54));
//	    assertNull(newCache.anropsAdress.get(55));
//	    assertNull(newCache.vagval.get(56));
//	    assertNull(newCache.anropsbehorighet.get(57));
//	    assertNull(newCache.filter.get(58));
//	    assertNull(newCache.filtercategorization.get(59));
//	   }	
}
