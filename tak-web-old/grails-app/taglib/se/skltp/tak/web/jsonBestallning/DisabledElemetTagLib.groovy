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

package se.skltp.tak.web.jsonBestallning

class DisabledElemetTagLib {
    def myTextField = { attrs, body ->
        def title = attrs.remove("title")
        def isDisabled = attrs.remove("disabled")

        if ("true".equals(isDisabled)) {
            out << """<input title="${title}" disabled="${isDisabled}" """
            attrs.each { k,v ->
                out << k << "=\"" << v << "\" "
            }
            out << "/>"
        } else {
            out << """<input title="${title}" """
            attrs.each { k,v ->
                out << k << "=\"" << v << "\" "
            }
            out << "/>"
        }
    }

    def mySubmitButton ={attrs, body ->
        def isDisabled = attrs.remove("disabled")
        if("true".equals(isDisabled)){
            out << """<input disabled="${isDisabled}" """
            attrs.each { k,v ->
                out << k << "=\"" << v << "\" "
            }
            out << "/>"
        }else{
            out << """<input """
            attrs.each { k,v ->
                out << k << "=\"" << v << "\" "
            }
            out << "/>"
        }

    }
}
