/**
 * Created: 16.07.2011
 */
package de.freese.base.utils;

import static de.freese.base.utils.ByteUtils.HEX_CHARS;
import static de.freese.base.utils.ByteUtils.HEX_INDEX;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Nuetzliches fuer Strings.
 *
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
     * Fügt am Index 1 der Liste eine Trenn-Linie ein.<br>
     * Die Breite pro Spalte orientiert sich am ersten Wert (Header) der Spalte.<br>
     *
     * @param <T> Konkreter Typ
     * @param rows {@link List}
     * @param separator String
     */
    @SuppressWarnings("unchecked")
    public static <T extends CharSequence> void addHeaderSeparator(final List<T[]> rows, final String separator)
    {
        if (ListUtils.isEmpty(rows))
        {
            return;
        }

        String sep = (separator == null) || separator.strip().isEmpty() ? "-" : separator;

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
    }

    /**
     * @param text String
     * @return String, not null
     */
    public static String asciiToUnicode(final String text)
    {
        if (isBlank(text))
        {
            return EMPTY;
        }

        if (text.indexOf("\\u") == -1)
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
     * @return String
     */
    public static String capitalize(final String text)
    {
        return org.apache.commons.lang3.StringUtils.capitalize(text);
    }

    /**
     * <pre>
     * StringUtils.containsIgnoreCase(null, *) = false
     * StringUtils.containsIgnoreCase(*, null) = false
     * StringUtils.containsIgnoreCase("", "") = true
     * StringUtils.containsIgnoreCase("abc", "") = true
     * StringUtils.containsIgnoreCase("abc", "a") = true
     * StringUtils.containsIgnoreCase("abc", "z") = false
     * StringUtils.containsIgnoreCase("abc", "A") = true
     * StringUtils.containsIgnoreCase("abc", "Z") = false
     * </pre>
     *
     * @param cs {@link CharSequence}
     * @param search {@link CharSequence}
     * @return boolean
     */
    public static boolean containsIgnoreCase(final CharSequence cs, final CharSequence search)
    {
        return org.apache.commons.lang3.StringUtils.containsIgnoreCase(cs, search);
    }

    /**
     * <pre>
     * StringUtils.defaultIfBlank(null, "NULL")  = "NULL"
     * StringUtils.defaultIfBlank("", "NULL")    = "NULL"
     * StringUtils.defaultIfBlank(" ", "NULL")   = "NULL"
     * StringUtils.defaultIfBlank("bat", "NULL") = "bat"
     * StringUtils.defaultIfBlank("", null)      = null
     * </pre>
     *
     * @param text String
     * @param defaultText String
     * @return String
     */
    public static String defaultIfBlank(final String text, final String defaultText)
    {
        return isBlank(text) ? defaultText : text;
    }

    /**
     * <pre>
     * StringUtils.defaultIfEmpty(null, "NULL")  = "NULL"
     * StringUtils.defaultIfEmpty("", "NULL")    = "NULL"
     * StringUtils.defaultIfEmpty(" ", "NULL")   = " "
     * StringUtils.defaultIfEmpty("bat", "NULL") = "bat"
     * StringUtils.defaultIfEmpty("", null)      = null
     * </pre>
     *
     * @param text String
     * @param defaultText String
     * @return String
     */
    public static <T extends CharSequence> T defaultIfEmpty(final T text, final T defaultText)
    {
        return isEmpty(text) ? defaultText : text;
    }

    /**
     * Liefert die max. Zeichenbreite der Elemente.<br>
     * Leerzeichen werden ignoriert.
     *
     * @param array String[]
     * @return int
     */
    public static int getMaxWidth(final String[] array)
    {
        if (ArrayUtils.isEmpty(array))
        {
            return 0;
        }

        // @formatter:off
        int width = Stream.of(array)
                .parallel()
                .map(s -> strip(s))
                .filter(s -> isNotBlank(s))
                //.peek(System.out::println)
                .mapToInt(String::length)
                .max()
                .orElse(0);
        // @formatter:on

        return width;
    }

    /**
     * @param cs {@link CharSequence}
     * @return String, not null
     * @throws Exception Falls was schief geht.
     */
    public static String hexStringToUnicode(final CharSequence cs) throws Exception
    {
        if (isBlank(cs))
        {
            return EMPTY;
        }

        byte[] bytes = ByteUtils.hexToBytes(cs);
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
     * @return boolean
     */
    public static boolean isBlank(final CharSequence cs)
    {
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

        // return org.apache.commons.lang3.StringUtils.isBlank(cs);
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
     * @return boolean
     */
    public static boolean isNotBlank(final CharSequence cs)
    {
        return !isNotBlank(cs);
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
     * @return String
     */
    public static boolean isNumeric(final CharSequence cs)
    {
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

        // return org.apache.commons.lang3.StringUtils.isNumeric(cs);
    }

    /**
     * @param array Object[]
     * @param separator String
     * @return String, not null
     */
    public static String join(final Object[] array, final String separator)
    {
        if (ArrayUtils.isEmpty(array))
        {
            return EMPTY;
        }

        return Stream.of(array).map(obj -> obj != null ? obj.toString() : "null").collect(Collectors.joining(separator));
        // return Joiner.on(separator).join(array);
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
     * @return String
     */
    public static String leftPad(final String text, final int size, final String padStr)
    {
        return org.apache.commons.lang3.StringUtils.leftPad(text, size, padStr);
    }

    /**
     * @param cs {@link CharSequence}
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
     * @return String
     * @see Character#isWhitespace
     */
    public static String normalizeSpace(final CharSequence cs)
    {
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
            else if (!lastWasWhitespace && isActualWhitespace)
            {
                sb.append(ASCII_SPACE);
                lastWasWhitespace = true;
            }
        }

        return sb.toString().strip();

        // return org.apache.commons.lang3.StringUtils.normalizeSpace(text);
    }

    /**
     * Die Spaltenbreite der Elemente wird auf den breitesten Wert durch das Padding aufgefüllt.<br>
     * Beim Padding werden die CharSequences durch Strings ersetzt.
     *
     * @param <T> Konkreter Typ
     * @param rows {@link List}
     * @param padding String
     * @see #write(List, PrintStream, String)
     */
    @SuppressWarnings("unchecked")
    public static <T extends CharSequence> void padding(final List<T[]> rows, final String padding)
    {
        if (ListUtils.isEmpty(rows))
        {
            return;
        }

        int columnCount = rows.get(0).length;

        // Breite pro Spalte rausfinden.
        int[] columnWidth = new int[columnCount];

        // @formatter:off
        IntStream.range(0, columnCount).forEach(column
                ->
                {
                    columnWidth[column] = rows.stream()
                            .parallel()
                            .map(r -> r[column])
                            .mapToInt(CharSequence::length)
                            .max()
                            .orElse(0);
        });
        // @formatter:on

        // Strings pro Spalte formatieren und schreiben.
        String pad = (padding == null) || padding.strip().isEmpty() ? SPACE : padding;

        rows.stream().parallel().forEach(r -> {
            for (int column = 0; column < columnCount; column++)
            {
                String value = rightPad(r[column].toString(), columnWidth[column], pad);

                r[column] = (T) value;
            }
        });
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
     * @return String
     */
    public static String repeat(final CharSequence cs, final int repeat)
    {
        if (cs == null)
        {
            return null;
        }

        if (repeat <= 0)
        {
            return EMPTY;
        }

        if (cs.length() == 0)
        {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < repeat; i++)
        {
            sb.append(cs);
        }

        return sb.toString();

        // return org.apache.commons.lang3.StringUtils.repeat(text, repeat);
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
     * @param padStr String
     * @return String
     */
    public static String rightPad(final String text, final int size, final String padStr)
    {
        return org.apache.commons.lang3.StringUtils.rightPad(text, size, padStr);
    }

    /**
     * Trennt zusammengefuegte Woerter anhand unterschied Uppercase/Lowercase der Buchstaben<br>
     *
     * @param text String
     * @return String, not null
     */
    public static String splitAddedWords(final String text)
    {
        if (isBlank(text))
        {
            return EMPTY;
        }

        String m_Text = text.strip();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < (m_Text.length() - 1); i++)
        {
            char c = m_Text.charAt(i);
            sb.append(c);

            if ((Character.isLowerCase(c) && Character.isUpperCase(m_Text.charAt(i + 1))))
            {
                sb.append(" ");
            }
        }

        // Das letzte Zeichen nicht vergessen.
        sb.append(m_Text.charAt(m_Text.length() - 1));

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
     * @return String
     */
    public static String substringBefore(final String text, final String separator)
    {
        return org.apache.commons.lang3.StringUtils.substringBefore(text, separator);
    }

    /**
     * Konvertiert mehrzeiligen Text in einen zeiligen Text.<br>
     *
     * @param text String
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
                .map(t -> t.replaceAll("\n", SPACE)) // Keine Zeilenumbrüche
                .map(t -> t.replaceAll("\t", SPACE)) // Keine Tabulatoren
                .map(t -> t.split(SPACE))
                .flatMap(Arrays::stream) // Stream<String[]> in Stream<String> konvertieren
                //.peek(System.out::println)
                .filter(s -> isNotBlank(s)) // leere Strings filtern
                .collect(Collectors.joining(SPACE));  // Strings wieder zusammenführen
        // @formatter:on

        return line;
    }

    /**
     * Verwendet intern die neue {@link String#strip()} Methode mit Unicode-Standards.
     *
     * @param text String
     * @return String
     */
    public static String trim(final String text)
    {
        return strip(text);
    }

    /**
     * @param cs {@link CharSequence}
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

        return ByteUtils.bytesToHex(bytearrayoutputstream.toByteArray());
    }

    /**
     * Schreibt die Liste in den PrintStream.<br>
     * Der Stream wird nicht geschlossen.
     *
     * @param <T> Konkreter Typ von CharSequence
     * @param rows {@link List}
     * @param ps {@link PrintStream}
     * @param separator String
     * @see #padding(List, String)
     */
    @SuppressWarnings("resource")
    public static <T extends CharSequence> void write(final List<T[]> rows, final PrintStream ps, final String separator)
    {
        Objects.requireNonNull(rows, "rows required");
        Objects.requireNonNull(ps, "printStream required");

        if (rows.isEmpty())
        {
            return;
        }

        // int columnCount = rows.get(0).length;

        // Strings pro Spalte schreiben, parallel() verfälscht die Reihenfolge.
        rows.forEach(r -> ps.println(join(r, separator)));

        ps.flush();
    }

    /**
     * Erstellt ein neues {@link StringUtils} Object.
     */
    private StringUtils()
    {
        super();
    }
}
