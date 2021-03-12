package de.freese.base.swing.state;

import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * GuiState fuer ein {@link JTextField}.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "TextComponentGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class TextComponentGuiState extends AbstractGuiState
{
    /**
     *
     */
    private static final long serialVersionUID = 2574739641182232056L;

    /**
     *
     */
    private String text;

    /**
     * Erstellt ein neues {@link TextComponentGuiState} Object.
     */
    public TextComponentGuiState()
    {
        super((Class<?>) null);
    }

    /**
     * @return String
     */
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
        if (JTextComponent.class.isAssignableFrom(type))
        {
            return true;
        }

        return false;
    }
}
