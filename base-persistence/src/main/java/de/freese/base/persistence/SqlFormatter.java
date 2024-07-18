// Created: 20.07.2010
package de.freese.base.persistence;

import org.hibernate.engine.jdbc.internal.FormatStyle;

/**
 * Format SQLs by the Hibernate Formatter.
 *
 * @author Thomas Freese
 */
public final class SqlFormatter {
    public static String formatDDL(final String value) {
        return FormatStyle.DDL.getFormatter().format(value);
    }

    public static String formatNone(final String value) {
        return FormatStyle.NONE.getFormatter().format(value);
    }

    public static String formatSQL(final String value) {
        return FormatStyle.DDL.getFormatter().format(value);
    }

    private SqlFormatter() {
        super();
    }
}
