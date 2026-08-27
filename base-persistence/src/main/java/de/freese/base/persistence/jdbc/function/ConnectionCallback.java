package de.freese.base.persistence.jdbc.function;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 * @since 04.02.2017
 */
@FunctionalInterface
public interface ConnectionCallback<T> {
    T doInConnection(Connection connection) throws SQLException;
}
