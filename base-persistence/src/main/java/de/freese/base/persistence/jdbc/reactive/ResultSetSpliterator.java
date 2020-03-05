/**
 * Created: 10.04.2019
 */

package de.freese.base.persistence.jdbc.reactive;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * {@link Spliterator} für ein {@link ResultSet}.<br>
 *
 * @author Thomas Freese
 * @param <T> Type of Entity
 */
public class ResultSetSpliterator<T> extends Spliterators.AbstractSpliterator<T>
{
    /**
    *
    */
    private final Iterator<T> iterator;

    /**
     * Erstellt ein neues {@link ResultSetSpliterator} Object.
     *
     * @param resultSet {@link ResultSet}
     * @param rowMapper {@link RowMapper}
     */
    public ResultSetSpliterator(final ResultSet resultSet, final RowMapper<T> rowMapper)
    {
        super(Long.MAX_VALUE, Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL);

        this.iterator = new ResultSetIterator<>(resultSet, rowMapper);
    }

    /**
     * @see java.util.Spliterator#tryAdvance(java.util.function.Consumer)
     */
    @Override
    public boolean tryAdvance(final Consumer<? super T> action)
    {
        if (this.iterator.hasNext())
        {
            action.accept(this.iterator.next());

            return true;
        }

        return false;
    }
}