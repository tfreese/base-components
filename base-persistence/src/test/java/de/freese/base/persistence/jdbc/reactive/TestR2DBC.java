/**
 * Created: 09.04.2019
 */

package de.freese.base.persistence.jdbc.reactive;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.TestSuiteJdbc;
import io.r2dbc.client.R2dbc;
import io.r2dbc.h2.H2ConnectionFactoryProvider;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// @Disabled // Häufige API-Änderungen lassen den Test fehlschlagen.
public class TestR2DBC
{
    /**
     *
     */
    static ConnectionFactory connectionFactory = null;

    /**
     *
     */
    static R2dbc r2dbc = null;

    /**
     *
     */
    static Scheduler scheduler = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        if (connectionFactory instanceof ConnectionPool)
        {
            Thread.sleep(300);

            ConnectionPool pool = (ConnectionPool) connectionFactory;

            if (!pool.isDisposed())
            {
                pool.dispose();
            }
        }

        Thread.sleep(300);

        scheduler.dispose();
    }

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        int poolSize = Math.max(2, Runtime.getRuntime().availableProcessors());

        // Connection Factory Discovery (Client-API)
        // @formatter:off
        ConnectionFactory connectionFactoryDB = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, H2ConnectionFactoryProvider.H2_DRIVER)
                .option(ConnectionFactoryOptions.PROTOCOL, H2ConnectionFactoryProvider.PROTOCOL_MEM)
                .option(ConnectionFactoryOptions.DATABASE, "test" + TestSuiteJdbc.ATOMIC_INTEGER.getAndIncrement())
                //.option(ConnectionFactoryOptions.PROTOCOL, H2ConnectionFactoryProvider.PROTOCOL_FILE)
                //.option(ConnectionFactoryOptions.DATABASE, System.getProperty("user.dir") + "/db/h2" + TestSuiteJdbc.ATOMIC_INTEGER.getAndIncrement())
                .option(H2ConnectionFactoryProvider.OPTIONS, "AUTOCOMMIT=FALSE;DB_CLOSE_DELAY=-1")
                .build());
        // @formatter:on

        // Programmatisch
        // @formatter:off
//        ConnectionFactory connectionFactoryDB = new H2ConnectionFactory(H2ConnectionConfiguration.builder()
//                 .inMemory("test" + TestSuiteJdbc.ATOMIC_INTEGER.getAndIncrement())
//                 .build());
        // @formatter:on

        // @formatter:off
        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder(connectionFactoryDB)
                .validationQuery("SELECT 1")
                .maxIdleTime(Duration.ofMinutes(10))
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

        // r2dbc.open().doFinally(Handle::close).subscribe(hanlde -> handle...);
    }

    /**
     * Erstellt ein neues {@link TestR2DBC} Object.
     */
    public TestR2DBC()
    {
        super();
    }

    /**
     * !!! Ohne subscribe erfolgt keine Ausführung !!!
     *
     * @throws SQLException Falls was schief geht.
     */
    @Test
    @Order(1)
    public void create() throws SQLException
    {
        // @formatter:off
        r2dbc.withHandle(handle -> handle
                .execute("CREATE TABLE PERSON(ID BIGINT NOT NULL, NAME VARCHAR(25) NOT NULL, VORNAME VARCHAR(25))")
                )
            //.subscribeOn(scheduler)
            .subscribe(affectedRows -> System.out.printf("create [%s]: affectedRows = %d%n", Thread.currentThread().getName(), affectedRows))
            ;
        // @formatter:on
    }

    /**
     * !!! Ohne subscribe erfolgt keine Ausführung !!!
     *
     * @throws SQLException Falls was schief geht.
     */
    @Test
    @Order(2)
    public void insert() throws SQLException
    {
        // @formatter:off
        r2dbc.inTransaction(handle -> handle
                .execute("INSERT INTO PERSON (ID, NAME, VORNAME) VALUES ($1, $2, $3)", 1, "Freese" ,"Thomas")
                )
            //.subscribeOn(scheduler)
            .subscribe(affectedRows -> System.out.printf("insert [%s]: affectedRows = %d%n", Thread.currentThread().getName(), affectedRows))
            ;
        // @formatter:on
    }

    /**
     * !!! Ohne subscribe erfolgt keine Ausführung !!!
     *
     * @throws SQLException Falls was schief geht.
     */
    @Test
    @Order(4)
    public void insertBatch() throws SQLException
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
            .subscribe(affectedRows -> System.out.printf("insertBatch [%s]: affectedRows = %d%n", Thread.currentThread().getName(), affectedRows))
            ;
        // @formatter:on
    }

    /**
     * !!! Ohne subscribe erfolgt keine Ausführung !!
     *
     * @throws SQLException Falls was schief geht.
     */
    @Test
    @Order(10)
    public void select() throws SQLException
    {
        // @formatter:off
        r2dbc.withHandle(h -> h
                .select("SELECT * FROM PERSON")
                        .mapResult(result -> result.map((row, rowMetadata) -> {
                            System.out.printf("map [%s]%n", Thread.currentThread().getName());
                            return new Person(row.get("ID", Long.class), row.get("NAME", String.class), row.get("VORNAME", String.class));
                        }
                        ))
                )
            //.subscribeOn(scheduler)
            .subscribe(person -> System.out.printf("select [%s]: person = %s%n", Thread.currentThread().getName(), person.toString()))
            //.collectList().block().forEach(System.out::println)
            ;
        // @formatter:on
    }

    /**
     * !!! Ohne subscribe erfolgt keine Ausführung !!!
     *
     * @throws SQLException Falls was schief geht.
     */
    @Test
    @Order(3)
    public void update() throws SQLException
    {
        // @formatter:off
        r2dbc.inTransaction(handle -> handle
                .createUpdate("UPDATE PERSON set VORNAME = $1 where ID = $2")
                    .bind("$1", "Hugo")
                    .bind("$2", 1)
                    .execute()
                )
            //.subscribeOn(scheduler)
            .subscribe(affectedRows -> System.out.printf("update [%s]: affectedRows = %d%n", Thread.currentThread().getName(), affectedRows))
            ;
        // @formatter:on
    }
}
