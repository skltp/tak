package se.skltp.tak.web.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import se.skltp.tak.web.entity.Anvandare;

import java.util.Collection;
import java.util.Collections;

public class TakWebUserDetails implements UserDetails {

    private final Anvandare anvandare;

    public TakWebUserDetails(Anvandare anvandare) {
        this.anvandare = anvandare;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (anvandare.getAdministrator()) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));  // Lägg till roll om användaren är admin
        }
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));  // Annars, tilldela "ROLE_USER"
    }

    @Override
    public String getPassword() {
        return anvandare.getLosenordHash();  // Hashad version av lösenordet
    }

    @Override
    public String getUsername() {
        return anvandare.getAnvandarnamn();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
