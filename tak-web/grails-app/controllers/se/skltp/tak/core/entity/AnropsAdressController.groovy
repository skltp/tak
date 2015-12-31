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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException

class AnropsAdressController extends AbstractController {
	
	private static final log = LogFactory.getLog(this)
	
	def scaffold = AnropsAdress
	
	def msg = { message(code: 'anropsAdress.label', default: 'AnropsAdress') }
	
	def save() {
		def anropsAdressInstance = new AnropsAdress(params)			
		saveEntity(anropsAdressInstance, msg())
	}
	
	def update(Long id, Long version) {
		def anropsAdressInstance = AnropsAdress.get(id)	
		
		if (!anropsAdressInstance) {
			flash.message = message(code: 'default.not.found.message', args: [msg(), id])
			redirect(action: "list")
			return
		}
		anropsAdressInstance.properties = params
		updateEntity(anropsAdressInstance, version, msg())
	}
	
	def delete(Long id) {
		def anropsAdressInstance = AnropsAdress.get(id)
		List<AbstractVersionInfo> entityList = new ArrayList<AbstractVersionInfo>();
		addIfNotNull(entityList, anropsAdressInstance?.getVagVal())
		
		boolean contraintViolated = isEntitySetToDeleted(entityList);
		if (contraintViolated) {
			deleteEntity(anropsAdressInstance, id, msg())
		} else {
			log.info "Entity ${anropsAdressInstance.toString()} could not be set to deleted by ${anropsAdressInstance.getUpdatedBy()} due to constraint violation"
			flash.message = message(code: 'default.not.deleted.constraint.violation.message', args: [msg(), anropsAdressInstance.id])
			redirect(action: "show", id: anropsAdressInstance.id)
		}
	}
	
	def filterPaneService
		
	def filter() {
		render( view:'list',
				model:[ anropsAdressInstanceList: filterPaneService.filter( params, AnropsAdress ),
						anropsAdressInstanceTotal: filterPaneService.count( params, AnropsAdress ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}
}
