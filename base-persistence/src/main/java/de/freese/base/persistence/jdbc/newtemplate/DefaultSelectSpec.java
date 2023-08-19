// Created: 18.08.23
package de.freese.base.persistence.jdbc.newtemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.jdbc.template.ArgumentPreparedStatementSetter;
import de.freese.base.persistence.jdbc.template.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.template.function.StatementCreator;

/**
 * @author Thomas Freese
 */
public class DefaultSelectSpec implements SelectSpec {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSelectSpec.class);

    private final Supplier<Connection> connectionSupplier;
    private final List<Object> params = new ArrayList<>();
    private final CharSequence sql;
    private Consumer<Statement> configurer;

    public DefaultSelectSpec(final CharSequence sql, Supplier<Connection> connectionSupplier) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.connectionSupplier = Objects.requireNonNull(connectionSupplier, "connectionSupplier required");
    }

    @Override
    public ResultQuerySpec execute() {
        return execute(new ArgumentPreparedStatementSetter(params));
    }

    @Override
    public ResultQuerySpec execute(final PreparedStatementSetter pss) {
        StatementCreator<PreparedStatement> sc = con -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("query: {}", sql);
            }

            PreparedStatement stmt = con.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            if (configurer != null) {
                configurer.accept(stmt);
            }

            stmt.clearParameters();

            if (pss != null) {
                pss.setValues(stmt);
            }

            return stmt;
        };

        return new DefaultResultQuerySpec(connectionSupplier, sc);
    }

    @Override
    public SelectSpec param(final Object value) {
        params.add(value);

        return this;
    }

    @Override
    public SelectSpec statementConfigurer(final Consumer<Statement> configurer) {
        this.configurer = configurer;

        return this;
    }
}
