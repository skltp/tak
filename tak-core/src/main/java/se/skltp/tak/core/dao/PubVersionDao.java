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

import java.io.*;
import java.sql.*;
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
	public static final String HSQL_GET_LATEST_PUBVERSION = "Select p from PubVersion p order by id desc";
	public static final String HSQL_GET_ALL_FROM_PUBVERSION_P = "Select p from PubVersion p";
	public static final String HSQL_GET_PUBVERSION_WHERE_ID = "Select p from PubVersion p where id = ";

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<PubVersion> getAllPubVersion() {
		List<PubVersion> list = em.createQuery(HSQL_GET_ALL_FROM_PUBVERSION_P).getResultList();
		return list;
	}
		
	@SuppressWarnings("unchecked")
	public PubVersion getLatestPubVersion() {
		List<PubVersion> result = em.createQuery(HSQL_GET_LATEST_PUBVERSION).setMaxResults(1).getResultList();
		if (result.size() == 0) {
			return null;
		} else {
			return result.get(0);
		}
  	}

	@SuppressWarnings("unchecked")
	public PublishedVersionCache getPublishedVersionCacheOnId(long id) {
		log.info("Getting PubVersion with id:"+id);
		List<PubVersion> result = em.createQuery(HSQL_GET_PUBVERSION_WHERE_ID + id).getResultList();

		if (result.isEmpty()) {
			String errMsg = "No published version with id: " +id + " found in database. Publish a core version first";
			log.error(errMsg);
			throw new IllegalStateException(errMsg);
		}

        return getPublishedVersionCache(result);
	}

	@SuppressWarnings("unchecked")
	public PublishedVersionCache getLatestPublishedVersionCache() {
		List<PubVersion> result = em.createQuery(HSQL_GET_LATEST_PUBVERSION).setMaxResults(1).getResultList();

		if (result.isEmpty()) {
			String errMsg = "No published version found in database. Publish a core version first";
			log.error(errMsg);
			throw new IllegalStateException(errMsg);
		}

		return getPublishedVersionCache(result);
	}

	private PublishedVersionCache getPublishedVersionCache(List<PubVersion> result) {
		try {
			Blob dataBlob = result.get(0).getData();
			return getPublishedVersionCacheFromInputStream(dataBlob.getBinaryStream());
		} catch (SQLException e) {
			log.error("Failed get TAK version from database", e);
			return null;
		} catch (NullPointerException e) {
			log.error("PubVersion data is empty. "
					+ "It's possible that it has not yet been recorded in 'data' field in database.", e);
			return null;
		}
	}

	private PublishedVersionCache getPublishedVersionCacheFromInputStream(InputStream is) {
		try {
			byte[] compressedTakVersion = streamToByteArray(is);
			String decompressedTakVersion = Util.decompress(compressedTakVersion);
			PublishedVersionCache pvc = new PublishedVersionCache(decompressedTakVersion);
			log.info("Fetched a new TAK version from database");
			return pvc;
		} catch ( IOException e) {
			log.error("IO error getting TAK version", e);
			return null;
		}
	}

	private byte[] streamToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] oneByte = new byte[1];
		while (is.read(oneByte) > 0) {
			outputStream.write(oneByte);
		}
		return outputStream.toByteArray();
	}

}
