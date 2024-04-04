// Created: 30.11.2020
package de.freese.base.swing.components.graph.model;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * @param <T> Entity Type
 *
 * @author Thomas Freese
 */
public final class Values<T extends Comparable<?>> {
    private final TreeSet<T> treeSet = new TreeSet<>();
    private final LinkedList<T> valueList = new LinkedList<>();

    private LinkedList<T> newValues;

    public synchronized void addValue(final T value) {
        if (this.newValues == null) {
            this.newValues = new LinkedList<>();
        }

        this.newValues.add(value);
    }

    public synchronized List<T> getLastValues(final int count) {
        final List<T> lastValues = this.newValues;
        this.newValues = null;

        if (lastValues != null) {
            // Neue Werte hinzufügen.
            for (T value : lastValues) {
                valueList.add(value);
            }
        }

        // Alte Werte entfernen.
        final int n = Math.min(count, valueList.size());

        while (valueList.size() > n) {
            final T oldValue = valueList.removeFirst();

            treeSet.remove(oldValue);
        }

        if (lastValues != null) {
            // Neue Werte für min.-/max. hinzufügen.
            for (T value : lastValues) {
                treeSet.add(value);
            }
        }

        return valueList;
    }

    public T getMaxValue() {
        return treeSet.last();
    }

    public T getMinValue() {
        return treeSet.first();
    }
}
