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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;

import se.skltp.tak.core.dao.PubVersionDao;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;

public class TakPublishVersionTest extends AbstractCoreTest {

	@Autowired
	TakPublishVersion takPublishVersion;
	
	@Autowired
	PubVersionDao pubVersionDao;

	@PersistenceContext
	private EntityManager em;

	private String insertedData = "<?xml version='1.0' encoding='UTF-8'?> "
			+ "<dataset>"

			+ "<tjanstekontrakt id='6' namnrymd='QWERTY' version='1' beskrivning='QWERTY kontrakt' majorVersion='1' minorVersion='0' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
			
			+ "</dataset>";

	private String updatedData = "<?xml version='1.0' encoding='UTF-8'?> "
			+ "<dataset>"

			+ "<tjanstekontrakt id='4' namnrymd='AAA' version='1' beskrivning='AAA kontrakt' majorVersion='1' minorVersion='0' pubVersion='1' deleted='TRUE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"
			+ "<tjanstekontrakt id='5' namnrymd='BBB' version='1' beskrivning='Updated kontrakt' majorVersion='1' minorVersion='0' pubVersion='1' deleted='FALSE' updatedBy='Pelle' updatedTime='2015-10-20 12:13:14'/>"

			+ "</dataset>";
	
	public void testGetAllPubVersion() throws Exception {
		List<PubVersion> result = takPublishVersion.getAllPubVersions();
    	assertEquals(1, result.size());
    	
    	// Check if BLOB unpacked equals export.json
    	Blob dataBlob = result.get(0).getData();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1];
		InputStream is = dataBlob.getBinaryStream();
	    while (is.read(buffer) > 0) {
	    	baos.write(buffer);
		}
	 
	    String decompressedData = Util.decompress(baos.toByteArray());
	    String matchingJsonString = new String(readAllBytes(get("./src/test/resources/export.json")));
	    		
	    assertEquals(decompressedData,matchingJsonString);
	}
	
//	public void testTjanstekontraktToPubVersion() throws Exception {
//		// Insert updated data to mimic a user
//		InputStream fsUpdate = new ByteArrayInputStream(updatedData.getBytes());
//	    IDataSet dsUpdate = new FlatXmlDataSet(fsUpdate);
//		InputStream fsInsert = new ByteArrayInputStream(insertedData.getBytes());
//	    IDataSet dsInsert = new FlatXmlDataSet(fsInsert);
//	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
//	    DatabaseOperation.UPDATE.execute(connection, dsUpdate);
//	    DatabaseOperation.INSERT.execute(connection, dsInsert);
//
//	    // Get changed data from DB
//		List<Tjanstekontrakt> tkList = em.createQuery("Select tk from Tjanstekontrakt tk where updatedBy != null").getResultList();
//				
//		// Create a new PubVersion (no data... )
//		PubVersion pv = new PubVersion();
//		pv.setKommentar("new kommentar");
//		pv.setUtforare("Pelle");
//		
//		takPublishVersion.publishANewVersion(pv, tkList);		
//		
//		// Read newly created and updated BLOB and check changed data
//	    PublishedVersionCache newCache = pubVersionDao.getLatestPublishedVersion();
//	    assertEquals("2", newCache.tjanstekontrakt.get(6).getPubVersion()); 
//	    assertEquals("QWERTY", newCache.tjanstekontrakt.get(6).getNamnrymd());
//	    
//	    assertEquals("Updated kontrakt", newCache.tjanstekontrakt.get(5).getBeskrivning());
//	    
//	    // Check deleted values, should be absent in cache
//	    assertEquals(null, newCache.tjanstekontrakt.get(4));
//	    
//		
//	}
	
}
