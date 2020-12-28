// Created: 15.11.2020
package de.freese.base.swing.components.graph.model;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPainterModel
{
    /**
    *
    */
    private LinkedList<Float> newValues;

    /**
    *
    */
    private final TreeSet<Float> treeSet = new TreeSet<>();

    /**
    *
    */
    private final LinkedList<Float> valueList = new LinkedList<>();

    /**
     * Erstellt ein neues {@link AbstractPainterModel} Object.
     */
    protected AbstractPainterModel()
    {
        super();
    }

    /**
     * @param value float
     */
    public synchronized void addValue(final float value)
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
     * @return List<Float>
     */
    protected synchronized List<Float> getLastValues(final int count)
    {
        final List<Float> lastValues = this.newValues;
        this.newValues = null;

        if (lastValues != null)
        {
            // Neue Werte hinzufügen.
            for (Float value : lastValues)
            {
                getValues().add(value);
            }
        }

        // Alte Werte entfernen.
        int n = Math.min(count, getValues().size());

        while (getValues().size() > n)
        {
            float oldValue = getValues().removeFirst();

            getTreeSet().remove(oldValue);
        }

        if (lastValues != null)
        {
            // Neue Werte für min.-/max. hinzufügen.
            for (Float value : lastValues)
            {
                getTreeSet().add(value);
            }
        }

        return getValues();
    }

    /**
     * @return float
     */
    protected float getMaxValue()
    {
        float maxValue = getTreeSet().last();

        return maxValue;
    }

    /**
     * @return float
     */
    protected float getMinValue()
    {
        float minValue = getTreeSet().first();

        return minValue;
    }

    /**
     * @return {@link TreeSet}<Float>
     */
    private TreeSet<Float> getTreeSet()
    {
        return this.treeSet;
    }

    /**
     * @return LinkedList<Float>
     */
    private LinkedList<Float> getValues()
    {
        return this.valueList;
    }

    /**
     * Wert für Graph-Breite umrechnen/normalisieren.
     *
     * @param value float
     * @param index int; the n-th Value
     * @param witdh float: Breite des Graphen
     * @return float
     */
    protected float getXKoordinate(final float value, final int index, final float witdh)
    {
        // Pro Wert eine X-Koordinate.
        return index;
    }

    /**
     * Wert für Graph-Höhe umrechnen/normalisieren.
     *
     * @param value float
     * @param height float: Höhe des Graphen
     * @return float
     */
    protected float getYKoordinate(final float value, final float height)
    {
        float minValue = getMinValue();
        float maxValue = getMaxValue();
        float minNorm = 0.0F;
        float maxNorm = height;

        float y = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));

        return y;
    }
}
