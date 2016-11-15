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
/**
 * 
 */
package se.skltp.tak.core.entity

/**
 * @author muqkha
 *
 */
import java.util.List;

import grails.converters.JSON

import org.apache.shiro.SecurityUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException

class AbstractController {
	
	void setMetaData(def AbstractVersionInfo versionInfo, def isDeleted) {
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		versionInfo.setUpdatedTime(new Date())
		versionInfo.setUpdatedBy(principal)
		versionInfo.setDeleted(isDeleted)
	}
	
	void saveEntity(AbstractVersionInfo entity, def model, def msg) {		
		setMetaData(entity, false)
		
		def s = entity.validate()
		if (!entity.save(flush: true)) {
			render(view: "create", model: model)
			return
		}
		
		log.info "Entity ${entity.toString()} created by ${entity.getUpdatedBy()}:"
		log.info "${entity as JSON}"
		flash.message = message(code: 'default.created.message', args: [msg, entity.id])
		flash.isCreated = true;
		redirect(action: "show", id: entity.id)
	}
	
	void updateEntity(AbstractVersionInfo entity, def model, Long version, def msg) {
		/*
		if (!entity) {
			flash.message = message(code: 'default.not.found.message', args: [msg, id])
			redirect(action: "list")
			return
		}*/
		
		if (version != null) {
			if (entity.version > version) {
				entity.errors.rejectValue("version", "default.optimistic.locking.failure",
						  [msg] as Object[],
						  "Another user has updated this entity while you were editing")
				render(view: "edit", model: model)
				return
			}
		}
		
		setMetaData(entity, false)
		
		if (!entity.save(flush: true)) {
			render(view: "edit", model: model)
			return
		} else {
			log.info "Entity ${entity.toString()} updated by ${entity.getUpdatedBy()}:"
			log.info "${entity as JSON}"
			flash.message = message(code: 'default.updated.message', args: [msg, entity.id])
			redirect(action: "show", id: entity.id)
		}
	}
	
	void deleteEntity(AbstractVersionInfo entity, Long id, def msg) {
		
		if (!entity) {
			flash.message = message(code: 'default.not.found.message', args: [msg, id])
			redirect(action: "list")
			return
		}

		try {
			if (entity.getPubVersion()) {
				//To allow only one deleted=false and many deleted posts
				setMetaData(entity, null)
				
				entity.save(flush: true)
				log.info "Entity ${entity.toString()} was set to deleted by ${entity.getUpdatedBy()}:"
			} else {
				entity.delete(flush: true)
				log.info "Entity ${entity.toString()} was deleted by ${entity.getUpdatedBy()}:"
			}
			
			flash.message = message(code: 'default.deleted.message', args: [msg, entity.id])
			redirect(action: "list")
		}
		catch (DataIntegrityViolationException | UncategorizedSQLException e) {
			log.error "Entity ${entity.toString()} could not be set to deleted by ${entity.getUpdatedBy()}:"
			flash.message = message(code: 'default.not.deleted.message', args: [msg, entity.id])
			redirect(action: "show", id: entity.id)
		}
	}
	
	private void addIfNotNull(List<AbstractVersionInfo> entityList, Collection c) {
		if (c) {
			entityList.addAll(c);
		}
	}
	
	private boolean isEntitySetToDeleted(List<AbstractVersionInfo> entityList) {
		boolean deleteStatus = true;
		for (entity in entityList) {
			if (!entity.isDeleted()) {
				deleteStatus = false
				break;
			}
		}
		return deleteStatus;
	}
		
}
