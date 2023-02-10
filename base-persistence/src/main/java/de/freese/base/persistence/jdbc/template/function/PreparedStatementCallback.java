// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @param <T> Return-Type
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface PreparedStatementCallback<T> {
    T doInStatement(PreparedStatement statement) throws SQLException;
}
