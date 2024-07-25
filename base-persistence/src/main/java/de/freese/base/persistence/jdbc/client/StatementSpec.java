// Created: 25 Juli 2024
package de.freese.base.persistence.jdbc.client;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.function.LongConsumer;

import de.freese.base.persistence.jdbc.function.CallableStatementMapper;
import de.freese.base.persistence.jdbc.function.ParameterizedPreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementSetter;

/**
 * @author Thomas Freese
 */
public interface StatementSpec {
    /**
     * Execute the SQL with {@link CallableStatement#execute()}.
     */
    <R> R call(StatementSetter<CallableStatement> statementSetter, CallableStatementMapper<R> mapper);

    /**
     * Execute the SQL with {@link Statement#execute(String)}.
     *
     * @return true if the first result is a ResultSet object; false if it is an update count or there are no results
     */
    boolean execute();

    /**
     * Execute the SQL with {@link Statement#executeUpdate(String)}.
     *
     * @return int affectedRows
     */
    int executeUpdate();

    /**
     * Execute the SQL with {@link Statement#executeUpdate(String)}.
     *
     * @param generatedKeysConsumer Nullable
     *
     * @return int affectedRows
     */
    int executeUpdate(LongConsumer generatedKeysConsumer);

    /**
     * Execute the SQL with {@link Statement#executeBatch()}.
     *
     * @return int affectedRows
     */
    <T> int executeUpdateBatch(int batchSize, Collection<T> batchArgs, ParameterizedPreparedStatementSetter<T> parameterizedPreparedStatementSetter);

    QuerySpec query();

    StatementSpec statementConfigurer(StatementConfigurer statementConfigurer);

    /**
     * Not used for {@link #executeUpdateBatch(int, Collection, ParameterizedPreparedStatementSetter)}.
     */
    StatementSpec statementSetter(StatementSetter<PreparedStatement> statementSetter);
}
