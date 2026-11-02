/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.util;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PythonStyleJsonWriterTest {

    @Test
    void writesLikePythonJsonDumpsWithIndent4() {
        Map<String, Object> inner = new LinkedHashMap<>();
        inner.put("hsaId", "SE123");
        inner.put("majorVersion", 4);
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("utforare", "Inera - Nationella tjänsteplattformen");
        root.put("lista", List.of(inner));
        root.put("tom", List.of());
        root.put("tomMap", Map.of());

        // Expected value produced by: json.dumps(root, ensure_ascii=False, indent=4)
        String expected = "{\n"
                + "    \"utforare\": \"Inera - Nationella tjänsteplattformen\",\n"
                + "    \"lista\": [\n"
                + "        {\n"
                + "            \"hsaId\": \"SE123\",\n"
                + "            \"majorVersion\": 4\n"
                + "        }\n"
                + "    ],\n"
                + "    \"tom\": [],\n"
                + "    \"tomMap\": {}\n"
                + "}";
        assertEquals(expected, PythonStyleJsonWriter.write(root));
    }

    @Test
    void escapesLikePython() {
        assertEquals("\"a\\\"b\\\\c\\nd\\te\\u0001f\"", PythonStyleJsonWriter.write("a\"b\\c\nd\te\u0001f"));
    }

    @Test
    void writesScalars() {
        assertEquals("null", PythonStyleJsonWriter.write(null));
        assertEquals("true", PythonStyleJsonWriter.write(true));
        assertEquals("[\n    1,\n    \"x\"\n]", PythonStyleJsonWriter.write(List.of(1, "x")));
    }

    @Test
    void rejectsUnsupportedTypes() {
        Object unsupported = new Object();
        assertThrows(IllegalArgumentException.class, () -> PythonStyleJsonWriter.write(unsupported));
    }
}
