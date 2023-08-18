// Created: 18.08.23
package de.freese.base.persistence.jdbc.newtemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.stream.Stream;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import reactor.core.publisher.Flux;

import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * @author Thomas Freese
 */
public interface ResultQuerySpec {

    <T> T extract(ResultSetExtractor<T> resultSetExtractor);

    <T> Flux<T> flux(RowMapper<T> rowMapper);

    List<Map<String, Object>> list();

    <T> List<T> list(RowMapper<T> rowMapper);

    <T> Flow.Publisher<T> publisher(RowMapper<T> rowMapper);

    SqlRowSet rowSet();

    Map<String, Object> singleRow();

    <T> Stream<T> stream(RowMapper<T> rowMapper);
}
