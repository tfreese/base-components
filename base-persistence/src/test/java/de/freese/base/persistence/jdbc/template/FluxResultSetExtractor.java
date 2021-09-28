// Created: 26.03.2019
package de.freese.base.persistence.jdbc.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;
import reactor.core.publisher.Flux;

/**
 * {@link ResultSetExtractor} der einen {@link RowMapper} verwendet für die Erzeugung eines {@link Flux}.
 *
 * @param <T> Row-Type
 *
 * @author Thomas Freese
 */
public class FluxResultSetExtractor<T> implements ResultSetExtractor<Flux<T>>
{
    /**
     *
     */
    private final RowMapper<T> rowMapper;

    /**
     * Erzeugt eine neue Instanz von {@link FluxResultSetExtractor}
     *
     * @param rowMapper {@link RowMapper}
     */
    public FluxResultSetExtractor(final RowMapper<T> rowMapper)
    {
        super();

        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    /**
     * @see de.freese.base.persistence.jdbc.template.function.ResultSetExtractor#extractData(java.sql.ResultSet)
     */
    @Override
    public Flux<T> extractData(final ResultSet rs) throws SQLException
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
