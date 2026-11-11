/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Helpers for replacing per-row lookups with batched IN-queries.
 */
public final class BatchLookupUtil {

    public static final int CHUNK_SIZE = 500;

    private BatchLookupUtil() {
    }

    /**
     * Case-insensitive composite key, since string comparisons in the TAK MySQL database
     * (utf8 general_ci) are case-insensitive.
     */
    public static String key(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part == null ? "" : part.toUpperCase(Locale.ROOT)).append('\u0000');
        }
        return sb.toString();
    }

    /**
     * Runs the query for chunks of {@code values} (to keep IN-lists bounded) and concatenates the results.
     */
    public static <T> List<T> queryInChunks(Collection<String> values, Function<List<String>, List<T>> query) {
        List<T> result = new ArrayList<>();
        List<String> all = new ArrayList<>(values);
        for (int i = 0; i < all.size(); i += CHUNK_SIZE) {
            result.addAll(query.apply(all.subList(i, Math.min(i + CHUNK_SIZE, all.size()))));
        }
        return result;
    }
}
