// Created: 26.03.2019
package de.freese.base.persistence.jdbc.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Stream;

import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * {@link ResultSetExtractor} der einen {@link RowMapper} verwendet für die Erzeugung eines {@link Stream}.
 *
 * @param <T> Row-Type
 *
 * @author Thomas Freese
 */
public class StreamResultSetExtractor<T> implements ResultSetExtractor<Stream<T>>
{
    /**
     *
     */
    private final RowMapper<T> rowMapper;

    /**
     * Erzeugt eine neue Instanz von {@link StreamResultSetExtractor}
     *
     * @param rowMapper {@link RowMapper}
     */
    public StreamResultSetExtractor(final RowMapper<T> rowMapper)
    {
        super();

        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    /**
     * @see de.freese.base.persistence.jdbc.template.function.ResultSetExtractor#extractData(java.sql.ResultSet)
     */
    @Override
    public Stream<T> extractData(final ResultSet rs) throws SQLException
    {
        // List<T> results = new ArrayList<>();
        //
        // while (rs.next())
        // {
        // results.add(this.rowMapper.mapRow(rs));
        // }
        //
        // return results;

        throw new UnsupportedOperationException("not implemented");
    }
}
