package de.freese.base.swing.state;

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * GuiState für eine {@link JTabbedPane}.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "TabbedPaneGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class TabbedPaneGuiState extends AbstractGuiState
{
    /**
     *
     */
    private static final long serialVersionUID = -5629441991603272347L;
    /**
     *
     */
    private int selectedIndex;

    /**
     * Erstellt ein neues {@link TabbedPaneGuiState} Object.
     */
    public TabbedPaneGuiState()
    {
        super(JTabbedPane.class);
    }

    /**
     * @return int
     */
    public int getSelectedIndex()
    {
        return this.selectedIndex;
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        super.restore(component);

        JTabbedPane tabbedPane = (JTabbedPane) component;

        try
        {
            tabbedPane.setSelectedIndex(this.selectedIndex != -1 ? this.selectedIndex : 0);
        }
        catch (Exception ex)
        {
            // Ignore
        }
    }

    /**
     * @param selectedIndex int
     */
    public void setSelectedIndex(final int selectedIndex)
    {
        this.selectedIndex = selectedIndex;
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        super.store(component);

        JTabbedPane tabbedPane = (JTabbedPane) component;

        this.selectedIndex = tabbedPane.getSelectedIndex();
    }
}
