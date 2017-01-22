/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 					<http://cehis.se/>
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
package tak.web

import java.sql.Blob

import javax.sql.rowset.serial.SerialBlob

import org.apache.shiro.SecurityUtils

import se.skltp.tak.core.entity.AbstractVersionInfo
import se.skltp.tak.core.entity.AnropsAdress
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.Filter
import se.skltp.tak.core.entity.Filtercategorization
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.PubVersion
import se.skltp.tak.core.entity.RivTaProfil
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt
import se.skltp.tak.core.entity.Vagval
import se.skltp.tak.core.util.Util

import org.springframework.transaction.annotation.Transactional

@Transactional
class PublishService {
		
	def beforePublish() {
		// Get a new copy of latest pubVersion from db
		def pvList = PubVersion.executeQuery("from PubVersion order by id DESC", [max: 1, offset: 0])
		return pvList[0];
	}
	
    def doPublish(PubVersion oldPVInstance, PubVersion newPVInstance) {
		// Create a PVCache from oldInstance 	
		def pvCache = Util.getPublishedVersionCacheInstance(oldPVInstance)
		
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		// Get all pending changes from DB	
		def rivTaProfilList = RivTaProfil.findAllByUpdatedBy(principal)
		def tjanstekontraktList = Tjanstekontrakt.findAllByUpdatedBy(principal)
		def tjanstekomponentList = Tjanstekomponent.findAllByUpdatedBy(principal)
		def logiskAdressList = LogiskAdress.findAllByUpdatedBy(principal)
		def anropsAdressList = AnropsAdress.findAllByUpdatedBy(principal)
		def anropsbehorighetList = Anropsbehorighet.findAllByUpdatedBy(principal)
		def vagvalList = Vagval.findAllByUpdatedBy(principal)
		def filterList = Filter.findAllByUpdatedBy(principal)
		def filtercategorizationList = Filtercategorization.findAllByUpdatedBy(principal)
		
		//Populate pvCache
		pvCache.setTime(newPVInstance.getTime())
		pvCache.setUtforare(newPVInstance.getUtforare())
		pvCache.setFormatVersion(newPVInstance.getFormatVersion())
		pvCache.setKommentar(newPVInstance.getKommentar())
		pvCache.setVersion(newPVInstance.getId())
			
		// Add and update all object in correct order
		addUpdateRivTaProfil(pvCache, newPVInstance.id, rivTaProfilList);
		addUpdateTjanstekontrakt(pvCache, newPVInstance.id, tjanstekontraktList);
		addUpdateTjanstekomponent(pvCache, newPVInstance.id, tjanstekomponentList);
		addUpdateLogiskAdress(pvCache, newPVInstance.id, logiskAdressList);
		addUpdateAnropsAdress(pvCache, newPVInstance.id, anropsAdressList);
		addUpdateVagval(pvCache, newPVInstance.id, vagvalList);
		addUpdateAnropsbehorighet(pvCache, newPVInstance.id, anropsbehorighetList);		
		addUpdateFilter(pvCache, newPVInstance.id, filterList);
		addUpdateFiltercategorizations(pvCache, newPVInstance.id, filtercategorizationList);

		// Remove objects in correct order
		deleteFiltercategorizations(pvCache, newPVInstance.id, filtercategorizationList);
		deleteFilter(pvCache, newPVInstance.id, filterList);
		deleteAnropsbehorighet(pvCache, newPVInstance.id, anropsbehorighetList);
		deleteVagval(pvCache, newPVInstance.id, vagvalList);
		deleteAnropsAdress(pvCache, newPVInstance.id, anropsAdressList);
		deleteLogiskAdress(pvCache, newPVInstance.id, logiskAdressList);
		deleteTjanstekomponent(pvCache, newPVInstance.id, tjanstekomponentList);
		deleteTjanstekontrakt(pvCache, newPVInstance.id, tjanstekontraktList);
		deleteRivTaProfil(pvCache, newPVInstance.id, rivTaProfilList);
		
		// Create a new BLOB and save to db
		String newCacheJSON = Util.fromPublishedVersionToJSON(pvCache);
		Blob blob = new SerialBlob(Util.compress(newCacheJSON));
		newPVInstance.setData(blob);
		newPVInstance.setStorlek(blob.length());
		newPVInstance.setVersion(newPVInstance.getId());
		newPVInstance.save();
    }
	
	private void addUpdateRivTaProfil(pvCache, newPVId, listRTP) {
		// Apply changeset for RivTaProfil on new cache
		listRTP.each { rtp ->
			if (rtp.isNewlyCreated() || rtp.isUpdated()) {
				rtp.setPubVersion(Long.toString(newPVId));
				rtp.setUpdatedBy(null);
				rtp.setUpdatedTime(null);
				rtp.save(validate:false);
				pvCache.rivTaProfil.put((int) rtp.getId(), rtp);
			}
		}
	}
	
	private void addUpdateAnropsAdress(pvCache, newPVId, listAA) {
		// Apply changeset for AnropsAdress on new cache
		listAA.each { aa ->
			if (aa.isNewlyCreated() || aa.isUpdated()) {
				aa.setPubVersion(Long.toString(newPVId));
				aa.setUpdatedBy(null);
				aa.setUpdatedTime(null);
				aa.save(validate:false);
				pvCache.anropsAdress.put((int) aa.getId(), aa);
			}
		}
	}

	private void addUpdateFiltercategorizations(pvCache, newPVId, listFC) {
		// Apply changeset for Filtercategorization on new cache
		listFC.each { fc ->
			if (fc.isNewlyCreated() || fc.isUpdated()) {
				fc.setPubVersion(Long.toString(newPVId));
				fc.setUpdatedBy(null);
				fc.setUpdatedTime(null);
				fc.save(validate:false);
				pvCache.filtercategorization.put((int) fc.getId(), fc);
			}
		}
	}

	private void addUpdateFilter(pvCache, newPVId, listF) {
		// Apply changeset for Filter on new cache
		listF.each { f ->
			if (f.isNewlyCreated() || f.isUpdated()) {
				f.setPubVersion(Long.toString(newPVId));
				f.setUpdatedBy(null);
				f.setUpdatedTime(null);
				f.save(validate:false);
				pvCache.filter.put((int) f.getId(), f);
			}
		}
	}

	private void addUpdateVagval(pvCache, newPVId, listVV) {
		// Apply changeset for Vagval on new cache
		listVV.each { vv ->
			if (vv.isNewlyCreated() || vv.isUpdated()) {
				vv.setPubVersion(Long.toString(newPVId));
				vv.setUpdatedBy(null);
				vv.setUpdatedTime(null);
				vv.save(validate:false);
				pvCache.vagval.put((int) vv.getId(), vv);
			}
		}
	}

	private void addUpdateAnropsbehorighet( pvCache, newPVId, listAB) {
		// Apply changeset for Anropsbehorighet on new cache
		listAB.each { ab ->
			if (ab.isNewlyCreated() || ab.isUpdated()) {
				ab.setPubVersion(Long.toString(newPVId));
				ab.setUpdatedBy(null);
				ab.setUpdatedTime(null);
				ab.save(validate:false);
				pvCache.anropsbehorighet.put((int) ab.getId(), ab);
			}
		}
	}

	private void addUpdateTjanstekomponent(pvCache, newPVId, ListTKomp) {
		// Apply changeset for Tjanstekomponent on new cache
		ListTKomp.each { tk ->
			if (tk.isNewlyCreated() || tk.isUpdated()) {
				tk.setPubVersion(Long.toString(newPVId));
				tk.setUpdatedBy(null);
				tk.setUpdatedTime(null);
				tk.save(validate:false);
				pvCache.tjanstekomponent.put((int) tk.getId(), tk);
			}
		}
	}

	private void addUpdateTjanstekontrakt(pvCache, newPVId, listTK) {
		// Apply changeset for Tjanstekontrakt on new cache
		listTK.each { tk ->
			if (tk.isNewlyCreated() || tk.isUpdated()) {
				tk.setPubVersion(Long.toString(newPVId));
				tk.setUpdatedBy(null);
				tk.setUpdatedTime(null);
				tk.save(validate:false);
				pvCache.tjanstekontrakt.put((int) tk.getId(), tk);
			}
		}
	}

	private void addUpdateLogiskAdress(pvCache, newPVId, listLA) {
		// Apply changeset for LogiskAdress on new cache
		listLA.each { la ->
			if (la.isNewlyCreated() || la.isUpdated()) {
				la.setPubVersion(Long.toString(newPVId));
				la.setUpdatedBy(null);
				la.setUpdatedTime(null);
				la.save(validate:false);
				pvCache.logiskAdress.put((int) la.getId(), la);
			}
		}
	}
	
	private void deleteAnropsAdress(pvCache, newPVId, listAA) {
		// Apply changeset for AnropsAdress on new cache
		listAA.each { aa ->
			if (aa.isDeleted()) {
				aa.setPubVersion(Long.toString(newPVId));
				aa.setUpdatedBy(null);
				aa.setUpdatedTime(null);
				aa.save(validate:false);
				pvCache.anropsAdress.remove((int) aa.getId());
			}
		}
	}

	private void deleteFiltercategorizations(pvCache, newPVId, listFC) {
		// Apply changeset for Filtercategorization on new cache
		listFC.each { fc ->
			if (fc.isDeleted()) {
				fc.setPubVersion(Long.toString(newPVId));
				fc.setUpdatedBy(null);
				fc.setUpdatedTime(null);
				fc.save(validate:false);
				pvCache.filtercategorization.remove((int) fc.getId());
			}
		}
	}
	
	private void deleteFilter(pvCache,newPVId, listF) {
		// Apply changeset for Filter on new cache
		listF.each { f ->
			if (f.isDeleted()) {
				f.setPubVersion(Long.toString(newPVId));
				f.setUpdatedBy(null);
				f.setUpdatedTime(null);
				f.save(validate:false);
				pvCache.filter.remove((int) f.getId());
			}
		}
	}
	
	private void deleteVagval(pvCache, newPVId, listVV) {
		// Apply changeset for Vagval on new cache
		listVV.each { vv ->
			if (vv.isDeleted()) {
				vv.setPubVersion(Long.toString(newPVId));
				vv.setUpdatedBy(null);
				vv.setUpdatedTime(null);
				vv.save(validate:false);
				pvCache.vagval.remove((int) vv.getId());
			}
		}
	}
	
	private void deleteAnropsbehorighet(pvCache, newPVId, listAB) {
		// Apply changeset for Anropsbehorighet on new cache
		listAB.each { ab ->
			if (ab.isDeleted() ) {
				ab.setPubVersion(Long.toString(newPVId));
				ab.setUpdatedBy(null);
				ab.setUpdatedTime(null);
				ab.save(validate:false);
				pvCache.anropsbehorighet.remove((int) ab.getId());
			}
		}
	}
	
	private void deleteTjanstekomponent(pvCache, newPVId, ListTKomp) {
		// Apply changeset for Tjanstekomponent on new cache
		ListTKomp.each { tk ->
			if (tk.isDeleted()) {
				tk.setPubVersion(Long.toString(newPVId));
				tk.setUpdatedBy(null);
				tk.setUpdatedTime(null);
				tk.save(validate:false);
				pvCache.tjanstekomponent.remove((int) tk.getId());
			}
		}
	}
	
	private void deleteTjanstekontrakt(pvCache, newPVId, listTK) {
		// Apply changeset for Tjanstekontrakt on new cache
		listTK.each { tk ->
			if (tk.isDeleted()) {
				tk.setPubVersion(Long.toString(newPVId));
				tk.setUpdatedBy(null);
				tk.setUpdatedTime(null);
				tk.save(validate:false);
				pvCache.tjanstekontrakt.remove((int) tk.getId());
			}
		}
	}

	private void deleteLogiskAdress(pvCache, newPVId, listLA) {
		// Apply changeset for LogiskAdress on new cache
		listLA.each { la ->
			if (la.isDeleted()) {
				la.setPubVersion(Long.toString(newPVId));
				la.setUpdatedBy(null);
				la.setUpdatedTime(null);
				la.save(validate:false);
				pvCache.logiskAdress.remove((int) la.getId());
			}
		}		
	}
	
	private void deleteRivTaProfil(pvCache, newPVId, listRTP) {
		// Apply changeset for RivTaProfil on new cache
		listRTP.each { rtp ->
			if (rtp.isDeleted()) {
				rtp.setPubVersion(Long.toString(newPVId));
				rtp.setUpdatedBy(null);
				rtp.setUpdatedTime(null);
				rtp.save(validate:false);
				pvCache.rivTaProfil.remove((int) rtp.getId());
			}
		}
	}
	
	def rollbackPublish(List<AbstractVersionInfo> entityList, PubVersion pubVersionInstance) {
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		
		entityList.each { entity ->
			log.info "Rollback entity: " + entity
			entity.setUpdatedTime(new Date())
			entity.setUpdatedBy(principal)
			entity.setDeleted(false)
			entity.setPubVersion(null)
			if (!entity.save(flush: true)) {
				log.error "rollback failed on entity " + entity.getPubVersion()
				throw new IllegalStateException();
			}
		}
		log.info "Entity rollback done, items rollback size:" + entityList.size()
		
		pubVersionInstance.delete(flush: true)
	}
}
