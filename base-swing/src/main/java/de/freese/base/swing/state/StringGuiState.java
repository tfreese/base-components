package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "StringGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class StringGuiState extends AbstractGuiState {
    @Serial
    private static final long serialVersionUID = -8014962022018271108L;

    private String value;

    public StringGuiState() {
        super(String.class);
    }

    public String getValue() {
        return this.value;
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component) {
        // Empty
    }

    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component) {
        // Empty
    }
}
