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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import javax.sql.rowset.serial.SerialBlob;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.skltp.tak.core.dao.PubVersionDao;
import se.skltp.tak.core.dao.PublishDao;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;

public class TakPublishVersionTest extends AbstractCoreTest {

	@Autowired
	TakPublishVersion takPublishVersion;
	
	@Autowired
	PubVersionDao pubVersionDao;

	@Autowired
	PublishDao publishDao;

	@PersistenceContext
	private EntityManager em;

	@Test
	// Problem to make String diffs from File and db ...
	public void testJSONFromDB() throws Exception {
		// Create JSON string from DB entities
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		takPublishVersion.getJSONFromDb(baos);
		String jsonFromCache = new String(readAllBytes(get("./src/test/resources/export.json")), "utf-8");
		String jsonFromDBEntities = baos.toString(StandardCharsets.UTF_8);
		assertTrue(compareJson(jsonFromDBEntities, jsonFromCache));
	}

	@Test
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		Util.fromPublishedVersionToJSON(newPVFromDataRows, gzos);
		Blob blob = new SerialBlob(baos.toByteArray());
		pubVersion.setData(blob);
		pubVersion.setStorlek(2);
		
		em.persist(pubVersion);
		em.flush();
		
		// Read gzipped JSON as PV from DB
		baos.reset();
		takPublishVersion.getJSONFromDb(baos);
		PublishedVersionCache readFromPBDB = pubVersionDao.getLatestPublishedVersionCache();
		
		// Check values!
		assertEquals("Comment", readFromPBDB.getKommentar());
	}
}
