package de.freese.base.persistence.jdbc.transaction;

import java.sql.Connection;

/**
 * @author Thomas Freese
 * @since 30.08.2023
 */
public interface Transaction { // extends AutoCloseable {
    void begin();

    // void close();

    void commit();

    Connection getConnection();

    void rollback();
}
