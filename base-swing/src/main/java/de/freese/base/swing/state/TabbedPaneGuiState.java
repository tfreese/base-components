package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JTabbedPane;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "TabbedPaneGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class TabbedPaneGuiState extends AbstractGuiState {
    @Serial
    private static final long serialVersionUID = -5629441991603272347L;

    private int selectedIndex;

    public TabbedPaneGuiState() {
        super(JTabbedPane.class);
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    @Override
    public void restore(final Component component) {
        super.restore(component);

        final JTabbedPane tabbedPane = (JTabbedPane) component;

        try {
            tabbedPane.setSelectedIndex(this.selectedIndex != -1 ? this.selectedIndex : 0);
        }
        catch (Exception ex) {
            // Ignore
        }
    }

    public void setSelectedIndex(final int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    public void store(final Component component) {
        super.store(component);

        final JTabbedPane tabbedPane = (JTabbedPane) component;

        this.selectedIndex = tabbedPane.getSelectedIndex();
    }
}
