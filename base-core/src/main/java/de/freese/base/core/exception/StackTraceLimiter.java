package de.freese.base.core.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * Diese Klasse dient zur teilweisen Ausgabe eines StackTraces.
 *
 * @author Thomas Freese
 */
public final class StackTraceLimiter
{
    /**
     * Liefert die ersten n Elemente des StackTraces.
     *
     * @param th {@link Throwable}
     * @param elements int
     *
     * @return {@link StackTraceElement}[]
     */
    public static StackTraceElement[] getLimitedStackTrace(final Throwable th, final int elements)
    {
        StackTraceElement[] limitedTrace = new StackTraceElement[elements];

        System.arraycopy(th.getStackTrace(), 0, limitedTrace, 0, elements);

        return limitedTrace;
    }

    /**
     * Gibt nicht den kompletten StackTrace aus, sondern nur die ersten n Elemente.
     *
     * @param th {@link Throwable}
     * @param elements int
     * @param ps {@link PrintStream}
     */
    public static void printStackTrace(final Throwable th, final int elements, final PrintStream ps)
    {
        printStackTrace(th, elements, ps::println);
    }

    /**
     * Gibt nicht den kompletten StackTrace aus, sondern nur die ersten n Elemente.
     *
     * @param th {@link Throwable}
     * @param elements int
     * @param pw {@link PrintWriter}
     */
    public static void printStackTrace(final Throwable th, final int elements, final PrintWriter pw)
    {
        printStackTrace(th, elements, pw::println);
    }

    /**
     * Gibt nicht den kompletten StackTrace aus, sondern nur die ersten n Elemente.
     *
     * @param th {@link Throwable}
     * @param elements int
     * @param sb {@link StringBuilder}
     */
    public static void printStackTrace(final Throwable th, final int elements, final StringBuilder sb)
    {
        printStackTrace(th, elements, obj -> sb.append(obj).append(System.lineSeparator()));
    }

    /**
     * Gibt nicht den kompletten StackTrace aus, sondern nur die ersten n Elemente.
     *
     * @param th {@link Throwable}
     * @param elements int
     * @param consumer {@link Consumer}
     */
    private static void printStackTrace(final Throwable th, final int elements, final Consumer<Object> consumer)
    {
        consumer.accept(th);
        StackTraceElement[] limitedTrace = getLimitedStackTrace(th, elements);

        for (StackTraceElement stackTraceElement : limitedTrace)
        {
            consumer.accept("\tat " + stackTraceElement);
        }

        consumer.accept("\t...");
    }

    /**
     * Erstellt ein neues {@link StackTraceLimiter} Objekt.
     */
    private StackTraceLimiter()
    {
        super();
    }
}
