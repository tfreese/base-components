// Created: 15.11.2020
package de.freese.base.swing.components.graph.model;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPainterModel
{
    /**
     *
     */
    private final Values<Float> values = new Values<>();

    /**
     * @return {@link Values}
     */
    public Values<Float> getValues()
    {
        return this.values;
    }

    /**
     * Wert für Graphen Breite umrechnen/normalisieren.
     *
     * @param value float
     * @param index int; the n-th Value
     * @param with float: Breite des Graphen
     *
     * @return float
     */
    protected float getXKoordinate(final float value, final int index, final float with)
    {
        // Pro Wert eine X-Koordinate.
        return index;
    }

    /**
     * Wert für Graphen Höhe umrechnen/normalisieren.
     *
     * @param value float
     * @param height float: Höhe des Graphen
     *
     * @return float
     */
    protected float getYKoordinate(final float value, final float height)
    {
        float minValue = getValues().getMinValue();
        float maxValue = getValues().getMaxValue();
        float minNorm = 0.0F;
        float maxNorm = height;

        float y = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));

        return y;
    }
}
