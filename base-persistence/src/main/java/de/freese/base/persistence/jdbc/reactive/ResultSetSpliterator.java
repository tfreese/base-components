/**
 * Created: 10.04.2019
 */

package de.freese.base.persistence.jdbc.reactive;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
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
    private final ResultSet resultSet;

    /**
    *
    */
    private final RowMapper<T> rowMapper;

    /**
     * Erstellt ein neues {@link ResultSetSpliterator} Object.
     *
     * @param resultSet {@link ResultSet}
     * @param rowMapper {@link RowMapper}
     */
    public ResultSetSpliterator(final ResultSet resultSet, final RowMapper<T> rowMapper)
    {
        super(Long.MAX_VALUE, Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL);

        this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    /**
     * @see java.util.Spliterator#tryAdvance(java.util.function.Consumer)
     */
    @Override
    public boolean tryAdvance(final Consumer<? super T> action)
    {
        try
        {
            boolean hasMore = !this.resultSet.isClosed() && !this.resultSet.isAfterLast() && this.resultSet.next();

            if (hasMore)
            {
                action.accept(this.rowMapper.mapRow(this.resultSet));

                return true;
            }
        }
        catch (SQLException sex)
        {
            // close();
            throw new RuntimeException(sex);
        }

        // close();
        return false;
    }
}