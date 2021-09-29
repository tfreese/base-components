// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inspired by org.springframework.jdbc.core.ResultSetExtractor<br>
 *
 * @author Thomas Freese
 *
 * @param <T> Type
 */
@FunctionalInterface
public interface ResultSetExtractor<T>
{
    /**
     * Konvertiert das {@link ResultSet} in eine andere Objektstruktur.
     *
     * @param resultSet {@link ResultSet}
     *
     * @return Object
     *
     * @throws SQLException Falls was schief geht.
     */
    T extractData(ResultSet resultSet) throws SQLException;
}
