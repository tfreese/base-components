// Created: 21 Okt. 2025
package de.freese.base.persistence.jdbc.paging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import de.freese.base.persistence.jdbc.function.RowMapper;

/**
 * @author Thomas Freese
 */
public class JdbcPaginator<T> implements Paginator<T> {
    private final DataSource dataSource;
    private final RowMapper<T> rowMapper;
    private final CharSequence sql;

    public JdbcPaginator(final DataSource dataSource, final CharSequence sql, final RowMapper<T> rowMapper) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
        this.sql = Objects.requireNonNull(sql, "sql required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    @Override
    public List<T> getPage(final int offset, final int limit) {
        final List<T> result = new ArrayList<>();

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getRowMapper().mapRow(resultSet));
                }
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected RowMapper<T> getRowMapper() {
        return rowMapper;
    }

    protected CharSequence getSql() {
        return sql;
    }
}
