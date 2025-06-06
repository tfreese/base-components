package de.freese.base.persistence.jdbc.reactive;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Spring-Konfiguration des {@link Scheduler}s vom reactor-Framework für JDBC-Verarbeitung.<br>
 *
 * <pre>{@code
 * Mono<City> city = Mono.defer(() -> Mono.just(cityRepository.findByNameAndCountryAllIgnoringCase(name, country)))
 *                  .subscribeOn(jdbcScheduler);
 *
 * Mono<Iterable<City>> cities = Mono.fromCallable(() -> cityRepository.findAll())
 *                  .subscribeOn(jdbcScheduler);
 *
 * return Mono.fromCallable(() -> transactionTemplate.execute(status -> {
 *                  return cityRepository.save(new City(name, country));
 *              })).subscribeOn(jdbcScheduler);
 * }</pre>
 *
 * @author Thomas Freese
 */
@Configuration
public class ReactorSchedulerConfiguration {
    private final Scheduler scheduler;

    public ReactorSchedulerConfiguration(@Value("${spring.datasource.maximum-pool-size}") final int connectionPoolSize) {
        super();

        if (connectionPoolSize <= 0) {
            throw new IllegalArgumentException("connectionPoolSize <= 0: " + connectionPoolSize);
        }

        scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
    }

    @Bean
    public Scheduler jdbcScheduler() {
        return scheduler;
    }
}
