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

class TjanstekomponentController extends AbstractController {
	
	private static final log = LogFactory.getLog(this)
	
    def scaffold = Tjanstekomponent
	
	//def msg = message(code: 'tjanstekomponent.label', default: 'Tjanstekomponent')
	
	def save() {
		def tjanstekomponentInstance = new Tjanstekomponent(params)
		def msg = message(code: 'tjanstekomponent.label', default: 'Tjanstekomponent')
		saveEntity(tjanstekomponentInstance, msg)
	}
	
	def update(Long id, Long version) {
		def tjanstekomponentInstance = Tjanstekomponent.get(id)
		
		def msg = message(code: 'tjanstekomponent.label', default: 'Tjanstekomponent')
		if (!tjanstekomponentInstance) {
			flash.message = message(code: 'default.not.found.message', args: [msg, id])
			redirect(action: "list")
			return
		}
		tjanstekomponentInstance.properties = params
		updateEntity(tjanstekomponentInstance, version, msg)
	}
	
	def delete(Long id) {
		def tjanstekomponentInstance = Tjanstekomponent.get(id)
		def msg = message(code: 'tjanstekomponent.label', default: 'Tjanstekomponent')
		deleteEntity(tjanstekomponentInstance, id, msg)
	}
	
	def filterPaneService

	def filter() {
		render( view:'list',
				model:[ tjanstekomponentInstanceList: filterPaneService.filter( params, Tjanstekomponent ),
						tjanstekomponentInstanceTotal: filterPaneService.count( params, Tjanstekomponent ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
    }
}
