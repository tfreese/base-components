// Created: 26.03.2019
package de.freese.base.persistence.jdbc.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.function.ResultSetCallback;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
public class RowMapperResultSetCallback<T> implements ResultSetCallback<List<T>> {

    private final RowMapper<T> rowMapper;

    public RowMapperResultSetCallback(final RowMapper<T> rowMapper) {
        super();

        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    @Override
    public List<T> doInResultSet(final ResultSet resultSet) throws SQLException {
        List<T> results = new ArrayList<>();

        while (resultSet.next()) {
            results.add(this.rowMapper.mapRow(resultSet));
        }

        return results;
    }
}
