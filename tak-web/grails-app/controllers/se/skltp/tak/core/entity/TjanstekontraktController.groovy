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

class TjanstekontraktController {

	private static final log = LogFactory.getLog(this)
	
	def scaffold = Tjanstekontrakt
	/*
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [tjanstekontraktInstanceList: Tjanstekontrakt.list(params), tjanstekontraktInstanceTotal: Tjanstekontrakt.count()]
    }

    def create() {
        [tjanstekontraktInstance: new Tjanstekontrakt(params)]
    }*/
	
	def save() {
		
		def tjanstekontraktInstance = new Tjanstekontrakt(params)
		setMetaData(tjanstekontraktInstance, false)
		
		if (!tjanstekontraktInstance.save(flush: true)) {
			render(view: "create", model: [tjanstekontraktInstance: tjanstekontraktInstance])
			return
		}
		
		log.info "tjanstekontrakt ${tjanstekontraktInstance.toString()} created by ${tjanstekontraktInstance.getUpdatedBy()}:"
		log.info "${tjanstekontraktInstance as JSON}"
		flash.message = message(code: 'default.created.message', args: [message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt'), tjanstekontraktInstance.id])
		redirect(action: "show", id: tjanstekontraktInstance.id)		
	}
	
	def update(Long id, Long version) {
		def tjanstekontraktInstance = Tjanstekontrakt.get(id)
		if (!tjanstekontraktInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt'), id])
			redirect(action: "list")
			return
		}

		if (version != null) {
			if (tjanstekontraktInstance.version > version) {
				tjanstekontraktInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
						  [message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt')] as Object[],
						  "Another user has updated this Tjanstekontrakt while you were editing")
				render(view: "edit", model: [tjanstekontraktInstance: tjanstekontraktInstance])
				return
			}
		}

		setMetaData(tjanstekontraktInstance, false)
		
		if (!tjanstekontraktInstance.save(flush: true)) {
			render(view: "edit", model: [tjanstekontraktInstance: tjanstekontraktInstance])
			return
		}

		log.info "tjanstekontrakt ${tjanstekontraktInstance.toString()} updated by ${tjanstekontraktInstance.getUpdatedBy()}:"
		log.info "${tjanstekontraktInstance as JSON}"
		flash.message = message(code: 'default.updated.message', args: [message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt'), tjanstekontraktInstance.id])
		redirect(action: "show", id: tjanstekontraktInstance.id)
	}
	
	def delete(Long id) {
		def tjanstekontraktInstance = Tjanstekontrakt.get(id)
		
		if (!tjanstekontraktInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt'), id])
			redirect(action: "list")
			return
		}

		try {
			setMetaData(tjanstekontraktInstance, true)
			
			tjanstekontraktInstance.save(flush: true)
			log.info "tjanstekontrakt ${tjanstekontraktInstance.toString()} was set to deleted by ${tjanstekontraktInstance.getUpdatedBy()}:"
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt'), id])
			redirect(action: "list")
		}
		catch (DataIntegrityViolationException | UncategorizedSQLException e) {
			log.error "tjanstekontrakt ${tjanstekontraktInstance.toString()} could not be deleted by ${tjanstekontraktInstance.getUpdatedBy()}:"
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt'), id])
			redirect(action: "show", id: id)
		}
		
	}
	
	Tjanstekontrakt setMetaData(def Tjanstekontrakt tjanstekontraktInstance, def isDeleted) {
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		tjanstekontraktInstance.setUpdatedTime(new Date())
		tjanstekontraktInstance.setUpdatedBy(principal)
		tjanstekontraktInstance.setDeleted(isDeleted)
	}

	/*
    def show(Long id) {
        def tjanstekontraktInstance = Tjanstekontrakt.get(id)
        if (!tjanstekontraktInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt'), id])
            redirect(action: "list")
            return
        }

        [tjanstekontraktInstance: tjanstekontraktInstance]
    }

    def edit(Long id) {
        def tjanstekontraktInstance = Tjanstekontrakt.get(id)
        if (!tjanstekontraktInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt'), id])
            redirect(action: "list")
            return
        }

        [tjanstekontraktInstance: tjanstekontraktInstance]
    }*/
    
    
}
