/**
 * Created: 26.03.2019
 */

package de.freese.base.persistence.jdbc.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * {@link ResultSetExtractor} der einen {@link RowMapper} verwendet für die Erzeugung Ergebnis-Liste.
 *
 * @param <T> Konkreter Row-Typ
 * @author Thomas Freese
 */
public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>>
{
    /**
     *
     */
    private final RowMapper<T> rowMapper;

    /**
     * Erzeugt eine neue Instanz von {@link RowMapperResultSetExtractor}
     *
     * @param rowMapper {@link RowMapper}
     */
    public RowMapperResultSetExtractor(final RowMapper<T> rowMapper)
    {
        super();

        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    /**
     * @see de.freese.base.persistence.jdbc.template.function.ResultSetExtractor#extractData(java.sql.ResultSet)
     */
    @Override
    public List<T> extractData(final ResultSet rs) throws SQLException
    {
        List<T> results = new ArrayList<>();

        while (rs.next())
        {
            results.add(this.rowMapper.mapRow(rs));
        }

        return results;
    }
}
