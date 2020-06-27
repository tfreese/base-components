/**
 * Created: 16.06.2016
 */
package de.freese.base.persistence.jdbc.reactive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;
import reactor.core.publisher.Flux;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestReactiveSpringJdbcTemplate
{
    /**
     *
     */
    private static ReactiveSpringJdbcTemplate jdbcTemplate = null;

    /**
    *
    */
    @RegisterExtension
    static final DbServerExtension SERVER = new DbServerExtension();

    /**
     *
     */
    private static org.springframework.jdbc.core.RowMapper<Person> springRowMapper = null;

    /**
     *
     */
    @BeforeAll
    static void beforeClass()
    {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("hsqldb-schema.sql"));
        populator.addScript(new ClassPathResource("hsqldb-data.sql"));
        populator.execute(SERVER.getDataSource());

        jdbcTemplate = new ReactiveSpringJdbcTemplate(SERVER.getDataSource());

        PersonRowMapper prm = new PersonRowMapper();
        springRowMapper = (rs, row) -> prm.mapRow(rs);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test01Stream() throws Exception
    {
        try (Stream<Person> stream = jdbcTemplate.queryAsStream("select * from person", springRowMapper))
        {
            Stream<Person> s = stream.peek(person -> {
                assertTrue(person.getId() > 0);
            });

            assertEquals(3, s.count());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test02Stream() throws Exception
    {
        try (Stream<Person> stream = jdbcTemplate.queryAsStream("select * from person where name = ?", springRowMapper, "reese"))
        {
            assertEquals(0, stream.count());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test03Flux() throws Exception
    {
        Flux<Person> flux = jdbcTemplate.queryAsFlux("select * from person", springRowMapper).doOnNext(person -> {
            assertTrue(person.getId() > 0);
        });

        assertEquals(3, flux.count().block().longValue());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test04Flux() throws Exception
    {
        Flux<Person> testFlux = jdbcTemplate.queryAsFlux("select * from person where name = ?", springRowMapper, "reese");

        assertEquals(0, testFlux.count().block().longValue());
    }
}
