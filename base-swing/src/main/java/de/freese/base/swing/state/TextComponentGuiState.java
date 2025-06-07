package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;

import javax.swing.text.JTextComponent;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "TextComponentGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class TextComponentGuiState extends AbstractGuiState {
    @Serial
    private static final long serialVersionUID = 2574739641182232056L;

    private String text;

    public TextComponentGuiState() {
        super((Class<?>) null);
    }

    public String getText() {
        return text;
    }

    @Override
    public void restore(final Component component) {
        super.restore(component);

        final JTextComponent textComponent = (JTextComponent) component;

        try {
            textComponent.setText(getText());
        }
        catch (Exception ex) {
            // Ignore
        }
    }

    @Override
    public void store(final Component component) {
        super.store(component);

        final JTextComponent textComponent = (JTextComponent) component;

        text = textComponent.getText();
    }

    @Override
    public boolean supportsType(final Class<?> type) {
        return JTextComponent.class.isAssignableFrom(type);
    }
}
