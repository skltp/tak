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

abstract class AbstractCRUDController {

	protected abstract String getEntityLabel()
	protected abstract Class getEntityClass()
	protected abstract AbstractVersionInfo createEntity(Map paramsMap)
	protected abstract String getModelName()
	protected abstract List<AbstractVersionInfo> getEntityDependencies(AbstractVersionInfo entityInstance)
	protected void onDeleteEntityAction(AbstractVersionInfo entityInstance){}

	def save() {
		def entityInstance = createEntity(params)
		setMetaData(entityInstance, false)
		if (!entityInstance.save(flush: true)) {
			render(view: "create", model: getModelMap(entityInstance))
			return
		}

		log.info "Entity ${entityInstance.toString()} created by ${entityInstance.getUpdatedBy()}:"
		log.info "${entityInstance as JSON}"
		flash.message = message(code: 'default.created.message', args: [getEntityLabel(), entityInstance.id])
		flash.isCreated = true
		redirect(action: "show", id: entityInstance.id)
	}

	def update(Long id, Long version) {
		def entityInstance = getEntityClass().get(id)
		if (!entityInstance) {
			flash.message = message(code: 'default.not.found.message', args: [getEntityLabel(), id])
			redirect(action: "list")
			return
		}

		if (version != null) {
			if (entityInstance.version > version) {
				entityInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
						[getEntityLabel()] as Object[],
						"Another user has updated this entity while you were editing")
				render(view: "edit", model: getModelMap(entityInstance))
				return
			}
		}

		entityInstance.properties = params
		setMetaData(entityInstance, false)
		if (!entityInstance.save(flush: true)) {
			render(view: "edit", model: getModelMap(entityInstance))
			return
		} else {
			log.info "Entity ${entityInstance.toString()} updated by ${entityInstance.getUpdatedBy()}:"
			log.info "${entityInstance as JSON}"
			flash.message = message(code: 'default.updated.message', args: [getEntityLabel(), entityInstance.id])
			redirect(action: "show", id: entityInstance.id)
		}
	}

	def bulkDelete() {
		def deleteList = params.list('toDelete')

		def messages = []

		deleteList.each {
			long id = Long.parseLong(it)

			messages << deleteForBulk(id, getEntityLabel(), getEntityClass())
		}

		flash.messages = messages

		redirect(action: "deletelist")
	}

	def deleteForBulk(long id, String entityLabel, Class entityClass) {
		def entityInstance = entityClass.get(id)
		if (!entityInstance) {
            return message(code: 'default.not.found.message', args: [entityLabel, id])
		}

		ArrayList<AbstractVersionInfo> entityList = getEntityDependencies(entityInstance)
		boolean deleteConstraintSatisfied = isEntitySetToDeleted(entityList);
		if (deleteConstraintSatisfied) {
			onDeleteEntityAction(entityInstance)

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

                return message(code: 'default.deleted.message', args: [entityLabel, entityInstance.id])
			}
			catch (DataIntegrityViolationException | UncategorizedSQLException e) {
				log.error "Entity ${entityInstance.toString()} could not be set to deleted by ${entityInstance.getUpdatedBy()}:"
                return message(code: 'default.not.deleted.message', args: [entityLabel, entityInstance.id])
			}

		} else {
			log.info "Entity ${entityInstance.toString()} could not be set to deleted by ${entityInstance.getUpdatedBy()} due to constraint violation"
            return message(code: 'default.not.deleted.constraint.violation.message', args: [entityLabel, entityInstance.id])
		}

	}

	def delete(Long id) {
		def entityInstance = getEntityClass().get(id)
		if (!entityInstance) {
			flash.message = message(code: 'default.not.found.message', args: [getEntityLabel(), id])
			redirect(action: "list")
			return
		}

		ArrayList<AbstractVersionInfo> entityList = getEntityDependencies(entityInstance)
		boolean deleteConstraintSatisfied = isEntitySetToDeleted(entityList);
		if (deleteConstraintSatisfied) {
			onDeleteEntityAction(entityInstance)

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

				flash.message = message(code: 'default.deleted.message', args: [getEntityLabel(), entityInstance.id])
				redirect(action: "list")
			}
			catch (DataIntegrityViolationException | UncategorizedSQLException e) {
				log.error "Entity ${entityInstance.toString()} could not be set to deleted by ${entityInstance.getUpdatedBy()}:"
				flash.message = message(code: 'default.not.deleted.message', args: [getEntityLabel(), entityInstance.id])
				redirect(action: "show", id: entityInstance.id)
			}

		} else {
			log.info "Entity ${entityInstance.toString()} could not be set to deleted by ${entityInstance.getUpdatedBy()} due to constraint violation"
			flash.message = message(code: 'default.not.deleted.constraint.violation.message', args: [getEntityLabel(), entityInstance.id])
			redirect(action: "show", id: entityInstance.id)
		}
	}

	protected void setMetaData(AbstractVersionInfo versionInfo, isDeleted) {
		def principal = SecurityUtils.getSubject()?.getPrincipal()
		versionInfo.setUpdatedTime(new Date())
		versionInfo.setUpdatedBy(principal)
		versionInfo.setDeleted(isDeleted)
	}

	protected void addIfNotNull(List<AbstractVersionInfo> entityList, Collection c) {
		if (c) {
			entityList.addAll(c)
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

	private Map getModelMap(AbstractVersionInfo entityInstance) {
		def modelMap = [:]
		modelMap[getModelName()] = entityInstance
		modelMap
	}
}
