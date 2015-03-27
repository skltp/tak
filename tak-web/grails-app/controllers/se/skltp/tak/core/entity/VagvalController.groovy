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
import java.security.MessageDigest;

import org.grails.plugin.filterpane.FilterPaneUtils

import se.skltp.tak.web.command.AnropsbehorighetBulk;
import se.skltp.tak.web.command.VagvalBulk
class VagvalController {

    static scaffold = true
	def filterPaneService

	def filter() {
    	render( view:'list',
    			model:[ logiskAdressInstanceList: filterPaneService.filter( params, Vagval ),
    			        logiskAdressInstanceTotal: filterPaneService.count( params, Vagval ),
    			        filterParams: FilterPaneUtils.extractFilterParams(params),
    			        params:params ] )
	}

    def bulkadd() {
        VagvalBulk ab = new VagvalBulk()
        render (view:'bulkadd',
                model:[vagValBulk:ab])
    }
    
    def bulkvalidate(VagvalBulk vb) {
        log.info 'bulkvalidate'
        
        if (vb == null || !vb.logiskAdressBulk) {
            log.debug("bulkvalidate - no command - redirecting to bulkadd (probably user navigation error)")
            redirect(action: 'bulkadd')
        } else {
            vb.rejectedLogiskAdress = []
            vb.logiskAdressBulk.replace(",", " ").trim().split("\\s+").each {
                LogiskAdress l = LogiskAdress.findByHsaId(it.toUpperCase())
                if (l == null) {
                    if (!vb.rejectedLogiskAdress.contains(it)) {
                      vb.rejectedLogiskAdress << it
                    }
                } else if (vb.logiskaAdresser.contains(l)) {
                    vb.rejectedLogiskAdress <<  "${it} [${message(code:'hsaid.existsmorethanonceinimport')}]"
                } else {
                    // Vagval is instantiated only for validation
                    // it is not persisted (that happens in bulksave)
                    // objective is to prevent creating overlapping Vagval
                    Vagval v          = new Vagval()
                    v.anropsAdress    = vb.anropsAdress
                    v.tjanstekontrakt = vb.tjanstekontrakt
                    v.tomTidpunkt     = vb.tomTidpunkt
                    v.fromTidpunkt    = vb.fromTidpunkt
                    v.logiskAdress    = l
                    if (!v.validate()) {
                        log.error("Validation failed for Vagval " + v)
                        v.errors.allErrors.each {
                            log.error(it)
                        }
                        vb.rejectedLogiskAdress << "${it} [${v.errors.allErrors}]"
                    } else {
                        vb.logiskaAdresser << l
                    }
                }
            }
    
            if (vb.logiskaAdresser.isEmpty()) {
                vb.errors.rejectValue("logiskAdressBulk", "logiskAdressBulk.nomatches")
                log.info('no valid logiskaAdresser')
                render (view:'bulkadd', model:[vagvalBulk:vb])
            } else {
                // store vagvalBulk in flash scope for next step (bulksave)
                flash.vb = vb
                
                flash.message = message(code:'vagval.checkclick')
                render (view:'bulkconfirm', model:[vagvalBulk:vb])
            }
        }
    }
    
    def bulksave() {
        log.info 'bulksave'
        VagvalBulk vb = flash.vb
        if (vb == null) {
            log.debug("bulksave - no command in flash scope - redirecting to bulkadd (probably user navigation error)")
            redirect(action: 'bulkadd')
            return
        } else {
            List failedHsaId = []  
            int countSuccess = 0
            int countFailed  = 0
            vb.logiskaAdresser.each  {
                Vagval v          = new Vagval()
                v.anropsAdress    = vb.anropsAdress
                v.tjanstekontrakt = vb.tjanstekontrakt
                v.tomTidpunkt     = vb.tomTidpunkt
                v.fromTidpunkt    = vb.fromTidpunkt
                v.logiskAdress    = it

                if (!v.validate()) {
                    countFailed++
                    log.error("Validation failed for Vagval " + v)
                    v.errors.allErrors.each {
                        log.error(it)
                    }
                    failedHsaId << it.hsaId
                } else {
                    def result = v.save()
                    if (result == null) {
                        countFailed++
                        log.error("Failed to save Vagval " + v)
                        failedHsaId << it.hsaId
                    } else {
                        countSuccess++
                        log.info("Saved Vagval " + v.id)
                    }
                }
            }
            flash.message = message(code:'vagval.created',args:[countSuccess,countFailed])
            if (!failedHsaId.empty) {
                flash.message = flash.message + " ("
                failedHsaId.each {
                    flash.message = flash.message + " " + it
                }
                flash.message = flash.message + ")"
            }
            redirect(action: 'list')
        }
    }
}
