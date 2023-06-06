// Created: 26.03.2019
package de.freese.base.persistence.jdbc.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @param <T> Row-Type
 *
 * @author Thomas Freese
 */
public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {
    private final RowMapper<T> rowMapper;

    public RowMapperResultSetExtractor(final RowMapper<T> rowMapper) {
        super();

        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }
    
    @Override
    public List<T> extractData(final ResultSet resultSet) throws SQLException {
        List<T> results = new ArrayList<>();

        while (resultSet.next()) {
            results.add(this.rowMapper.mapRow(resultSet));
        }

        return results;
    }
}
