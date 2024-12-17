// Created: 17.05.2020
package de.freese.base.persistence.jdbc.function;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
public class ResultSetCallbackCachedRowSet implements ResultSetCallback<CachedRowSet> {

    private final RowSetFactory rowSetFactory;

    public ResultSetCallbackCachedRowSet() throws SQLException {
        this(RowSetProvider.newFactory());
    }

    public ResultSetCallbackCachedRowSet(final RowSetFactory rowSetFactory) {
        super();

        this.rowSetFactory = Objects.requireNonNull(rowSetFactory, "rowSetFactory required");
    }

    @Override
    public CachedRowSet doInResultSet(final ResultSet resultSet) throws SQLException {
        final CachedRowSet rowSet = rowSetFactory.createCachedRowSet();

        rowSet.populate(resultSet);

        return rowSet;
    }
}
