/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Rejects form posts of orders that exceed {@code server.tomcat.max-http-form-post-size}.
 * <p>
 * Tomcat silently drops all parameters of a too large form post, which would make the CSRF check fail
 * and send the user to the login page. This filter runs before Spring Security and instead gives the
 * user a message about the size.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class FormPostSizeFilter extends OncePerRequestFilter {

    public static final String CONFIRM_PATH = "/bestallning/confirm";
    public static final String REST_CREATE_PATH = "/rest/create";
    public static final String TOO_LARGE_PARAM = "tooLarge";

    private static final Locale SWEDISH = Locale.forLanguageTag("sv-SE");

    private static final Logger log = LoggerFactory.getLogger(FormPostSizeFilter.class);

    private final long maxFormPostSize;

    public FormPostSizeFilter(@Value("${server.tomcat.max-http-form-post-size:2MB}") DataSize maxFormPostSize) {
        this.maxFormPostSize = maxFormPostSize.toBytes();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod()) || maxFormPostSize < 0) return true;
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT)
                .startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) return true;
        String path = request.getServletPath();
        return !CONFIRM_PATH.equals(path) && !REST_CREATE_PATH.equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long size = request.getContentLengthLong();
        if (size <= maxFormPostSize) {
            filterChain.doFilter(request, response);
            return;
        }
        log.warn("Rejected order post to {}: {} bytes exceeds max {} bytes", request.getServletPath(), size, maxFormPostSize);
        if (CONFIRM_PATH.equals(request.getServletPath())) {
            response.sendRedirect(request.getContextPath() + "/bestallning?" + TOO_LARGE_PARAM + "=" + size);
        } else {
            response.setStatus(HttpStatus.PAYLOAD_TOO_LARGE.value());
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("JsonBestallning ERROR::: " + tooLargeMessage(size, maxFormPostSize));
        }
    }

    public static String tooLargeMessage(long size, long maxSize) {
        return String.format("Beställningen är för stor (%s). Maximal storlek är %s.",
                formatMegabytes(size), formatMegabytes(maxSize));
    }

    public static String formatMegabytes(long bytes) {
        return String.format(SWEDISH, "%.1f MB", bytes / (1024.0 * 1024.0));
    }
}
