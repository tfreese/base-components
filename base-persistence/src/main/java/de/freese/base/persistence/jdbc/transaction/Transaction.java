// Created: 30.08.23
package de.freese.base.persistence.jdbc.transaction;

import java.sql.Connection;

/**
 * @author Thomas Freese
 */
public interface Transaction extends AutoCloseable {
    void begin();

    void close();

    void commit();

    Connection getConnection();

    void rollback();
}
