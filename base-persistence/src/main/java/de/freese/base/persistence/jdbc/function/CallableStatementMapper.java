// Created: 23 Juli 2024
package de.freese.base.persistence.jdbc.function;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface CallableStatementMapper<R> {
    R map(CallableStatement callableStatement) throws SQLException;
}
