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

import org.apache.commons.logging.LogFactory
import org.apache.shiro.SecurityUtils
import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.web.context.request.RequestContextHolder
import se.skltp.tak.web.command.AnropsbehorighetBulk

class AnropsbehorighetController extends AbstractCRUDController {
	
	private static final log = LogFactory.getLog(this)
	
    def scaffold = Anropsbehorighet

    def entityLabel = { message(code: 'anropsbehorighet.label', default: 'Anropsbehorighet') }

    @Override
    protected String getEntityLabel() {
        return entityLabel()
    }
    @Override
    protected Class getEntityClass() {
        Anropsbehorighet
    }
    @Override
    protected AbstractVersionInfo createEntity(Map paramsMap) {
        new Anropsbehorighet(paramsMap)
    }
    @Override
    protected String getModelName() {
        "anropsbehorighetInstance"
    }
    @Override
    protected List<AbstractVersionInfo> getEntityDependencies(AbstractVersionInfo entityInstance) {
        //No dependency no constraints
        []
    }

	def filterPaneService

	def filter() {
        if(!params.max) params.max = 10
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
        
        def incomingTjanstekontrakts = params.list("tjanstekontrakts")
        if (params != null) {
            if (params.tjanstekontrakts != null) {
                log.info "processing ${incomingTjanstekontrakts.size()} tjanstekontrakts ${incomingTjanstekontrakts}"
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
        
        // based on code from VagvalController
        def principal = SecurityUtils.getSubject()?.getPrincipal()
        Closure notDeletedQuery = {
            !it.isDeletedInPublishedVersionOrByUser(principal)
        }
        ab.rejectedLogiskAdress = []
        ab.logiskAdressBulk.replace(",", " ").trim().split("\\s+").each {
            LogiskAdress l = LogiskAdress.findAllByHsaId(it).find(notDeletedQuery)
            if (l == null) {
                if (!ab.rejectedLogiskAdress.contains(it)) {
                    log.info("rejecting ${it} (null)")
                    ab.rejectedLogiskAdress << it
                }
            } else if (l.getDeleted()) {
                log.info("rejecting ${it} (is set to deleted)")
                ab.rejectedLogiskAdress <<  "${it} [${message(code:'hsaid.wassettodeleted')}]"
            } else if (ab.logiskaAdresser.contains(l)) {
                log.info("rejecting ${it} (more than once in import)")
                ab.rejectedLogiskAdress <<  "${it} [${message(code:'hsaid.existsmorethanonceinimport')}]"
            } else {
            
               boolean validation = true
               ab.tjanstekontrakts.each { tkk ->
            
                   // Anropsbehorighet is instantiated only for validation
                   // it is not persisted (that happens in bulksave)
                   // objective is to prevent creating overlapping Anropsbehorighet
                   Anropsbehorighet a = new Anropsbehorighet()
                   a.integrationsavtal = ab.integrationsavtal
                   a.tjanstekonsument  = ab.tjanstekonsument
                   a.tjanstekontrakt   = tkk
                   a.tomTidpunkt       = ab.tomTidpunkt
                   a.fromTidpunkt      = ab.fromTidpunkt
                   a.logiskAdress      = l
                   if (!a.validate()) {
                       validation = false
                       log.info("Validation failed for Anropsbehorighet ${a}")
                       StringBuilder errorTexts = new StringBuilder()
                       a.errors.allErrors.each {
                           log.info(message(code:"${it.code}"))
                           errorTexts.append(message(code:"${it.code}"))
                       }
                       ab.rejectedLogiskAdress << "${l}, ${a.tjanstekonsument}, ${a.tjanstekontrakt}, ${a.fromTidpunkt}, ${a.tomTidpunkt} [${errorTexts}]"
                   }
               } 
               
               if (validation) {
                   log.info("logiskAdress ${l} passed validation")
                   ab.logiskaAdresser << l
               }
            }
        }
        
        if (ab.logiskaAdresser.isEmpty()) {
            ab.errors.rejectValue("logiskAdressBulk", "logiskAdressBulk.nomatches")
            log.info('no valid logiskaAdresser')
            flash.message = null
            render (view:'bulkadd', model:[anropsbehorighetBulk:ab])
        } else {
            // store anropsbehorighetBulk in session scope for next step (bulksave)
            def session = RequestContextHolder.currentRequestAttributes().getSession()
            session.ab = ab
            
            flash.message = message(code:'anropsbehorighet.checkclick') // "Granska, sedan klicka 'Skapa' för att skapa anropsbehörigheter"

            render (view:'bulkconfirm', model:[anropsbehorighetBulk:ab])
        }
    }
    
    def bulksave() {
        log.info 'bulksave anropbehörighet'
        def session = RequestContextHolder.currentRequestAttributes().getSession()
        AnropsbehorighetBulk ab = session.ab
        if (ab == null) {
            log.debug("bulksave - no command in flash scope - redirecting to bulkadd (probably user navigation error)")
            redirect(action: 'bulkadd')
            return
        } else {
            Anropsbehorighet[] allAnropsbehorigheter = Anropsbehorighet.findAllByDeleted(false)
            
            int countSuccess = 0
            int countFailed  = 0        
            int countExist   = 0        
            ab.logiskaAdresser.each  { la ->
                ab.tjanstekontrakts.each { tk ->
                    
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
                               log.error(message(code:"${it.code}"))
                           }
                        } else {
							setMetaData(a, false)
                            def result = a.save(validate:false)
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
            
            flash.message = "Skapade ${countSuccess} anropsbehörigheter, ${countFailed} skapades inte"
            redirect(controller:'vagval', action: 'bulkadd')
        }
    }

    static int maxNum
    def deletelist() {
        final int maxNumber = filterPaneService.count( params, Anropsbehorighet )
        maxNum = maxNumber
        if(!params.max) params.max = 10
        render( view:'deletelist',
                model:[ anropsbehorighetInstanceList: filterPaneService.filter( params, Anropsbehorighet ),
                        anropsbehorighetInstanceTotal: filterPaneService.count( params, Anropsbehorighet ),
                        filterParams: FilterPaneUtils.extractFilterParams(params),
                        params:params ] )
    }

    def filterdeletelist() {
        if (filterPaneService.count( params, Anropsbehorighet ) == maxNum) {
            params.max = 10
        }
        render( view:'deletelist',
                model:[ anropsbehorighetInstanceList: filterPaneService.filter( params, Anropsbehorighet ),
                        anropsbehorighetInstanceTotal: filterPaneService.count( params, Anropsbehorighet ),
                        filterParams: FilterPaneUtils.extractFilterParams(params),
                        params:params ] )
    }

    def bulkDeleteConfirm() {
        def deleteList = params.list('toDelete')
        Closure query = {deleteList.contains(Long.toString(it.id))}
        render( view:'bulkdeleteconfirm',
                model: [ anropsbehorighetInstanceListDelete       : filterPaneService.filter( params, Anropsbehorighet ).findAll(query)
                ]
        )
    }

    def bulkDelete() {
        super.bulkDelete()
    }
}
