package de.freese.base.swing.components.combobox;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

/**
 * Guarantees that only one ComboBox has an selectedObject that is not null.
 *
 * @author Thomas Freese
 */
public class ComboBoxGroup implements ItemListener {
    private final List<JComboBox<?>> boxes = Collections.synchronizedList(new ArrayList<>());

    public void add(final JComboBox<?> comboBox) {
        if ((comboBox == null) || this.boxes.contains(comboBox)) {
            return;
        }

        this.boxes.add(comboBox);
        comboBox.addItemListener(this);
    }

    @Override
    public void itemStateChanged(final ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            final JComboBox<?> srcComboBox = (JComboBox<?>) event.getSource();

            // Remove selection from all other ComboBoxes.
            for (JComboBox<?> comboBox : this.boxes) {
                if (comboBox != srcComboBox) {
                    comboBox.removeItemListener(this);
                    comboBox.setSelectedIndex(-1);
                    comboBox.addItemListener(this);
                }
            }
        }
    }

    public void remove(final JComboBox<?> comboBox) {
        if (comboBox == null) {
            return;
        }

        this.boxes.remove(comboBox);
        comboBox.removeItemListener(this);
    }
}
