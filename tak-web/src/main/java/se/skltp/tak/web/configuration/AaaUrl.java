/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.configuration;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Accepts an empty/blank value (integration disabled) or a well-formed absolute {@code https} base
 * URL with a resolvable host, a legal port and no user info.
 *
 * @see AaaUrlValidator
 */
@Documented
@Constraint(validatedBy = AaaUrlValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AaaUrl {

    String message() default "aaa.url must be empty or a valid https URL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

