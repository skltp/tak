/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.configuration;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("HttpUrlsUsage") // HTTP links used in test
class AaaConfigTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Test
    void testDefaults() {
        AaaConfig properties = new AaaConfig();

        assertEquals(20, properties.getMaxBatchSize(), "Default batch size must match the AAA server limit");
        assertEquals("", properties.getUrl(), "URL defaults to empty, meaning the integration is disabled");
        assertTrue(validator.validate(properties).isEmpty(), "Defaults must be valid");
    }

    @Test
    void testSettersRoundTrip() {
        AaaConfig properties = new AaaConfig();
        properties.setUrl("https://aaa.example.com/aaa");
        properties.setMaxBatchSize(5);

        assertEquals("https://aaa.example.com/aaa", properties.getUrl());
        assertEquals(5, properties.getMaxBatchSize());
        assertTrue(validator.validate(properties).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 20, 100})
    void testValidMaxBatchSizeAccepted(int validBatchSize) {
        AaaConfig properties = new AaaConfig();
        properties.setMaxBatchSize(validBatchSize);

        assertTrue(validator.validate(properties).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void testMaxBatchSizeBelowOneIsRejected(int invalidBatchSize) {
        AaaConfig properties = new AaaConfig();
        properties.setMaxBatchSize(invalidBatchSize);

        Set<ConstraintViolation<AaaConfig>> violations = validator.validate(properties);

        assertEquals(1, violations.size(), "Expected exactly one violation for " + invalidBatchSize);
        ConstraintViolation<AaaConfig> violation = violations.iterator().next();
        assertEquals("maxBatchSize", violation.getPropertyPath().toString());
        assertEquals("aaa.maxBatchSize must be at least 1", violation.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {101, 1000, Integer.MAX_VALUE})
    void testMaxBatchSizeAboveHundredIsRejected(int invalidBatchSize) {
        AaaConfig properties = new AaaConfig();
        properties.setMaxBatchSize(invalidBatchSize);

        Set<ConstraintViolation<AaaConfig>> violations = validator.validate(properties);

        assertEquals(1, violations.size(), "Expected exactly one violation for " + invalidBatchSize);
        ConstraintViolation<AaaConfig> violation = violations.iterator().next();
        assertEquals("maxBatchSize", violation.getPropertyPath().toString());
        assertEquals("aaa.maxBatchSize must be at most 100", violation.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "https://aaa.example.com",
            "https://aaa.example.com/aaa",
            "https://aaa.example.com:8443/aaa",
            "https://aaa.example.com:65535",
            "https://192.168.0.1:8443/aaa",
            "https://[2001:db8::1]:8443/aaa"
    })
    void testValidUrlAccepted(String validUrl) {
        AaaConfig properties = new AaaConfig();
        properties.setUrl(validUrl);

        assertTrue(validator.validate(properties).isEmpty(), "Expected no violation for '" + validUrl + "'");
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void testBlankUrlIsNormalizedToEmptyAndAccepted(String blankUrl) {
        AaaConfig properties = new AaaConfig();
        properties.setUrl(blankUrl);

        assertEquals("", properties.getUrl(), "Blank URL must be normalized to empty, i.e. disabled");
        assertTrue(validator.validate(properties).isEmpty(), "Blank URL must disable the integration, not fail startup");
    }

    @Test
    void testNullUrlIsNormalizedToEmpty() {
        AaaConfig properties = new AaaConfig();
        properties.setUrl(null);

        assertEquals("", properties.getUrl());
        assertTrue(validator.validate(properties).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {" https://aaa.example.com", "https://aaa.example.com/aaa  "})
    void testSurroundingWhitespaceIsRejected(String paddedUrl) {
        AaaConfig properties = new AaaConfig();
        properties.setUrl(paddedUrl);

        assertEquals(paddedUrl, properties.getUrl(), "Non-blank values must be kept as configured");
        assertEquals(1, validator.validate(properties).size(), "Expected a violation for '" + paddedUrl + "'");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // Wrong or missing scheme
            "http://aaa.example.com/aaa",
            "aaa.example.com",
            "ftp://aaa.example.com",
            "Https://aaa.example.com",
            "/aaa",
            // Missing or malformed host
            "https://",
            "https://:80",
            "https:///aaa",
            "https:// aaa.example.com",
            "https://aaa.example.com/aa a",
            "https://aaa exempel.com",
            // Illegal port
            "https://aaa.example.com:port",
            "https://aaa.example.com:0",
            "https://aaa.example.com:65536",
            "https://aaa.example.com:999999",
            // User info must not be part of a service base URL
            "https://user:password@aaa.example.com",
            "https://user@aaa.example.com/aaa",
            // Query/fragment would break the URL Feign builds from the base URL
            "https://aaa.example.com?x=1",
            "https://aaa.example.com:8443/aaa?x=1",
            "https://aaa.example.com#top",
            // Broken percent encoding
            "https://aaa.example.com/aaa%2",
            "https://aaa.example.com/%GG%00"
    })
    void testInvalidUrlIsRejected(String invalidUrl) {
        AaaConfig properties = new AaaConfig();
        properties.setUrl(invalidUrl);

        Set<ConstraintViolation<AaaConfig>> violations = validator.validate(properties);

        assertEquals(1, violations.size(), "Expected exactly one violation for '" + invalidUrl + "'");
        ConstraintViolation<AaaConfig> violation = violations.iterator().next();
        assertEquals("url", violation.getPropertyPath().toString());
        assertEquals("aaa.url must be empty or a valid https URL", violation.getMessage());
    }
}


