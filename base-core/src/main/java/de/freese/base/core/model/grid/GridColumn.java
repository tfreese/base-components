// Created: 18.03.2020
package de.freese.base.core.model.grid;

/**
 * @author Thomas Freese
 */
public interface GridColumn<T> {
    String getComment();

    int getLength();

    String getName();

    int getPrecision();

    Class<T> getType();

    T getValue(Object object);
}
