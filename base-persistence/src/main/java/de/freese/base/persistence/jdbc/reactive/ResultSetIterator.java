// Created: 27.05.2016
package de.freese.base.persistence.jdbc.reactive;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import de.freese.base.persistence.jdbc.function.RowMapper;

/**
 * {@link Iterator} for a {@link ResultSet}.<br/>
 *
 * @author Thomas Freese
 */
public class ResultSetIterator<T> implements Iterator<T> {

    private final ResultSet resultSet;

    private final RowMapper<T> rowMapper;

    public ResultSetIterator(final ResultSet resultSet, final RowMapper<T> rowMapper) {
        super();

        this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    @Override
    public boolean hasNext() {
        try {
            return this.resultSet.next();
            // return !this.resultSet.isClosed() && !this.resultSet.isAfterLast() && this.resultSet.next();
        }
        catch (SQLException sex) {
            throw new NoSuchElementException(sex.getMessage());
        }
    }

    @Override
    public T next() {
        try {
            return this.rowMapper.mapRow(this.resultSet);
        }
        catch (SQLException ex) {
            throw new NoSuchElementException(ex);
        }
    }
}
