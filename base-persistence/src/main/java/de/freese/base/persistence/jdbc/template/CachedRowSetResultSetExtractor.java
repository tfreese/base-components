// Created: 17.05.2020
package de.freese.base.persistence.jdbc.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
public class CachedRowSetResultSetExtractor implements ResultSetExtractor<CachedRowSet>
{
    private final RowSetFactory rowSetFactory;

    public CachedRowSetResultSetExtractor() throws SQLException
    {
        this(RowSetProvider.newFactory());
    }

    public CachedRowSetResultSetExtractor(final RowSetFactory rowSetFactory)
    {
        super();

        this.rowSetFactory = Objects.requireNonNull(rowSetFactory, "rowSetFactory required");
    }

    /**
     * @see de.freese.base.persistence.jdbc.template.function.ResultSetExtractor#extractData(java.sql.ResultSet)
     */
    @Override
    public CachedRowSet extractData(final ResultSet resultSet) throws SQLException
    {
        CachedRowSet rowSet = this.rowSetFactory.createCachedRowSet();

        rowSet.populate(resultSet);

        return rowSet;
    }
}
