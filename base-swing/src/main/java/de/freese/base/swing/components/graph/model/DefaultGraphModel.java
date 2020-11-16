// Created: 16.11.2020
package de.freese.base.swing.components.graph.model;

/**
 * Skaliert/Normalisiert die Y-Koordinate.<br>
 *
 * @author Thomas Freese
 */
public class DefaultGraphModel extends AbstractGraphModel
{
    /**
     * Erstellt ein neues {@link DefaultGraphModel} Object.
     */
    public DefaultGraphModel()
    {
        super();
    }

    /**
     * @see de.freese.base.swing.components.graph.model.AbstractGraphModel#getYKoordinate(int, float)
     */
    @Override
    public float getYKoordinate(final int index, final float graphHeight)
    {
        float value = getValues().get(index);
        float minValue = getMinValue();
        float maxValue = getMaxValue();
        float minNorm = 0.0F;
        float maxNorm = graphHeight;

        float y = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));

        return y;
    }
}
