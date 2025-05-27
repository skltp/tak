package se.skltp.tak.web.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("forwardauth")
@RestController
@RequestMapping("/forwardauth")
public class ForwardAuthController {

    @GetMapping
    public ResponseEntity<Void> verify(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return ResponseEntity.ok()
                    .header("X-User", auth.getName())
                    .build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Login\"")
                .build();
    }
}
