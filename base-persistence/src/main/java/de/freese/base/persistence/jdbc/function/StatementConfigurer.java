package de.freese.base.persistence.jdbc.function;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Thomas Freese
 * @since 20.08.23
 */
@FunctionalInterface
public interface StatementConfigurer {
    void configure(Statement statement) throws SQLException;
}
