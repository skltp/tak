package se.skltp.tak.web.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import se.skltp.tak.web.entity.Anvandare;
import se.skltp.tak.web.service.AnvandareService;

public class ShiroDbRealm extends AuthorizingRealm {

    @Autowired
    AnvandareService service;

    public ShiroDbRealm() {
        super(new HashedCredentialsMatcher("Sha1"));
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        String userName = (String) principalCollection.getPrimaryPrincipal();
        Anvandare anvandare = service.getAnvandareByUsername(userName);
        if (anvandare.getAdministrator()) {
            authorizationInfo.addRole("Administrator");
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        Anvandare anvandare = service.getAnvandareByUsername(token.getUsername());
        if (anvandare == null) return null;

        return new SimpleAccount(anvandare.getAnvandarnamn(), anvandare.getLosenordHash(), null,"ShiroDbRealm");

    }
}
