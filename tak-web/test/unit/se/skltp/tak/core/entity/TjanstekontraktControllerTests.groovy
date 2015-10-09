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



import grails.test.mixin.*
import org.junit.*

@TestFor(TjanstekontraktController)
@Mock(Tjanstekontrakt)
class TjanstekontraktControllerTests extends AbstractTestSetup {
	
	
	@Before
	void before() {
		setupUser()		
	}
	
    def populateValidParams(params) {		
		params['namnrymd'] = 'GetInvoice'
		params['beskrivning'] = 'test'
		
        assert params != null
    }

    
    void testSave() {
        controller.save()

        assert model.entity != null
        assert view == '/tjanstekontrakt/create'

        response.reset()

        populateValidParams(params)
        controller.save()
		
        assert response.redirectedUrl == "/tjanstekontrakt/show/0"
        assert controller.flash.message != null
        assert Tjanstekontrakt.count() == 1
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/tjanstekontrakt/list'

        response.reset()

        populateValidParams(params)
        def tjanstekontrakt = new Tjanstekontrakt(params)

        assert tjanstekontrakt.save() != null

        // test invalid parameters in update
        params.id = tjanstekontrakt.id
        params.namnrymd = null

        controller.update()

        assert view == "/tjanstekontrakt/edit"
        assert model.entity != null

        tjanstekontrakt.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/tjanstekontrakt/show/$tjanstekontrakt.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        tjanstekontrakt.clearErrors()

        populateValidParams(params)
        params.id = tjanstekontrakt.id
        params.version = -1
        controller.update()

        assert view == "/tjanstekontrakt/edit"
        assert model.entity != null
        assert model.entity.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/tjanstekontrakt/list'

        response.reset()

        populateValidParams(params)
        def tjanstekontrakt = new Tjanstekontrakt(params)

        assert tjanstekontrakt.save() != null
        assert Tjanstekontrakt.count() == 1

        params.id = tjanstekontrakt.id

        controller.delete()

        assert Tjanstekontrakt.count() == 1
        assert Tjanstekontrakt.get(tjanstekontrakt.id) != null
        assert response.redirectedUrl == '/tjanstekontrakt/list'
    }
}
