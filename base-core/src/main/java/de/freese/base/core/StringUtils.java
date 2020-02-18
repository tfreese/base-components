/**
 * Created: 16.07.2011
 */
package de.freese.base.core;

import static de.freese.base.core.ByteUtils.HEX_CHARS;
import static de.freese.base.core.ByteUtils.HEX_INDEX;
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
        if ((rows == null) || rows.isEmpty())
        {
            return;
        }

        String sep = (separator == null) || separator.trim().isEmpty() ? "-" : separator;

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
     * @param s String
     * @return String
     */
    public static String asciiToUnicode(final String s)
    {
        if ((s == null) || (s.indexOf("\\u") == -1))
        {
            return s;
        }

        int i = s.length();
        char[] ac = new char[i];
        int j = 0;

        for (int k = 0; k < i; k++)
        {
            char c = s.charAt(k);

            if ((c == '\\') && (k < (i - 5)))
            {
                char c1 = s.charAt(k + 1);

                if (c1 == 'u')
                {
                    k++;

                    int l = HEX_INDEX.indexOf(s.charAt(++k)) << 12;
                    l += (HEX_INDEX.indexOf(s.charAt(++k)) << 8);
                    l += (HEX_INDEX.indexOf(s.charAt(++k)) << 4);
                    l += HEX_INDEX.indexOf(s.charAt(++k));
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
     * Liefert die max. Zeichenbreite der Elemente.<br>
     * Leerzeichen werden ignoriert.
     *
     * @param iterable String[]
     * @return int
     */
    public static int getMaxWidth(final String[] iterable)
    {
        // @formatter:off
        int width = Arrays.stream(iterable) // Stream.of
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
     * @param s {@link CharSequence}
     * @return String
     * @throws Exception Falls was schief geht.
     */
    public static String hexStringToUnicode(final CharSequence s) throws Exception
    {
        byte[] bytes = ByteUtils.hexToBytes(s);
        String sign = null;

        try (ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(bytes);
             DataInputStream datainputstream = new DataInputStream(bytearrayinputstream))
        {
            sign = datainputstream.readUTF();
        }
        catch (IOException ioexception)
        {
            sign = null;
        }

        return sign;
    }

    /**
     * @see org.apache.commons.lang3.StringUtils#isBlank(CharSequence)
     * @param cs {@link CharSequence}
     * @return boolean
     */
    public static boolean isBlank(final CharSequence cs)
    {
        return org.apache.commons.lang3.StringUtils.isBlank(cs);
    }

    /**
     * @see org.apache.commons.lang3.StringUtils#isEmpty(CharSequence)
     * @param cs {@link CharSequence}
     * @return boolean
     */
    public static boolean isEmpty(final CharSequence cs)
    {
        return org.apache.commons.lang3.StringUtils.isEmpty(cs);
    }

    /**
     * @see org.apache.commons.lang3.StringUtils#isNotBlank(CharSequence)
     * @param cs {@link CharSequence}
     * @return boolean
     */
    public static boolean isNotBlank(final CharSequence cs)
    {
        return org.apache.commons.lang3.StringUtils.isNotBlank(cs);
    }

    /**
     * @see org.apache.commons.lang3.StringUtils#join(Object[], String)
     * @param array Object[]
     * @param separator String
     * @return String
     */
    public static String join(final Object[] array, final String separator)
    {
        // Collector verwendet intern den StringJoiner.
        // rows.forEach(r -> ps.println(Stream.of(r).collect(Collectors.joining(separator))));
        return org.apache.commons.lang3.StringUtils.join(array, separator);
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
        if ((rows == null) || rows.isEmpty())
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
        String pad = (padding == null) || padding.trim().isEmpty() ? " " : padding;

        rows.stream().parallel().forEach(r -> {
            for (int column = 0; column < columnCount; column++)
            {
                String value = rightPad(r[column].toString(), columnWidth[column], pad);

                r[column] = (T) value;
            }
        });
    }

    /**
     * @see org.apache.commons.lang3.StringUtils#repeat(String, int)
     * @param str String
     * @param repeat int
     * @return String
     */
    public static String repeat(final String str, final int repeat)
    {
        return org.apache.commons.lang3.StringUtils.repeat(str, repeat);
    }

    /**
     * @see org.apache.commons.lang3.StringUtils#rightPad(String, int, String)
     * @param str String
     * @param size int
     * @param padStr String
     * @return String
     */
    public static String rightPad(final String str, final int size, final String padStr)
    {
        return org.apache.commons.lang3.StringUtils.rightPad(str, size, padStr);
    }

    /**
     * Trennt zusammengefuegte Woerter anhand unterschied Uppercase/Lowercase der Buchstaben<br>
     *
     * @param text String
     * @return String
     */
    public static String splitAddedWords(final String text)
    {
        if (isBlank(text))
        {
            return "";
        }

        String m_Text = text.trim();

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
     * @see org.apache.commons.lang3.StringUtils#strip(String)
     * @param str String
     * @return String
     */
    public static String strip(final String str)
    {
        return org.apache.commons.lang3.StringUtils.strip(str);
    }

    /**
     * Konvertiert mehrzeiligen Text in einen zeiligen Text.<br>
     *
     * @param text String
     * @return Formatierter String oder null wenn String null war
     */
    public static String toSingleLine(final String text)
    {
        if (isBlank(text))
        {
            return "";
        }

        // @formatter:off
        String line = Stream.of(text)
                .map(t -> t.replaceAll("\n", " ")) // Keine Zeilenumbrüche
                .map(t -> t.replaceAll("\t", " ")) // Keine Tabulatoren
                .map(t -> t.split(" "))
                .flatMap(Arrays::stream) // Stream<String[]> in Stream<String> konvertieren
                //.peek(System.out::println)
                .filter(s -> isNotBlank(s)) // leere Strings filtern
                .collect(Collectors.joining(" "));  // Strings wieder zusammenführen
        // @formatter:on

        return line;
    }

    /**
     * @param s {@link CharSequence}
     * @return String
     */
    public static String unicodeToAscii(final CharSequence s)
    {
        if ((s == null) || (s.length() == 0))
        {
            return null;
        }

        int i = s.length();
        StringBuilder sb = new StringBuilder(i + 16);

        for (int j = 0; j < i; j++)
        {
            char c = s.charAt(j);

            if (c == '\\')
            {
                if ((j < (i - 1)) && (s.charAt(j + 1) == 'u'))
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
     * @param s {@link CharSequence}
     * @return String
     */
    public static String unicodeToHexString(final CharSequence s)
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

        try (DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream))
        {
            dataoutputstream.writeUTF(s.toString());
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
