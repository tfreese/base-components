package de.freese.base.swing.filter.editor;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JTable;

/**
 * Interface fuer eine {@link Component}, deren Inhalt als FilterValue verwendet wird.
 *
 * @author Thomas Freese
 */
public interface FilterEditor
{
    /**
     * Registers a listener to track changes of the content of the filter, must use the getFilterPropertyName() as the name of the property.
     * <p>
     * This method is already part of the JComponent signature, and there is no need to override it.
     * </p>
     *
     * @param propertyName Name der Eigenschaft
     * @param listener {@link PropertyChangeListener}
     *
     * @see #removePropertyChangeListener
     * @see #getFilterPropertyName
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Liefert die Spalte fuer die {@link JTable}.
     *
     * @return int
     */
    int getColumn();

    /**
     * Liefertn die Komponente des FilterEditors.
     *
     * @return {@link JComponent}
     */
    JComponent getComponent();

    /**
     * Liefert den Namen fuer das {@link PropertyChangeEvent}.
     *
     * @return String
     */
    String getFilterPropertyName();

    /**
     * Liefert den Wert des Filters.
     *
     * @return Object
     */
    Object getValue();

    /**
     * Removes a property change listener.
     *
     * @param propertyName Name der Eigenschaft
     * @param listener {@link PropertyChangeListener}
     *
     * @see #addPropertyChangeListener
     * @see #getFilterPropertyName
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Setzt den Wert des Filters.
     *
     * @param value Object
     */
    void setValue(Object value);
}
