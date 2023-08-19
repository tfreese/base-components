// Created: 18.08.23
package de.freese.base.persistence.jdbc.newtemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.DataSource;

/**
 * @author Thomas Freese
 */
public class NewJdbcTemplate {

    private final DataSource dataSource;

    public NewJdbcTemplate(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    /**
     * {call my_procedure(?)};
     */
    public CallSpec call(CharSequence sql) {
        return null;
    }

    public DeleteSpec delete(CharSequence sql) {
        return null;
    }

    public InsertSpec insert(CharSequence sql) {
        return null;
    }

    public SelectSpec select(CharSequence sql) {
        return new DefaultSelectSpec(sql, () -> getConnection());
    }

    public UpdateSpec update(CharSequence sql) {
        return null;
    }

    protected Connection getConnection() {
        try {
            return dataSource.getConnection();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
