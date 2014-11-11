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
package se.skltp.tak.web.entity

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException
import org.apache.shiro.SecurityUtils
import grails.converters.*

class AnvandareController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [anvandareInstanceList: Anvandare.list(params), anvandareInstanceTotal: Anvandare.count()]
    }

    def create() {
        [anvandareInstance: new Anvandare(params)]
    }

    def save() {
        def anvandareInstance = new Anvandare(params)
        if (!anvandareInstance.save(flush: true)) {
            render(view: "create", model: [anvandareInstance: anvandareInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'anvandare.label', default: 'Anvandare'), anvandareInstance.id])
        redirect(action: "show", id: anvandareInstance.id)
    }

    def show(Long id) {
        def anvandareInstance = Anvandare.get(id)
        if (!anvandareInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'anvandare.label', default: 'Anvandare'), id])
            redirect(action: "list")
            return
        }

        [anvandareInstance: anvandareInstance]
    }

    def edit(Long id) {
        def anvandareInstance = Anvandare.get(id)
        if (!anvandareInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'anvandare.label', default: 'Anvandare'), id])
            redirect(action: "list")
            return
        }

        [anvandareInstance: anvandareInstance]
    }

    def update(Long id, Long version) {
        def anvandareInstance = Anvandare.get(id)
        if (!anvandareInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'anvandare.label', default: 'Anvandare'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (anvandareInstance.version > version) {
                anvandareInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'anvandare.label', default: 'Anvandare')] as Object[],
                          "Another user has updated this Anvandare while you were editing")
                render(view: "edit", model: [anvandareInstance: anvandareInstance])
                return
            }
        }

        anvandareInstance.properties = params

        if (!anvandareInstance.save(flush: true)) {
            render(view: "edit", model: [anvandareInstance: anvandareInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'anvandare.label', default: 'Anvandare'), anvandareInstance.id])
        redirect(action: "show", id: anvandareInstance.id)
    }

    def delete(Long id) {
        def anvandareInstance = Anvandare.get(id)
        if (!anvandareInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'anvandare.label', default: 'Anvandare'), id])
            redirect(action: "list")
            return
        }

        try {
            anvandareInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'anvandare.label', default: 'Anvandare'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException | UncategorizedSQLException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'anvandare.label', default: 'Anvandare'), id])
            redirect(action: "show", id: id)
        }
    }
}
