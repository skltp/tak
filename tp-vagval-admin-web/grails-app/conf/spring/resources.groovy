import org.apache.shiro.authc.credential.Sha1CredentialsMatcher
// Place your Spring DSL code here
beans = {
    credentialMatcher(Sha1CredentialsMatcher) {
        storedCredentialsHexEncoded = true
    }
}
