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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import se.skltp.tak.core.memdb.LatestPublishedVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;

@Service("takPublishVersion")
public class TakPublishVersionImpl implements TakPublishVersion {

	@Autowired
	PubVersionDao pubversionDao;

	@Autowired
	PublishDao publishDao;

	@Autowired
	LatestPublishedVersion latestPublishVersion;

	@PersistenceContext
	private EntityManager em;

	private static final long formatVersion = 1L;
	
	@Override
	public List<PubVersion> getAllPubVersions() {
		List<PubVersion> list = pubversionDao.getAllPubVersion();
		return list;
	}

	@Override
	public String getJSONFromDb() throws Exception {

		// Get latest pubVersion from db
		PubVersion pvLatest = pubversionDao.getLatestPubVersion(); 
		
		// Get all entities from DB
		List<AnropsAdress> listAA = publishDao.getAnropsadress();
		List<Anropsbehorighet> listAB = publishDao.getAnropsbehorighet();
		List<Filter> listF = publishDao.getFilter();
		List<Filtercategorization> listFC = publishDao.getFiltercategorization();
		List<LogiskAdress> listLA = publishDao.getLogiskAdress();
		List<RivTaProfil> listRTP = publishDao.getRivTaProfil();
		List <Tjanstekomponent> listTKomp = publishDao.getTjanstekomponent();
		List<Tjanstekontrakt> listTK = publishDao.getTjanstekontrakt();		
		List<Vagval> listVV = publishDao.getVagval();
		
		// Get a PVCache for the above data
		PublishedVersionCache pvc = Util.getPublishedVersionCache(pvLatest, listRTP, listTK, listTKomp, listLA, listAA, listVV, listAB, listF, listFC );
		
		// Get JSON string from PVCache
		String jsonPV = Util.fromPublishedVersionToJSON(pvc);

		return jsonPV;
	}

	@Override
	public void resetPVCache() {
		latestPublishVersion.reinitializePVCache();
	}

	@Override
	public long getCurrentVersion() {
		return latestPublishVersion.getCurrentVersion();
	}
}
