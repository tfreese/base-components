/**
 *
 */
package de.freese.base.core.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * Diese Klasse dient dient zur Teilweisen Ausgabe eines StackTraces.
 *
 * @author Thomas Freese
 */
public class StackTraceLimiter
{
    /**
     * Liefert die ersten n Elemente des StackTraces.
     *
     * @param th {@link Throwable}
     * @param elements int
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
     * @param consumer {@link Consumer}
     * @param elements int
     */
    private static void printStackTrace(final Throwable th, final Consumer<Object> consumer, final int elements)
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
     * Gibt nicht den kompletten StackTrace aus, sondern nur die ersten n Elemente.
     *
     * @param th {@link Throwable}
     * @param ps {@link PrintStream}
     * @param elements int
     */
    public static void printStackTrace(final Throwable th, final PrintStream ps, final int elements)
    {
        printStackTrace(th, ps::println, elements);
    }

    /**
     * Gibt nicht den kompletten StackTrace aus, sondern nur die ersten n Elemente.
     *
     * @param th {@link Throwable}
     * @param pw {@link PrintWriter}
     * @param elements int
     */
    public static void printStackTrace(final Throwable th, final PrintWriter pw, final int elements)
    {
        printStackTrace(th, pw::println, elements);
    }

    /**
     * Gibt nicht den kompletten StackTrace aus, sondern nur die ersten n Elemente.
     *
     * @param th {@link Throwable}
     * @param sb {@link StringBuilder}
     * @param elements int
     */
    public static void printStackTrace(final Throwable th, final StringBuilder sb, final int elements)
    {
        printStackTrace(th, obj -> sb.append(obj).append("\n"), elements);
    }

    /**
     * Erstellt ein neues {@link StackTraceLimiter} Objekt.
     */
    private StackTraceLimiter()
    {
        super();
    }
}
