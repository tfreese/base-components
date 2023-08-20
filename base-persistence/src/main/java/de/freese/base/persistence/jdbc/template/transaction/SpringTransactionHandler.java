// Created: 18.06.23
package de.freese.base.persistence.jdbc.template.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;

/**
 * {@link TransactionHandler} to get the current transactional {@link Connection} managed by Spring.
 *
 * @author Thomas Freese
 * @see TransactionManager
 * @see TransactionDefinition
 * @see TransactionStatus
 */
public class SpringTransactionHandler implements TransactionHandler {
    
    @Override
    public void beginTransaction(final DataSource dataSource) throws SQLException {
        // Handled by Spring-TransactionManager.
    }

    @Override
    public void close(final Connection connection, final DataSource dataSource) {
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    @Override
    public void commitTransaction() throws SQLException {
        // Handled by Spring-TransactionManager.
    }

    @Override
    public Connection getConnection(final DataSource dataSource) throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public void rollbackTransaction() throws SQLException {
        // Handled by Spring-TransactionManager.
    }
}
