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

import org.codehaus.groovy.grails.io.support.IOUtils

import grails.converters.*

import org.apache.commons.logging.LogFactory

class PubVersionController {

	private static final log = LogFactory.getLog(this)
	
	def scaffold = PubVersion
	
	def create() {
		log.info "${params as JSON}"
		//log.info "${params as JSON}"
		def rivTaProfilList = RivTaProfil.findAllByUpdatedTimeIsNotNull()
		def anropsAdressList = AnropsAdress.findAllByUpdatedTimeIsNotNull()
		def anropsbehorighetList = Anropsbehorighet.findAllByUpdatedTimeIsNotNull()
		def filtercategorizationList = Filtercategorization.findAllByUpdatedTimeIsNotNull()
		def filterList = Filter.findAllByUpdatedTimeIsNotNull()
		def logiskAdressList = LogiskAdress.findAllByUpdatedTimeIsNotNull()
		def tjanstekomponentList = Tjanstekomponent.findAllByUpdatedTimeIsNotNull()
		def tjanstekontraktList = Tjanstekontrakt.findAllByUpdatedTimeIsNotNull()
		def vagvalList = Vagval.findAllByUpdatedTimeIsNotNull()
		
		log.info "${rivTaProfilList as JSON}"
		
		
		[pubVersionInstance: new PubVersion(params), 
			rivTaProfilList: rivTaProfilList, anropsAdressList: anropsAdressList, anropsbehorighetList: anropsbehorighetList,
			filtercategorizationList: filtercategorizationList, filterList: filterList, logiskAdressList: logiskAdressList,
			tjanstekomponentList: tjanstekomponentList, tjanstekontraktList: tjanstekontraktList, vagvalList: vagvalList]
	}
	
	def download(Long id) {
		def pubVersionInstance = PubVersion.get(id)
		
		if (!pubVersionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), id])
			redirect(action: "list")
			return
		}
		
		downloadFile(pubVersionInstance.getData(), "PubVersion-" + id);
	}
	
	
	def downloadFile(def file, def fileName) {
		InputStream contentStream
		try {
			response.setHeader "Content-disposition", "attachment; filename=" + fileName + ".gzip"
			response.setHeader("Content-Length", String.valueOf(file.length()))
			response.setContentType("application/x-gzip")
			contentStream = file.getBinaryStream()
			response.outputStream << contentStream			
		} finally {
			if (contentStream) {
				contentStream.close();
			}
		}		
		webRequest.renderView = false
	}
	
	/*
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [pubVersionInstanceList: PubVersion.list(params), pubVersionInstanceTotal: PubVersion.count()]
    }

    def create() {
        [pubVersionInstance: new PubVersion(params)]
    }

    def save() {
        def pubVersionInstance = new PubVersion(params)
        if (!pubVersionInstance.save(flush: true)) {
            render(view: "create", model: [pubVersionInstance: pubVersionInstance])
            return
        }

        def principal = SecurityUtils.getSubject()?.getPrincipal();
        log.info "pubVersion ${pubVersionInstance.toString()} created by ${principal}:"
        log.info "${pubVersionInstance as JSON}"
        flash.message = message(code: 'default.created.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), pubVersionInstance.id])
        redirect(action: "show", id: pubVersionInstance.id)
    }

    def show(Long id) {
        def pubVersionInstance = PubVersion.get(id)
        if (!pubVersionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), id])
            redirect(action: "list")
            return
        }

        [pubVersionInstance: pubVersionInstance]
    }

    def edit(Long id) {
        def pubVersionInstance = PubVersion.get(id)
        if (!pubVersionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), id])
            redirect(action: "list")
            return
        }

        [pubVersionInstance: pubVersionInstance]
    }

    def update(Long id, Long version) {
        def pubVersionInstance = PubVersion.get(id)
        if (!pubVersionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (pubVersionInstance.version > version) {
                pubVersionInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'pubVersion.label', default: 'PubVersion')] as Object[],
                          "Another user has updated this PubVersion while you were editing")
                render(view: "edit", model: [pubVersionInstance: pubVersionInstance])
                return
            }
        }

        pubVersionInstance.properties = params

        if (!pubVersionInstance.save(flush: true)) {
            render(view: "edit", model: [pubVersionInstance: pubVersionInstance])
            return
        }

        def principal = SecurityUtils.getSubject()?.getPrincipal();
        log.info "pubVersion ${pubVersionInstance.toString()} updated by ${principal}:"
        log.info "${pubVersionInstance as JSON}"
        flash.message = message(code: 'default.updated.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), pubVersionInstance.id])
        redirect(action: "show", id: pubVersionInstance.id)
    }

    def delete(Long id) {
        def pubVersionInstance = PubVersion.get(id)
        def principal = SecurityUtils.getSubject()?.getPrincipal();
		log.info "pubVersion ${pubVersionInstance.toString()} about to be deleted by ${principal}:"
		log.info "${pubVersionInstance as JSON}"
        if (!pubVersionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), id])
            redirect(action: "list")
            return
        }

        try {
            pubVersionInstance.delete(flush: true)
	        log.info "pubVersion ${pubVersionInstance.toString()} was deleted by ${principal}:"
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException | UncategorizedSQLException e) {
			log.error "pubVersion ${pubVersionInstance.toString()} could not be deleted by ${principal}:"
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), id])
            redirect(action: "show", id: id)
        }
    }*/
}
