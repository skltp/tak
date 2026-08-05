/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.configuration;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Settings for the AAA (Analysera AnslutningsAdress) integration used by the "Anslutningar" page.
 */
@Configuration
@Validated
@ConfigurationProperties(prefix = "aaa")
public class AaaConfig {

    /**
     * Base URL to the AAA service. Empty or blank when the integration is disabled, in which case no
     * {@code AaaClient} bean is created and analysis is skipped. Blank values are normalized to an
     * empty string, so configuration/templating whitespace disables the integration rather than
     * failing startup. When set, it must be a valid absolute {@code https} base URL, see
     * {@link AaaUrlValidator}.
     */
    @AaaUrl
    private String url = "";

    /**
     * Maximum number of AAA analyze requests performed in parallel. Each connection is analyzed with
     * its own request, so this limits how many concurrent calls the "Anslutningar" page makes
     * towards AAA.
     */
    @Min(value = 1, message = "aaa.maxConcurrentRequests must be at least 1")
    @Max(value = 100, message = "aaa.maxConcurrentRequests must be at most 100")
    private int maxConcurrentRequests = 20;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = normalizeUrl(url);
    }

    /**
     * Normalizes a configured URL: {@code null} and blank (whitespace only) values become an empty
     * string, i.e. the integration is disabled. Any other value is kept as is, so that it is
     * validated and resolved by Feign exactly as configured.
     */
    static String normalizeUrl(String url) {
        return url == null || url.isBlank() ? "" : url;
    }

    public int getMaxConcurrentRequests() {
        return maxConcurrentRequests;
    }

    public void setMaxConcurrentRequests(int maxConcurrentRequests) {
        this.maxConcurrentRequests = maxConcurrentRequests;
    }
}

