package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * State für eine Liste.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "ListGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListGuiState extends AbstractGuiState
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6237079790822368033L;
    /**
     * Die selektierten Indizies der Liste
     */
    private int[] selectedIndices;

    /**
     * Creates a new {@link ListGuiState} object.
     */
    public ListGuiState()
    {
        super(JList.class);
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        super.restore(component);

        JList<?> list = (JList<?>) component;

        try
        {
            list.setSelectedIndices(this.selectedIndices);
        }
        catch (Exception ex)
        {
            // Ignore
        }
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        super.store(component);

        JList<?> list = (JList<?>) component;

        this.selectedIndices = list.getSelectedIndices();
    }
}
