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

class RivTaProfilController extends AbstractCRUDController {

	private static final log = LogFactory.getLog(this)
	
	def scaffold = RivTaProfil
	
	def entityLabel = { message(code: 'rivTaProfil.label', default: 'RivTaProfil') }

	public String getEntityLabel() {
		return entityLabel()
	}
	public Class getEntityClass() {
		RivTaProfil
	}
	public AbstractVersionInfo createEntityInstance(params) {
		new RivTaProfil(params)
	}
	public String getModelName() {
		"rivTaProfilInstance"
	}
	public List<AbstractVersionInfo> getEntityDependencies(entityInstance) {
		List<AbstractVersionInfo> entityList = new ArrayList<AbstractVersionInfo>();
		addIfNotNull(entityList, entityInstance?.getAnropsAdresser())
		entityList
	}

}
