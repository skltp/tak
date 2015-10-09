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



import org.junit.*

import grails.test.mixin.*

@TestFor(RivTaProfilController)
@Mock(RivTaProfil)
class RivTaProfilControllerTests extends AbstractTestSetup {
	
	@Before
	void before() {
		setupUser()
	}
	
    def populateValidParams(params) {		
		params['namn'] = 'RIVTA BA 3.0'
		params['beskrivning'] = 'test rivta profil'
		
        assert params != null
    }

    void testSave() {
        controller.save()

        assert model.entity != null
        assert view == '/rivTaProfil/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/rivTaProfil/show/0'
        assert controller.flash.message != null
        assert RivTaProfil.count() == 1
    }
/*
    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/rivTaProfil/list'

        populateValidParams(params)
        def rivTaProfil = new RivTaProfil(params)

        assert rivTaProfil.save() != null

        params.id = rivTaProfil.id

        def model = controller.show()

        assert model.rivTaProfilInstance == rivTaProfil
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/rivTaProfil/list'

        populateValidParams(params)
        def rivTaProfil = new RivTaProfil(params)

        assert rivTaProfil.save() != null

        params.id = rivTaProfil.id

        def model = controller.edit()

        assert model.rivTaProfilInstance == rivTaProfil
    }
*/
    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/rivTaProfil/list'

        response.reset()

        populateValidParams(params)
        def rivTaProfil = new RivTaProfil(params)

        assert rivTaProfil.save() != null

        // test invalid parameters in update
        params.id = rivTaProfil.id
        params.namn = null

        controller.update()

        assert view == "/rivTaProfil/edit"
        assert model.entity != null

        rivTaProfil.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/rivTaProfil/show/$rivTaProfil.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        rivTaProfil.clearErrors()

        populateValidParams(params)
        params.id = rivTaProfil.id
        params.version = -1
        controller.update()

        assert view == "/rivTaProfil/edit"
        assert model.entity != null
        assert model.entity.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/rivTaProfil/list'

        response.reset()

        populateValidParams(params)
        def rivTaProfil = new RivTaProfil(params)

        assert rivTaProfil.save() != null
        assert RivTaProfil.count() == 1

        params.id = rivTaProfil.id

        controller.delete()

        assert RivTaProfil.count() == 1
        assert RivTaProfil.get(rivTaProfil.id) != null
        assert response.redirectedUrl == '/rivTaProfil/list'
    }
}
