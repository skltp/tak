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
/**
 * Generated by the Shiro plugin. This filters class protects all URLs
 * via access control by convention.
 */
class ShiroSecurityFilters {
    def filters = {
        all(uri: "/**") {
            before = {
                // Ignore direct views (e.g. the default main index page).
                if (!controllerName) return true

                // Access control by convention.
                accessControl()
            }
        }
        // Creating, modifying, or deleting users requires the "Administrator" role.
		userEditing(controller: "anvandare", action: "(create|edit|save|update|delete)") {
        	before = {
        		accessControl { role("Administrator") }
        	}
        }
        
        beställning(controller:"anropsbehorighet", action:"(bulkadd|bulkvalidate|bulksave)") {
            before = {
                accessControl { role("Administrator") }
            }
        }
        
        
    }
}
