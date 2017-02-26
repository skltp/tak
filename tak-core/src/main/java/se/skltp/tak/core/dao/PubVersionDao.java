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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;

@Service()
public class PubVersionDao {
	
	private static final Logger log = LoggerFactory.getLogger(PubVersionDao.class);
	
	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<PubVersion> getAllPubVersion() {
		List<PubVersion> list = em.createQuery("Select p from PubVersion p").getResultList();
		return list;
	}
		
	@SuppressWarnings("unchecked")
	public PubVersion getLatestPubVersion() {
		List<PubVersion> result = em.createQuery("Select p from PubVersion p order by id desc limit 1").getResultList();
		if (result.size() == 0) {
			return null;
		} else {
			return result.get(0);
		}
  	}

	@SuppressWarnings("unchecked")
	public PublishedVersionCache getPublishedVersionCacheOnId(long id) {
		List<PubVersion> result = em.createQuery("Select p from PubVersion p where id = " + id).getResultList();

		if (result.isEmpty()) {
			String errMsg = "No published version with id: " +id + " found in database. Publish a core version first";
			log.error(errMsg);
			throw new IllegalStateException(errMsg);
		}

        return getPublishedVersionCache(result);
	}

    @SuppressWarnings("unchecked")
	public PublishedVersionCache getLatestPublishedVersionCache() {
		List<PubVersion> result = em.createQuery("Select p from PubVersion p order by id desc limit 1").getResultList();
		
		if (result.isEmpty()) {
			String errMsg = "No published version found in database. Publish a core version first";
			log.error(errMsg);
			throw new IllegalStateException(errMsg);
		}

        return getPublishedVersionCache(result);
	}

    private PublishedVersionCache getPublishedVersionCache(List<PubVersion> result) {
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
            log.info("Fetched a new TAK version from database");
            return pvc;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
