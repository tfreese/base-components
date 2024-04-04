// Created: 16.07.2011
package de.freese.base.utils;

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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Thomas Freese
 */
public final class StringUtils {
    /**
     * non-breaking space
     */
    public static final char ASCII_NON_BREAKING_SPACE = 160;
    public static final char ASCII_SPACE = 32;
    public static final String EMPTY = "";
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
     */
    public static String abbreviate(final String str, final int maxWidth) {
        return org.apache.commons.lang3.StringUtils.abbreviate(str, "...", 0, maxWidth);
    }

    /**
     * Fügt vor und nach dem ersten Eintrag der Liste eine Trennlinie ein.<br>
     * Die Breite pro Spalte orientiert sich am ersten Wert (Header) der Spalte.<br>
     */
    public static void addHeaderSeparator(final List<String[]> rows, final String separator) {
        if (rows == null || rows.isEmpty()) {
            return;
        }

        final String sep = separator == null || separator.isBlank() ? "|" : separator;

        final int columnCount = rows.getFirst().length;

        // Trenner zwischen Header und Daten.
        // final T[] row = (T[]) Array.newInstance(String.class, getColumnCount);
        // final T[] row = Arrays.copyOf(rows.get(0), getColumnCount);
        // final T[] row = rows.get(0).clone();
        final String[] row = new String[columnCount];

        for (int column = 0; column < columnCount; column++) {
            row[column] = sep.repeat(rows.getFirst()[column].length());
        }

        rows.add(1, row);
        rows.add(0, row);
    }

    /**
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * StringUtils.capitalize("'cat'") = "'cat'"
     * </pre>
     */
    public static String capitalize(final String text) {
        // value.substring(0, 1).toUpperCase() + value.substring(1);
        return org.apache.commons.lang3.StringUtils.capitalize(text);
    }

    public static void escape(final List<String[]> list, final char escape) {
        if (list == null || list.isEmpty()) {
            return;
        }

        final int columnCount = list.getFirst().length;

        list.stream().parallel().forEach(r -> {
            for (int column = 0; column < columnCount; column++) {
                r[column] = escape + r[column] + escape;
            }
        });
    }

    public static void escape(final String[] array, final char escape) {
        if (ArrayUtils.isEmpty(array)) {
            return;
        }

        for (int column = 0; column < array.length; column++) {
            array[column] = escape + array[column] + escape;
        }
    }

    /**
     * Liefert die Zeichenbreite der Elemente.<br>
     */
    public static int[] getWidths(final List<String[]> list) {
        if (list == null || list.isEmpty()) {
            return new int[0];
        }

        final int columnCount = list.getFirst().length;

        final int[] columnWidths = new int[columnCount];
        Arrays.fill(columnWidths, 0);

        IntStream.range(0, columnCount).forEach(column ->
                columnWidths[column] = list.stream()
                        .parallel()
                        .map(d -> d[column])
                        .filter(Objects::nonNull)
                        .mapToInt(CharSequence::length)
                        .max()
                        .orElse(0)
        );

        return columnWidths;
    }

    /**
     * Liefert die Zeichenbreite der Elemente.<br>
     */
    public static int[] getWidths(final String[] array) {
        if (ArrayUtils.isEmpty(array)) {
            return new int[0];
        }

        final List<String[]> list = new ArrayList<>(1);
        list.add(array);

        return getWidths(list);
    }

    public static String hexStringToUnicode(final CharSequence cs) throws Exception {
        if (isBlank(cs)) {
            return EMPTY;
        }

        final byte[] bytes = HexFormat.of().parseHex(cs);
        String sign = null;

        try (ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(bytes);
             DataInputStream datainputstream = new DataInputStream(bytearrayinputstream)) {
            sign = datainputstream.readUTF();
        }
        catch (IOException ioexception) {
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
     */
    public static boolean isBlank(final CharSequence cs) {
        // return org.apache.commons.lang3.StringUtils.isBlank(cs);

        if (cs == null || cs.isEmpty()) {
            return true;
        }

        final int strLen = cs.length();

        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
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
     */
    public static boolean isEmpty(final CharSequence cs) {
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
     */
    public static boolean isNotBlank(final CharSequence cs) {
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
     */
    public static boolean isNotEmpty(final CharSequence cs) {
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
     */
    public static boolean isNumeric(final CharSequence cs) {
        // return org.apache.commons.lang3.StringUtils.isNumeric(cs);

        if (isEmpty(cs)) {
            return false;
        }

        final int length = cs.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
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
     */
    public static String leftPad(final String text, final int size, final String padStr) {
        // return String.format("%" + size + "s", text).replace(" ", padding);

        return org.apache.commons.lang3.StringUtils.leftPad(text, size, padStr);
    }

    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * Entfernt mehrfach hintereinander auftretende Whitespace Characters und führt ein abschliessendes {@link String#strip()} durch.<br>
     * ASCII 160 (non-breaking space) wird als Space interpretiert.
     */
    public static String normalizeSpace(final CharSequence cs) {
        // return org.apache.commons.lang3.StringUtils.normalizeSpace(text);

        if (isEmpty(cs)) {
            return EMPTY;
        }

        final int size = cs.length();
        final StringBuilder sb = new StringBuilder();
        boolean lastWasWhitespace = false;

        for (int i = 0; i < size; i++) {
            char actualChar = cs.charAt(i);
            boolean isActualWhitespace = Character.isWhitespace(actualChar);

            // ASCII 160 = non-breaking space
            if (actualChar == ASCII_NON_BREAKING_SPACE) {
                actualChar = ASCII_SPACE;
                isActualWhitespace = true;
            }

            if (!isActualWhitespace) {
                sb.append(actualChar);
                lastWasWhitespace = false;
            }
            else if (!lastWasWhitespace) {
                sb.append(ASCII_SPACE);
                lastWasWhitespace = true;
            }
        }

        return sb.toString().strip();
    }

    /**
     * Removes all ASCII Chars < 32 (SPACE) und > 126 (~).<br>
     */
    public static String removeNonAscii(final String input) {
        return removeNonAscii(input, null);
    }

    /**
     * Removes all ASCII Chars < 32 (SPACE) und > 126 (~) and except the Predicates.
     */
    public static String removeNonAscii(final String input, final Predicate<Character> keep) {
        Predicate<Character> filter = c -> c >= 32 && c <= 126;

        if (keep != null) {
            filter = filter.or(keep);
        }

        return replaceChars(input, filter, c -> null);
    }

    /**
     * Removes all ASCII Chars < 32 (SPACE) und > 126 (~) and except german special chars.
     */
    public static String removeNonAsciiGerman(final String input) {
        final Set<Character> keepChars = Set.of(
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
                'ß');

        return removeNonAscii(input, keepChars::contains);
    }

    /**
     * ReplacementFunction:<br>
     * To remove: char -> null<br>
     * To replace: char-> 'something else'
     */
    public static String replaceChars(final String input, final Predicate<Character> keep, final Function<Character, String> replacementFunction) {
        if (input == null || input.isBlank()) {
            return input;
        }

        final StringBuilder sb = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (keep.test(c)) {
                sb.append(c);
            }
            else {
                final String replacement = replacementFunction.apply(c);

                if (replacement != null) {
                    sb.append(replacement);
                }
            }
        }

        return sb.toString();

        //        final char[] chars = input.toCharArray();
        //        int pos = 0;
        //
        //        for (char c : chars) {
        //            if (!keep.test(c)) {
        //                continue;
        //            }
        //
        //            chars[pos++] = c;
        //        }
        //
        //        return new String(chars, 0, pos);
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
     */
    public static String rightPad(final String text, final int size, final String padding) {
        // return String.format("%-" + size + "s", text).replace(" ", padding);

        return org.apache.commons.lang3.StringUtils.rightPad(text, size, padding);
    }

    /**
     * Trennt zusammengefügte Wörter anhand unterschiedlicher Uppercase/Lowercase Schreibweise der Buchstaben<br>
     */
    public static String splitAddedWords(final String text) {
        if (isBlank(text)) {
            return EMPTY;
        }

        final String mText = text.strip();

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < (mText.length() - 1); i++) {
            final char c = mText.charAt(i);
            sb.append(c);

            if (Character.isLowerCase(c) && Character.isUpperCase(mText.charAt(i + 1))) {
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
     */
    public static String[] splitByWholeSeparator(final String text, final String separator) {
        return org.apache.commons.lang3.StringUtils.splitByWholeSeparator(text, separator);
    }

    /**
     * Neue Methode mit Unicode-Standards als {@link String#trim()} Alternative.
     */
    public static String strip(final String text) {
        if (isEmpty(text)) {
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
     */
    public static String stripToEmpty(final String text) {
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
     */
    public static String stripToNull(final String text) {
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
     */
    public static String substringBefore(final String text, final String separator) {
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
    public static String[] substringsBetween(final String str, final String open, final String close) {
        if (str == null || isEmpty(open) || isEmpty(close)) {
            return null;
        }

        final int strLen = str.length();

        if (strLen == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        final int closeLen = close.length();
        final int openLen = open.length();
        final List<String> list = new ArrayList<>();
        int pos = 0;

        while (pos < (strLen - closeLen)) {
            int start = str.indexOf(open, pos);

            if (start < 0) {
                break;
            }

            start += openLen;

            final int end = str.indexOf(close, start);

            if (end < 0) {
                break;
            }

            list.add(str.substring(start, end));
            pos = end + closeLen;
        }

        if (list.isEmpty()) {
            return null;
        }

        return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Konvertiert mehrzeiligen Text in einen einzeiligen Text.<br>
     */
    public static String toSingleLine(final String text) {
        if (isBlank(text)) {
            return EMPTY;
        }

        return text.lines().map(String::strip).collect(Collectors.joining(SPACE));

        // return Stream.of(text)
        //         .map(t -> t.replace("\n", SPACE)) // Keine Zeilenumbrüche
        //         .map(t -> t.replace("\t", SPACE)) // Keine Tabulatoren
        //         .map(t -> t.split(SPACE))
        //         .flatMap(Arrays::stream) // Stream<String[]> in Stream<String> konvertieren
        //         //.peek(System.out::println)
        //         .filter(StringUtils::isNotBlank) // leere Strings filtern
        //         .collect(Collectors.joining(SPACE));  // Strings wieder zusammenführen
    }

    public static String unicodeToHexString(final CharSequence cs) {
        final ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

        try (DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream)) {
            dataoutputstream.writeUTF(cs.toString());
        }
        catch (IOException ex) {
            return null;
        }

        return HexFormat.of().withUpperCase().formatHex(bytearrayoutputstream.toByteArray());
    }

    private StringUtils() {
        super();
    }
}
