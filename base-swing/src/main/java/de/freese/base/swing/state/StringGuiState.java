package de.freese.base.swing.state;

import java.awt.Component;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * State eines Strings.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "StringGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class StringGuiState extends AbstractGuiState
{
    /**
     *
     */
    private static final long serialVersionUID = -8014962022018271108L;

    /**
     *
     */
    private String value = null;

    /**
     * Erstellt ein neues {@link StringGuiState} Object.
     */
    public StringGuiState()
    {
        super(String.class);
    }

    /**
     * Liefert den Wert.
     *
     * @return String
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        // Empty
    }

    /**
     * Setzt den Wert.
     *
     * @param value String
     */
    public void setValue(final String value)
    {
        this.value = value;
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        // Empty
    }
}
