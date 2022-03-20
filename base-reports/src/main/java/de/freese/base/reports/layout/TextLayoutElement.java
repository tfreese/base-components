// Created: 15.04.2008
package de.freese.base.reports.layout;

/**
 * Implementierung eines LayoutElementes für Text.
 *
 * @author Thomas Freese
 */
public class TextLayoutElement extends AbstractLayoutElement
{
    /**
     * Creates a new TextLayoutElement object.
     */
    public TextLayoutElement()
    {
        super("");
    }

    /**
     * Text des Elementes.
     *
     * @return String
     */
    public String getText()
    {
        return getName();
    }

    /**
     * Text des Elementes.
     *
     * @param text String
     */
    public void setText(final String text)
    {
        setName(text);
    }
}
