// Created: 16.11.2020
package de.freese.base.swing.components.graph.model;

/**
 * @author Thomas Freese
 */
public interface GraphModel
{
    /**
     * Neuen Wert erzeugen.
     */
    public void generateValue();

    /**
     * Liefert max. die letzten n Values.
     *
     * @param count int
     * @return float[]
     */
    public float[] getValues(int count);

    /**
     * @param value float
     * @param index int; the n-th Value
     * @param graphWitdh float
     * @return float
     */
    public float getXKoordinate(float value, int index, float graphWitdh);

    /**
     * @param value float
     * @param graphHeight float
     * @return float
     */
    public float getYKoordinate(float value, float graphHeight);

    /**
     * @param size int; max. Anzahl der Daten
     */
    public void setSize(int size);

    /**
     * @return int
     */
    public int size();
}