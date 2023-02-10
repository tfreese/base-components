// Created: 12.12.2017
package de.freese.base.persistence.jdbc.reactive;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Objects;

import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * {@link Iterable} für ein {@link ResultSet}.<br>
 *
 * @param <T> Type of Entity
 *
 * @author Thomas Freese
 */
public class ResultSetIterable<T> implements Iterable<T> {
    private final ResultSet resultSet;

    private final RowMapper<T> rowMapper;

    public ResultSetIterable(final ResultSet resultSet, final RowMapper<T> rowMapper) {
        super();

        this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return new ResultSetIterator<>(this.resultSet, this.rowMapper);
    }
}
