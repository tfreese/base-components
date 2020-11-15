// Created: 15.11.2020
package de.freese.base.swing.components.graph;

import java.util.LinkedList;
import java.util.Objects;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 */
public class GraphModel
{
    /**
     * Skaliert einen Wert (Wikipedia).<br>
     *
     * @author Thomas Freese
     */
    public static class DefaultYKoordinateSupplier implements YKoordinateSupplier
    {
        /**
         * @see de.freese.base.swing.components.graph.GraphModel.YKoordinateSupplier#getYCoordinate(float, float, float, float)
         */
        @Override
        public float getYCoordinate(final float graphHeight, final float value, final float minValue, final float maxValue)
        {
            float minNorm = 0.0F;
            float maxNorm = graphHeight;

            float y = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));

            return y;
        }
    }

    /**
     * @author Thomas Freese
     */
    @FunctionalInterface
    public interface YKoordinateSupplier
    {
        /**
         * Liefert die umgerechnete Y-Koordinate des Values für den Graphen.
         *
         * @param graphHeight float
         * @param value float
         * @param minValue float
         * @param maxValue float
         * @return float
         */
        public float getYCoordinate(final float graphHeight, float value, float minValue, float maxValue);
    }

    /**
     *
     */
    private final YKoordinateSupplier koordinateSupplier;

    /**
    *
    */
    private final TreeSet<Float> treeSet = new TreeSet<>();

    /**
    *
    */
    private final LinkedList<Float> values = new LinkedList<>();

    /**
     * Erstellt ein neues {@link GraphModel} Object.
     */
    public GraphModel()
    {
        this(new DefaultYKoordinateSupplier());
    }

    /**
     * Erstellt ein neues {@link GraphModel} Object.
     *
     * @param koordinateSupplier {@link YKoordinateSupplier}
     */
    public GraphModel(final YKoordinateSupplier koordinateSupplier)
    {
        super();

        this.koordinateSupplier = Objects.requireNonNull(koordinateSupplier, "koordinateSupplier required");
    }

    /**
     * @param value float
     * @param size int; max. Anzahl der Daten
     */
    public void addValue(final float value, final int size)
    {
        setNewSize(size);

        getValues().add(value);
        getTreeSet().add(value);
    }

    /**
     * @return {@link YKoordinateSupplier}
     */
    protected YKoordinateSupplier getKoordinateSupplier()
    {
        return this.koordinateSupplier;
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
     * @param index int
     * @param graphHeight float
     * @return float
     */
    public float getYKoordinate(final int index, final float graphHeight)
    {
        float value = getValues().get(index);
        float minValue = getMinValue();
        float maxValue = getMaxValue();

        float y = getKoordinateSupplier().getYCoordinate(graphHeight, value, minValue, maxValue);

        return y;
    }

    /**
     * @param size int; max. Anzahl der Daten
     */
    public void setNewSize(final int size)
    {
        while (getValues().size() > size)
        {
            float value = getValues().removeFirst();

            getTreeSet().remove(value);
        }
    }

    /**
     * @return int
     */
    public int size()
    {
        return getValues().size();
    }
}
