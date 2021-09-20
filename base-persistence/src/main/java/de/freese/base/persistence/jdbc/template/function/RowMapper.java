// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.RowMapper<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 *
 * @param <R> Konkreter Row-Typ
 */
@FunctionalInterface
public interface RowMapper<R> // extends Function<ResultSet, R>
{
    // /**
    // * @see java.util.function.Function#apply(java.lang.Object)
    // */
    // @Override
    // public default R apply(final ResultSet resultSet)
    // {
    // return mapRow(resultSet);
    // }

    /**
     * Mapped die aktuelle Zeile des {@link ResultSet} in ein Objekt.
     *
     * @param resultSet {@link ResultSet}
     *
     * @return Object
     *
     * @throws SQLException Falls was schief geht.
     */
    R mapRow(ResultSet resultSet) throws SQLException;
}
