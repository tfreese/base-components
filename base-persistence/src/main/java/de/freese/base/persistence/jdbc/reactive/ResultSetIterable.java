package de.freese.base.persistence.jdbc.reactive;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import de.freese.base.persistence.jdbc.function.RowMapper;

/**
 * {@link Iterable} for a {@link ResultSet}.<br/>
 *
 * @author Thomas Freese
 * @since 12.12.2017
 */
public class ResultSetIterable<T> implements Iterable<T> {
    private final ResultSet resultSet;
    private final RowMapper<T> rowMapper;

    public ResultSetIterable(final ResultSet resultSet, final RowMapper<T> rowMapper) {
        super();

        this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return new ResultSetIterator<>(resultSet, rowMapper);
    }
}
