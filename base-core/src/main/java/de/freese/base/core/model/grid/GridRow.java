// Created: 13.02.23
package de.freese.base.core.model.grid;

import java.util.List;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface GridRow {

    static GridRow of(final List<?> list) {
        return columnIndex -> {
            if (columnIndex > list.size()) {
                return null;
            }

            return list.get(columnIndex);
        };
    }

    static GridRow of(final Object[] objectArray) {
        return columnIndex -> {
            if (columnIndex > objectArray.length) {
                return null;
            }

            return objectArray[columnIndex];
        };
    }

    Object getObject(int columnIndex);
}
