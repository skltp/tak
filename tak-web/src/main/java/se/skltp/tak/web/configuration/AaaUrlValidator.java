/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.configuration;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Validates the {@code aaa.url} property. A blank value means the AAA integration is disabled and is
 * therefore accepted. Any other value must be a syntactically valid, absolute {@code https} URL with
 * a server-based authority (real host name/IP), a port within range and no user info, so that
 * misconfiguration fails fast at startup instead of later in Feign/URI handling.
 */
public class AaaUrlValidator implements ConstraintValidator<AaaUrl, String> {

    static final int MAX_PORT = 65535;

    private static final String HTTPS_SCHEME = "https";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String url = AaaConfig.normalizeUrl(value);
        if (url.isEmpty()) {
            return true; // Integration disabled
        }
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException ex) {
            return false;
        }
        return HTTPS_SCHEME.equals(uri.getScheme())
                && uri.getHost() != null
                && !uri.getHost().isEmpty()
                && uri.getUserInfo() == null
                && isPortValid(uri)
                // A base URL that Feign appends the operation path to must not carry a query or
                // fragment, since that would produce a malformed request URL.
                && uri.getQuery() == null
                && uri.getFragment() == null;
    }

    /**
     * A port of {@code -1} means "not specified" and is allowed. Values outside the legal TCP range
     * are rejected. Note that a non-numeric port makes the authority registry-based, which is caught
     * by the host check above.
     */
    private static boolean isPortValid(URI uri) {
        int port = uri.getPort();
        return port == -1 || (port >= 1 && port <= MAX_PORT);
    }
}


