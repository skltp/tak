package se.skltp.tak.web.dto.connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionStatusTest {
    public static Stream<String> badAaaUrls() {
        return Stream.of(null, "", " ");
    }

    @Test
    void testAaaUrlAvailable() {
        ConnectionStatus connectionStatus = new ConnectionStatus("hsaId", "https://example.com:1234", "https://aaa.url:8443/aaa");
        assertEquals("https://aaa.url:8443/aaa?url=https%3A%2F%2Fexample.com%3A1234&method=HEAD", connectionStatus.getAaaUrl());
    }

    @ParameterizedTest
    @MethodSource("badAaaUrls")
    void testAaaUrlNotAvailable(String aaaBaseUrl) {
        ConnectionStatus connectionStatus = new ConnectionStatus("hsaId", "https://example.com:1234", aaaBaseUrl);
        assertEquals("#", connectionStatus.getAaaUrl());
    }

    @Test
    void testAnalysisResult() {
        ConnectionStatus connectionStatus = new ConnectionStatus("hsaId", "url", "aaaBaseUrl");
        assertNull(connectionStatus.getAnalysisResult());
        connectionStatus.setAnalysisResult(new AnalysisResultV1().url("analysisResultUrl"));
        assertEquals(Optional.of("analysisResultUrl"), connectionStatus.getAnalysisResult().getUrl());
    }

    @Test
    void testObjectComparison() {
        ConnectionStatus connectionStatusA1 = new ConnectionStatus("hsaIdA", "url", "aaaBaseUrl1");
        ConnectionStatus connectionStatusA2 = new ConnectionStatus("hsaIdA", "url", "aaaBaseUrl2");
        ConnectionStatus connectionStatusB1 = new ConnectionStatus("hsaId", "urlB", "aaaBaseUrl3");
        ConnectionStatus connectionStatusB2 = new ConnectionStatus("hsaId", "urlB", "aaaBaseUrl4");

        assertEquals(connectionStatusA1, connectionStatusA2);
        assertNotEquals(connectionStatusA1, connectionStatusB1);
        assertNotEquals(connectionStatusA1, connectionStatusB2);
        assertNotEquals(connectionStatusA2, connectionStatusB1);
        assertNotEquals(connectionStatusA2, connectionStatusB2);

        assertEquals(connectionStatusB1, connectionStatusB2);
        assertNotEquals(connectionStatusB1, connectionStatusA1);
        assertNotEquals(connectionStatusB1, connectionStatusA2);
        assertNotEquals(connectionStatusB2, connectionStatusA1);
        assertNotEquals(connectionStatusB2, connectionStatusA2);

        //noinspection ConstantValue,SimplifiableAssertion
        assertFalse(connectionStatusA1.equals(null));
        //noinspection EqualsBetweenInconvertibleTypes,SimplifiableAssertion
        assertFalse(connectionStatusA1.equals(42));
    }

    @Test
    void testHashCode() {
        ConnectionStatus connectionStatusA1 = new ConnectionStatus("hsaIdA", "url", "aaaBaseUrl1");
        ConnectionStatus connectionStatusA2 = new ConnectionStatus("hsaIdA", "url", "aaaBaseUrl2");
        ConnectionStatus connectionStatusB1 = new ConnectionStatus("hsaId", "urlB", "aaaBaseUrl3");
        ConnectionStatus connectionStatusB2 = new ConnectionStatus("hsaId", "urlB", "aaaBaseUrl4");

        assertEquals(connectionStatusA1.hashCode(), connectionStatusA2.hashCode());
        assertNotEquals(connectionStatusA1.hashCode(), connectionStatusB1.hashCode());
        assertNotEquals(connectionStatusA1.hashCode(), connectionStatusB2.hashCode());
        assertNotEquals(connectionStatusA2.hashCode(), connectionStatusB1.hashCode());
        assertNotEquals(connectionStatusA2.hashCode(), connectionStatusB2.hashCode());

        assertEquals(connectionStatusB1.hashCode(), connectionStatusB2.hashCode());
        assertNotEquals(connectionStatusB1.hashCode(), connectionStatusA1.hashCode());
        assertNotEquals(connectionStatusB1.hashCode(), connectionStatusA2.hashCode());
        assertNotEquals(connectionStatusB2.hashCode(), connectionStatusA1.hashCode());
        assertNotEquals(connectionStatusB2.hashCode(), connectionStatusA2.hashCode());
    }

    @Test
    void testSorting() {
        List<ConnectionStatus> unordered = List.of(
                new ConnectionStatus("C1", "B", null),
                new ConnectionStatus("C1", "A", null),
                new ConnectionStatus("A1", "A", null),
                new ConnectionStatus("B1", "A", null),
                new ConnectionStatus("B1", "B", null)
        );
        List<ConnectionStatus> expected = List.of(
                new ConnectionStatus("A1", "A", null),
                new ConnectionStatus("B1", "A", null),
                new ConnectionStatus("B1", "B", null),
                new ConnectionStatus("C1", "A", null),
                new ConnectionStatus("C1", "B", null)
        );
        assertEquals(expected, unordered.stream().sorted().toList());
    }

    @Test
    void testLogging() {
        ConnectionStatus connectionStatus = new ConnectionStatus("hsaId", "url", "aaaBaseUrl");
        String s = connectionStatus.toString();
        assertTrue(s.startsWith("ConnectionStatus"));
        assertTrue(s.contains("hsaId"));
        assertTrue(s.contains("url"));
    }
}
