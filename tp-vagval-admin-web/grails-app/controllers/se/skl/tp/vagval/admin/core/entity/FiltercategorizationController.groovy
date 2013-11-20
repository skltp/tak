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
package se.skl.tp.vagval.admin.core.entity

import grails.converters.JSON

import org.apache.shiro.SecurityUtils
import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException


class FiltercategorizationController {

    static scaffold = Filtercategorization
	
	def filterPaneService
	
	def filter() {
		render( view:'list',
				model:[ filtercategorizationInstanceList: filterPaneService.filter( params, Filtercategorization),
						filtercategorizationInstanceTotal: filterPaneService.count( params, Filtercategorization ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}
	
	def delete(Long id) {
		def filtercategorizationInstance = Filtercategorization.get(id)
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		log.info "filtercategorization ${filtercategorizationInstance.toString()} about to be deleted by ${principal}:"
		log.info "${filtercategorizationInstance as JSON}"
		if (!filtercategorizationInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'filtercategorization.label', default: 'Filtercategorization'), id])
			redirect(action: "list")
			return
		}

		try {
			Filter filter = filtercategorizationInstance.filter
			filter.removeFromCategorization(filtercategorizationInstance)
			filtercategorizationInstance.delete(flush: true)
			log.info "filtercategorization ${filtercategorizationInstance.toString()} was deleted by ${principal}:"
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'filtercategorization.label', default: 'Filtercategorization'), id])
			redirect(action: "list")
		}
		catch (DataIntegrityViolationException | UncategorizedSQLException e) {
			log.error "filtercategorization ${filtercategorizationInstance.toString()} could not be deleted by ${principal}:"
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'filtercategorization.label', default: 'Filtercategorization'), id])
			redirect(action: "show", id: id)
		}
	}
}
