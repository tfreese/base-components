package de.freese.base.persistence.jdbc.function;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 * @since 19.08.23
 */
@FunctionalInterface
public interface ResultSetCallback<T> {
    T doInResultSet(ResultSet resultSet) throws SQLException;
}
