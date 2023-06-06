// Created: 20.07.2010
package de.freese.base.persistence;

import java.util.Objects;

import org.hibernate.engine.jdbc.internal.FormatStyle;

/**
 * Format SQLs by the Hibernate Formatter.
 *
 * @author Thomas Freese
 */
public class SqlFormatter {
    private FormatStyle formatStyle = FormatStyle.NONE;

    public SqlFormatter disableFormatter() {
        return formatStyle(FormatStyle.NONE);
    }

    public SqlFormatter enableFormatter() {
        return formatStyle(FormatStyle.BASIC);
    }

    public String format(final String sql) {
        return this.formatStyle.getFormatter().format(sql);
    }

    public SqlFormatter formatStyle(final FormatStyle formatStyle) {
        this.formatStyle = Objects.requireNonNull(formatStyle, "formatStyle required");

        return this;
    }

    public boolean isEnabled() {
        return (this.formatStyle != FormatStyle.NONE);
    }
}
