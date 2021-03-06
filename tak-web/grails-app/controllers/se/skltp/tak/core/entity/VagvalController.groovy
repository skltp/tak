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
import org.apache.commons.logging.LogFactory
import se.skltp.tak.web.command.VagvalBulk

class VagvalController extends AbstractCRUDController {
	
	private static final log = LogFactory.getLog(this)
	
    def scaffold = Vagval
	
	def entityLabel = { message(code: 'vagval.label', default: 'Vagval') }

    @Override
    protected String getEntityLabel() {
        return entityLabel()
    }
    @Override
    protected Class getEntityClass() {
        Vagval
    }
    @Override
    protected AbstractVersionInfo createEntity(Map paramsMap) {
        new Vagval(paramsMap)
    }
    @Override
    protected String getModelName() {
        "vagvalInstance"
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
    			model:[ vagvalInstanceList       : filterPaneService.filter( params, Vagval ),
    			        vagvalInstanceTotal      : filterPaneService.count( params, Vagval ),
    			        filterParams             : FilterPaneUtils.extractFilterParams(params),
    			        params                   : params 
                      ] 
              )
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
                if (l == null || l.getDeleted()) {
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
                       StringBuilder errorTexts = new StringBuilder()
                        v.errors.allErrors.each {
                            log.error(it)
                           errorTexts.append(message(code:"${it.code}"))
                        }
                        vb.rejectedLogiskAdress << "${l}, ${v.anropsAdress}, ${v.tjanstekontrakt}, ${v.fromTidpunkt}, ${v.tomTidpunkt} [${errorTexts}]"
                    } else {
                        vb.logiskaAdresser << l
                    }
                }
            }
    
            if (vb.logiskaAdresser.isEmpty()) {
                vb.errors.rejectValue("logiskAdressBulk", "logiskAdressBulk.nomatches")
                log.info('no valid logiskaAdresser')
                flash.message = null
                render (view:'bulkadd', model:[vagvalBulk:vb])
            } else {
                // store vagvalBulk in flash scope for next step (bulksave)
                // this is convenient, but is not compatible with a security timeout (redirect to login)
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
					setMetaData(v, false)
                    def result = v.save(validate:false)
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

    static int maxNum
    def deletelist() {
        final int maxNumber = filterPaneService.count( params, Vagval )
        maxNum = maxNumber
        if(!params.max) params.max = 10
        render( view:'deletelist',
                model:[ vagvalInstanceList       : filterPaneService.filter( params, Vagval ),
                        vagvalInstanceTotal      : filterPaneService.count( params, Vagval ),
                        filterParams             : FilterPaneUtils.extractFilterParams(params),
                        params                   : params
                ] )
    }

    def filterdeletelist() {
        if (filterPaneService.count( params, Vagval ) == maxNum) {
            params.max = 10
        }
        render( view:'deletelist',
                model:[ vagvalInstanceList       : filterPaneService.filter( params, Vagval ),
                        vagvalInstanceTotal      : filterPaneService.count( params, Vagval ),
                        filterParams             : FilterPaneUtils.extractFilterParams(params),
                        params                   : params
                ]
        )
    }

    def bulkDeleteConfirm() {
        def deleteList = params.list('toDelete')
        Closure query = {deleteList.contains(Long.toString(it.id))}
        render( view:'/vagval/bulkdeleteconfirm',
                model: [ vagvalInstanceListDelete       : filterPaneService.filter( params, Vagval ).findAll(query)
                ]
        )
    }

    def bulkDelete() {
        super.bulkDelete()
    }
}
