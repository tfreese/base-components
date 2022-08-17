package de.freese.base.resourcemap;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.Locale;

/**
 * @author Thomas Freese
 */
public final class LookupException extends RuntimeException
{
    /**
     * Print only the first 'n' {@link StackTraceElement}s.
     */
    private static final int LOGGABLE_STACKTRACES = 5;
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7433783834856512381L;

    /**
     * @param baseName String
     * @param key String
     * @param value String
     * @param type Class
     * @param locale {@link Locale}
     * @param info String
     *
     * @return String
     */
    public static String createMessage(final String baseName, final String key, final String value, final Class<?> type, final Locale locale, final String info)
    {
        String format = "%s: Bundle=\"%s\", Key=\"%s\", Value=\"%s\", Type=\"%s\", Locale=\"%s\"";

        return String.format(format, info, baseName, key, value, type.getName(), locale.toString());
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
        this.value = truncate(value);
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

    /**
     * @param s String
     *
     * @return String
     */
    private String truncate(final String s)
    {
        int n = s.length();

        return (n < 128) ? s : s.substring(0, 128) + "...[" + (n - 128) + " more characters]";
    }
}
