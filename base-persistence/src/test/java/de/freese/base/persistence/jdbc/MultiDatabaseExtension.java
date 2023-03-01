// Created: 05.04.2021
package de.freese.base.persistence.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Service to start and stop the DB-Instances.
 *
 * @author Thomas Freese
 */
public class MultiDatabaseExtension implements BeforeAllCallback, AfterAllCallback // , ArgumentsProvider
{
    private final Map<EmbeddedDatabaseType, DbServerExtension> servers = new HashMap<>();

    /**
     * Junit-{@link Extension} needs a Default-Constructor !
     */
    public MultiDatabaseExtension() {
        super();

        this.servers.computeIfAbsent(EmbeddedDatabaseType.H2, DbServerExtension::new);
        this.servers.computeIfAbsent(EmbeddedDatabaseType.HSQL, DbServerExtension::new);
        this.servers.computeIfAbsent(EmbeddedDatabaseType.DERBY, DbServerExtension::new);
    }

    /**
     * @see org.junit.jupiter.api.extension.AfterAllCallback#afterAll(org.junit.jupiter.api.extension.ExtensionContext)
     */
    @Override
    public void afterAll(final ExtensionContext context) throws Exception {
        for (DbServerExtension server : this.servers.values()) {
            server.afterAll(context);
        }

        DbServerExtension.showMemory();
    }

    /**
     * @see org.junit.jupiter.api.extension.BeforeAllCallback#beforeAll(org.junit.jupiter.api.extension.ExtensionContext)
     */
    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        DbServerExtension.showMemory();

        for (DbServerExtension server : this.servers.values()) {
            server.beforeAll(context);
        }
    }

    public DbServerExtension getServer(final EmbeddedDatabaseType databaseType) {
        return this.servers.get(databaseType);
    }

    public Collection<DbServerExtension> getServers() {
        return this.servers.values();
    }

    // /**
    // * @see org.junit.jupiter.params.provider.ArgumentsProvider#provideArguments(org.junit.jupiter.api.extension.ExtensionContext)
    // */
    // @Override
    // public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception
    // {
    // return getServers().stream().map(server -> Arguments.of(server.getDatabaseType(), server));
    // }
}
