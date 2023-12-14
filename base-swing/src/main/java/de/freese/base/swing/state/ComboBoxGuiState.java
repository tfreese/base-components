package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "ComboBoxGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComboBoxGuiState extends AbstractGuiState {
    @Serial
    private static final long serialVersionUID = -8701963133645177327L;

    private int selectedIndex;
    private String selectedName = "";

    public ComboBoxGuiState() {
        super(JComboBox.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restore(final Component component) {
        super.restore(component);

        final JComboBox<Object> comboBox = (JComboBox<Object>) component;

        // Try to find the object by name.
        try {
            if ((this.selectedName != null) && (this.selectedName.length() > 0)) {
                final ListCellRenderer<? super Object> renderer = comboBox.getRenderer();
                final JList<Object> dummy = new JList<>();

                for (int i = 0; i < comboBox.getModel().getSize(); i++) {
                    final Object value = comboBox.getModel().getElementAt(i);
                    final Component c = renderer.getListCellRendererComponent(dummy, value, i, true, true);

                    if (c instanceof JLabel l) {
                        final String text = l.getText();

                        if (this.selectedName.equals(text)) {
                            this.selectedIndex = i;

                            break;
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            // Ignore
        }

        try {
            comboBox.setSelectedIndex(this.selectedIndex);
        }
        catch (Exception ex) {
            // Ignore
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void store(final Component component) {
        super.store(component);

        final JComboBox<Object> comboBox = (JComboBox<Object>) component;
        this.selectedIndex = comboBox.getSelectedIndex();
        this.selectedIndex = (this.selectedIndex == -1) ? 0 : this.selectedIndex;

        try {
            final JList<Object> dummy = new JList<>();
            final Object value = comboBox.getSelectedItem();
            final ListCellRenderer<? super Object> renderer = comboBox.getRenderer();
            final Component c = renderer.getListCellRendererComponent(dummy, value, this.selectedIndex, true, true);

            if (c instanceof JLabel l) {
                this.selectedName = l.getText();
            }
        }
        catch (Exception ex) {
            // Ignore
        }
    }
}
