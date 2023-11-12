// Created: 18.06.23
package de.freese.base.persistence.jdbc.transaction;

import java.sql.Connection;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;

/**
 * {@link Transaction} to get the current transactional {@link Connection} managed by Spring.
 *
 * @author Thomas Freese
 * @see TransactionManager
 * @see TransactionDefinition
 * @see TransactionStatus
 */
public class SpringTransaction implements Transaction {
    private final DataSource dataSource;

    public SpringTransaction(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    @Override
    public void begin() {
        // Handled by Spring-TransactionManager.
    }

    @Override
    public void close() {
        DataSourceUtils.releaseConnection(getConnection(), dataSource);
    }

    @Override
    public void commit() {
        // Handled by Spring-TransactionManager.
    }

    @Override
    public Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public void rollback() {
        // Handled by Spring-TransactionManager.
    }
}
