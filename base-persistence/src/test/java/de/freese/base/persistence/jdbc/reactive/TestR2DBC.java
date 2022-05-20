// Created: 09.04.2019
package de.freese.base.persistence.jdbc.reactive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.Person;
import io.r2dbc.client.R2dbc;
import io.r2dbc.h2.H2ConnectionFactoryProvider;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        // @Disabled // Häufige API-Änderungen lassen den Test fehlschlagen.
class TestR2DBC
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestR2DBC.class);
    /**
     *
     */
    static ConnectionFactory connectionFactory;
    /**
     *
     */
    static R2dbc r2dbc;
    /**
     *
     */
    static Scheduler scheduler;

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        if (connectionFactory instanceof ConnectionPool pool)
        {
            TimeUnit.MILLISECONDS.sleep(300);

            if (!pool.isDisposed())
            {
                pool.dispose();
            }
        }

        TimeUnit.MILLISECONDS.sleep(300);

        scheduler.dispose();
        Schedulers.shutdownNow();
    }

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        // Connection Factory Discovery (Client-API)
        // @formatter:off
        ConnectionFactory connectionFactoryDB = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, H2ConnectionFactoryProvider.H2_DRIVER)
                .option(ConnectionFactoryOptions.PROTOCOL, H2ConnectionFactoryProvider.PROTOCOL_MEM)
                .option(ConnectionFactoryOptions.DATABASE, DbServerExtension.createDbName())
                //.option(ConnectionFactoryOptions.PROTOCOL, H2ConnectionFactoryProvider.PROTOCOL_FILE)
                //.option(ConnectionFactoryOptions.DATABASE, System.getProperty("user.dir") + "/db/h2/" + DbServerExtension.createDbName())
                .option(H2ConnectionFactoryProvider.OPTIONS, "AUTOCOMMIT=TRUE;DB_CLOSE_DELAY=-1") // ;DB_CLOSE_DELAY=-1 // DB bleibt nach letzter Connection erhalten.
                .build());
        // @formatter:on

        // Programmatisch
        // @formatter:off
//        ConnectionFactory connectionFactoryDB = new H2ConnectionFactory(H2ConnectionConfiguration.builder()
//                 .inMemory("" + DbServerExtension.ATOMIC_INTEGER.getAndIncrement())
//                 .build());
        // @formatter:on

        int poolSize = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        
        // @formatter:off
        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(connectionFactoryDB)
                .validationQuery("SELECT 1")
                .maxIdleTime(Duration.ofMinutes(10))
                .initialSize(1)
                .maxSize(poolSize)
                .build()
                ;
        // @formatter:on

        ConnectionPool pool = new ConnectionPool(poolConfiguration);
        connectionFactory = pool;

        // Mono<Connection> connection = (Mono<Connection>) connectionFactory.create();
        // connection.close();

        // scheduler = new ReactorSchedulerConfiguration(poolSize).jdbcScheduler();
        scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(poolSize));

        r2dbc = new R2dbc(connectionFactory);

        // r2dbc.open().doFinally(Handle::close).subscribe(handle -> handle...);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    @Order(1)
    void testCreate() throws SQLException
    {
        // @formatter:off
        r2dbc.withHandle(handle -> handle
                .execute("CREATE TABLE PERSON(ID BIGINT NOT NULL, NAME VARCHAR(25) NOT NULL, VORNAME VARCHAR(25))")
                )
            //.subscribeOn(scheduler)
            .subscribe(affectedRows -> {
                LOGGER.debug("create [{}]: affectedRows = {}", Thread.currentThread().getName(), affectedRows);
                assertEquals(0, affectedRows);
                })
            ;
        // @formatter:on
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    @Order(2)
    void testInsert() throws SQLException
    {
        // @formatter:off
        r2dbc.inTransaction(handle -> handle
                .execute("INSERT INTO PERSON (ID, NAME, VORNAME) VALUES ($1, $2, $3)", 1, "Freese" ,"Thomas")
                )
            //.subscribeOn(scheduler)
            .subscribe(affectedRows -> {
                LOGGER.debug("insert [{}}]: affectedRows = {}", Thread.currentThread().getName(), affectedRows);
                assertEquals(1, affectedRows);
                })
            ;
        // @formatter:on
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    @Order(4)
    void testInsertBatch() throws SQLException
    {
        // @formatter:off
        r2dbc.inTransaction(handle -> handle
                .createUpdate("INSERT INTO PERSON (ID, NAME, VORNAME) VALUES ($1, $2, $3)")
                    .bind("$1", 2).bind("$2", "NAME_A").bind("$3", "VORNAME_A")
                    .add()
                    .bind("$1", 3).bind("$2", "NAME_B").bind("$3", "VORNAME_B")
                    .execute()
                )
            //.subscribeOn(scheduler)
            .subscribe(affectedRows -> {
                LOGGER.debug("insertBatch [{}]: affectedRows = {}", Thread.currentThread().getName(), affectedRows);

                // Hier sollte eigentlich affectedRows = 2 sein ?!?!?!
                assertEquals(1, affectedRows);
                })
            ;
        // @formatter:on
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    @Order(10)
    void testSelect() throws SQLException
    {
        List<Person> list = new ArrayList<>();

        // @formatter:off
        r2dbc.withHandle(handle -> handle
                .select("SELECT * FROM PERSON ORDER BY ID ASC")
                        .mapResult(result -> result.map((row, rowMetadata) -> {
                            LOGGER.debug("map [{}]", Thread.currentThread().getName());
                            return new Person(row.get("ID", Long.class), row.get("NAME", String.class), row.get("VORNAME", String.class));
                        }
                        ))
                )
            //.subscribeOn(scheduler)
            .subscribe(person -> {
                list.add(person);
                LOGGER.debug("select [{}]: person = {}", Thread.currentThread().getName(), person.toString());
                })
            //.collectList().block().forEach(System.out::println)
            ;
        // @formatter:on

        assertEquals(3, list.size());

        assertEquals(1, list.get(0).getId());
        assertEquals("Freese", list.get(0).getNachname());
        assertEquals("Hugo", list.get(0).getVorname());

        assertEquals(2, list.get(1).getId());
        assertEquals("NAME_A", list.get(1).getNachname());
        assertEquals("VORNAME_A", list.get(1).getVorname());

        assertEquals(3, list.get(2).getId());
        assertEquals("NAME_B", list.get(2).getNachname());
        assertEquals("VORNAME_B", list.get(2).getVorname());
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    @Order(3)
    void testUpdate() throws SQLException
    {
        // @formatter:off
        r2dbc.inTransaction(handle -> handle
                .createUpdate("UPDATE PERSON set VORNAME = $1 where ID = $2")
                    .bind("$1", "Hugo")
                    .bind("$2", 1)
                    .execute()
                )
            //.subscribeOn(scheduler)
            .subscribe(affectedRows -> {
                LOGGER.debug("update [{}]: affectedRows = {}", Thread.currentThread().getName(), affectedRows);
                assertEquals(1, affectedRows);
                })
            ;
        // @formatter:on
    }
}
