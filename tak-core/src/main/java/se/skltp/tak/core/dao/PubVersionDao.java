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
package se.skltp.tak.core.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;

@Service()
public class PubVersionDao {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<PubVersion> getAllPubVersion() {
		List<PubVersion> list = em.createQuery("Select p from PubVersion p").getResultList();
		return list;
	}
		
	@SuppressWarnings("unchecked")
	public PublishedVersionCache getLatestPublishedVersion() {
		List<PubVersion> result = em.createQuery("Select p from PubVersion p order by id desc limit 1").getResultList();
		
    	Blob dataBlob = result.get(0).getData();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1];
		InputStream is;
		try {
			is = dataBlob.getBinaryStream();
		    while (is.read(buffer) > 0) {
		    	baos.write(buffer);
			}
		 
		    String decompressedData = Util.decompress(baos.toByteArray());

			PublishedVersionCache pvc = new PublishedVersionCache(decompressedData);
			return pvc;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
