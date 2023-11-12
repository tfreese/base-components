// Created: 11.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.LongConsumer;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.jdbc.function.ParameterizedPreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.StatementCallback;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementCreator;

/**
 * @author Thomas Freese
 */
class DefaultInsertSpec implements JdbcClient.InsertSpec {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInsertSpec.class);

    private final JdbcClient jdbcClient;
    private final CharSequence sql;

    private PreparedStatementSetter preparedStatementSetter;
    private StatementConfigurer statementConfigurer;

    DefaultInsertSpec(final CharSequence sql, final JdbcClient jdbcClient) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
    }

    @Override
    public int execute(final LongConsumer generatedKeysConsumer) {
        StatementCreator<PreparedStatement> statementCreator = con -> this.jdbcClient.createPreparedStatement(con, sql, statementConfigurer);
        StatementCallback<PreparedStatement, Integer> statementCallback = stmt -> {

            if (preparedStatementSetter != null) {
                preparedStatementSetter.setValues(stmt);
            }

            int affectedRows = stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                while (generatedKeys.next()) {
                    generatedKeysConsumer.accept(generatedKeys.getLong(1));
                }
            }

            return affectedRows;
        };

        return this.jdbcClient.execute(statementCreator, statementCallback, true);
    }

    @Override
    public int execute() {
        return execute(key -> {
        });
    }

    @Override
    public <T> int[] executeBatch(final Collection<T> batchArgs, final ParameterizedPreparedStatementSetter<T> ppss, final int batchSize) {
        StatementCreator<PreparedStatement> statementCreator = con -> this.jdbcClient.createPreparedStatement(con, sql, statementConfigurer);
        StatementCallback<PreparedStatement, int[]> statementCallback = stmt -> {

            boolean supportsBatch = this.jdbcClient.isBatchSupported(stmt.getConnection());

            List<int[]> affectedRows = new ArrayList<>();
            int n = 0;

            for (T arg : batchArgs) {
                stmt.clearParameters();
                ppss.setValues(stmt, arg);
                n++;

                if (supportsBatch) {
                    stmt.addBatch();

                    if (((n % batchSize) == 0) || (n == batchArgs.size())) {
                        if (LOGGER.isDebugEnabled()) {
                            int batchIndex = ((n % batchSize) == 0) ? (n / batchSize) : ((n / batchSize) + 1);
                            int items = n - ((((n % batchSize) == 0) ? ((n / batchSize) - 1) : (n / batchSize)) * batchSize);
                            LOGGER.debug("Sending SQL batch update #{} with {} items", batchIndex, items);
                        }

                        affectedRows.add(stmt.executeBatch());
                        stmt.clearBatch();
                    }
                }
                else {
                    // Batch not possible -> direct execution.
                    int affectedRow = stmt.executeUpdate();

                    affectedRows.add(new int[]{affectedRow});
                }
            }

            return affectedRows.stream().flatMapToInt(IntStream::of).toArray();
        };

        return this.jdbcClient.execute(statementCreator, statementCallback, true);
    }

    @Override
    public JdbcClient.InsertSpec statementConfigurer(final StatementConfigurer statementConfigurer) {
        this.statementConfigurer = statementConfigurer;

        return this;
    }

    @Override
    public JdbcClient.InsertSpec statementSetter(final PreparedStatementSetter preparedStatementSetter) {
        this.preparedStatementSetter = preparedStatementSetter;

        return this;
    }
}
