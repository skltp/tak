/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.dto.bestallning;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Lenient deserializer for LocalDate that accepts both:
 *  - "yyyy-MM-dd" (date only)
 *  - "yyyy-MM-dd'T'HH:mm:ssZ" (full timestamp, time portion ignored)
 */
public class LenientLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return null;
        }

        // Extract only the date part (first 10 characters: yyyy-MM-dd)
        String datePart = value.length() >= 10 ? value.substring(0, 10) : value;

        try {
            return LocalDate.parse(datePart, DATE_ONLY);
        } catch (DateTimeParseException e) {
            throw new IOException("Cannot parse date: '" + value + "'. Expected format: yyyy-MM-dd (e.g., 2026-07-31)", e);
        }
    }
}
