// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @param <R> Row-Type
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface RowMapper<R>
{
    /**
     * @param resultSet {@link ResultSet}
     *
     * @return Object
     *
     * @throws SQLException Falls was schiefgeht.
     */
    R mapRow(ResultSet resultSet) throws SQLException;
}
