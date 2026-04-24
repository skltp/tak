/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.entity.Anvandare;
import se.skltp.tak.web.repository.AnvandareRepository;


@Service
public class TakWebUserDetailsService implements UserDetailsService {

    private final AnvandareRepository anvandareRepository;

    @Autowired
    public TakWebUserDetailsService(AnvandareRepository anvandareRepository) {
        this.anvandareRepository = anvandareRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String anvandarnamn) throws UsernameNotFoundException {
        Anvandare anvandare = anvandareRepository.findFirstByAnvandarnamn(anvandarnamn);
        if (anvandare == null) {
            throw new UsernameNotFoundException("Could not find user: " + anvandarnamn);
        }

        return new TakWebUserDetails(anvandare);
    }
}