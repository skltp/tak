/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.configuration;

import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Matches when the AAA integration is enabled, i.e. when {@code aaa.url} is set to a non-blank
 * value. A missing, empty or blank value disables the integration, consistently with
 * {@link AaaConfig#normalizeUrl(String)} which normalizes blank values to an empty string.
 */
public class AaaEnabledCondition implements Condition {

    static final String URL_PROPERTY = "aaa.url";

    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        String url = AaaConfig.normalizeUrl(context.getEnvironment().getProperty(URL_PROPERTY));
        return !url.isEmpty();
    }
}

