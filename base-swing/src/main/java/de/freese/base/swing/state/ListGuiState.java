package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "ListGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListGuiState extends AbstractGuiState {
    @Serial
    private static final long serialVersionUID = -6237079790822368033L;

    private int[] selectedIndices;

    public ListGuiState() {
        super(JList.class);
    }

    @Override
    public void restore(final Component component) {
        super.restore(component);

        final JList<?> list = (JList<?>) component;

        try {
            list.setSelectedIndices(selectedIndices);
        }
        catch (Exception _) {
            // Ignore
        }
    }

    @Override
    public void store(final Component component) {
        super.store(component);

        final JList<?> list = (JList<?>) component;

        selectedIndices = list.getSelectedIndices();
    }
}
