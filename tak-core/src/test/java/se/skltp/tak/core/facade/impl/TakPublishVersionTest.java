/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.facade.impl;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import javax.sql.rowset.serial.SerialBlob;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.skltp.tak.core.dao.PubVersionDao;
import se.skltp.tak.core.dao.PublishDao;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;

class TakPublishVersionTest extends AbstractCoreTest {

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
	void testJSONFromDB() throws Exception {
		// Create JSON string from DB entities
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		takPublishVersion.getJSONFromDb(baos);
		String jsonFromCache = new String(readAllBytes(get("./src/test/resources/export.json")), "utf-8");
		String jsonFromDBEntities = baos.toString(StandardCharsets.UTF_8);
		assertTrue(compareJson(jsonFromDBEntities, jsonFromCache));
	}

	@Test
	void testPublishAndReadFromDB() throws Exception {
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
