package de.freese.base.swing.state;

import javax.swing.JButton;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * State eines Buttons.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "ButtonGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class ButtonGuiState extends AbstractGuiState
{
    /**
     *
     */
    private static final long serialVersionUID = 8640371387385687109L;

    /**
     * Creates a new {@link ButtonGuiState} object.
     */
    public ButtonGuiState()
    {
        super(JButton.class);
    }
}
