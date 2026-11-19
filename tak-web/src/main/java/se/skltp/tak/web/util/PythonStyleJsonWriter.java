/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Writes JSON formatted exactly as Python's {@code json.dumps(content, ensure_ascii=False, indent=4)}, so that files
 * produced by tak-web are identical to the ones produced by the mass-TAKning Python script.
 * <p>
 * Supports maps (with string keys), collections, strings, numbers, booleans and null.
 */
public final class PythonStyleJsonWriter {

    private static final String INDENT = "    ";

    private PythonStyleJsonWriter() {
    }

    public static String write(Object value) {
        StringBuilder sb = new StringBuilder();
        writeValue(sb, value, 0);
        return sb.toString();
    }

    private static void writeValue(StringBuilder sb, Object value, int level) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof Map<?, ?> map) {
            writeMap(sb, map, level);
        } else if (value instanceof Collection<?> collection) {
            writeCollection(sb, collection, level);
        } else if (value instanceof CharSequence text) {
            writeString(sb, text.toString());
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
        } else {
            throw new IllegalArgumentException("Unsupported JSON value type: " + value.getClass().getName());
        }
    }

    private static void writeMap(StringBuilder sb, Map<?, ?> map, int level) {
        if (map.isEmpty()) {
            sb.append("{}");
            return;
        }
        sb.append('{');
        Iterator<? extends Map.Entry<?, ?>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?, ?> entry = it.next();
            newLine(sb, level + 1);
            writeString(sb, String.valueOf(entry.getKey()));
            sb.append(": ");
            writeValue(sb, entry.getValue(), level + 1);
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        newLine(sb, level);
        sb.append('}');
    }

    private static void writeCollection(StringBuilder sb, Collection<?> collection, int level) {
        if (collection.isEmpty()) {
            sb.append("[]");
            return;
        }
        sb.append('[');
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            newLine(sb, level + 1);
            writeValue(sb, it.next(), level + 1);
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        newLine(sb, level);
        sb.append(']');
    }

    private static void newLine(StringBuilder sb, int level) {
        sb.append('\n');
        sb.append(INDENT.repeat(level));
    }

    private static void writeString(StringBuilder sb, String text) {
        sb.append('"');
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }
}
