/**
 * Copyright (c) 2013 Center för eHälsa i samverkan (CeHis).
 * 							<http://cehis.se/>
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

import grails.test.mixin.Mock
import grails.test.mixin.TestFor

@TestFor(RivTaProfilController)
@Mock(RivTaProfil)
class RivTaProfilControllerUnitTest extends AbstractCRUDControllerUnitTest {

	@Override
	def getEntityName() {
		"rivTaProfil"
	}

	@Override
	def getEntityClass() {
		RivTaProfil
	}

	@Override
	def createValidEntity() {
		def paramsMap = [:]
		populateValidParams(paramsMap)
		new RivTaProfil(paramsMap)
	}

	@Override
	def createEntityWithNotSetDeletedDependencies() {
		def rivTaProfil = new RivTaProfil()
		rivTaProfil.setAnropsAdresser([new AnropsAdress()] as Set)
		rivTaProfil
	}

	@Override
	def populateValidParams(Map paramsMap) {
		paramsMap['namn'] = 'RIVTA BA 3.0'
		paramsMap['beskrivning'] = 'test rivta profil'
    }

	@Override
	def populateInvalidParams(Map paramsMap) {
		paramsMap['namn'] = null
		paramsMap['beskrivning'] = 'test rivta profil'
	}
}
