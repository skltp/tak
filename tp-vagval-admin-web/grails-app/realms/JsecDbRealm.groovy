import org.jsecurity.authc.AccountException
import org.jsecurity.authc.IncorrectCredentialsException
import org.jsecurity.authc.UnknownAccountException
import org.jsecurity.authc.SimpleAccount

import se.skl.tp.vagval.admin.web.entity.*

class JsecDbRealm {
    static authTokenClass = org.jsecurity.authc.UsernamePasswordToken

    def credentialMatcher

    def authenticate(authToken) {
        log.info "Attempting to authenticate ${authToken.username} in DB realm..."
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

        log.info "Found user '${user.anvandarnamn}' in DB"

        // Now check the user's password against the hashed value stored
        // in the database.
        def account = new SimpleAccount(anvandarnamn, user.losenordHash, "JsecDbRealm")
        if (!credentialMatcher.doCredentialsMatch(authToken, account)) {
            log.info 'Invalid password (DB realm)'
            throw new IncorrectCredentialsException("Invalid password for user '${anvandarnamn}'")
        }

        return account
    }

    def hasRole(principal, roleName) {
    	def user = Anvandare.findByAnvandarnamn(principal)
        return roleName = "Administrator" && user?.administrator
    }

}
