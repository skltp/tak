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

import org.grails.plugin.filterpane.FilterPaneUtils
import grails.converters.JSON

import org.apache.commons.logging.LogFactory
import org.apache.shiro.SecurityUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException

class FilterController extends AbstractController {
	
	private static final log = LogFactory.getLog(this)

    def scaffold = Filter
	
	def msg = { message(code: 'filterInstance.label', default: 'Filter') }
	
	def save() {
		def filterInstance = new Filter(params)
		filterInstance.setServicedomain(filterInstance.getServicedomain().trim())
		saveEntity(filterInstance, msg())
	}
	
	def update(Long id, Long version) {
		def filterInstance = Filter.get(id)
		
		if (!filterInstance) {
			flash.message = message(code: 'default.not.found.message', args: [msg(), id])
			redirect(action: "list")
			return
		}
		filterInstance.properties = params
		filterInstance.setServicedomain(filterInstance.getServicedomain().trim())
		updateEntity(filterInstance, version, msg())
	}
	
	def delete(Long id) {
		def filterInstance = Filter.get(id)
		
		List<AbstractVersionInfo> entityList = new ArrayList<AbstractVersionInfo>();
		addIfNotNull(entityList, filterInstance?.getCategorization())
		
		boolean contraintViolated = isEntitySetToDeleted(entityList);
		if (contraintViolated) {
			deleteEntity(filterInstance, id, msg())
		} else {
			log.info "Entity ${filterInstance.toString()} could not be set to deleted by ${filterInstance.getUpdatedBy()} due to constraint violation"
			flash.message = message(code: 'default.not.deleted.constraint.violation.message', args: [msg(), filterInstance.id])
			redirect(action: "show", id: filterInstance.id)
		}
	}
		
	def filterPaneService

	def filter() {
		render( view:'list',
				model:[ filterInstanceList: filterPaneService.filter( params, Filter),
						filterInstanceTotal: filterPaneService.count( params, Filter ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}
}
