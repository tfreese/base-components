package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * GuiState für ein {@link JTextField}.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "TextComponentGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class TextComponentGuiState extends AbstractGuiState
{
    @Serial
    private static final long serialVersionUID = 2574739641182232056L;

    private String text;

    public TextComponentGuiState()
    {
        super((Class<?>) null);
    }

    public String getText()
    {
        return this.text;
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        super.restore(component);

        JTextComponent textComponent = (JTextComponent) component;

        try
        {
            textComponent.setText(getText());
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

        JTextComponent textComponent = (JTextComponent) component;

        this.text = textComponent.getText();
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#supportsType(java.lang.Class)
     */
    @Override
    public boolean supportsType(final Class<?> type)
    {
        return JTextComponent.class.isAssignableFrom(type);
    }
}
