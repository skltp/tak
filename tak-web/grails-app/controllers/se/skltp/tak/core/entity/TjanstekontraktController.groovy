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
import grails.converters.*

import org.apache.commons.logging.LogFactory
import org.apache.shiro.SecurityUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException

class TjanstekontraktController extends AbstractController {

	private static final log = LogFactory.getLog(this)
	
	def scaffold = Tjanstekontrakt
	
	def msg = { message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt') }
	
	def save() {
		def tjanstekontraktInstance = new Tjanstekontrakt(params)
		saveEntity(tjanstekontraktInstance, [tjanstekontraktInstance: tjanstekontraktInstance], msg())
	}
	
	def update(Long id, Long version) {
		def tjanstekontraktInstance = Tjanstekontrakt.get(id)
		
		if (!tjanstekontraktInstance) {
			flash.message = message(code: 'default.not.found.message', args: [msg(), id])
			redirect(action: "list")
			return
		}
		tjanstekontraktInstance.properties = params
		updateEntity(tjanstekontraktInstance, [tjanstekontraktInstance: tjanstekontraktInstance], version, msg())
	}
	
	def delete(Long id) {
		def tjanstekontraktInstance = Tjanstekontrakt.get(id)
		
		List<AbstractVersionInfo> entityList = new ArrayList<AbstractVersionInfo>();
		addIfNotNull(entityList, tjanstekontraktInstance?.getVagval())
		addIfNotNull(entityList, tjanstekontraktInstance?.getAnropsbehorigheter())
		
		boolean contraintViolated = isEntitySetToDeleted(entityList);
		if (contraintViolated) {
			deleteEntity(tjanstekontraktInstance, id, msg())
		} else {
			log.info "Entity ${tjanstekontraktInstance.toString()} could not be set to deleted by ${tjanstekontraktInstance.getUpdatedBy()} due to constraint violation"
			flash.message = message(code: 'default.not.deleted.constraint.violation.message', args: [msg(), tjanstekontraktInstance.id])
			redirect(action: "show", id: tjanstekontraktInstance.id)
		}
	}
		
	def filterPaneService

	def filter() {
		render( view:'list',
				model:[ tjanstekontraktInstanceList: filterPaneService.filter( params, Tjanstekontrakt ),
						tjanstekontraktInstanceTotal: filterPaneService.count( params, Tjanstekontrakt ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}
    
    
}
