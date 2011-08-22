/**
 * Copyright 2009 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
 */
package se.skl.tp.vagval.admin.web.entity

class AnvandareController {

    def index = { redirect(action: "list", params: params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def list = {
        params.max = Math.min(params.max ? params.max.toInteger() : 10,  100)
        [anvandareInstanceList: Anvandare.list(params), anvandareInstanceTotal: Anvandare.count()]
    }

    def create = {
        def anvandareInstance = new Anvandare()
        anvandareInstance.properties = params
        return [anvandareInstance: anvandareInstance]
    }

    def save = {
        def anvandareInstance = new Anvandare(params)
        if (!anvandareInstance.hasErrors() && anvandareInstance.save()) {
            flash.message = "anvandare.created"
            flash.args = [anvandareInstance.anvandarnamn]
            flash.defaultMessage = "User ${anvandareInstance.anvandarnamn} created"
            redirect(action: "show", id: anvandareInstance.id)
        }
        else {
            render(view: "create", model: [anvandareInstance: anvandareInstance])
        }
    }

    def show = {
        def anvandareInstance = Anvandare.get(params.id)
        if (!anvandareInstance) {
            flash.message = "anvandare.not.found"
            flash.args = [params.id]
            flash.defaultMessage = "User not found with id ${params.id}"
            redirect(action: "list")
        }
        else {
            return [anvandareInstance: anvandareInstance]
        }
    }

    def edit = {
        def anvandareInstance = Anvandare.get(params.id)
        if (!anvandareInstance) {
            flash.message = "anvandare.not.found"
            flash.args = [params.id]
            flash.defaultMessage = "User not found with id ${params.id}"
            redirect(action: "list")
        }
        else {
            return [anvandareInstance: anvandareInstance]
        }
    }

    def update = {
        def anvandareInstance = Anvandare.get(params.id)
        if (anvandareInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (anvandareInstance.version > version) {
                    
                    anvandareInstance.errors.rejectValue("version", "anvandare.optimistic.locking.failure", "Another user has updated this Anvandare while you were editing")
                    render(view: "edit", model: [anvandareInstance: anvandareInstance])
                    return
                }
            }
            anvandareInstance.properties = params
            if (!anvandareInstance.hasErrors() && anvandareInstance.save()) {
                flash.message = "anvandare.updated"
                flash.args = [params.anvandarnamn]
                flash.defaultMessage = "User ${params.anvandarnamn} updated"
                redirect(action: "show", id: anvandareInstance.id)
            }
            else {
                render(view: "edit", model: [anvandareInstance: anvandareInstance])
            }
        }
        else {
            flash.message = "anvandare.not.found"
            flash.args = [params.id]
            flash.defaultMessage = "User not found with id ${params.id}"
            redirect(action: "edit", id: params.id)
        }
    }

    def delete = {
        def anvandareInstance = Anvandare.get(params.id)
        if (anvandareInstance) {
            try {
                anvandareInstance.delete()
                flash.message = "anvandare.deleted"
                flash.args = [params.id]
                flash.defaultMessage = "User ${params.id} deleted"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "anvandare.not.deleted"
                flash.args = [params.id]
                flash.defaultMessage = "User ${params.id} could not be deleted"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "anvandare.not.found"
            flash.args = [params.id]
            flash.defaultMessage = "User not found with id ${params.id}"
            redirect(action: "list")
        }
    }
}
