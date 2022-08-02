// Created: 16.07.2011
package de.freese.base.utils;

import static de.freese.base.utils.ByteUtils.HEX_CHARS;
import static de.freese.base.utils.ByteUtils.HEX_INDEX;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Thomas Freese
 */
public final class StringUtils
{
    /**
     * non-breaking space
     */
    public static final char ASCII_NON_BREAKING_SPACE = 160;

    /**
     *
     */
    public static final char ASCII_SPACE = 32;

    /**
     *
     */
    public static final String EMPTY = "";

    /**
     *
     */
    public static final String SPACE = " ";

    /**
     * <pre>
     * StringUtils.abbreviate(null, *)      = null
     * StringUtils.abbreviate("", 4)        = ""
     * StringUtils.abbreviate("abcdefg", 6) = "abc..."
     * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 4) = "a..."
     * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str String
     * @param maxWidth int
     *
     * @return String
     */
    public static String abbreviate(final String str, final int maxWidth)
    {
        return org.apache.commons.lang3.StringUtils.abbreviate(str, "...", 0, maxWidth);
    }

    /**
     * Fügt vor und nach dem ersten Eintrag der Liste eine Trenn-Linie ein.<br>
     * Die Breite pro Spalte orientiert sich am ersten Wert (Header) der Spalte.<br>
     *
     * @param <T> Konkreter Typ
     * @param rows {@link List}
     * @param separator String
     */
    @SuppressWarnings("unchecked")
    public static <T extends CharSequence> void addHeaderSeparator(final List<T[]> rows, final String separator)
    {
        if (rows == null || rows.isEmpty())
        {
            return;
        }

        String sep = (separator == null) || separator.isBlank() ? "|" : separator;

        int columnCount = rows.get(0).length;

        // Trenner zwischen Header und Daten.
        // T[] row = (T[]) Array.newInstance(String.class, columnCount);
        // T[] row = Arrays.copyOf(rows.get(0), columnCount);
        // T[] row = rows.get(0).clone();
        String[] row = new String[columnCount];

        for (int column = 0; column < columnCount; column++)
        {
            row[column] = repeat(sep, rows.get(0)[column].length());
        }

        rows.add(1, (T[]) row);
        rows.add(0, (T[]) row);
    }

    /**
     * @param text String
     *
     * @return String, not null
     */
    public static String asciiToUnicode(final String text)
    {
        if (isBlank(text))
        {
            return EMPTY;
        }

        if (!text.contains("\\u"))
        {
            return text;
        }

        int i = text.length();
        char[] ac = new char[i];
        int j = 0;

        for (int k = 0; k < i; k++)
        {
            char c = text.charAt(k);

            if ((c == '\\') && (k < (i - 5)))
            {
                char c1 = text.charAt(k + 1);

                if (c1 == 'u')
                {
                    k++;

                    int l = HEX_INDEX.indexOf(text.charAt(++k)) << 12;
                    l += (HEX_INDEX.indexOf(text.charAt(++k)) << 8);
                    l += (HEX_INDEX.indexOf(text.charAt(++k)) << 4);
                    l += HEX_INDEX.indexOf(text.charAt(++k));
                    ac[j++] = (char) l;
                }
                else
                {
                    ac[j++] = c;
                }
            }
            else
            {
                ac[j++] = c;
            }
        }

        return new String(ac, 0, j);
    }

    /**
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * StringUtils.capitalize("'cat'") = "'cat'"
     * </pre>
     *
     * @param text String
     *
     * @return String
     */
    public static String capitalize(final String text)
    {
        // value.substring(0, 1).toUpperCase() + value.substring(1);
        return org.apache.commons.lang3.StringUtils.capitalize(text);
    }

    /**
     * @param list {@link List}
     * @param escape char
     */
    public static void escape(final List<String[]> list, final char escape)
    {
        if (list == null || list.isEmpty())
        {
            return;
        }

        int columnCount = list.get(0).length;

        list.stream().parallel().forEach(r ->
        {
            for (int column = 0; column < columnCount; column++)
            {
                r[column] = escape + r[column] + escape;
            }
        });
    }

    /**
     * @param array String[]
     * @param escape char
     */
    public static void escape(final String[] array, final char escape)
    {
        if (ArrayUtils.isEmpty(array))
        {
            return;
        }

        for (int column = 0; column < array.length; column++)
        {
            array[column] = escape + array[column] + escape;
        }
    }

    /**
     * Liefert die Zeichenbreite der Elemente.<br>
     *
     * @param list {@link List}
     *
     * @return int[]
     */
    public static int[] getWidths(final List<String[]> list)
    {
        if (list == null || list.isEmpty())
        {
            return new int[0];
        }

        int columnCount = list.get(0).length;

        int[] columnWidths = new int[columnCount];
        Arrays.fill(columnWidths, 0);

        // @formatter:off
        IntStream.range(0, columnCount).forEach(column
                ->
                    columnWidths[column]  = list.stream()
                            .parallel()
                            .map(d -> d[column])
                            .filter(Objects::nonNull)
                            .mapToInt(CharSequence::length)
                            .max()
                            .orElse(0)
                );
        // @formatter:on

        return columnWidths;
    }

    /**
     * Liefert die Zeichenbreite der Elemente.<br>
     *
     * @param array String[]
     *
     * @return int[]
     */
    public static int[] getWidths(final String[] array)
    {
        if (ArrayUtils.isEmpty(array))
        {
            return new int[0];
        }

        List<String[]> list = new ArrayList<>(1);
        list.add(array);

        return getWidths(list);
    }

    /**
     * @param cs {@link CharSequence}
     *
     * @return String, not null
     *
     * @throws Exception Falls was schiefgeht.
     */
    public static String hexStringToUnicode(final CharSequence cs) throws Exception
    {
        if (isBlank(cs))
        {
            return EMPTY;
        }

        byte[] bytes = HexFormat.of().parseHex(cs);
        String sign = null;

        try (ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(bytes);
             DataInputStream datainputstream = new DataInputStream(bytearrayinputstream))
        {
            sign = datainputstream.readUTF();
        }
        catch (IOException ioexception)
        {
            sign = EMPTY;
        }

        return sign;
    }

    /**
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs {@link CharSequence}
     *
     * @return boolean
     */
    public static boolean isBlank(final CharSequence cs)
    {
        // return org.apache.commons.lang3.StringUtils.isBlank(cs);

        int strLen;

        if ((cs == null) || ((strLen = cs.length()) == 0))
        {
            return true;
        }

        for (int i = 0; i < strLen; i++)
        {
            if (!Character.isWhitespace(cs.charAt(i)))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param cs {@link CharSequence}
     *
     * @return boolean
     */
    public static boolean isEmpty(final CharSequence cs)
    {
        return length(cs) == 0;
    }

    /**
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs {@link CharSequence}
     *
     * @return boolean
     */
    public static boolean isNotBlank(final CharSequence cs)
    {
        return !isBlank(cs);
    }

    /**
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param cs {@link CharSequence}
     *
     * @return boolean
     */
    public static boolean isNotEmpty(final CharSequence cs)
    {
        return !isEmpty(cs);
    }

    /**
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = false
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("\u0967\u0968\u0969")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * StringUtils.isNumeric("-123") = false
     * StringUtils.isNumeric("+123") = false
     * </pre>
     *
     * @param cs {@link CharSequence}
     *
     * @return String
     */
    public static boolean isNumeric(final CharSequence cs)
    {
        // return org.apache.commons.lang3.StringUtils.isNumeric(cs);

        if (isEmpty(cs))
        {
            return false;
        }

        final int length = cs.length();

        for (int i = 0; i < length; i++)
        {
            if (!Character.isDigit(cs.charAt(i)))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * <pre>
     * StringUtils.leftPad(null, *, *)      = null
     * StringUtils.leftPad("", 3, "z")      = "zzz"
     * StringUtils.leftPad("bat", 3, "yz")  = "bat"
     * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtils.leftPad("bat", 1, "yz")  = "bat"
     * StringUtils.leftPad("bat", -1, "yz") = "bat"
     * StringUtils.leftPad("bat", 5, null)  = "  bat"
     * StringUtils.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param text String
     * @param size int
     * @param padStr String
     *
     * @return String
     */
    public static String leftPad(final String text, final int size, final String padStr)
    {
        // return String.format("%" + size + "s", text).replace(" ", padding);

        return org.apache.commons.lang3.StringUtils.leftPad(text, size, padStr);
    }

    /**
     * @param cs {@link CharSequence}
     *
     * @return String
     */
    public static int length(final CharSequence cs)
    {
        return cs == null ? 0 : cs.length();
    }

    /**
     * Entfernt mehrfach hintereinander auftretende Whitespace Characters und führt ein abschliesendes {@link String#strip()} durch.<br>
     * ASCII 160 (non-breaking space) wird als Space interpretiert.
     *
     * @param cs {@link CharSequence}
     *
     * @return String
     *
     * @see Character#isWhitespace
     */
    public static String normalizeSpace(final CharSequence cs)
    {
        // return org.apache.commons.lang3.StringUtils.normalizeSpace(text);

        if (isEmpty(cs))
        {
            return EMPTY;
        }

        final int size = cs.length();
        final StringBuilder sb = new StringBuilder();
        boolean lastWasWhitespace = false;

        for (int i = 0; i < size; i++)
        {
            char actualChar = cs.charAt(i);
            boolean isActualWhitespace = Character.isWhitespace(actualChar);

            // ASCII 160 = non-breaking space
            if (actualChar == ASCII_NON_BREAKING_SPACE)
            {
                actualChar = ASCII_SPACE;
                isActualWhitespace = true;
            }

            if (!isActualWhitespace)
            {
                sb.append(actualChar);
                lastWasWhitespace = false;
            }
            else if (!lastWasWhitespace)
            {
                sb.append(ASCII_SPACE);
                lastWasWhitespace = true;
            }
        }

        return sb.toString().strip();
    }

    /**
     * Entfernt alle ASCII Zeichen < 32 (SPACE) und > 126 (~).<br>
     *
     * @param input input
     *
     * @return String
     *
     * @see org.apache.commons.lang3.StringUtils#remove(String, char)
     */
    public static String removeNonAscii(final String input)
    {
        return removeNonAscii(input, c -> false);
    }

    /**
     * Entfernt alle ASCII Zeichen < 32 (SPACE) und > 126 (~).<br>
     * Andere nicht entfernbare Zeichen können über das {@link Predicate} definiert werden.
     *
     * @param input input
     * @param keep {@link IntPredicate}
     *
     * @return String
     *
     * @see org.apache.commons.lang3.StringUtils#remove(String, char)
     */
    public static String removeNonAscii(final String input, Predicate<Character> keep)
    {
        if (input == null || input.isBlank())
        {
            return input;
        }

        char[] chars = input.toCharArray();
        int pos = 0;

        for (char c : chars)
        {
            if (!keep.test(c) && (c < 32 || c > 126))
            {
                continue;
            }

            chars[pos++] = c;
        }

        return new String(chars, 0, pos);
    }

    /**
     * Entfernt alle ASCII Zeichen < 32 (SPACE) und > 126 (~).<br>
     * Behält die Umlaute und das 'ß'.
     *
     * @param input input
     *
     * @return String
     *
     * @see org.apache.commons.lang3.StringUtils#remove(String, char)
     */
    public static String removeNonAsciiGerman(final String input)
    {
        Set<Character> keepChars = Set.of(
                // 228
                'ä',
                // 196
                'Ä',
                // 252
                'ü',
                // 220
                'Ü',
                // 246
                'ö',
                // 214
                'Ö',
                // 223
                'ß'
        );

        return removeNonAscii(input, keepChars::contains);
    }

    /**
     * <pre>
     * StringUtils.repeat(null, 2) = null
     * StringUtils.repeat("", 0)   = ""
     * StringUtils.repeat("", 2)   = ""
     * StringUtils.repeat("a", 3)  = "aaa"
     * StringUtils.repeat("ab", 2) = "abab"
     * StringUtils.repeat("a", -2) = ""
     * </pre>
     *
     * @param cs {@link CharSequence}
     * @param repeat int
     *
     * @return String
     */
    public static String repeat(final CharSequence cs, final int repeat)
    {
        // return org.apache.commons.lang3.StringUtils.repeat(text, repeat);

        if (cs == null)
        {
            return null;
        }

        if ((repeat <= 0) || (cs.length() == 0))
        {
            return EMPTY;
        }

        //        StringBuilder sb = new StringBuilder();
        //
        //        for (int i = 0; i < repeat; i++)
        //        {
        //            sb.append(cs);
        //        }
        //
        //        return sb.toString();

        return cs.toString().repeat(repeat);
    }

    /**
     * <pre>
     * StringUtils.rightPad(null, *, *)      = null
     * StringUtils.rightPad("", 3, "z")      = "zzz"
     * StringUtils.rightPad("bat", 3, "yz")  = "bat"
     * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
     * StringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
     * StringUtils.rightPad("bat", 1, "yz")  = "bat"
     * StringUtils.rightPad("bat", -1, "yz") = "bat"
     * StringUtils.rightPad("bat", 5, null)  = "bat  "
     * StringUtils.rightPad("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param text String
     * @param size int
     * @param padding String
     *
     * @return String
     */
    public static String rightPad(final String text, final int size, final String padding)
    {
        // return String.format("%-" + size + "s", text).replace(" ", padding);

        return org.apache.commons.lang3.StringUtils.rightPad(text, size, padding);
    }

    /**
     * Trennt zusammengefügte Wörter anhand unterschiedlicher Uppercase/Lowercase Schreibweise der Buchstaben<br>
     *
     * @param text String
     *
     * @return String, not null
     */
    public static String splitAddedWords(final String text)
    {
        if (isBlank(text))
        {
            return EMPTY;
        }

        String mText = text.strip();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < (mText.length() - 1); i++)
        {
            char c = mText.charAt(i);
            sb.append(c);

            if ((Character.isLowerCase(c) && Character.isUpperCase(mText.charAt(i + 1))))
            {
                sb.append(" ");
            }
        }

        // Das letzte Zeichen nicht vergessen.
        sb.append(mText.charAt(mText.length() - 1));

        return sb.toString();
    }

    /**
     * <pre>
     * StringUtils.splitByWholeSeparator(null, *)               = null
     * StringUtils.splitByWholeSeparator("", *)                 = []
     * StringUtils.splitByWholeSeparator("ab de fg", null)      = ["ab", "de", "fg"]
     * StringUtils.splitByWholeSeparator("ab   de fg", null)    = ["ab", "de", "fg"]
     * StringUtils.splitByWholeSeparator("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
     * StringUtils.splitByWholeSeparator("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param text String
     * @param separator String
     *
     * @return String[]
     */
    public static String[] splitByWholeSeparator(final String text, final String separator)
    {
        return org.apache.commons.lang3.StringUtils.splitByWholeSeparator(text, separator);
    }

    /**
     * Neue Methode mit Unicode-Standards als {@link String#trim()} Alternative.
     *
     * @param text String
     *
     * @return String, not null
     */
    public static String strip(final String text)
    {
        if (isEmpty(text))
        {
            return EMPTY;
        }

        return text.strip();
    }

    /**
     * <pre>
     * StringUtils.stripToEmpty(null)          = ""
     * StringUtils.stripToEmpty("")            = ""
     * StringUtils.stripToEmpty("     ")       = ""
     * StringUtils.stripToEmpty("abc")         = "abc"
     * StringUtils.stripToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param text String
     *
     * @return String, not null
     */
    public static String stripToEmpty(final String text)
    {
        return text == null ? EMPTY : text.strip();
    }

    /**
     * <pre>
     * StringUtils.stripToNull(null)          = null
     * StringUtils.stripToNull("")            = null
     * StringUtils.stripToNull("     ")       = null
     * StringUtils.stripToNull("abc")         = "abc"
     * StringUtils.stripToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param text String
     *
     * @return String
     */
    public static String stripToNull(final String text)
    {
        final String s = strip(text);

        return isBlank(s) ? null : s;
    }

    /**
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param text String
     * @param separator String
     *
     * @return String
     */
    public static String substringBefore(final String text, final String separator)
    {
        return org.apache.commons.lang3.StringUtils.substringBefore(text, separator);
    }

    /**
     * <p>
     * Searches a String for substrings delimited by a start and end tag, returning all matching substrings in an array.
     * </p>
     * <p>
     * A {@code null} input String returns {@code null}. A {@code null} open/close returns {@code null} (no match). An empty ("") open/close returns
     * {@code null} (no match).
     * </p>
     *
     * <pre>
     * StringUtils.substringsBetween("[a][b][c]", "[", "]") = ["a","b","c"]
     * StringUtils.substringsBetween(null, *, *)            = null
     * StringUtils.substringsBetween(*, null, *)            = null
     * StringUtils.substringsBetween(*, *, null)            = null
     * StringUtils.substringsBetween("", "[", "]")          = []
     * </pre>
     *
     * @param str the String containing the substrings, null returns null, empty returns empty
     * @param open the String identifying the start of the substring, empty returns null
     * @param close the String identifying the end of the substring, empty returns null
     *
     * @return a String Array of substrings, or {@code null} if no match
     *
     * @since 2.3
     */
    public static String[] substringsBetween(final String str, final String open, final String close)
    {
        if ((str == null) || isEmpty(open) || isEmpty(close))
        {
            return null;
        }

        final int strLen = str.length();

        if (strLen == 0)
        {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        final int closeLen = close.length();
        final int openLen = open.length();
        final List<String> list = new ArrayList<>();
        int pos = 0;

        while (pos < (strLen - closeLen))
        {
            int start = str.indexOf(open, pos);

            if (start < 0)
            {
                break;
            }

            start += openLen;

            final int end = str.indexOf(close, start);

            if (end < 0)
            {
                break;
            }

            list.add(str.substring(start, end));
            pos = end + closeLen;
        }

        if (list.isEmpty())
        {
            return null;
        }

        return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Konvertiert mehrzeiligen Text in einen zeiligen Text.<br>
     *
     * @param text String
     *
     * @return String, not null
     */
    public static String toSingleLine(final String text)
    {
        if (isBlank(text))
        {
            return EMPTY;
        }

        // @formatter:off
        String line = Stream.of(text)
                .map(t -> t.replace("\n", SPACE)) // Keine Zeilenumbrüche
                .map(t -> t.replace("\t", SPACE)) // Keine Tabulatoren
                .map(t -> t.split(SPACE))
                .flatMap(Arrays::stream) // Stream<String[]> in Stream<String> konvertieren
                //.peek(System.out::println)
                .filter(StringUtils::isNotBlank) // leere Strings filtern
                .collect(Collectors.joining(SPACE));  // Strings wieder zusammenführen
        // @formatter:on

        return line;
    }

    /**
     * @param cs {@link CharSequence}
     *
     * @return String, not null
     */
    public static String unicodeToAscii(final CharSequence cs)
    {
        if (isBlank(cs))
        {
            return EMPTY;
        }

        int i = cs.length();
        StringBuilder sb = new StringBuilder(i + 16);

        for (int j = 0; j < i; j++)
        {
            char c = cs.charAt(j);

            if (c == '\\')
            {
                if ((j < (i - 1)) && (cs.charAt(j + 1) == 'u'))
                {
                    sb.append(c);
                    sb.append("u005c");
                }
                else
                {
                    sb.append(c);
                }
            }
            else if ((c >= ' ') && (c <= '\177'))
            {
                sb.append(c);
            }
            else
            {
                sb.append("\\u");
                sb.append(HEX_CHARS[(c >> 12) & 0xf]);
                sb.append(HEX_CHARS[(c >> 8) & 0xf]);
                sb.append(HEX_CHARS[(c >> 4) & 0xf]);
                sb.append(HEX_CHARS[c & 0xf]);
            }
        }

        return sb.toString();
    }

    /**
     * @param cs {@link CharSequence}
     *
     * @return String
     */
    public static String unicodeToHexString(final CharSequence cs)
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

        try (DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream))
        {
            dataoutputstream.writeUTF(cs.toString());
        }
        catch (IOException ex)
        {
            return null;
        }

        return HexFormat.of().withUpperCase().formatHex(bytearrayoutputstream.toByteArray());
    }

    /**
     * Erstellt ein neues {@link StringUtils} Object.
     */
    private StringUtils()
    {
        super();
    }
}
