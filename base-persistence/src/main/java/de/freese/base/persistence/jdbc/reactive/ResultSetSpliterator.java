// Created: 10.04.2019
package de.freese.base.persistence.jdbc.reactive;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * {@link Spliterator} for a {@link ResultSet}.<br/>
 *
 * @author Thomas Freese
 */
public class ResultSetSpliterator<T> implements Spliterator<T> // extends Spliterators.AbstractSpliterator<T>
{
    private final Iterator<T> iterator;

    public ResultSetSpliterator(final ResultSet resultSet, final RowMapper<T> rowMapper) {
        super();

        this.iterator = new ResultSetIterator<>(resultSet, rowMapper);
    }

    @Override
    public int characteristics() {
        return Spliterator.ORDERED;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {
        if (this.iterator.hasNext()) {
            action.accept(this.iterator.next());

            return true;
        }

        return false;
    }

    /**
     * No Parallelism.
     */
    @Override
    public Spliterator<T> trySplit() {
        return null;
    }
}
