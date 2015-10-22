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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.skltp.tak.core.dao.PubVersionDao;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;

@Service("takPublishVersion")
public class TakPublishVersionImpl implements TakPublishVersion {

	@Autowired
	PubVersionDao pubversionDao;

	@PersistenceContext
	private EntityManager em;

	private static final long formatVersion = 1L;
	
	@Override
	public List<PubVersion> getAllPubVersions() {
		List<PubVersion> list = pubversionDao.getAllPubVersion();
		return list;
	}

	@Override
	public void publishANewVersion( PubVersion pv, 
									List<Tjanstekontrakt> lTjanstekontrakt) throws Exception {

		// Get a new copy of latest pubVersion from db
		PublishedVersionCache pvCache = pubversionDao.getLatestPublishedVersion(); 
		
		// Write new PubVersion to db, we will add BLOB in later steps as an update
		pv.setFormatVersion(formatVersion);
		pv.setTime(new java.sql.Date(System.currentTimeMillis()));
		em.persist(pv);
		em.flush();
		Long newPVId = pv.getId(); // Used in all changesets below
		
		// Apply changeset for Tjanstekontrakt on new cache
		for (Tjanstekontrakt tk : lTjanstekontrakt) {
			// Determine kind of change
			if (tk.isNewlyCreated() || tk.isModified()) {
				// Modify record
				tk.setPubVersion(Long.toString(newPVId));
				tk.setUpdatedBy(null);
				tk.setUpdatedTime(null);
				pvCache.tjanstekontrakt.put((int) tk.getId(), tk);
			} else if (tk.isDeleted()) {
				pvCache.tjanstekontrakt.remove((int) tk.getId());				
			} else {
				// Should't happen !!!
				continue;
			}
			
			// Save to database
			em.persist(tk);
		}		
		
		// Do the above steps for each entity table
		
		// Create a new BLOB and save to db
		String newCacheJSON = Util.fromPublishedVersionToJSON(pvCache);
		Blob blob = new SerialBlob(Util.compress(newCacheJSON));
	    pv.setData(blob);
	    System.out.print("df");;
	}
}
