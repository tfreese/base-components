// Created: 15.11.2020
package de.freese.base.swing.components.graph.model;

import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGraphModel implements GraphModel
{
    /**
     *
     */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     *
     */
    private int size;

    /**
    *
    */
    private final TreeSet<Float> treeSet = new TreeSet<>();

    /**
    *
    */
    private final LinkedList<Float> valueList = new LinkedList<>();

    /**
     * Erstellt ein neues {@link AbstractGraphModel} Object.
     */
    public AbstractGraphModel()
    {
        super();
    }

    /**
     * @param value float
     */
    protected void addValue(final float value)
    {
        getLock().lock();

        try
        {
            while (getValueList().size() > size())
            {
                float oldValue = getValueList().removeFirst();

                getTreeSet().remove(oldValue);
            }

            getValueList().add(value);
            getTreeSet().add(value);
        }
        finally
        {
            getLock().unlock();
        }
    }

    /**
     * @return {@link ReentrantLock}
     */
    protected ReentrantLock getLock()
    {
        return this.lock;
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
    protected LinkedList<Float> getValueList()
    {
        return this.valueList;
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#getValues(int)
     */
    @Override
    public float[] getValues(final int count)
    {
        getLock().lock();

        try
        {
            int n = Math.min(count, getValueList().size());

            float[] values = new float[n];

            for (int i = n - 1; i >= 0; i--)
            {
                values[i] = getValueList().get(i);
            }

            return values;
        }
        finally
        {
            getLock().unlock();
        }
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#setSize(int)
     */
    @Override
    public void setSize(final int size)
    {
        this.size = size;
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#size()
     */
    @Override
    public int size()
    {
        return Math.min(this.size, getValueList().size());
    }
}
