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
import org.apache.shiro.authc.AccountException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.UnknownAccountException
import org.apache.shiro.authc.SimpleAccount
import org.apache.shiro.authz.permission.WildcardPermission

import se.skltp.tak.web.entity.Anvandare

class ShiroDbRealm {
    static authTokenClass = org.apache.shiro.authc.UsernamePasswordToken

    def credentialMatcher
    def shiroPermissionResolver

    def authenticate(authToken) {
        log.debug "Attempting to authenticate ${authToken.username} in DB realm..."
        def anvandarnamn = authToken.username


        // Null anvandarnamn is invalid
        if (anvandarnamn == null) {
            throw new AccountException('Null usernames are not allowed by this realm.')
        }

        // Get the user with the given username. If the user is not
        // found, then they don't have an account and we throw an
        // exception.
        def user = Anvandare.findByAnvandarnamn(anvandarnamn)
        if (!user) {
            throw new UnknownAccountException("No account found for user [${anvandarnamn}]")
        }

        log.debug "Found user '${user.anvandarnamn}' in DB"

        // Now check the user's password against the hashed value stored
        // in the database.
        def account = new SimpleAccount(anvandarnamn, user.losenordHash, "ShiroDbRealm")
        if (!credentialMatcher.doCredentialsMatch(authToken, account)) {
            log.debug 'Invalid password (DB realm)'
            throw new IncorrectCredentialsException("Invalid password for user '${anvandarnamn}'")
        }

        return account
    }

    def hasRole(principal, roleName) {
    	def user = Anvandare.findByAnvandarnamn(principal)
        return roleName = "Administrator" && user?.administrator
    }

    def hasAllRoles(principal, roles) {
        return roles.every {role ->
			hasRole(principal, role)
		}
    }

    def isPermitted(principal, requiredPermission) {
        return true
    }
}
