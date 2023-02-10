// Created: 04.02.2017
package de.freese.base.persistence.jdbc.sequence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import javax.sql.DataSource;

/**
 * Liefert den nächsten Wert einer Sequence.
 *
 * @author Thomas Freese
 */
public class SequenceQueryExecutor {
    private final SequenceQuery sequenceQuery;

    public SequenceQueryExecutor(final SequenceQuery sequenceQuery) {
        super();

        this.sequenceQuery = Objects.requireNonNull(sequenceQuery, "sequenceQuery required");
    }

    public long getNextID(final String sequence, final Connection connection) throws SQLException {
        String sql = this.sequenceQuery.apply(sequence);
        long id = 0;

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            id = rs.getLong(1);
        }

        return id;
    }

    public long getNextID(final String sequence, final DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return getNextID(sequence, connection);
        }
    }
}
