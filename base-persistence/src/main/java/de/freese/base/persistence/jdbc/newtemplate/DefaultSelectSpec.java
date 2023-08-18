// Created: 18.08.23
package de.freese.base.persistence.jdbc.newtemplate;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class DefaultSelectSpec implements SelectSpec {

    private final CharSequence sql;

    public DefaultSelectSpec(final CharSequence sql) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
    }

    @Override
    public SelectSpec param(final int index, final Object value) {
        return this;
    }
}
