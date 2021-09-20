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
 * {@link ResultSetExtractor} der ein {@link CachedRowSet} liefert.
 *
 * @author Thomas Freese
 */
public class CachedRowSetResultSetExtractor implements ResultSetExtractor<CachedRowSet>
{
    /**
     *
     */
    private static final RowSetFactory DEFAULT_ROW_SET_FACTORY;

    static
    {
        try
        {
            DEFAULT_ROW_SET_FACTORY = RowSetProvider.newFactory();
        }
        catch (SQLException ex)
        {
            throw new IllegalStateException("Can't create RowSetFactory through RowSetProvider", ex);
        }
    }

    /**
     *
     */
    private final RowSetFactory rowSetFactory;

    /**
     * Erstellt ein neues {@link CachedRowSetResultSetExtractor} Object.
     */
    public CachedRowSetResultSetExtractor()
    {
        this(DEFAULT_ROW_SET_FACTORY);
    }

    /**
     * Erstellt ein neues {@link CachedRowSetResultSetExtractor} Object.
     *
     * @param rowSetFactory {@link RowSetFactory}
     */
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
