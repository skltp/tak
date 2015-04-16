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

import se.skltp.tak.web.command.AnropsbehorighetBulk

class AnropsbehorighetController {

    def scaffold = Anropsbehorighet

	def filterPaneService

	def filter() {
		render( view:'list',
				model:[ anropsbehorighetInstanceList: filterPaneService.filter( params, Anropsbehorighet ),
						anropsbehorighetInstanceTotal: filterPaneService.count( params, Anropsbehorighet ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}
    
    def bulkadd() {
        log.info 'bulkadd'
        AnropsbehorighetBulk ab = new AnropsbehorighetBulk()
        render (view:'bulkadd',
                model:[anropsbehorighetBulk:ab])
    }
    
    def bulkvalidate(AnropsbehorighetBulk ab) {
        log.info 'bulkvalidate'
        
        ab.validate()

        log.info "params.tjanstekontrakts: ${params.tjanstekontrakts}"
        
        // Problem - unable to bind id to a Tjanstekontrakt (binds to a String instead)
        // Workaround is to manually retrieve Tjanstekontrakt using id
        ab.tjanstekontrakts = [] // array of Strings containing ids
        
        def incomingTjanstekontrakts = []
        if (params != null) {
            if (params.tjanstekontrakts != null) {
                if (params.tjanstekontrkats instanceof Collection) {
                    incomingTjanstekontrakts = params.tjanstekontrakts
                } else if (params.tjanstekontrakts instanceof String) {
                    // each() on String gives each character, which is not what we would want
                    incomingTjanstekontrakts << params.tjanstekontrakts
                }
                incomingTjanstekontrakts.each {
                    Tjanstekontrakt t = Tjanstekontrakt.get(it)
                    if (t == null) {
                        log.warn "Not found: Tjanstekontrakt ${it}"
                    } else {
                        log.info "Found: Tjanstekontrakt ${it} ${t.namnrymd} ${t.beskrivning}"
                        if (!ab.tjanstekontrakts.contains(t)) {
                           ab.tjanstekontrakts << t
                        }
                    }
                }
            }
        }
        if (ab.tjanstekontrakts.empty) {
            ab.errors.rejectValue("tjanstekontrakts", "tjanstekontrakts.missing")
        }
        
        ab.rejectedLogiskAdress = []
        ab.logiskAdressBulk.replace(",", " ").trim().split("\\s+").each {
            LogiskAdress l = LogiskAdress.findByHsaId(it.toUpperCase())
            if (l == null) {
                if (!ab.rejectedLogiskAdress.contains(it)) {
                  ab.rejectedLogiskAdress << it
                }
            } else if (!ab.logiskaAdresser.contains(l)) {
                ab.logiskaAdresser << l
            }
        }

        if (ab.logiskaAdresser.isEmpty()) {
            ab.errors.rejectValue("logiskAdressBulk", "logiskAdressBulk.nomatches")
            log.info('no valid logiskaAdresser')
            render (view:'bulkadd', model:[anropsbehorighetBulk:ab])
        } else {
            // store anropsbehorighetBulk in flash scope for next step (bulksave)
            flash.ab = ab
            
            flash.message = message(code:'anropsbehorighet.checkclick') // "Granska, sedan klicka 'Skapa' för att skapa anropsbehörigheter"
            render (view:'bulkconfirm', model:[anropsbehorighetBulk:ab])
        }
    }
    
    def bulksave() {
        log.info 'bulksave'
        AnropsbehorighetBulk ab = flash.ab
        if (ab == null) {
            log.debug("bulksave - no command in flash scope - redirecting to bulkadd (probably user navigation error)")
            redirect(action: 'bulkadd')
            return
        } else {
            Anropsbehorighet[] allAnropsbehorigheter = Anropsbehorighet.findAll()
            
            int countSuccess = 0
            int countFailed  = 0        
            int countExist   = 0        
            ab.logiskaAdresser.each  { la ->
                ab.tjanstekontrakts.each { tk ->
                    
                    if (false) {
                        // don't understand why where clause is not applied .. ..
                        def queryForExisting = Anropsbehorighet.where {(integrationsavtal == ab.integrationsavtal && fromTidpunkt == ab.fromTidpunkt && tomTidpunkt == ab.tomTidpunkt && tjanstekonsument.id == ab.tjanstekonsument.id && tjanstekontrakt.id == tk.id && logiskAdress.id == la.id)}
                        def existingAnropsbehorigheter = queryForExisting.list()
                        log.warn("Found w " + existingAnropsbehorigheter.size())
                    }
    
                    if (false) {
                        // this doesn't work either
                        def existingAnropsbehorigheter = Anropsbehorighet.findAll {
                            integrationsavtal == ab.integrationsavtal
                            fromTidpunkt      == ab.fromTidpunkt
                            tomTidpunkt       == ab.tomTidpunkt
                            tjanstekonsument  == ab.tjanstekonsument
                            tjanstekontrakt   == tjanstekontrakt
                            logiskAdress      == logiskAdress
                        }
                    }
    
                    Anropsbehorighet[] duplicates = allAnropsbehorigheter.findAll {
                        it.integrationsavtal == ab.integrationsavtal && it.fromTidpunkt == ab.fromTidpunkt && it.tomTidpunkt == ab.tomTidpunkt && it.logiskAdress.id == la.id && it.tjanstekontrakt.id == tk.id && it.tjanstekonsument.id == ab.tjanstekonsument.id   
                    }
                    
                    if (duplicates != null && duplicates.size() > 0) {
                        countExist++
                        log.warn("Found ${duplicates.size()} duplicates")
                        log.warn("Not creating duplicate Anropsbehoriget for tjanstekontrakt:" + tk + ", logiskAdress:" + la)
                    } else {
                        Anropsbehorighet a  = new Anropsbehorighet()
                        a.integrationsavtal = ab.integrationsavtal
                        a.fromTidpunkt      = ab.fromTidpunkt
                        a.tomTidpunkt       = ab.tomTidpunkt
                        a.tjanstekonsument  = ab.tjanstekonsument
                        a.tjanstekontrakt   = tk
                        a.logiskAdress      = la
                        // a.filter         = null
                        if (!a.validate()) {
                            countFailed++
                            log.error("Validation failed for Anropsbehorighet " + a)
                            a.errors.allErrors.each {
                                log.error(it)
                            }
                        } else {
                            def result = a.save()
                            if (result == null) {
                                countFailed++
                                log.error("Failed to save Anropsbehorighet " + a) 
                            } else {
                                countSuccess++
                                log.info("Saved Anropsbehorighet " + a.id)
                            }
                        }
                    }
                }
            }
            
            flash.message = "Skapade ${countSuccess} anropsbehörigheter, ${countExist} fanns redan, ${countFailed} skapades inte"
            redirect(controller:'vagval', action: 'bulkadd')
        }
    }
}
