package de.freese.base.swing.filter.editor;

import java.awt.Component;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

/**
 * Interface für eine {@link Component}, deren Inhalt als FilterValue verwendet wird.
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
     * @see #removePropertyChangeListener
     * @see #getFilterPropertyName
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    int getColumn();

    JComponent getComponent();

    String getFilterPropertyName();

    Object getValue();

    /**
     * @see #addPropertyChangeListener
     * @see #getFilterPropertyName
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void setValue(Object value);
}
