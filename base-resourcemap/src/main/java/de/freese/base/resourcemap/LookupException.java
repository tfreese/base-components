package de.freese.base.resourcemap;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Diese {@link Exception} wird von einer {@link ResourceMap} geworfen, wenn eine Resource nicht gefunden wurde.
 *
 * @author Thomas Freese
 */
public final class LookupException extends RuntimeException
{
    /**
     * Konstante um nur die ersten n {@link StackTraceElement}e auszugeben.
     */
    private static final int LOGGABLE_STACKTRACES = 5;

    /**
     *
     */
    private static final long serialVersionUID = 7433783834856512381L;

    /**
     * @param baseName String
     * @param key String
     * @param value String
     * @param type Class
     * @param locale {@link Locale}
     * @param info String
     * @return String
     */
    public static final String createMessage(final String baseName, final String key, final String value, final Class<?> type, final Locale locale,
                                             final String info)
    {
        String format = "%s: Bundle=\"%s\", Key=\"%s\", Type=\"%s\", Locale=\"%s\"";

        return String.format(format, info, baseName, key, type.getName(), locale.toString());
    }

    /**
     *
     */
    private final String baseName;

    /**
     *
     */
    private final String info;

    /**
     *
     */
    private final String key;

    /**
    *
    */
    private final Locale locale;

    /**
     *
     */
    private final Class<?> type;

    /**
     *
     */
    private final String value;

    /**
     * Constructs an instance of this class with some useful information about the failure.
     *
     * @param baseName String
     * @param key String
     * @param value String
     * @param type Class
     * @param locale {@link Locale}
     * @param info String
     */
    LookupException(final String baseName, final String key, final String value, final Class<?> type, final Locale locale, final String info)
    {
        super(createMessage(baseName, key, value, type, locale, info));

        this.baseName = baseName;
        this.key = key;
        this.value = maybeShorten(value);
        this.type = type;
        this.locale = locale;
        this.info = info;
    }

    /**
     * @return String
     */
    public String getBaseName()
    {
        return this.baseName;
    }

    /**
     * @return String
     */
    public String getInfo()
    {
        return this.info;
    }

    /**
     * @return String
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * @return {@link Locale}
     */
    public Locale getLocale()
    {
        return this.locale;
    }

    /**
     * @return Class
     */
    public Class<?> getType()
    {
        return this.type;
    }

    /**
     * @return String
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Kuerzung der Fehlermeldung.
     *
     * @param s String
     * @return String
     */
    private String maybeShorten(final String s)
    {
        int n = s.length();

        return (n < 128) ? s : s.substring(0, 128) + "...[" + (n - 128) + " more characters]";
    }

    /**
     * Gibt nicht den kompletten StackTrace aus, sondern nur die ersten n Elemente.
     *
     * @see LookupException#LOGGABLE_STACKTRACES
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    @Override
    public void printStackTrace(final PrintStream printStream)
    {
        printStream.println(this);
        StackTraceElement[] trace = getStackTrace();

        for (int i = 0; i < LOGGABLE_STACKTRACES; i++)
        {
            printStream.println("\tat " + trace[i]);
        }

        printStream.println("\t...");
    }

    /**
     * Gibt nicht den kompletten StackTrace aus, sondern nur die ersten n Elemente.
     *
     * @see LookupException#LOGGABLE_STACKTRACES
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    @Override
    public void printStackTrace(final PrintWriter printWriter)
    {
        printWriter.println(this);
        StackTraceElement[] trace = getStackTrace();

        for (int i = 0; i < LOGGABLE_STACKTRACES; i++)
        {
            printWriter.println("\tat " + trace[i]);
        }

        printWriter.println("\t...");
    }
}
