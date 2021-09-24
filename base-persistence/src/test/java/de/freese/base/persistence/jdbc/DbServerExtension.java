/*
 * Copyright 2017-2018 the original author or authors. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed
 * to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */

package de.freese.base.persistence.jdbc;

import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

/**
 * @author Thomas Freese
 */
public final class DbServerExtension implements BeforeAllCallback, BeforeTestExecutionCallback, AfterAllCallback, AfterTestExecutionCallback
{
    /**
    *
    */
    public static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DbServerExtension.class);

    /**
     *
     */
    public static void showMemory()
    {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        long divider = 1024 * 1024;
        String unit = "MB";

        NumberFormat format = NumberFormat.getInstance();

        LOGGER.debug("Free memory: " + format.format(freeMemory / divider) + unit);
        LOGGER.debug("Allocated memory: " + format.format(allocatedMemory / divider) + unit);
        LOGGER.debug("Max memory: " + format.format(maxMemory / divider) + unit);
        LOGGER.debug("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / divider) + unit);
    }

    /**
     *
     */
    private HikariDataSource dataSource;

    /**
     * @see org.junit.jupiter.api.extension.AfterAllCallback#afterAll(org.junit.jupiter.api.extension.ExtensionContext)
     */
    @Override
    public void afterAll(final ExtensionContext context)
    {
        LOGGER.debug("afterAll");

        HikariPoolMXBean poolMXBean = this.dataSource.getHikariPoolMXBean();

        LOGGER.debug("Connections: idle={}, active={}, waiting={}", poolMXBean.getIdleConnections(), poolMXBean.getActiveConnections(),
                poolMXBean.getThreadsAwaitingConnection());

        LOGGER.debug("close datasource");
        this.dataSource.close();

        long startTime = getStoreForGlobal(context).remove("start-time", long.class);
        long duration = System.currentTimeMillis() - startTime;

        LOGGER.debug("All Tests took {} ms.", duration);
    }

    /**
     * @see org.junit.jupiter.api.extension.AfterTestExecutionCallback#afterTestExecution(org.junit.jupiter.api.extension.ExtensionContext)
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception
    {
        // Method testMethod = context.getRequiredTestMethod();
        // long startTime = getStoreForMethod(context).remove("start-time", long.class);
        // long duration = System.currentTimeMillis() - startTime;

        // LOGGER.debug("Method [{}] took {} ms.", testMethod.getName(), duration);

        // LOGGER.debug("Idle Connections = {}", this.dataSource.getHikariPoolMXBean().getIdleConnections());
    }

    /**
     * @see org.junit.jupiter.api.extension.BeforeAllCallback#beforeAll(org.junit.jupiter.api.extension.ExtensionContext)
     */
    @Override
    public void beforeAll(final ExtensionContext context) throws Exception
    {
        LOGGER.debug("beforeAll");

        getStoreForGlobal(context).put("start-time", System.currentTimeMillis());

        // @formatter:off
//        this.dataSource = DataSourceBuilder.create().type(HikariDataSource.class)
//                .driverClassName("org.hsqldb.jdbc.JDBCDriver")
//                .url("jdbc:hsqldb:mem:%d"+ System.nanoTime())
//                .username("sa")
//                .password("")
//                .build()
//                ;
        // @formatter:on

        // ;MVCC=true;LOCK_MODE=0

        this.dataSource = new HikariDataSource();

        // this.dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        // this.dataSource.setJdbcUrl("jdbc:hsqldb:mem:" + System.nanoTime() + ";shutdown=true");
        // ;shutdown=true schliesst die DB nach Ende der letzten Connection.

        this.dataSource.setDriverClassName("org.h2.Driver");
        this.dataSource.setJdbcUrl("jdbc:h2:mem:" + System.nanoTime());
        // ;DB_CLOSE_DELAY=-1 schliesst NICHT die DB nach Ende der letzten Connection.

        this.dataSource.setUsername("sa");
        this.dataSource.setPassword("");

        this.dataSource.setMaximumPoolSize(2);

        // EmbeddedDatabase database = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).setName("" +
        // TestSuiteJdbc.ATOMIC_INTEGER.getAndIncrement()).build();
        //
        // // SingleConnectionDataSource singleConnectionDataSource = new SingleConnectionDataSource();
        // // singleConnectionDataSource.setDriverClassName("org.generic.jdbc.JDBCDriver");
        // // singleConnectionDataSource.setUrl("jdbc:generic:mem:" + TestSuiteJdbc.ATOMIC_INTEGER.getAndIncrement());
        // // // singleConnectionDataSource.setUrl("jdbc:generic:file:db/generic/generic;create=false;shutdown=true");
        // // singleConnectionDataSource.setSuppressClose(true);
        // // singleConnectionDataSource.setAutoCommit(true);
        //
        // // DataSource dataSource = singleConnectionDataSource;
        // dataSource = database;
        //
        // ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // populator.addScript(new ClassPathResource("hsqldb-schema.sql"));
        // populator.addScript(new ClassPathResource("hsqldb-data.sql"));
        // populator.execute(dataSource);
    }

    /**
     * @see org.junit.jupiter.api.extension.BeforeTestExecutionCallback#beforeTestExecution(org.junit.jupiter.api.extension.ExtensionContext)
     */
    @Override
    public void beforeTestExecution(final ExtensionContext context) throws Exception
    {
        getStoreForMethod(context).put("start-time", System.currentTimeMillis());
    }

    /**
     * @return {@link DataSource}
     */
    public DataSource getDataSource()
    {
        return this.dataSource;
    }

    /**
     * @return String
     */
    public String getDriver()
    {
        return this.dataSource.getDriverClassName();
    }

    /**
     * @return String
     */
    public String getPassword()
    {
        return this.dataSource.getPassword();
    }

    /**
     * Object-Store pro Test-Klasse.
     *
     * @param context {@link ExtensionContext}
     *
     * @return {@link Store}
     */
    Store getStoreForClass(final ExtensionContext context)
    {
        return context.getStore(Namespace.create(getClass()));
    }

    /**
     * Object-Store für den gesamten Test.
     *
     * @param context {@link ExtensionContext}
     *
     * @return {@link Store}
     */
    Store getStoreForGlobal(final ExtensionContext context)
    {
        return context.getStore(Namespace.create("global"));
    }

    /**
     * Object-Store pro Test-Methode.
     *
     * @param context {@link ExtensionContext}
     *
     * @return {@link Store}
     */
    Store getStoreForMethod(final ExtensionContext context)
    {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }

    /**
     * @return String
     */
    public String getUrl()
    {
        return this.dataSource.getJdbcUrl();
    }

    /**
     * @return String
     */
    public String getUsername()
    {
        return this.dataSource.getUsername();
    }
}
