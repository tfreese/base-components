/**
 * Created: 14.04.2020
 */

package de.freese.base.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
public class TestStringUtils
{
    /**
     *
     */
    @Test
    public void testStringUtilsNormalizeSpace()
    {
        String text = "  ab c  d    efg ";
        String result = StringUtils.normalizeSpace(text);
        assertEquals("ab c d efg", result);

        StringBuilder sb = new StringBuilder();
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
