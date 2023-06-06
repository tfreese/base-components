// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ResultSetExtractor<T> {
    T extractData(ResultSet resultSet) throws SQLException;
}
