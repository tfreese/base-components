package de.freese.base.core.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * Provides the first n Elements of the StackTrace.
 *
 * @author Thomas Freese
 */
public final class StackTraceLimiter {
    public static StackTraceElement[] getLimitedStackTrace(final Throwable th, final int elements) {
        final StackTraceElement[] limitedTrace = new StackTraceElement[elements];

        System.arraycopy(th.getStackTrace(), 0, limitedTrace, 0, elements);

        return limitedTrace;
    }

    public static void printStackTrace(final Throwable th, final int elements, final PrintStream ps) {
        printStackTrace(th, elements, ps::println);
    }

    public static void printStackTrace(final Throwable th, final int elements, final PrintWriter pw) {
        printStackTrace(th, elements, pw::println);
    }

    public static void printStackTrace(final Throwable th, final int elements, final StringBuilder sb) {
        printStackTrace(th, elements, obj -> sb.append(obj).append(System.lineSeparator()));
    }

    private static void printStackTrace(final Throwable th, final int elements, final Consumer<Object> consumer) {
        consumer.accept(th);

        final StackTraceElement[] limitedTrace = getLimitedStackTrace(th, elements);

        for (StackTraceElement stackTraceElement : limitedTrace) {
            consumer.accept("\tat " + stackTraceElement);
        }

        consumer.accept("\t...");
    }

    private StackTraceLimiter() {
        super();
    }
}
