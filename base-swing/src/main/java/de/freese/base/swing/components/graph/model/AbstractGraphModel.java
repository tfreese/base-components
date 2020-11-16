// Created: 15.11.2020
package de.freese.base.swing.components.graph.model;

import java.util.LinkedList;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGraphModel implements GraphModel
{
    /**
    *
    */
    private final TreeSet<Float> treeSet = new TreeSet<>();

    /**
    *
    */
    private final LinkedList<Float> values = new LinkedList<>();

    /**
     * Erstellt ein neues {@link AbstractGraphModel} Object.
     */
    public AbstractGraphModel()
    {
        super();
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#addValue(float, int)
     */
    @Override
    public void addValue(final float value, final int size)
    {
        setNewSize(size);

        getValues().add(value);
        getTreeSet().add(value);
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
    protected TreeSet<Float> getTreeSet()
    {
        return this.treeSet;
    }

    /**
     * @return LinkedList<Float>
     */
    protected LinkedList<Float> getValues()
    {
        return this.values;
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#setNewSize(int)
     */
    @Override
    public void setNewSize(final int size)
    {
        while (getValues().size() > size)
        {
            float value = getValues().removeFirst();

            getTreeSet().remove(value);
        }
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#size()
     */
    @Override
    public int size()
    {
        return getValues().size();
    }
}
