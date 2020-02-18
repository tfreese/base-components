// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.ResultSetExtractor<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Return-Typ
 */
@FunctionalInterface
public interface ResultSetExtractor<T>
{
    /**
     * Konvertiert das {@link ResultSet} in eine andere Objektstruktur.
     *
     * @param resultSet {@link ResultSet}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public T extractData(ResultSet resultSet) throws SQLException;
}
