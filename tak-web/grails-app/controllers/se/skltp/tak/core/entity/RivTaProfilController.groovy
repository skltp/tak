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

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException
import org.apache.shiro.SecurityUtils

import grails.converters.*

import org.apache.commons.logging.LogFactory

class RivTaProfilController {

	private static final log = LogFactory.getLog(this)
	
	def scaffold = RivTaProfil
	/*
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [rivTaProfilInstanceList: RivTaProfil.list(params), rivTaProfilInstanceTotal: RivTaProfil.count()]
    }

    def create() {
        [rivTaProfilInstance: new RivTaProfil(params)]
    }
*/
    def save() {
		
		def rivTaProfilInstance = new RivTaProfil(params)
		setMetaData(rivTaProfilInstance, false)
		
		if (!rivTaProfilInstance.save(flush: true)) {
            render(view: "create", model: [rivTaProfilInstance: rivTaProfilInstance])
            return
        }
        
        log.info "rivTaProfil ${rivTaProfilInstance.toString()} created by ${rivTaProfilInstance.getUpdatedBy()}:"
        log.info "${rivTaProfilInstance as JSON}"
        flash.message = message(code: 'default.created.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), rivTaProfilInstance.id])
        redirect(action: "show", id: rivTaProfilInstance.id)
    }
	
	def update(Long id, Long version) {
		def rivTaProfilInstance = RivTaProfil.get(id)
		if (!rivTaProfilInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), id])
			redirect(action: "list")
			return
		}

		if (version != null) {
			if (rivTaProfilInstance.version > version) {
				rivTaProfilInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
						  [message(code: 'rivTaProfil.label', default: 'RivTaProfil')] as Object[],
						  "Another user has updated this RivTaProfil while you were editing")
				render(view: "edit", model: [rivTaProfilInstance: rivTaProfilInstance])
				return
			}
		}
		rivTaProfilInstance.properties = params
		setMetaData(rivTaProfilInstance, false)
		
		if (!rivTaProfilInstance.save(flush: true)) {
			render(view: "edit", model: [rivTaProfilInstance: rivTaProfilInstance])
			return
		}

		log.info "rivTaProfil ${rivTaProfilInstance.toString()} updated by ${rivTaProfilInstance.getUpdatedBy()}:"
		log.info "${rivTaProfilInstance as JSON}"
		flash.message = message(code: 'default.updated.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), rivTaProfilInstance.id])
		redirect(action: "show", id: rivTaProfilInstance.id)
	}
	
	def delete(Long id) {
		def rivTaProfilInstance = RivTaProfil.get(id)
		
		if (!rivTaProfilInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), id])
			redirect(action: "list")
			return
		}

		try {
			setMetaData(rivTaProfilInstance, true)
			
			rivTaProfilInstance.save(flush: true)
			log.info "rivTaProfil ${rivTaProfilInstance.toString()} was set to deleted by ${rivTaProfilInstance.getUpdatedBy()}:"
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), id])
			redirect(action: "list")
		}
		catch (DataIntegrityViolationException | UncategorizedSQLException e) {
			log.error "rivTaProfil ${rivTaProfilInstance.toString()} could not be deleted by ${principal}:"
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), id])
			redirect(action: "show", id: id)
		}
	}
	
	RivTaProfil setMetaData(def RivTaProfil rivTaProfilInstance, def isDeleted) {
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		rivTaProfilInstance.setUpdatedTime(new Date())
		rivTaProfilInstance.setUpdatedBy(principal)
		rivTaProfilInstance.setDeleted(isDeleted)
	}

/*
    def show(Long id) {
        def rivTaProfilInstance = RivTaProfil.get(id)
        if (!rivTaProfilInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), id])
            redirect(action: "list")
            return
        }

        [rivTaProfilInstance: rivTaProfilInstance]
    }

    def edit(Long id) {
        def rivTaProfilInstance = RivTaProfil.get(id)
        if (!rivTaProfilInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), id])
            redirect(action: "list")
            return
        }

        [rivTaProfilInstance: rivTaProfilInstance]
    }

    
    def delete(Long id) {
        def rivTaProfilInstance = RivTaProfil.get(id)
        def principal = SecurityUtils.getSubject()?.getPrincipal();
		log.info "rivTaProfil ${rivTaProfilInstance.toString()} about to be deleted by ${principal}:"
		log.info "${rivTaProfilInstance as JSON}"
        if (!rivTaProfilInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), id])
            redirect(action: "list")
            return
        }

        try {
            rivTaProfilInstance.delete(flush: true)
	        log.info "rivTaProfil ${rivTaProfilInstance.toString()} was deleted by ${principal}:"
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException | UncategorizedSQLException e) {
			log.error "rivTaProfil ${rivTaProfilInstance.toString()} could not be deleted by ${principal}:"
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'rivTaProfil.label', default: 'RivTaProfil'), id])
            redirect(action: "show", id: id)
        }
    }*/
}
