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

import grails.converters.*
import grails.validation.Validateable

import java.sql.Date

import org.apache.commons.logging.LogFactory
import org.apache.shiro.SecurityUtils

class PubVersionController {

	private static final log = LogFactory.getLog(this)
	
	def currentFormatVersion = 1
	
	def publishService
	
	def scaffold = PubVersion
	
	def msg = { message(code: 'pubVersion.publish', default: 'x_Publish') }
	
	def msgReferenceError = { message(code: 'pubVersion.references.error', default: 'x_Reference error') }
	
	def labelFilter = { message(code: 'default.filter.label', default: 'x_Filter') }
	def labelVagval = { message(code: 'default.vagval.label', default: 'x_Vagval') }
	def labelRivTaProfil = { message(code: 'default.rivTaProfil.label', default: 'x_RivTaProfil') }
	def labelLogiskAdress = { message(code: 'default.logiskAdress.label', default: 'x_LogiskAdress') }
	def labelAnropsAdress = { message(code: 'default.anropsadress.label', default: 'x_AnropsAdress') }
	def labelTjanstekontrakt = { message(code: 'default.tjanstekontrakt.label', default: 'x_Tjanstekontrakt') }
	def labelTjanstekomponent = { message(code: 'default.tjanstekomponent.label', default: 'x_Tjanstekomponent') }
	def labelAnropsbehorighet = { message(code: 'default.anropsbehorighet.label', default: 'x_Anropsbehorighet') }
	def labelFiltercategorization = { message(code: 'default.filtercategorization.label', default: 'x_Filtercategorization') }
	
	
	def show(Long id) {
		
		def pubVersionInstance = PubVersion.get(id)
		if (!pubVersionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), id])
			redirect(action: "list")
			return
		}
		
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		
		def rivTaProfilList = RivTaProfil.findAllByPubVersion(pubVersionInstance.id)
		def anropsAdressList = AnropsAdress.findAllByPubVersion(pubVersionInstance.id)
		def anropsbehorighetList = Anropsbehorighet.findAllByPubVersion(pubVersionInstance.id)
		def filtercategorizationList = Filtercategorization.findAllByPubVersion(pubVersionInstance.id)
		def filterList = Filter.findAllByPubVersion(pubVersionInstance.id)
		def logiskAdressList = LogiskAdress.findAllByPubVersion(pubVersionInstance.id)
		def tjanstekomponentList = Tjanstekomponent.findAllByPubVersion(pubVersionInstance.id)
		def tjanstekontraktList = Tjanstekontrakt.findAllByPubVersion(pubVersionInstance.id)
		def vagvalList = Vagval.findAllByPubVersion(pubVersionInstance.id)
		
		List<AbstractVersionInfo> entityList = new ArrayList<AbstractVersionInfo>();
		entityList.addAll(rivTaProfilList);
		entityList.addAll(anropsAdressList);
		entityList.addAll(anropsbehorighetList);
		entityList.addAll(filtercategorizationList);
		entityList.addAll(filterList);
		entityList.addAll(logiskAdressList);
		entityList.addAll(tjanstekomponentList);
		entityList.addAll(tjanstekontraktList);
		entityList.addAll(vagvalList);
		
		[pubVersionInstance: pubVersionInstance, 
			rivTaProfilList: rivTaProfilList, anropsAdressList: anropsAdressList, anropsbehorighetList: anropsbehorighetList,
				filtercategorizationList: filtercategorizationList, filterList: filterList, logiskAdressList: logiskAdressList,
					tjanstekomponentList: tjanstekomponentList, tjanstekontraktList: tjanstekontraktList, vagvalList: vagvalList,
					 	currentUser: principal]
	}
	
	def create() {
		log.info "${params as JSON}"
		
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		
		def rivTaProfilList = RivTaProfil.findAllByUpdatedByIsNotNull()
		def anropsAdressList = AnropsAdress.findAllByUpdatedByIsNotNull()
		def anropsbehorighetList = Anropsbehorighet.findAllByUpdatedByIsNotNull()
		def filtercategorizationList = Filtercategorization.findAllByUpdatedByIsNotNull()
		def filterList = Filter.findAllByUpdatedByIsNotNull()
		def logiskAdressList = LogiskAdress.findAllByUpdatedByIsNotNull()
		def tjanstekomponentList = Tjanstekomponent.findAllByUpdatedByIsNotNull()
		def tjanstekontraktList = Tjanstekontrakt.findAllByUpdatedByIsNotNull()
		def vagvalList = Vagval.findAllByUpdatedByIsNotNull()
		
		def enablePublish = false;
		
		List<AbstractVersionInfo> entityList = new ArrayList<AbstractVersionInfo>();
		entityList.addAll(rivTaProfilList);
		entityList.addAll(anropsAdressList);
		entityList.addAll(anropsbehorighetList);
		entityList.addAll(filtercategorizationList);
		entityList.addAll(filterList);
		entityList.addAll(logiskAdressList);
		entityList.addAll(tjanstekomponentList);
		entityList.addAll(tjanstekontraktList);
		entityList.addAll(vagvalList);
		
		
		
		entityList.each { entity ->
			if (entity.getUpdatedBy().equalsIgnoreCase(principal.toString())) {
				enablePublish = true
				return enablePublish
			}
		}
		
		try {
			checkAnropsAdressReferences(anropsAdressList);
			checkVagvalReferences(vagvalList);
			checkAnropsbehorighetReferences(anropsbehorighetList);
			checkFiltercategorizationReferences(filtercategorizationList);
			
		} catch (IllegalStateException isE) {
			enablePublish = false;
		}

		log.info "Enable publish: ${enablePublish}"
				
		[pubVersionInstance: new PubVersion(params), 
			rivTaProfilList: rivTaProfilList, anropsAdressList: anropsAdressList, anropsbehorighetList: anropsbehorighetList,
				filtercategorizationList: filtercategorizationList, filterList: filterList, logiskAdressList: logiskAdressList,
					tjanstekomponentList: tjanstekomponentList, tjanstekontraktList: tjanstekontraktList, vagvalList: vagvalList, 
						enablePublish: enablePublish, currentUser: principal]
	}
	/*
	def checkRivTaProfilReferences(List<RivTaProfil> rivTaProfilList) {
		for (RivTaProfil rivTaProfil : rivTaProfilList) {
			def rivProfilCreatedBy = rivTaProfil.getUpdatedBy();
			for (AnropsAdress anropsAdress : rivTaProfil.getAnropsAdresser()) {
				throwIllegalStateIfBeingUsedByAnotherUser(rivProfilCreatedBy, anropsAdress.getUpdatedBy())
			}
		}
	}*/
	
	private void checkAnropsAdressReferences(List<AnropsAdress> anropsAdressList) {
		for (AnropsAdress anropsAdress : anropsAdressList) {
			def anropsAdressCreatedBy = anropsAdress.getUpdatedBy();
			
			for (Tjanstekomponent tjanstekomponent : anropsAdress.getTjanstekomponent()) {
				if (checkIfEntityIsBeingReferredByAnotherUser(anropsAdressCreatedBy, tjanstekomponent.getUpdatedBy())) {
					def composedMsg = [labelAnropsAdress(), anropsAdress.getAdress(),
						anropsAdressCreatedBy, labelTjanstekomponent(), tjanstekomponent.getHsaId(), tjanstekomponent.getUpdatedBy()]
					throwIllegalStateIfBeingUsedByAnotherUser(composedMsg)
				}
			}
			
			for (RivTaProfil rivTaProfil : anropsAdress.getRivTaProfil()) {
				if (checkIfEntityIsBeingReferredByAnotherUser(anropsAdressCreatedBy, rivTaProfil.getUpdatedBy())) {
					def composedMsg = [labelAnropsAdress(), anropsAdress.getAdress(),
						anropsAdressCreatedBy, labelRivTaProfil(), rivTaProfil.getNamn(), rivTaProfil.getUpdatedBy()]
					throwIllegalStateIfBeingUsedByAnotherUser(composedMsg)
				}
			}
		}
	}
	
	private void checkVagvalReferences(List<Vagval> vagvalList) {
		for (Vagval vagval : vagvalList) {
			def vagvalCreatedBy = vagval.getUpdatedBy();
			
			for (Tjanstekontrakt tjanstekontrakt : vagval.getTjanstekontrakt()) {
				if (checkIfEntityIsBeingReferredByAnotherUser(vagvalCreatedBy, tjanstekontrakt.getUpdatedBy())) {
					def composedMsg = [labelVagval(), vagval.getId(),
						vagvalCreatedBy, labelTjanstekontrakt(), tjanstekontrakt.getNamnrymd(), tjanstekontrakt.getUpdatedBy()]
					throwIllegalStateIfBeingUsedByAnotherUser(composedMsg)
				}
			}
			
			for (LogiskAdress logiskAdress : vagval.getLogiskAdress()) {
				if (checkIfEntityIsBeingReferredByAnotherUser(vagvalCreatedBy, logiskAdress.getUpdatedBy())) {
					def composedMsg = [labelVagval(), vagval.getId(),
						vagvalCreatedBy, labelLogiskAdress(), logiskAdress.getHsaId(), logiskAdress.getUpdatedBy()]
					throwIllegalStateIfBeingUsedByAnotherUser(composedMsg)
				}
			}
			
			for (AnropsAdress anropsAdress : vagval.getAnropsAdress()) {
				if (checkIfEntityIsBeingReferredByAnotherUser(vagvalCreatedBy, anropsAdress.getUpdatedBy())) {
					def composedMsg = [labelVagval(), vagval.getId(),
						vagvalCreatedBy, labelAnropsAdress(), anropsAdress.getAdress(), anropsAdress.getUpdatedBy()]
					throwIllegalStateIfBeingUsedByAnotherUser(composedMsg)
				}
			}
		}
	}
	
	private void checkAnropsbehorighetReferences(List<Anropsbehorighet> anropsbehorighetList) {
		for (Anropsbehorighet anropsbehorighet : anropsbehorighetList) {
			def anropsbehorighetCreatedBy = anropsbehorighet.getUpdatedBy();
			
			for (Tjanstekontrakt tjanstekontrakt : anropsbehorighet.getTjanstekontrakt()) {
				if (checkIfEntityIsBeingReferredByAnotherUser(anropsbehorighetCreatedBy, tjanstekontrakt.getUpdatedBy())) {
					def composedMsg = [labelAnropsbehorighet(), anropsbehorighet.getIntegrationsavtal(),
						anropsbehorighetCreatedBy, labelTjanstekontrakt(), tjanstekontrakt.getNamnrymd(), tjanstekontrakt.getUpdatedBy()]
					throwIllegalStateIfBeingUsedByAnotherUser(composedMsg)
				}
			}
			
			for (LogiskAdress logiskAdress : anropsbehorighet.getLogiskAdress()) {
				if (checkIfEntityIsBeingReferredByAnotherUser(anropsbehorighetCreatedBy, logiskAdress.getUpdatedBy())) {
					def composedMsg = [labelAnropsbehorighet(), anropsbehorighet.getIntegrationsavtal(),
						anropsbehorighetCreatedBy, labelLogiskAdress(), logiskAdress.getHsaId(), logiskAdress.getUpdatedBy()]
					throwIllegalStateIfBeingUsedByAnotherUser(composedMsg)
				}
			}
			
			for (Tjanstekomponent tjanstekomponent : anropsbehorighet.getTjanstekonsument()) {
				if (checkIfEntityIsBeingReferredByAnotherUser(anropsbehorighetCreatedBy, tjanstekomponent.getUpdatedBy())) {
					def composedMsg = [labelAnropsbehorighet(), anropsbehorighet.getIntegrationsavtal(),
						anropsbehorighetCreatedBy, labelTjanstekomponent(), tjanstekomponent.getHsaId(), tjanstekomponent.getUpdatedBy()]
					throwIllegalStateIfBeingUsedByAnotherUser(composedMsg)
				}
			}
		}
	}
	
	private void checkFiltercategorizationReferences(List<Filtercategorization> filtercategorizationList) {
		for (Filtercategorization filtercategorization : filtercategorizationList) {
			def filtercategorizationCreatedBy = filtercategorization.getUpdatedBy();
			
			for (Filter filter : filtercategorization.getFilter()) {
				if (checkIfEntityIsBeingReferredByAnotherUser(filtercategorizationCreatedBy, filter.getUpdatedBy())) {
					def composedMsg = [labelFiltercategorization(), filtercategorization.getCategory(),
						filtercategorizationCreatedBy, labelFilter(), filter.getServicedomain(), filter.getUpdatedBy()]
					throwIllegalStateIfBeingUsedByAnotherUser(composedMsg)
				}
			}
		}
	}
	/*
	private void throwIllegalStateIfBeingUsedByAnotherUser(String createdBy, String usedBy, String msgCode) {
		if (!(createdBy?.equalsIgnoreCase(usedBy))) {
			String detailMsg = createdBy + " används av " + usedBy;
			flash.message = message(code: msgCode, args: [detailMsg])
			throw new IllegalStateException ("Cannot be published until referred items are published first. Check UpdatedBy")
		}
	}*/
	
	private void throwIllegalStateIfBeingUsedByAnotherUser(def composedMsg) {
		def errorMsg = { message(code: 'pubVersion.references.error', args: composedMsg) };
		def errorMsgStr = errorMsg();
		flash.error = errorMsgStr;
		log.error errorMsgStr;
		throw new IllegalStateException ("Cannot be published until referred items are published first. Details: " + errorMsgStr)
	}
	
	private boolean checkIfEntityIsBeingReferredByAnotherUser(String createdBy, String usedBy) {
		return (usedBy != null && !(createdBy?.equalsIgnoreCase(usedBy)));
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
	
	def save() {
		def pubVersionOldInstance = publishService.beforePublish()
		
		def principal = SecurityUtils.getSubject()?.getPrincipal();
		
		def pubVersionInstance = new PubVersion(params)
		
		try {
			pubVersionInstance.withTransaction {
		
				pubVersionInstance.setFormatVersion(currentFormatVersion)
				pubVersionInstance.setUtforare(principal)
				pubVersionInstance.setTime(new Date(System.currentTimeMillis()))
				
				if (!pubVersionInstance.save(flush: true)) {
					render(view: "create", model: [pubVersionInstance: pubVersionInstance])
					return
				}
				
				// Create a new complete pubVersion
				publishService.doPublish(pubVersionOldInstance, pubVersionInstance)
				
				log.info "pubVersion ${pubVersionInstance.toString()} created by ${principal}:"
				log.info "${pubVersionInstance as JSON}"
				flash.message = message(code: 'default.created.message', args: [message(code: 'pubVersion.label', default: 'PubVersion'), pubVersionInstance.id])
				redirect(action: "show", id: pubVersionInstance.id)
			}
		} catch (Exception e) {
			log.error "pubVersion ${pubVersionInstance.toString()} failed to be created by ${principal} due to " + e
			log.error "${pubVersionInstance as JSON}"
			flash.message = message(code: 'pubVersion.create.error', args: [message(code: 'pubVersion.label', default: 'PubVersion')])
			redirect(action: "create", model: [pubVersionInstance: pubVersionInstance])
			return
		}
	}
	
	def delete(Long id) {
		def pubVersionInstance = PubVersion.get(id)
		
		log.info "Entity ${pubVersionInstance.toString()} cannot be set to deleted or deleted by any user"
		flash.message = message(code: 'default.not.deleted.message', args: [msg(), pubVersionInstance.id])
		redirect(action: "show", id: pubVersionInstance.id)
	}
	
	def list(Integer max) {
		params.max = Math.min(max ?: 10, 100)
		params.sort = params.sort ?: "id"
		
		params.order = params.order ?: "desc"
		List list = PubVersion.list(params)
		//Workaround: defaultOrder on sortableColumn is not working so sorting on id and showing most latest
		[pubVersionInstanceList: list, pubVersionInstanceTotal: PubVersion.count()]
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
