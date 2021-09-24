// Created: 30.11.2020
package de.freese.base.swing.components.graph.model;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 *
 * @param <T> Entity Type
 */
public final class Values<T>
{
    /**
    *
    */
    private LinkedList<T> newValues;
    /**
    *
    */
    private final TreeSet<T> treeSet = new TreeSet<>();
    /**
    *
    */
    private final LinkedList<T> valueList = new LinkedList<>();

    /**
     * @param value Object
     */
    public synchronized void addValue(final T value)
    {
        if (this.newValues == null)
        {
            this.newValues = new LinkedList<>();
        }

        this.newValues.add(value);
    }

    /**
     * Liefert die letzten n Werte.<br>
     *
     * @param count int
     *
     * @return List<Object>
     */
    public synchronized List<T> getLastValues(final int count)
    {
        final List<T> lastValues = this.newValues;
        this.newValues = null;

        if (lastValues != null)
        {
            // Neue Werte hinzufügen.
            for (T value : lastValues)
            {
                getValues().add(value);
            }
        }

        // Alte Werte entfernen.
        int n = Math.min(count, getValues().size());

        while (getValues().size() > n)
        {
            T oldValue = getValues().removeFirst();

            getTreeSet().remove(oldValue);
        }

        if (lastValues != null)
        {
            // Neue Werte für min.-/max. hinzufügen.
            for (T value : lastValues)
            {
                getTreeSet().add(value);
            }
        }

        return getValues();
    }

    /**
     * @return Object
     */
    public T getMaxValue()
    {
        T maxValue = getTreeSet().last();

        return maxValue;
    }

    /**
     * @return Object
     */
    public T getMinValue()
    {
        T minValue = getTreeSet().first();

        return minValue;
    }

    /**
     * @return {@link TreeSet}<Object>
     */
    private TreeSet<T> getTreeSet()
    {
        return this.treeSet;
    }

    /**
     * @return LinkedList<Object>
     */
    private LinkedList<T> getValues()
    {
        return this.valueList;
    }
}
