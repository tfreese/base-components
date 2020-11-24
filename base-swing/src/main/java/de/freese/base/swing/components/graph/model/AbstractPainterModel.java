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
    // /**
    // *
    // */
    // private final LinkedList<Float> paintList = new LinkedList<>();

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
    public AbstractPainterModel()
    {
        super();
    }

    /**
     * @param value float
     * @param width int: Breite des Graphen
     */
    protected void addValue(final float value, final int width)
    {
        while (getValues().size() > width)
        {
            float oldValue = getValues().removeFirst();

            getTreeSet().remove(oldValue);
        }

        getValues().add(value);
        getTreeSet().add(value);
    }

    // /**
    // * Neuen Wert erzeugen.
    // */
    // public void generateValue()
    // {
    // generateValue(Integer.MAX_VALUE);
    // }

    /**
     * Neuen Wert erzeugen.
     *
     * @param width int: Breite des Graphen
     */
    protected abstract void generateValue(int width);

    /**
     * Liefert die letzten n Werte.<br>
     *
     * @param count int
     * @return List<Float>
     */
    protected List<Float> getLastValues(final int count)
    {
        int n = Math.min(count, getValues().size());

        // final int valuesChanged = this.valuesAdded;
        //
        // int oldPaintListsize = this.paintList.size();
        //
        // // Alte Werte seit dem letzten Paint entfernen.
        // if (this.paintList.size() > valuesChanged)
        // {
        // for (int i = 0; i < this.valuesAdded; i++)
        // {
        // this.paintList.removeFirst();
        // }
        // }
        //
        // // Neue Werte seit dem letzten Paint hinzufügen.
        // for (int i = this.paintList.size(); i < n; i++)
        // {
        // this.paintList.add(getValues().get((oldPaintListsize + i) - 1));
        // }
        //
        // this.valuesAdded = 0;

        // this.paintList.clear();
        //
        // for (int i = 0; i < n; i++)
        // {
        // this.paintList.add(getValues().get(i));
        // }

        // return this.paintList;

        return getValues().subList(getValues().size() - n, getValues().size());
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
