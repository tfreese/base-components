package de.freese.base.swing.state;

import java.awt.Component;

import javax.swing.JLabel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * State eines Labels.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "LabelGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class LabelGuiState extends AbstractGuiState
{
    /**
     *
     */
    private static final long serialVersionUID = 8640371387385687109L;
    /**
     *
     */
    private String text = "";

    /**
     * Creates a new {@link LabelGuiState} object.
     */
    public LabelGuiState()
    {
        super(JLabel.class);
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        super.restore(component);

        JLabel label = (JLabel) component;

        label.setText(this.text);
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        super.store(component);

        JLabel label = (JLabel) component;

        this.text = label.getText();
    }
}
