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

class AnropsAdressController extends AbstractCRUDController {
	
	private static final log = LogFactory.getLog(this)
	
	def scaffold = AnropsAdress

	def entityLabel = { message(code: 'anropsAdress.label', default: 'AnropsAdress') }
	def entityLabelVagval = { message(code: 'vagval.label', default: 'Vägval') }

	@Override
	protected String getEntityLabel() {
		return entityLabel()
	}
	@Override
	protected Class getEntityClass() {
		AnropsAdress
	}
	@Override
	protected AbstractVersionInfo createEntity(Map paramsMap) {
		new AnropsAdress(paramsMap)
	}
	@Override
	protected String getModelName() {
		"anropsAdressInstance"
	}
	@Override
	protected List<AbstractVersionInfo> getEntityDependencies(AbstractVersionInfo entityInstance) {
		List<AbstractVersionInfo> entityList = []
		if(entityInstance instanceof AnropsAdress) {
			addIfNotNull(entityList, entityInstance.getVagVal())
		}
		entityList
	}

	def filterPaneService
		
	def filter() {
		if(!params.max) params.max = 10
		render( view:'list',
				model:[ anropsAdressInstanceList: filterPaneService.filter( params, AnropsAdress ),
						anropsAdressInstanceTotal: filterPaneService.count( params, AnropsAdress ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}

	static int maxNum
	def deletelist() {
		final int maxNumber = filterPaneService.count( params, AnropsAdress )
		maxNum = maxNumber
		if(!params.max) params.max = 10
		render( view:'deletelist',
				model:[ anropsAdressInstanceList: filterPaneService.filter( params, AnropsAdress ),
						anropsAdressInstanceTotal: filterPaneService.count( params, AnropsAdress ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}

    def filterdeletelist() {
		if (filterPaneService.count( params, AnropsAdress ) == maxNum) {
			params.max = 10
		}
        render( view:'deletelist',
                model:[ anropsAdressInstanceList: filterPaneService.filter( params, AnropsAdress ),
                        anropsAdressInstanceTotal: filterPaneService.count( params, AnropsAdress ),
                        filterParams: FilterPaneUtils.extractFilterParams(params),
                        params:params ] )
    }

	def bulkDeleteConfirm() {
		def deleteList = params.list('toDelete')
		Closure query = { deleteList.contains(Long.toString(it.id)) }
		Closure queryVagval = { deleteList.contains(Long.toString(it.anropsAdress.id)) }
		render(view: '/anropsAdress/bulkdeleteconfirm',
				model: [anropsAdressInstanceListDelete: filterPaneService.filter(params, AnropsAdress).findAll(query),
						vagvalInstanceListDelete      : filterPaneService.filter(params, Vagval).findAll(queryVagval)
				]
		)
	}

	def bulkDelete() {
		super.bulkDelete()
	}
}
