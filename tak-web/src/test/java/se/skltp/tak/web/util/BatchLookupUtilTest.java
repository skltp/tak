/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class BatchLookupUtilTest {

    @Test
    void keyIsCaseInsensitive() {
        assertEquals(BatchLookupUtil.key("se123-abc", "urn:x"), BatchLookupUtil.key("SE123-ABC", "URN:X"));
    }

    @Test
    void keyKeepsPartsSeparate() {
        assertNotEquals(BatchLookupUtil.key("ab", "c"), BatchLookupUtil.key("a", "bc"));
    }

    @Test
    void queryInChunksSplitsAndConcatenates() {
        List<String> values = IntStream.range(0, BatchLookupUtil.CHUNK_SIZE * 2 + 1).mapToObj(String::valueOf).toList();
        List<Integer> chunkSizes = new ArrayList<>();

        List<String> result = BatchLookupUtil.queryInChunks(values, chunk -> {
            chunkSizes.add(chunk.size());
            return chunk;
        });

        assertEquals(List.of(BatchLookupUtil.CHUNK_SIZE, BatchLookupUtil.CHUNK_SIZE, 1), chunkSizes);
        assertEquals(values, result);
    }

    @Test
    void queryInChunksWithNoValuesDoesNotQuery() {
        List<String> result = BatchLookupUtil.queryInChunks(List.of(), chunk -> {
            throw new AssertionError("should not query");
        });
        assertEquals(List.of(), result);
    }
}
