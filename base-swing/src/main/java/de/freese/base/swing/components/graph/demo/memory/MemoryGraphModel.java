// Created: 21.11.2020
package de.freese.base.swing.components.graph.demo.memory;

import de.freese.base.swing.components.graph.model.AbstractGraphModel;

/**
 * @author Thomas Freese
 */
public class MemoryGraphModel extends AbstractGraphModel
{
    /**
     *
     */
    private final Runtime runtime;

    /**
     * Erstellt ein neues {@link MemoryGraphModel} Object.
     */
    public MemoryGraphModel()
    {
        super();

        this.runtime = Runtime.getRuntime();
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#generateValue()
     */
    @Override
    public void generateValue()
    {
        float freeMemory = getFreeMemory();
        float totalMemory = getTotalMemory();

        // Used Memory
        float value = 1F - (freeMemory / totalMemory);

        addValue(value);
    }

    /**
     * @return float
     */
    float getFreeMemory()
    {
        return getRuntime().freeMemory();
    }

    /**
     * @return {@link Runtime}
     */
    private Runtime getRuntime()
    {
        return this.runtime;
    }

    /**
     * @return float
     */
    float getTotalMemory()
    {
        return getRuntime().totalMemory();
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#getXKoordinate(float, int, float)
     */
    @Override
    public float getXKoordinate(final float value, final int index, final float graphWitdh)
    {
        // Eine X-Koordinate pro Wert.
        return index;
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#getYKoordinate(float, float)
     */
    @Override
    public float getYKoordinate(final float value, final float graphHeight)
    {
        float y = value * graphHeight;

        return y;

        // float minValue = getMinValue();
        // float maxValue = getMaxValue();
        // float minNorm = 0.0F;
        // float maxNorm = graphHeight;
        //
        // float y = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));
        //
        // return y;
    }
}
