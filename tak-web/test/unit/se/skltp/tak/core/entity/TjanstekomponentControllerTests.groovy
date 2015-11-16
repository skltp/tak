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

import org.junit.Before

@TestFor(TjanstekomponentController)
@Mock(Tjanstekomponent)
class TjanstekomponentControllerTests extends AbstractTestSetup {
	
	@Before
	void before() {
		setupUser()
	}
	
    def populateValidParams(params) {		
		params['hsaId'] = 'Schedulr'
		params['beskrivning'] = 'test app'
		
        assert params != null
    }
	
	def populateInvalidParams(params) {
		params['hsaId'] = null
		params['beskrivning'] = 'test app'
		
		assert params != null
	}
	
	def getEntity() {
		populateValidParams(params)
		return new Tjanstekomponent(params)
	}

    void testSave() {
		testSaveEntity(controller, '/tjanstekomponent/create', '/tjanstekomponent/show/0')		
    }
	
    void testUpdate() {		
		testUpdateEntity(controller, 'tjanstekomponent')
    }

    void testDelete() {
        testDeleteEntity(controller, '/tjanstekomponent/list')
    }
	
}