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
package se.skltp.tak.core.entity

import grails.converters.JSON

import org.apache.commons.logging.LogFactory
import org.apache.shiro.SecurityUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException

class FiltercategorizationController extends AbstractController {
	
	private static final log = LogFactory.getLog(this)
	
    def scaffold = Filtercategorization
	
	def msg = message(code: 'filtercategorization.label', default: 'Filtercategorization')
	
	def save() {
		def filtercategorizationInstance = new Filtercategorization(params)
		saveEntity(filtercategorizationInstance, msg)
	}
	
	def update(Long id, Long version) {
		def filtercategorizationInstance = Filtercategorization.get(id)
		
		if (!filtercategorizationInstance) {
			flash.message = message(code: 'default.not.found.message', args: [msg, id])
			redirect(action: "list")
			return
		}
		filtercategorizationInstance.properties = params
		updateEntity(filtercategorizationInstance,  version, msg)
	}
	
	def delete(Long id) {
		def filtercategorizationInstance = Filtercategorization.get(id)
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		log.info "filtercategorization ${filtercategorizationInstance.toString()} about to be deleted by ${principal}:"
		log.info "${filtercategorizationInstance as JSON}"
		if (!filtercategorizationInstance) {
			flash.message = message(code: 'default.not.found.message', args: [msg, id])
			redirect(action: "list")
			return
		}

		try {
			def filter = filtercategorizationInstance.filter
			filter.removeFromCategorization(filtercategorizationInstance)
			setMetaData(filtercategorizationInstance, true)
			filtercategorizationInstance.save(flush: true)
			log.info "filtercategorization ${filtercategorizationInstance.toString()} was deleted by ${principal}:"
			flash.message = message(code: 'default.deleted.message', args: [msg, id])
			redirect(action: "list")
		}
		catch (DataIntegrityViolationException | UncategorizedSQLException e) {
			log.error "filtercategorization ${filtercategorizationInstance.toString()} could not be deleted by ${principal}:"
			flash.message = message(code: 'default.not.deleted.message', args: [msg, id])
			redirect(action: "show", id: id)
		}
	}
	/*
	def delete(Long id) {
		def rivTaProfilInstance = RivTaProfil.get(id)
		deleteEntity(rivTaProfilInstance, message(code: 'rivTaProfil.label', default: 'RivTaProfil'))
	}*/
	
		
	def filterPaneService

	def filter() {
		render( view:'list',
				model:[ filtercategorizationInstanceList: filterPaneService.filter( params, Filtercategorization),
						filtercategorizationInstanceTotal: filterPaneService.count( params, Filtercategorization ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}

	
}
