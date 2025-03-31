package se.skltp.tak.web.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryGeneratorImplTest {

    @Test
    public void testEscapeSpecialCharsNullInput() {
        assertNull(QueryGeneratorImpl.escapeSpecialChars(null));
    }

    @Test
    public void testEscapeSpecialCharsEmptyString() {
        assertEquals("", QueryGeneratorImpl.escapeSpecialChars(""));
    }

    @Test
    public void testEscapeSpecialCharsNoSpecialChars() {
        assertEquals("hello", QueryGeneratorImpl.escapeSpecialChars("hello"));
    }

    @Test
    public void testEscapeSpecialCharsBackslash() {
        assertEquals("\\\\", QueryGeneratorImpl.escapeSpecialChars("\\"));
    }

    @Test
    public void testEscapeSpecialCharsPercent() {
        assertEquals("\\%", QueryGeneratorImpl.escapeSpecialChars("%"));
    }

    @Test
    public void testEscapeSpecialCharsUnderscore() {
        assertEquals("\\_", QueryGeneratorImpl.escapeSpecialChars("_"));
    }

    @Test
    public void testEscapeSpecialCharsMixedCharacters() {
        assertEquals("Hello \\\\ World\\%\\_", QueryGeneratorImpl.escapeSpecialChars("Hello \\ World%_"));
    }

}