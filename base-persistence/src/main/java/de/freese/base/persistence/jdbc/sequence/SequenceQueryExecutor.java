/**
 * Created: 04.02.2017
 */

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
public class SequenceQueryExecutor
{
    /**
     *
     */
    private final SequenceQuery sequenceQuery;

    /**
     * Erstellt ein neues {@link SequenceQueryExecutor} Object.
     *
     * @param sequenceQuery {@link SequenceQuery}
     */
    public SequenceQueryExecutor(final SequenceQuery sequenceQuery)
    {
        super();

        this.sequenceQuery = Objects.requireNonNull(sequenceQuery, "sequenceQuery required");
    }

    /**
     * @param sequence String
     * @param connection {@link Connection}
     * @return long
     * @throws SQLException Falls was schief geht.
     */
    public long getNextID(final String sequence, final Connection connection) throws SQLException
    {
        String sql = this.sequenceQuery.apply(sequence);
        long id = 0;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            rs.next();
            id = rs.getLong(1);
        }

        return id;
    }

    /**
     * @param sequence String
     * @param dataSource {@link DataSource}
     * @return long
     * @throws SQLException Falls was schief geht.
     */
    public long getNextID(final String sequence, final DataSource dataSource) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            return getNextID(sequence, connection);
        }
    }
}
