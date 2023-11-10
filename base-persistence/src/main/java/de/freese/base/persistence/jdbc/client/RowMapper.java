// Created: 12.01.2017
package de.freese.base.persistence.jdbc.client;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface RowMapper<R> {
    R mapRow(ResultSet resultSet) throws SQLException;
}
