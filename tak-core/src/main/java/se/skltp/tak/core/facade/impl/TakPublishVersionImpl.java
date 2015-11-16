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

import java.sql.Blob;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.rowset.serial.SerialBlob;

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
import se.skltp.tak.core.memdb.PublishedVersionCache;
import se.skltp.tak.core.util.Util;

@Service("takPublishVersion")
public class TakPublishVersionImpl implements TakPublishVersion {

	@Autowired
	PubVersionDao pubversionDao;

	@Autowired
	PublishDao publishDao;

	@PersistenceContext
	private EntityManager em;

	private static final long formatVersion = 1L;
	
	@Override
	public List<PubVersion> getAllPubVersions() {
		List<PubVersion> list = pubversionDao.getAllPubVersion();
		return list;
	}

	@Override
	public void publishANewVersion( PubVersion pv) throws Exception {

		// Get a new copy of latest pubVersion from db
		PublishedVersionCache pvCache = pubversionDao.getLatestPublishedVersion(); 
		
		// Write new PubVersion to db, we will add BLOB in later steps as an update
		pv.setFormatVersion(formatVersion);
		pv.setTime(new java.sql.Date(System.currentTimeMillis()));
		em.persist(pv);
		em.flush();
		Long newPVId = pv.getId(); // Used in all changesets below
		
		// Get all pending changes from DB and apply changes in created temporary cache!
		List<AnropsAdress> listAA = publishDao.getAnropsadressForPublish();
		List<Anropsbehorighet> listAB = publishDao.getAnropsbehorighetForPublish();
		List<Filter> listF = publishDao.getFilterForPublish();
		List<Filtercategorization> listFC = publishDao.getFiltercategorizationForPublish();
		List<LogiskAdress> listLA = publishDao.getLogiskAdressForPublish();
		List<RivTaProfil> listRTP = publishDao.getRivTaProfilForPublish();
		List <Tjanstekomponent> ListTKomp = publishDao.getTjanstekomponentForPublish();
		List<Tjanstekontrakt> listTK = publishDao.getTjanstekontraktForPublish();		
		List<Vagval> listVV = publishDao.getVagvalForPublish();
		
		// Add and update all object in correct order
		addUpdateRivTaProfil(pvCache, newPVId, listRTP);		
		addUpdateTjanstekontrakt(pvCache, newPVId, listTK);		
		addUpdateTjanstekomponent(pvCache, newPVId, ListTKomp);			
		addUpdateLogiskAdress(pvCache, newPVId, listLA);		
		addUpdateAnropsAdress(pvCache, newPVId, listAA);	
		addUpdateVagval(pvCache, newPVId, listVV);	
		addUpdateAnropsbehorighet(pvCache, newPVId, listAB);	
		addUpdateFilter(pvCache, newPVId, listF);	
		addUpdateFiltercategorizations(pvCache, newPVId, listFC);	

		// Remove objects in correct order
		deleteFiltercategorizations(pvCache, newPVId, listFC);	
		deleteFilter(pvCache, newPVId, listF);	
		deleteAnropsbehorighet(pvCache, newPVId, listAB);	
		deleteVagval(pvCache, newPVId, listVV);	
		deleteAnropsAdress(pvCache, newPVId, listAA);	
		deleteLogiskAdress(pvCache, newPVId, listLA);		
		deleteTjanstekomponent(pvCache, newPVId, ListTKomp);			
		deleteTjanstekontrakt(pvCache, newPVId, listTK);		
		deleteRivTaProfil(pvCache, newPVId, listRTP);		
		
		// Create a new BLOB and save to db
		String newCacheJSON = Util.fromPublishedVersionToJSON(pvCache);
		Blob blob = new SerialBlob(Util.compress(newCacheJSON));
	    pv.setData(blob);
	}

	private void addUpdateAnropsAdress(PublishedVersionCache pvCache,
			Long newPVId, List<AnropsAdress> listAA) {
		// Apply changeset for AnropsAdress on new cache
		for (AnropsAdress aa : listAA) {
			// Determine kind of change
			if (aa.isNewlyCreated() || aa.isUpdated()) {
				// Modify record
				aa.setPubVersion(Long.toString(newPVId));
				aa.setUpdatedBy(null);
				aa.setUpdatedTime(null);
				pvCache.anropsAdress.put((int) aa.getId(), aa);
				em.persist(aa);
			}
		}
	}

	private void addUpdateFiltercategorizations(PublishedVersionCache pvCache,
			Long newPVId, List<Filtercategorization> listFC) {
		// Apply changeset for Filtercategorization on new cache
		for (Filtercategorization fc : listFC) {
			// Determine kind of change
			if (fc.isNewlyCreated() || fc.isUpdated()) {
				// Modify record
				fc.setPubVersion(Long.toString(newPVId));
				fc.setUpdatedBy(null);
				fc.setUpdatedTime(null);
				pvCache.filtercategorization.put((int) fc.getId(), fc);
				em.persist(fc);
			}
		}
	}

	private void addUpdateFilter(PublishedVersionCache pvCache, Long newPVId,
			List<Filter> listF) {
		// Apply changeset for Filter on new cache
		for (Filter f : listF) {
			// Determine kind of change
			if (f.isNewlyCreated() || f.isUpdated()) {
				// Modify record
				f.setPubVersion(Long.toString(newPVId));
				f.setUpdatedBy(null);
				f.setUpdatedTime(null);
				pvCache.filter.put((int) f.getId(), f);
				em.persist(f);
			}
		}
	}

	private void addUpdateVagval(PublishedVersionCache pvCache, Long newPVId,
			List<Vagval> listVV) {
		// Apply changeset for Vagval on new cache
		for (Vagval vv : listVV) {
			// Determine kind of change
			if (vv.isNewlyCreated() || vv.isUpdated()) {
				// Modify record
				vv.setPubVersion(Long.toString(newPVId));
				vv.setUpdatedBy(null);
				vv.setUpdatedTime(null);
				pvCache.vagval.put((int) vv.getId(), vv);
				em.persist(vv);
			}
		}
	}

	private void addUpdateAnropsbehorighet(PublishedVersionCache pvCache,
			Long newPVId, List<Anropsbehorighet> listAB) {
		// Apply changeset for Anropsbehorighet on new cache
		for (Anropsbehorighet ab : listAB) {
			// Determine kind of change
			if (ab.isNewlyCreated() || ab.isUpdated()) {
				// Modify record
				ab.setPubVersion(Long.toString(newPVId));
				ab.setUpdatedBy(null);
				ab.setUpdatedTime(null);
				pvCache.anropsbehorighet.put((int) ab.getId(), ab);
				em.persist(ab);
			}
		}
	}

	private void addUpdateTjanstekomponent(PublishedVersionCache pvCache,
			Long newPVId, List<Tjanstekomponent> ListTKomp) {
		// Apply changeset for Tjanstekomponent on new cache
		for (Tjanstekomponent tk : ListTKomp) {
			// Determine kind of change
			if (tk.isNewlyCreated() || tk.isUpdated()) {
				// Modify record
				tk.setPubVersion(Long.toString(newPVId));
				tk.setUpdatedBy(null);
				tk.setUpdatedTime(null);
				pvCache.tjanstekomponent.put((int) tk.getId(), tk);
				em.persist(tk);
			}
		}
	}

	private void addUpdateTjanstekontrakt(PublishedVersionCache pvCache,
			Long newPVId, List<Tjanstekontrakt> listTK) {
		// Apply changeset for Tjanstekontrakt on new cache
		for (Tjanstekontrakt tk : listTK) {
			// Determine kind of change
			if (tk.isNewlyCreated() || tk.isUpdated()) {
				// Modify record
				tk.setPubVersion(Long.toString(newPVId));
				tk.setUpdatedBy(null);
				tk.setUpdatedTime(null);
				pvCache.tjanstekontrakt.put((int) tk.getId(), tk);
				em.persist(tk);
			}
		}
	}

	private void addUpdateLogiskAdress(PublishedVersionCache pvCache,
			Long newPVId, List<LogiskAdress> listLA) {
		// Apply changeset for LogiskAdress on new cache
		for (LogiskAdress la : listLA) {
			// Determine kind of change
			if (la.isNewlyCreated() || la.isUpdated()) {
				// Modify record
				la.setPubVersion(Long.toString(newPVId));
				la.setUpdatedBy(null);
				la.setUpdatedTime(null);
				pvCache.logiskAdress.put((int) la.getId(), la);
				em.persist(la);
			}
		}
	}

	private void addUpdateRivTaProfil(PublishedVersionCache pvCache,
			Long newPVId, List<RivTaProfil> listRTP) {
		// Apply changeset for RivTaProfil on new cache
		for (RivTaProfil rtp : listRTP) {
			if (rtp.isNewlyCreated() || rtp.isUpdated()) {
				// Modify record
				rtp.setPubVersion(Long.toString(newPVId));
				rtp.setUpdatedBy(null);
				rtp.setUpdatedTime(null);
				pvCache.rivTaProfil.put((int) rtp.getId(), rtp);
				em.persist(rtp);
			}			
		}
	}
	
	
	private void deleteAnropsAdress(PublishedVersionCache pvCache,
			Long newPVId, List<AnropsAdress> listAA) {
		// Apply changeset for AnropsAdress on new cache
		for (AnropsAdress aa : listAA) {
			if (aa.isDeleted()) {
				// Remove this anropsadress from tjanstekomponent list
				Iterator<AnropsAdress> aaIter = pvCache.tjanstekomponent.get((int) aa.getTjanstekomponent().getId()).getAnropsAdresser().iterator();
				while (aaIter.hasNext()) {
					if (aaIter.next().getId() == aa.getId()) {
						aaIter.remove();
					}
				}
				// Remove this anropsadress from rivtaprofil list
				aaIter = pvCache.rivTaProfil.get((int) aa.getRivTaProfil().getId()).getAnropsAdresser().iterator();
				while (aaIter.hasNext()) {
					if (aaIter.next().getId() == aa.getId()) {
						aaIter.remove();
					}
				}
				pvCache.anropsAdress.remove((int) aa.getId());				
				aa.setPubVersion(Long.toString(newPVId));
				aa.setUpdatedBy(null);
				aa.setUpdatedTime(null);
				em.persist(aa);
			}			
		}
	}

	private void deleteFiltercategorizations(PublishedVersionCache pvCache,
			Long newPVId, List<Filtercategorization> listFC) {
		// Apply changeset for Filtercategorization on new cache
		for (Filtercategorization fc : listFC) {
			if (fc.isDeleted()) {
				// Remove this filtercategorization from filter list
				Iterator<Filtercategorization> fcIter = pvCache.filter.get((int) fc.getFilter().getId()).getCategorization().iterator();
				while (fcIter.hasNext()) {
					if (fcIter.next().getId() == fc.getId()) {
						fcIter.remove();
					}
				}
				pvCache.filtercategorization.remove((int) fc.getId());				
				fc.setPubVersion(Long.toString(newPVId));
				fc.setUpdatedBy(null);
				fc.setUpdatedTime(null);
				em.persist(fc);
			}			
		}
	}

	private void deleteFilter(PublishedVersionCache pvCache, Long newPVId,
			List<Filter> listF) {
		// Apply changeset for Filter on new cache
		for (Filter f : listF) {
			if (f.isDeleted()) {
				// Remove this filter from anropsbehorighet list
				Iterator<Filter> fIter = pvCache.anropsbehorighet.get((int) f.getAnropsbehorighet().getId()).getFilter().iterator();
				while (fIter.hasNext()) {
					if (fIter.next().getId() == f.getId()) {
						fIter.remove();
					}
				}
				pvCache.filter.remove((int) f.getId());				
				f.setPubVersion(Long.toString(newPVId));
				f.setUpdatedBy(null);
				f.setUpdatedTime(null);
				em.persist(f);
			}			
		}
	}

	private void deleteVagval(PublishedVersionCache pvCache, Long newPVId,
			List<Vagval> listVV) {
		// Apply changeset for Vagval on new cache
		for (Vagval vv : listVV) {
			if (vv.isDeleted()) {
				// Remove this vagval from tjanstekontrakt list
				Iterator<Vagval> vvIter = pvCache.tjanstekontrakt.get((int) vv.getTjanstekontrakt().getId()).getVagval().iterator();
				while (vvIter.hasNext()) {
					if (vvIter.next().getId() == vv.getId()) {
						vvIter.remove();
					}
				}
				// Remove this vagval from anropsadress list
				vvIter = pvCache.anropsAdress.get((int) vv.getAnropsAdress().getId()).getVagVal().iterator();
				while (vvIter.hasNext()) {
					if (vvIter.next().getId() == vv.getId()) {
						vvIter.remove();
					}
				}
				pvCache.vagval.remove((int) vv.getId());				
				vv.setPubVersion(Long.toString(newPVId));
				vv.setUpdatedBy(null);
				vv.setUpdatedTime(null);
				em.persist(vv);
			}			
		}
	}

	private void deleteAnropsbehorighet(PublishedVersionCache pvCache,
			Long newPVId, List<Anropsbehorighet> listAB) {
		// Apply changeset for Anropsbehorighet on new cache
		for (Anropsbehorighet ab : listAB) {
			if (ab.isDeleted()) {
				// Remove this anroppsbehorighet from tjanstekontrakt list
				Iterator<Anropsbehorighet> abIter = pvCache.tjanstekontrakt.get((int) ab.getTjanstekontrakt().getId()).getAnropsbehorigheter().iterator();
				while (abIter.hasNext()) {
					if (abIter.next().getId() == ab.getId()) {
						abIter.remove();
					}
				}
				// Remove this anroppsbehorighet from tjänstekomponent list
				abIter = pvCache.tjanstekomponent.get((int) ab.getTjanstekonsument().getId()).getAnropsbehorigheter().iterator();
				while (abIter.hasNext()) {
					if (abIter.next().getId() == ab.getId()) {
						abIter.remove();
					}
				}
				// Remove this anroppsbehorighet from logiskadress list
				abIter = pvCache.logiskAdress.get((int) ab.getLogiskAdress().getId()).getAnropsbehorigheter().iterator();
				while (abIter.hasNext()) {
					if (abIter.next().getId() == ab.getId()) {
						abIter.remove();
					}
				}
				pvCache.anropsbehorighet.remove((int) ab.getId());				
				ab.setPubVersion(Long.toString(newPVId));
				ab.setUpdatedBy(null);
				ab.setUpdatedTime(null);
				em.persist(ab);
			}			
		}
	}

	private void deleteTjanstekomponent(PublishedVersionCache pvCache,
			Long newPVId, List<Tjanstekomponent> ListTKomp) {
		// Apply changeset for Tjanstekomponent on new cache
		for (Tjanstekomponent tk : ListTKomp) {
			if (tk.isDeleted()) {
				pvCache.tjanstekomponent.remove((int) tk.getId());				
				tk.setPubVersion(Long.toString(newPVId));
				tk.setUpdatedBy(null);
				tk.setUpdatedTime(null);
				em.persist(tk);
			}			
		}
	}

	private void deleteTjanstekontrakt(PublishedVersionCache pvCache,
			Long newPVId, List<Tjanstekontrakt> listTK) {
		// Apply changeset for Tjanstekontrakt on new cache
		for (Tjanstekontrakt tk : listTK) {
			if (tk.isDeleted()) {
				pvCache.tjanstekontrakt.remove((int) tk.getId());				
				tk.setPubVersion(Long.toString(newPVId));
				tk.setUpdatedBy(null);
				tk.setUpdatedTime(null);				
				em.persist(tk);
			}			
		}
	}

	private void deleteLogiskAdress(PublishedVersionCache pvCache,
			Long newPVId, List<LogiskAdress> listLA) {
		// Apply changeset for LogiskAdress on new cache
		for (LogiskAdress la : listLA) {
			if (la.isDeleted()) {
				pvCache.logiskAdress.remove((int) la.getId());				
				la.setPubVersion(Long.toString(newPVId));
				la.setUpdatedBy(null);
				la.setUpdatedTime(null);				
				em.persist(la);
			}			
		}
	}

	private void deleteRivTaProfil(PublishedVersionCache pvCache,
			Long newPVId, List<RivTaProfil> listRTP) {
		// Apply changeset for RivTaProfil on new cache
		for (RivTaProfil rtp : listRTP) {
			if (rtp.isDeleted()) {
				pvCache.rivTaProfil.remove((int) rtp.getId());				
				rtp.setPubVersion(Long.toString(newPVId));
				rtp.setUpdatedBy(null);
				rtp.setUpdatedTime(null);				
				em.persist(rtp);
			}			
		}
	}	
}
