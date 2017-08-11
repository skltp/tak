package se.skltp.tak.web.entity
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


class TAKSettingsController {

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [settingInstanceList: TAKSettings.list(params), settingInstanceTotal: TAKSettings.count()]
    }

    def show(Long id) {
        def settingInstance = TAKSettings.get(id)
        if (!settingInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'settings.label', default: 'TAK Settings'), id])
            redirect(action: "list")
            return
        }

        [settingInstance: settingInstance]
    }

    def edit(Long id) {
        def settingInstance = TAKSettings.get(id)
        if (!settingInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'settings.label', default: 'TAK Settings'), id])
            redirect(action: "list")
            return
        }

        [settingInstance: settingInstance]
    }

    def update(Long id) {
        def settingInstance = TAKSettings.get(id)
        if (!settingInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'settings.label', default: 'TAK Settings'), id])
            redirect(action: "list")
            return
        }

        settingInstance.properties = params

        if (!settingInstance.save(flush: true)) {
            render(view: "edit", model: [settingInstance: settingInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'settings.label', default: 'TAK Settings'), settingInstance.id])

        redirect(action: "show", id: id)
    }
}
