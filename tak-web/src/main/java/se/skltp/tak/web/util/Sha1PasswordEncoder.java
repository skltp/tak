package se.skltp.tak.web.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.apache.commons.codec.digest.DigestUtils;

public class Sha1PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return DigestUtils.sha1Hex(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return DigestUtils.sha1Hex(rawPassword.toString()).equals(encodedPassword);
    }
}
