package de.freese.base.swing.components.combobox;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;

/**
 * Gruppiert mehrere ComboBoxen, so dass nur jeweils eine ein selectedObject hat, was nicht NULL ist.
 *
 * @author Thomas Freese
 * @see ButtonGroup
 */
public class ComboBoxGroup implements ItemListener
{
    /**
     *
     */
    private final List<JComboBox<?>> boxes = Collections.synchronizedList(new ArrayList<>());

    /**
     * Verbindet eine {@link JComboBox} mit der Gruppe.
     *
     * @param comboBox {@link JComboBox}
     */
    public void add(final JComboBox<?> comboBox)
    {
        if ((comboBox == null) || this.boxes.contains(comboBox))
        {
            return;
        }

        this.boxes.add(comboBox);
        comboBox.addItemListener(this);
    }

    /**
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(final ItemEvent e)
    {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            JComboBox<?> srcComboBox = (JComboBox<?>) e.getSource();

            // Alle anderen ComboBoxen selection entfernen
            for (JComboBox<?> comboBox : this.boxes)
            {
                if (comboBox != srcComboBox)
                {
                    comboBox.removeItemListener(this);
                    comboBox.setSelectedIndex(-1);
                    comboBox.addItemListener(this);
                }
            }
        }
    }

    /**
     * Entfernt eine {@link JComboBox} aus der Gruppe.
     *
     * @param comboBox {@link JComboBox}
     */
    public void remove(final JComboBox<?> comboBox)
    {
        if (comboBox == null)
        {
            return;
        }

        this.boxes.remove(comboBox);
        comboBox.removeItemListener(this);
    }
}
