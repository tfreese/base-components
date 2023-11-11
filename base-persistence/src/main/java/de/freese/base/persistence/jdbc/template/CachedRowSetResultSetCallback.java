// Created: 17.05.2020
package de.freese.base.persistence.jdbc.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import de.freese.base.persistence.jdbc.function.ResultSetCallback;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
public class CachedRowSetResultSetCallback implements ResultSetCallback<CachedRowSet> {

    private final RowSetFactory rowSetFactory;

    public CachedRowSetResultSetCallback() throws SQLException {
        this(RowSetProvider.newFactory());
    }

    public CachedRowSetResultSetCallback(final RowSetFactory rowSetFactory) {
        super();

        this.rowSetFactory = Objects.requireNonNull(rowSetFactory, "rowSetFactory required");
    }

    @Override
    public CachedRowSet doInResultSet(final ResultSet resultSet) throws SQLException {
        CachedRowSet rowSet = this.rowSetFactory.createCachedRowSet();

        rowSet.populate(resultSet);

        return rowSet;
    }
}
