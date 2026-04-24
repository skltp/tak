package se.skltp.tak.core.memdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.skltp.tak.core.dao.PubVersionDao;

@Service()
public class LatestPublishedVersion {

	private static final Logger log = LoggerFactory.getLogger(LatestPublishedVersion.class);

	@Autowired
	PubVersionDao pubversionDao;
	
	private PublishedVersionCache pvc;
	private PublishedVersionCache pvc0 = null;

	private Integer version = null;

	public LatestPublishedVersion() {
		pvc= null;
	}
	
	public void setPvc(PublishedVersionCache pvc) {
		this.pvc = pvc;
		this.pvc0 = null;
	}

	public synchronized PublishedVersionCache getPvc() {
		if (pvc == null) {			
			try {
				if(version == null) {
					log.info("Get latest version");
					PublishedVersionCache latestPV = pubversionDao.getLatestPublishedVersionCache();
					setPvc(latestPV);
				} else {
					log.info("Get version {}", version.longValue());
					PublishedVersionCache selectedPV = pubversionDao.getPublishedVersionCacheOnId(version.longValue());
					setPvc(selectedPV);
				}
			} catch (Exception e) {
				if(pvc0 != null) {
					pvc = pvc0;
					throw new RuntimeException("An error occured. The cache has not been updated.",e);
				} else
					throw new RuntimeException("An error occured. Try rollback from TAK-WEB.",e);
			}
		}
		return pvc;
	}

	public void reinitializePVCache(Integer version) {
		this.version = version;
		pvc0 = pvc;
		pvc = null;
	}

	
	public long getCurrentVersion() {
		return pvc != null ? pvc.getVersion() : 0L;
	}
}
