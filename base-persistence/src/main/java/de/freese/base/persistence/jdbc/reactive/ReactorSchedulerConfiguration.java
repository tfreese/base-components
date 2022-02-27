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
 * <pre>
 * Mono<City> city = Mono.defer(() -> Mono.just(this.cityRepository.findByNameAndCountryAllIgnoringCase(name, country)))
 *                   .subscribeOn(jdbcScheduler);
 * <br>
 * Mono<Iterable<City>> cities = Mono.fromCallable(() -> this.cityRepository.findAll())
 *                   .subscribeOn(jdbcScheduler);
 * <br>
 * return Mono.fromCallable(() -> transactionTemplate.execute(status -> {
 * 			  City city = new City(name, country);
 * 			  City savedCity = cityRepository.save(city);
 * 			  return savedCity;
 *          })).subscribeOn(jdbcScheduler);
 * </pre>
 *
 * @author Thomas Freese
 */
@Configuration
public class ReactorSchedulerConfiguration
{
    /**
     *
     */
    private final Scheduler scheduler;

    /**
     * Erstellt ein neues {@link ReactorSchedulerConfiguration} Object.
     *
     * @param connectionPoolSize int
     */
    public ReactorSchedulerConfiguration(@Value("${spring.datasource.maximum-pool-size}") final int connectionPoolSize)
    {
        super();

        if (connectionPoolSize <= 0)
        {
            throw new IllegalArgumentException("connectionPoolSize <= 0: " + connectionPoolSize);
        }

        this.scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
    }

    /**
     * @return {@link Scheduler}
     */
    @Bean
    public Scheduler jdbcScheduler()
    {
        return this.scheduler;
    }
}
