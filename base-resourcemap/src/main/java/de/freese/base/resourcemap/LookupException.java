package de.freese.base.resourcemap;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.Locale;

/**
 * @author Thomas Freese
 */
public final class LookupException extends RuntimeException {
    /**
     * Print only the first 'n' {@link StackTraceElement}s.
     */
    private static final int LOGGABLE_STACKTRACES = 5;

    @Serial
    private static final long serialVersionUID = 7433783834856512381L;

    public static String createMessage(final String baseName, final String key, final String value, final Class<?> type, final Locale locale, final String info) {
        final String format = "%s: Bundle=\"%s\", Key=\"%s\", Value=\"%s\", Type=\"%s\", Locale=\"%s\"";

        return String.format(format, info, baseName, key, value, type.getName(), locale.toString());
    }

    private final String baseName;
    private final String info;
    private final String key;
    private final Locale locale;
    private final Class<?> type;
    private final String value;

    LookupException(final String baseName, final String key, final String value, final Class<?> type, final Locale locale, final String info) {
        super(createMessage(baseName, key, value, type, locale, info));

        this.baseName = baseName;
        this.key = key;
        this.value = truncate(value);
        this.type = type;
        this.locale = locale;
        this.info = info;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getInfo() {
        return info;
    }

    public String getKey() {
        return key;
    }

    public Locale getLocale() {
        return locale;
    }

    public Class<?> getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void printStackTrace(final PrintStream printStream) {
        printStream.println(this);
        final StackTraceElement[] trace = getStackTrace();

        for (int i = 0; i < LOGGABLE_STACKTRACES; i++) {
            printStream.println("\tat " + trace[i]);
        }

        printStream.println("\t...");
    }

    @Override
    public void printStackTrace(final PrintWriter printWriter) {
        printWriter.println(this);
        final StackTraceElement[] trace = getStackTrace();

        for (int i = 0; i < LOGGABLE_STACKTRACES; i++) {
            printWriter.println("\tat " + trace[i]);
        }

        printWriter.println("\t...");
    }

    private String truncate(final String s) {
        final int n = s.length();

        return (n < 128) ? s : (s.substring(0, 128) + "...[" + (n - 128) + " more characters]");
    }
}
