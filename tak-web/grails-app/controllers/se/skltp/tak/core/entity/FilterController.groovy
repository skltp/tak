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

class FilterController extends AbstractCRUDController {
	def daoService

	private static final log = LogFactory.getLog(this)

    def scaffold = Filter
	
	def entityLabel = { message(code: 'filter.label', default: 'Filter') }

	@Override
	protected String getEntityLabel() {
		return entityLabel()
	}
	@Override
	protected Class getEntityClass() {
		Filter
	}
	@Override
	protected AbstractVersionInfo createEntity(Map paramsMap) {
		List<Anropsbehorighet> behorighet = daoService.getAktuellaAnropsbehorigheter(
				paramsMap.get("logiskAdress.id"), paramsMap.get("tjanstekomponent.id"), paramsMap.get("tjanstekontrakt.id"))

		LogiskAdress la = LogiskAdress.get(paramsMap.get("logiskAdress.id"))
		Tjanstekomponent tkomp = Tjanstekomponent.get(paramsMap.get("tjanstekomponent.id"))
		Tjanstekontrakt tk = Tjanstekontrakt.get(paramsMap.get("tjanstekontrakt.id"))

		if(behorighet.size() == 0){
			flash.error = message(code: 'filter.anropbehörighet.notexists',
					args: [la.hsaId, tkomp.hsaId, tk.namnrymd])
			Filter filterInstance = new Filter()
			filterInstance.servicedomain = paramsMap.get("servicedomain")
			render(view: "create",  model:["filterInstance": filterInstance])
			return
		}
		if(behorighet.size() > 1){
			flash.error = message(code: 'filter.anropbehörighet.dubblett',
					args: [la.hsaId, tkomp.hsaId, tk.namnrymd])
			Filter filterInstance = new Filter()
			filterInstance.servicedomain = paramsMap.get("servicedomain")
			render(view: 'create')
			return
		}

		paramsMap.put("anropsbehorighet.id", behorighet.get(0).id)

		return new Filter(paramsMap)
	}

	@Override
	protected String getModelName() {
		"filterInstance"
	}
	@Override
	protected List<AbstractVersionInfo> getEntityDependencies(AbstractVersionInfo entityInstance) {
		List<AbstractVersionInfo> entityList = []
		addIfNotNull(entityList, entityInstance.getCategorization())
		entityList
	}

	def filterPaneService

	def filter() {
		render( view:'list',
				model:[ filterInstanceList: filterPaneService.filter( params, Filter),
						filterInstanceTotal: filterPaneService.count( params, Filter ),
						filterParams: FilterPaneUtils.extractFilterParams(params),
						params:params ] )
	}
}
