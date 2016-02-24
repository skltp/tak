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
package se.skltp.tak.core.entity;

constraints = {
	adress (blank:false, maxSize: 255, minSize:5, validator: { val, obj ->
		
		if (val?.startsWith(" ")) {
			return 'invalid.leadingspace'
		}
		if (val?.startsWith("\t")) {
			return 'invalid.leadingtab'
		}
		if (val?.endsWith(" ")) {
			return 'invalid.trailingspace'
		}
		if (val?.endsWith("\t")) {
			return 'invalid.trailingtab'
		}
        
        if (val) {
			boolean foundWhiteSpace = false
			
			for (char c : val.value) {
				if (c.isWhitespace()) {
					foundWhiteSpace = true
					break
				}
			}
			
			if (foundWhiteSpace) { return 'invalid.space.newline' }
		}
        
        return true
    })
	tjanstekomponent nullable:false, unique:['adress', 'rivTaProfil', 'deleted']
	rivTaProfil nullable:false
}
