// Created: 16.11.2020
package de.freese.base.swing.components.graph.model;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Skaliert/Normalisiert die Y-Koordinate.<br>
 *
 * @author Thomas Freese
 */
public class DefaultGraphModel extends AbstractGraphModel
{
    /**
     *
     */
    private final Supplier<Float> valueSupplier;

    /**
     * Erstellt ein neues {@link DefaultGraphModel} Object.
     *
     * @param valueSupplier {@link Supplier}
     */
    public DefaultGraphModel(final Supplier<Float> valueSupplier)
    {
        super();

        this.valueSupplier = Objects.requireNonNull(valueSupplier, "valueSupplier required");
    }

    /**
     * @see de.freese.base.swing.components.graph.model.GraphModel#generateValue()
     */
    @Override
    public void generateValue()
    {
        float value = this.valueSupplier.get();

        addValue(value);
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
        float minValue = getMinValue();
        float maxValue = getMaxValue();
        float minNorm = 0.0F;
        float maxNorm = graphHeight;

        float y = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));

        return y;
    }
}
