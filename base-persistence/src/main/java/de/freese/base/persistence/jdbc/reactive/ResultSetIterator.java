// Created: 27.05.2016
package de.freese.base.persistence.jdbc.reactive;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * {@link Iterator} für ein {@link ResultSet}.<br>
 *
 * @author Thomas Freese
 *
 * @param <T> Type of Entity
 */
public class ResultSetIterator<T> implements Iterator<T>
{
    /**
     *
     */
    private final ResultSet resultSet;
    /**
    *
    */
    private final RowMapper<T> rowMapper;

    /**
     * Erstellt ein neues {@link ResultSetIterator} Object.
     *
     * @param resultSet {@link ResultSet}
     * @param rowMapper {@link RowMapper}
     */
    public ResultSetIterator(final ResultSet resultSet, final RowMapper<T> rowMapper)
    {
        super();

        this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext()
    {
        try
        {
            return this.resultSet.next();
            // return !this.resultSet.isClosed() && !this.resultSet.isAfterLast() && this.resultSet.next();
        }
        catch (SQLException sex)
        {
            throw new NoSuchElementException(sex.getMessage());
        }
    }

    /**
     * @see java.util.Iterator#next()
     */
    @Override
    public T next()
    {
        try
        {
            return this.rowMapper.mapRow(this.resultSet);
        }
        catch (SQLException ex)
        {
            throw new NoSuchElementException(ex);
        }
    }
}
