package de.freese.base.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public final class ExceptionUtils {
    /**
     * Liefert den Cause des Typs, falls vorhanden.<br>
     */
    public static Exception findCause(final Throwable throwable, final Class<? extends Exception> type) {
        if (type.isInstance(throwable)) {
            return (Exception) throwable;
        }

        final List<Throwable> list = getThrowableList(throwable);

        return list.stream().filter(type::isInstance).map(type::cast).findFirst().orElse(null);
    }

    /**
     * Liefert die enthaltene SQL-Exception, falls vorhanden.<br>
     */
    public static SQLException findSQLException(final Throwable throwable) {
        return (SQLException) findCause(throwable, SQLException.class);
        // if (throwable instanceof SQLException)
        // {
        // return (SQLException) throwable;
        // }
        //
        // final List<Throwable> list = getThrowableList(throwable);
        //
        // return list.stream().filter(SQLException.class::isInstance).map(SQLException.class::cast).findFirst().orElse(null);
    }

    /**
     * <p>
     * Introspects the {@code Throwable} to obtain the root cause.
     * </p>
     * <p>
     * This method walks through the exception chain to the last element, "root" of the tree, using {@link Throwable#getCause()}, and returns that exception.
     * </p>
     * <p>
     * This method handles recursive cause structures that might otherwise cause infinite loops. If the throwable parameter has a cause of itself, then null
     * will be returned. If the throwable parameter cause chain loops, the last element in the chain before the loop is returned.
     * </p>
     *
     * @param throwable the throwable to get the root cause for, may be null
     *
     * @return the root cause of the {@code Throwable}, {@code null} if null throwable input
     */
    public static Throwable getRootCause(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);

        return list.isEmpty() ? null : list.getLast();
    }

    public static String getStackTrace(final Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        else {
            try (StringWriter stringWriter = new StringWriter()) {
                throwable.printStackTrace(new PrintWriter(stringWriter, true));

                return stringWriter.toString();
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * <p>
     * Returns the list of {@code Throwable} objects in the exception chain.
     * </p>
     * <p>
     * A throwable without cause will return a list containing one element - the input throwable. A throwable with one cause will return a list containing two
     * elements. - the input throwable and the cause throwable. A {@code null} throwable will return a list of size zero.
     * </p>
     * <p>
     * This method handles recursive cause structures that might otherwise cause infinite loops. The cause chain is processed until the end is reached, or until
     * the next item in the chain is already in the result set.
     * </p>
     *
     * @param throwable the throwable to inspect, may be null
     *
     * @return List, never null
     */
    public static List<Throwable> getThrowableList(final Throwable throwable) {
        Throwable th = throwable;
        final List<Throwable> list = new ArrayList<>();

        while (th != null && !list.contains(th)) {
            list.add(th);
            th = th.getCause();
        }

        return list;
    }

    private ExceptionUtils() {
        super();
    }
}
