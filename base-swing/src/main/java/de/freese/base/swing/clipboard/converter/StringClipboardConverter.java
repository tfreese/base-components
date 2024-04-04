// Created: 06.10.2008
package de.freese.base.swing.clipboard.converter;

/**
 * @author Thomas Freese
 */
public class StringClipboardConverter extends AbstractClipboardConverter {
    @Override
    public Object fromClipboard(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.strip();
    }
}
