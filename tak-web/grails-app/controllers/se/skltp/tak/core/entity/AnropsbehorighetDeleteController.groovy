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
import org.grails.plugin.filterpane.FilterPaneUtils
import se.skltp.tak.web.command.AnropsbehorighetBulk

class AnropsbehorighetDeleteController extends AbstractCRUDController {
	
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
		render( view:'list',
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
}
