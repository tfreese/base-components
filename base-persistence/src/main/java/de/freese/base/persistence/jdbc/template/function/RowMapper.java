// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core.RowMapper<br>
 *
 * @author Thomas Freese
 *
 * @param <R> Row-Type
 */
@FunctionalInterface
public interface RowMapper<R>
{
    /**
     * Mapped die aktuelle Zeile des {@link ResultSet} in ein Objekt.
     *
     * @param resultSet {@link ResultSet}
     *
     * @return Object
     *
     * @throws SQLException Falls was schief geht.
     */
    R mapRow(ResultSet resultSet) throws SQLException;
}
