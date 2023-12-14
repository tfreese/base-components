// Created: 14.04.2020
package de.freese.base.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestStringUtils {
    @Test
    void testRemoveNonAscii() {
        final StringBuilder sb = new StringBuilder();
        sb.append((char) 97); // a
        sb.append((char) 9); // Horizontal Tab
        sb.append((char) 98); // b
        sb.append((char) 10); // Line Feed
        sb.append((char) 99); // c
        sb.append((char) 13); // Carriage Return
        sb.append((char) 100); // d
        sb.append((char) 11); // Vertical Tab
        sb.append((char) 127); // DEL

        String result = StringUtils.removeNonAscii(sb.toString());
        assertEquals("abcd", result);

        // Horizontal Tab
        final Predicate<Character> keepChars = c -> c == 9;

        result = StringUtils.removeNonAscii(sb.toString(), keepChars);
        assertEquals("a" + (char) 9 + "bcd", result);

        sb.append('ä');
        sb.append('ß');
        result = StringUtils.removeNonAsciiGerman(sb.toString());
        assertEquals("abcdäß", result);

        result = StringUtils.removeNonAsciiGerman("💚iPhone 13 jetzt im GigaDeal ");
        assertEquals("iPhone 13 jetzt im GigaDeal ", result);
    }

    @Test
    void testStringUtilsNormalizeSpace() {
        final String text = "  ab c  d    efg ";
        String result = StringUtils.normalizeSpace(text);
        assertEquals("ab c d efg", result);

        final StringBuilder sb = new StringBuilder();
        sb.append(' ');
        sb.append(' ');
        sb.append('a');
        sb.append(StringUtils.ASCII_NON_BREAKING_SPACE);
        sb.append('b');
        sb.append(' ');
        sb.append('c');
        sb.append(' ');
        sb.append(' ');
        sb.append('d');
        sb.append(' ');
        sb.append(' ');
        sb.append(' ');
        sb.append('e');
        sb.append('f');
        sb.append('g');
        sb.append(' ');

        result = StringUtils.normalizeSpace(sb);
        assertEquals("a b c d efg", result);
    }
}
