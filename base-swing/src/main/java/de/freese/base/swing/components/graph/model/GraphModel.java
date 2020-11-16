// Created: 16.11.2020
package de.freese.base.swing.components.graph.model;

/**
 * @author Thomas Freese
 */
public interface GraphModel
{
    /**
     * @param value float
     * @param size int; max. Anzahl der Daten
     */
    public void addValue(float value, int size);

    /**
     * @param index int
     * @param graphHeight float
     * @return float
     */
    public float getYKoordinate(int index, float graphHeight);

    /**
     * @param size int; max. Anzahl der Daten
     */
    public void setNewSize(int size);

    /**
     * @return int
     */
    public int size();
}