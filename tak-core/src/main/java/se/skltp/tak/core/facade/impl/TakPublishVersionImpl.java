package se.skltp.tak.core.facade.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

	@Override
	public List<PubVersion> getAllPubVersions() {
		List<PubVersion> list = pubversionDao.getAllPubVersion();
		return list;
	}

	@Override
	public void getJSONFromDb(OutputStream jsonOutputStream) throws Exception {

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
		Util.fromPublishedVersionToJSON(pvc, jsonOutputStream);
	}

	@Override
	public void resetPVCache(Integer version) {
		latestPublishVersion.reinitializePVCache(version);
	}

	@Override
	public long getCurrentVersion() {
		return latestPublishVersion.getCurrentVersion();
	}
}
