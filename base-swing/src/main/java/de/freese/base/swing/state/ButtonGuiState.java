package de.freese.base.swing.state;

import java.io.Serial;

import javax.swing.JButton;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * State eines Buttons.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "ButtonGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class ButtonGuiState extends AbstractGuiState
{
    @Serial
    private static final long serialVersionUID = 8640371387385687109L;

    public ButtonGuiState()
    {
        super(JButton.class);
    }
}
