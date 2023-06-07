// Created: 26.03.2019
package de.freese.base.persistence.jdbc.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
public class RowMapperConsumableResultSetExtractor<T> implements ResultSetExtractor<Void> {
    private final Consumer<T> consumer;
    private final RowMapper<T> rowMapper;

    public RowMapperConsumableResultSetExtractor(final RowMapper<T> rowMapper, final Consumer<T> consumer) {
        super();

        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
        this.consumer = Objects.requireNonNull(consumer, "consumer required");
    }

    @Override
    public Void extractData(final ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            consumer.accept(this.rowMapper.mapRow(resultSet));
        }

        return null;
    }
}
