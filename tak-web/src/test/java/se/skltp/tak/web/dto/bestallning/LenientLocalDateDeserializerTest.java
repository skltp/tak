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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LenientLocalDateDeserializerTest {

    private LenientLocalDateDeserializer deserializer;
    private JsonParser jsonParser;
    private DeserializationContext context;

    @BeforeEach
    void setUp() {
        deserializer = new LenientLocalDateDeserializer();
        jsonParser = mock(JsonParser.class);
        context = mock(DeserializationContext.class);
    }

    @Test
    void testDeserializeDateOnly() throws IOException {
        when(jsonParser.getValueAsString()).thenReturn("2026-07-31");

        LocalDate result = deserializer.deserialize(jsonParser, context);

        assertEquals(LocalDate.of(2026, 7, 31), result);
    }

    @ParameterizedTest
    @CsvSource({
        "2022-05-24T12:00:01+0000, 2022-05-24",
        "2022-05-24T00:00:00+0000, 2022-05-24",
        "2022-05-24T23:59:59+0000, 2022-05-24",
        "2022-05-24T00:00:00Z, 2022-05-24",
        "2022-05-24T12:30:45.123Z, 2022-05-24",
        "2022-05-24T12:00:01+0200, 2022-05-24",
        "2022-05-24T12:00:01-0500, 2022-05-24"
    })
    void testDeserializeTimestampFormats(String input, String expectedDate) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(input);

        LocalDate result = deserializer.deserialize(jsonParser, context);

        assertEquals(LocalDate.parse(expectedDate), result);
    }

    @ParameterizedTest
    @CsvSource({
        "2022-12-31T23:59:59+0000, 2022-12-31",
        "2023-01-01T00:00:00+0000, 2023-01-01",
        "2022-12-31T00:00:01+0000, 2022-12-31"
    })
    void testDeserializeAroundMidnightAndYearBoundary(String input, String expectedDate) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(input);

        LocalDate result = deserializer.deserialize(jsonParser, context);

        assertEquals(LocalDate.parse(expectedDate), result);
    }

    @ParameterizedTest
    @CsvSource({
        "2020-02-29, 2020-02-29",
        "2024-02-29, 2024-02-29",
        "2000-01-01, 2000-01-01",
        "2099-12-31, 2099-12-31"
    })
    void testDeserializeEdgeDates(String input, String expectedDate) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(input);

        LocalDate result = deserializer.deserialize(jsonParser, context);

        assertEquals(LocalDate.parse(expectedDate), result);
    }

    @Test
    void testDeserializeNull() throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(null);

        LocalDate result = deserializer.deserialize(jsonParser, context);

        assertNull(result);
    }

    @Test
    void testDeserializeEmptyString() throws IOException {
        when(jsonParser.getValueAsString()).thenReturn("");

        LocalDate result = deserializer.deserialize(jsonParser, context);

        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "not-a-date",
        "31-07-2026",
        "07/31/2026",
        "2026/07/31",
        "20260731",
        "abc"
    })
    void testDeserializeInvalidFormat(String invalidInput) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(invalidInput);

        IOException exception = assertThrows(IOException.class,
            () -> deserializer.deserialize(jsonParser, context));

        assertTrue(exception.getMessage().contains("Cannot parse date"));
        assertTrue(exception.getMessage().contains(invalidInput));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "2026-07",
        "2026",
        "20"
    })
    void testDeserializeShortString(String shortInput) throws IOException {
        when(jsonParser.getValueAsString()).thenReturn(shortInput);

        assertThrows(IOException.class, () -> deserializer.deserialize(jsonParser, context));
    }
}




