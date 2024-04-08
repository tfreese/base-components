// Created: 15.04.2008
package de.freese.base.reports.layout;

/**
 * Implementierung eines LayoutElementes für Text.
 *
 * @author Thomas Freese
 */
public class TextLayoutElement extends AbstractLayoutElement {
    public TextLayoutElement() {
        super("");
    }

    public String getText() {
        return getName();
    }

    public void setText(final String text) {
        setName(text);
    }
}
