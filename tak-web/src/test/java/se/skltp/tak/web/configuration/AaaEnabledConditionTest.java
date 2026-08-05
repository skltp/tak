/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AaaEnabledConditionTest {

    private final AaaEnabledCondition condition = new AaaEnabledCondition();

    private boolean matches(Environment environment) {
        ConditionContext context = mock(ConditionContext.class);
        when(context.getEnvironment()).thenReturn(environment);
        return condition.matches(context, mock(AnnotatedTypeMetadata.class));
    }

    @Test
    void testMissingUrlDisablesIntegration() {
        assertFalse(matches(new MockEnvironment()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void testBlankUrlDisablesIntegration(String blankUrl) {
        assertFalse(matches(new MockEnvironment().withProperty(AaaEnabledCondition.URL_PROPERTY, blankUrl)));
    }

    @Test
    void testConfiguredUrlEnablesIntegration() {
        assertTrue(matches(new MockEnvironment()
                .withProperty(AaaEnabledCondition.URL_PROPERTY, "https://aaa.example.com")));
    }

    @Test
    void testPaddedUrlEnablesIntegration() {
        assertTrue(matches(new MockEnvironment()
                .withProperty(AaaEnabledCondition.URL_PROPERTY, "  https://aaa.example.com  ")),
                "Non-blank values enable the integration; validation rejects the padded value");
    }
}


