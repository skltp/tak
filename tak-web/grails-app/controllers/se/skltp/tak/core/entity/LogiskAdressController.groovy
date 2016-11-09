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

import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.LogFactory

import se.skltp.tak.web.command.LogiskaAdresserBulk

class LogiskAdressController extends AbstractController {
	
	private static final log = LogFactory.getLog(this)

    def scaffold = LogiskAdress
	
	def msg = { message(code: 'logiskAdress.label', default: 'LogiskAdress') } 
	
	def save() {
		def logiskAdressInstance = new LogiskAdress(params)
		saveEntity(logiskAdressInstance, [logiskAdressInstance: logiskAdressInstance], msg())
	}
	
	def update(Long id, Long version) {
		def logiskAdressInstance = LogiskAdress.get(id)
		
		if (!logiskAdressInstance) {
			flash.message = message(code: 'default.not.found.message', args: [msg(), id])
			redirect(action: "list")
			return
		}
		logiskAdressInstance.properties = params
		updateEntity(logiskAdressInstance, [logiskAdressInstance: logiskAdressInstance],  version, msg())
	}
	
	def delete(Long id) {
		def logiskAdressInstance = LogiskAdress.get(id)
		
		List<AbstractVersionInfo> entityList = new ArrayList<AbstractVersionInfo>();
		addIfNotNull(entityList, logiskAdressInstance?.getAnropsbehorigheter())
		addIfNotNull(entityList, logiskAdressInstance?.getVagval())

		boolean contraintViolated = isEntitySetToDeleted(entityList);
		if (contraintViolated) {
			deleteEntity(logiskAdressInstance, id, msg())
		} else {
			log.info "Entity ${logiskAdressInstance.toString()} could not be set to deleted by ${logiskAdressInstance.getUpdatedBy()} due to constraint violation"
			flash.message = message(code: 'default.not.deleted.constraint.violation.message', args: [msg(), logiskAdressInstance.id])
			redirect(action: "show", id: logiskAdressInstance.id)
		}
	}

	def filterPaneService

	def filter() {
		render( view:'list',
				model:[ logiskAdressInstanceList: filterPaneService.filter( params, LogiskAdress ),
						logiskAdressInstanceTotal: filterPaneService.count( params, LogiskAdress ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}
    
    def bulkcreate() {
        render (view:'bulkcreate')
    }

    def bulkcreatevalidate(LogiskaAdresserBulk lb) {
        
        if (!lb.logiskaAdresserBulk) {
            log.debug("bulkcreatevalidate - no input parameter - redirecting to bulkcreate (probably user navigation error)")
            redirect (action:'bulkcreate')
            return
        }

        def lines = lb.logiskaAdresserBulk.split("[\\r\\n]+")
        /*
        SE162321000255-O16560,  (Flexlab) Barnmorskemottagning Skurup
        SE162321000255-O16561,  (Flexlab) Barnmorskemottagning Ystad
        SE2321000016-12ZX, Solna Ungdomsmottagning
        .. ..
        */
        
        
        int missingHsaid       = 0
        int missingComma       = 0
        int missingDescription = 0
        int alreadyExists      = 0
        int duplicates         = 0
        
        lines.each {
            if (it) {
                def line = it.split(",",2)
                if (line.size() != 2) {
                    lb.rejectedLines << it + " [${message(code:'missing.comma')}]"
                    missingComma++
                } else {
                    line[0] = line[0].trim().toUpperCase() // hsa id uppercase
                    line[1] = line[1].trim()
                    
                    if (!line[0]) {
                        lb.rejectedLines << it + " [${message(code:'missing.hsaid')}]"
                        missingHsaid++
                    } else if (!line[1]) {
                        lb.rejectedLines << it + " [${message(code:'missing.description')}]"
                        missingDescription++
                    } else {
                        def existingHsaIds = LogiskAdress.findAllByHsaIdIlike(line[0])
                        if (existingHsaIds != null && !existingHsaIds.isEmpty()) {
                            lb.rejectedLines << it + " [${message(code:'hsaid.alreadyexists')}] ${existingHsaIds.get(0).beskrivning}]"
                            alreadyExists++
                        } else if (lb.acceptedLines.containsKey(line[0])) {
                            lb.rejectedLines << it + " [${message(code:'hsaid.existsmorethanonceinimport')}]"
                            duplicates++
                        } else {
                            lb.acceptedLines.put(line[0], line[1])
                        }
                    }
                }
            }
        }

        if (lb.acceptedLines.isEmpty()) {
            lb.rejectedLines.each {
                log.info(it)
            }
            flash.message = message(code:'logiskAdress.novalidimportlines', args:[missingHsaid, missingComma, missingDescription, alreadyExists, duplicates]) 
            render (view:'bulkcreate', model:[logiskaAdresserBulk:lb.logiskaAdresserBulk])
        } else {
            // store LogiskaAdresserBulk in flash scope for next step (bulksave)
            flash.lb = lb
            if (missingHsaid > 0 || missingComma > 0 || missingDescription > 0 || alreadyExists > 0 || duplicates > 0) {
                flash.message = message(code:'logiskAdress.clickcreatenewwitherrors', args:[missingHsaid, missingComma, missingDescription, alreadyExists, duplicates])
            } else {
                flash.message = message(code:'logiskAdress.clickcreatenewnoerrors')
            }
            render (view:'bulkcreateconfirm', model:[logiskaAdresserBulk:lb])
        }
    }

    def bulksave() {
        log.info 'bulksave'
        LogiskaAdresserBulk lb = flash.lb
        if (lb == null || lb.acceptedLines.empty) {
            log.debug("bulksave - no command in flash scope - redirecting to bulkcreate (probably user navigation error)")
            redirect(action: 'bulkcreate')
        } else {
            
            int countSuccess = 0
            int countFailed  = 0
            int countExist   = 0
            
            lb.acceptedLines.each  { line ->
                def existingHsaId = LogiskAdress.findAllByHsaId(line.key)
                if (existingHsaId != null && !existingHsaId.isEmpty()) {
                    countExist++
                    log.warn("Not creating duplicate LogiskAdress HSA id ${line.key}")
                } else {
                    LogiskAdress l = new LogiskAdress()
                    l.hsaId = line.key
                    l.beskrivning = line.value
					setMetaData(l, false)
                    def result = l.save()
                    if (result == null) {
                        countFailed++
                        log.error("Failed to save LogiskAdress hsa id ${line.key}")
                    } else {
                        countSuccess++
                        log.info("Saved LogiskAdress ${l.id} (${l.hsaId})")
                    }
                }
            }
            
            flash.message = message(code:'createdlogicaladdresses',args:[countSuccess,countExist,countFailed])
            redirect(controller:'anropsbehorighet', action: 'bulkadd')
        }
    }

    def bulkdelete() {
        render (view:'bulkdelete')
    }

    def bulkdeletevalidate(LogiskaAdresserBulk lb) {

        if (!lb.logiskaAdresserBulk) {
            log.debug("bulkdeletevalidate - no input parameter - redirecting to bulkdelete (probably user navigation error)")
            redirect (action:'bulkdelete')
            return
        }

        def lines = lb.logiskaAdresserBulk.split("[\\r\\n]+")
        /*
        SE162321000255-O16560
        SE162321000255-O16561
        SE2321000016-12ZX
        .. ..
        */


        int missingHsaid       = 0
        int notExist           = 0
        int duplicates         = 0

        lines.each {
            if (it) {
                it = it.trim().toUpperCase() // hsa id uppercase

                if (!it) {
                    lb.rejectedLines << it + " [${message(code:'missing.hsaid')}]"
                    missingHsaid++
                } else {
                    def notExistingHsaIds = LogiskAdress.findAllByHsaIdIlike(it)
                    if (notExistingHsaIds == null || notExistingHsaIds.isEmpty()) {
                        lb.rejectedLines << it + " [${message(code:'hsaid.notexists')}]]"
                        notExist++
                    } else if (lb.acceptedLines.containsKey(it)) {
                        lb.rejectedLines << it + " [${message(code:'hsaid.existsmorethanonceinbulkdelete')}]"
                        duplicates++
                    } else {
                        lb.acceptedLines.put(it, it)
                    }
                }
            }
        }

        if (lb.acceptedLines.isEmpty()) {
            lb.rejectedLines.each {
                log.info(it)
            }
            flash.message = message(code:'logiskAdress.novaliddeletelines', args:[missingHsaid, notExist, duplicates])
            render (view:'bulkdelete', model:[logiskaAdresserBulk:lb.logiskaAdresserBulk])
        } else {
            // store LogiskaAdresserBulk in flash scope for next step (bulkdeleteexecute)
            flash.lb = lb
            if (missingHsaid > 0 || notExist > 0 || duplicates > 0) {
                flash.message = message(code: 'logiskAdress.clickbulkdeletewitherrors', args: [missingHsaid, notExist, duplicates])
            } else {
                flash.message = message(code: 'logiskAdress.clickbulkdeletenoerrors')
            }
            render (view:'bulkdeleteconfirm', model:[logiskaAdresserBulk:lb])
        }
    }

    def bulkdeleteexecute() {
        log.info 'bulkdeleteexecute'
        LogiskaAdresserBulk lb = flash.lb
        if (lb == null || lb.acceptedLines.empty) {
            log.debug("bulkdeleteexecute - no command in flash scope - redirecting to bulkdelete (probably user navigation error)")
            redirect(action: 'bulkdelete')
        } else {

            int countSuccess    = 0
            int countNotExist   = 0

            lb.acceptedLines.each  { line ->
                def existingHsaId = LogiskAdress.findByHsaId(line.key)
                if (existingHsaId != null) {
                    setMetaData(existingHsaId, true)
                    def result = existingHsaId.save()
                    if (result == null) {
                        countFailed++
                        log.error("Failed to delete LogiskAdress hsa id ${line.key}")
                    } else {
                        countSuccess++
                        log.info("Makr 'deleted' LogiskAdress ${existingHsaId.id} (${existingHsaId.hsaId})")
                    }
                } else {
                    countNotExist--
                    log.info("LogiskAdress does not exist ${existingHsaId.id} (${existingHsaId.hsaId})")
                }
            }

            flash.message = message(code:'deletedlogicaladdresses',args:[countSuccess,countNotExist])
            redirect(action: 'list')
        }
    }
}
