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
import grails.converters.JSON

import org.apache.shiro.SecurityUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException

abstract class AbstractController {

	abstract String getEntityLabel()
	abstract Class getEntityClass()
	abstract AbstractVersionInfo createEntityInstance(params)
	abstract String getModelName()
	abstract List<AbstractVersionInfo> getEntityDependencies(entityInstance)
	void onDeleteEntityAction(AbstractVersionInfo entityInstance){}

	def save() {
		def entityInstance = createEntityInstance(params)
		def entityLabel = getEntityLabel()
		def modelMap = [:]
		modelMap[getModelName()] = entityInstance

		setMetaData(entityInstance, false)

		def s = entityInstance.validate()
		if (!entityInstance.save(flush: true)) {
			render(view: "create", model: modelMap)
			return
		}

		log.info "Entity ${entityInstance.toString()} created by ${entityInstance.getUpdatedBy()}:"
		log.info "${entityInstance as JSON}"
		flash.message = message(code: 'default.created.message', args: [entityLabel, entityInstance.id])
		flash.isCreated = true;
		redirect(action: "show", id: entityInstance.id)
	}

	def update(Long id, Long version) {
		def entityInstance = getEntityClass().get(id)
		def entityLabel = getEntityLabel()
		def modelMap = [:]
		modelMap[getModelName()] = entityInstance

		if (!entityInstance) {
			flash.message = message(code: 'default.not.found.message', args: [getEntityLabel(), id])
			redirect(action: "list")
			return
		}
		entityInstance.properties = params

		/*
		if (!entity) {
			flash.message = message(code: 'default.not.found.message', args: [entityLabel, id])
			redirect(action: "list")
			return
		}*/

		if (version != null) {
			if (entityInstance.version > version) {
				entityInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
						[entityLabel] as Object[],
						"Another user has updated this entity while you were editing")
				render(view: "edit", model: modelMap)
				return
			}
		}

		setMetaData(entityInstance, false)

		if (!entityInstance.save(flush: true)) {
			render(view: "edit", model: modelMap)
			return
		} else {
			log.info "Entity ${entityInstance.toString()} updated by ${entityInstance.getUpdatedBy()}:"
			log.info "${entityInstance as JSON}"
			flash.message = message(code: 'default.updated.message', args: [entityLabel, entityInstance.id])
			redirect(action: "show", id: entityInstance.id)
		}
	}

	def delete(Long id) {
		def entityInstance = getEntityClass().get(id)
		def entityLabel = getEntityLabel()

		ArrayList<AbstractVersionInfo> entityList = getEntityDependencies(entityInstance)

		boolean deleteConstraintSatisfied = isEntitySetToDeleted(entityList);
		if (deleteConstraintSatisfied) {
			onDeleteEntityAction(entityInstance)

			if (!entityInstance) {
				flash.message = message(code: 'default.not.found.message', args: [entityLabel, id])
				redirect(action: "list")
				return
			}

			try {
				if (entityInstance.getPubVersion()) {
					//To allow only one deleted=false and many deleted posts
					setMetaData(entityInstance, null)
					entityInstance.save(flush: true)
					log.info "Entity ${entityInstance.toString()} was set to deleted by ${entityInstance.getUpdatedBy()}:"
				} else {
					entityInstance.delete(flush: true)
					log.info "Entity ${entityInstance.toString()} was deleted by ${entityInstance.getUpdatedBy()}:"
				}

				flash.message = message(code: 'default.deleted.message', args: [entityLabel, entityInstance.id])
				redirect(action: "list")
			}
			catch (DataIntegrityViolationException | UncategorizedSQLException e) {
				log.error "Entity ${entityInstance.toString()} could not be set to deleted by ${entityInstance.getUpdatedBy()}:"
				flash.message = message(code: 'default.not.deleted.message', args: [entityLabel, entityInstance.id])
				redirect(action: "show", id: entityInstance.id)
			}

		} else {
			log.info "Entity ${entityInstance.toString()} could not be set to deleted by ${entityInstance.getUpdatedBy()} due to constraint violation"
			flash.message = message(code: 'default.not.deleted.constraint.violation.message', args: [getEntityLabel(), entityInstance.id])
			redirect(action: "show", id: entityInstance.id)
		}
	}

	void setMetaData(def AbstractVersionInfo versionInfo, def isDeleted) {
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		versionInfo.setUpdatedTime(new Date())
		versionInfo.setUpdatedBy(principal)
		versionInfo.setDeleted(isDeleted)
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
