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

@TestFor(TjanstekontraktController)
@Mock(Tjanstekontrakt)
class TjanstekontraktControllerUnitTest extends AbstractCRUDControllerUnitTest {

	@Override
	def getEntityName() {
		"tjanstekontrakt"
	}

	@Override
	def getEntityClass() {
		Tjanstekontrakt
	}

	@Override
	def createEntity(Map paramsMap) {
		new Tjanstekontrakt(paramsMap)
	}

	@Override
	def createEntityWithNotSetDeletedDependencies() {
		def tjanstekontrakt = new Tjanstekontrakt()
		tjanstekontrakt.setVagval([new Vagval()] as Set)
		tjanstekontrakt.setAnropsbehorigheter([new Anropsbehorighet()] as Set)
		tjanstekontrakt
	}

	@Override
	def populateValidParams(Map paramsMap) {
		paramsMap['namnrymd'] = 'urn:riv:itinfra:tp:PingResponder:1'
		paramsMap['beskrivning'] = 'Test ping Service'
    }

	@Override
	def populateInvalidParams(Map paramsMap) {
		paramsMap['namnrymd'] = null
		paramsMap['beskrivning'] = 'Test ping Service'
	}
}
