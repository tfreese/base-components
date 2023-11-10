// Created: 20.08.23
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface StatementConfigurer {
    void configure(PreparedStatement preparedStatement) throws SQLException;
}
