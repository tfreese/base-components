// Created: 16.01.2018
package de.freese.base.persistence.jdbc.reactive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@ExtendWith(MockitoExtension.class)

// Sonst müsste pro Test-Methode der Mock als Parameter definiert und konfiguriert werden.
@MockitoSettings(strictness = Strictness.LENIENT)
class TestReactiveSpringJdbcCrudRepository
{
    /**
     * @author Thomas Freese
     */
    private static class Entity
    {
        /**
         *
         */
        private Integer id = null;

        /**
         *
         */
        private String value = null;

        /**
         * Erzeugt eine neue Instanz von {@link Entity}.
         *
         * @param id Integer
         * @param value String
         */
        private Entity(final Integer id, final String value)
        {
            this.id = id;
            this.value = value;
        }

        /**
         * @return Integer
         */
        private Integer getId()
        {
            return this.id;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("Entity [id=");
            builder.append(this.id);
            builder.append(", value=");
            builder.append(this.value);
            builder.append("]");

            return builder.toString();
        }
    }

    /**
     *
     */
    private AtomicInteger counter = null;

    /**
     *
     */
    private final List<Entity> data = Arrays.asList(new Entity(0, "V0"), new Entity(1, "V1"), new Entity(2, "V2"), new Entity(3, "V3"), new Entity(4, "V4"));

    /**
     *
     */
    private ReactiveSpringJdbcCrudRepository<Entity, Integer> repo = null;

    /**
     * @throws SQLException Falls was schief geht.
     */
    @AfterEach
    void after() throws SQLException
    {
        System.out.println();
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() throws SQLException
    {
        this.counter = new AtomicInteger(0);

        this.repo = mock(ReactiveSpringJdbcCrudRepository.class);

        // when(this.datasourceMock.getConnection()).thenReturn(this.connectionMock);
        when(this.repo.deleteAll(ArgumentMatchers.anyIterable())).thenCallRealMethod();
        when(this.repo.deleteAll((Publisher<Entity>) ArgumentMatchers.any())).thenCallRealMethod();
        when(this.repo.deleteById((Publisher<Integer>) ArgumentMatchers.any())).thenCallRealMethod();
        when(this.repo.delete(ArgumentMatchers.any())).thenAnswer(invocation -> {

            this.counter.incrementAndGet();

            return null;
        });
        when(this.repo.deleteById(ArgumentMatchers.anyInt())).thenAnswer(invocation -> {
            this.counter.incrementAndGet();

            return null;
        });
    }

    /**
     *
     */
    @Test
    void test010Delete()
    {
        this.repo.delete(this.data.get(0));

        assertEquals(1, this.counter.get());
    }

    /**
     *
     */
    @Test
    void test011DeleteIterable()
    {
        this.repo.deleteAll(this.data);

        assertEquals(this.data.size(), this.counter.get());
    }

    /**
     *
     */
    @Test
    void test012DeleteMono()
    {
        Mono<Entity> mono = Mono.just(this.data.get(0));

        this.repo.deleteAll(mono);

        assertEquals(1, this.counter.get());
    }

    /**
    *
    */
    @Test
    void test013DeleteFlux()
    {
        Flux<Entity> flux = Flux.fromIterable(this.data);

        this.repo.deleteAll(flux);

        assertEquals(this.data.size(), this.counter.get());
    }

    /**
    *
    */
    @Test
    void test014DeleteByID()
    {
        Flux<Integer> flux = Flux.fromIterable(this.data).map(Entity::getId);

        this.repo.deleteById(flux);

        assertEquals(this.data.size(), this.counter.get());
    }
}
