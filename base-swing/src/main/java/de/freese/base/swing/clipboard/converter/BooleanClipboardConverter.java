// Created: 06.10.2008
package de.freese.base.swing.clipboard.converter;

/**
 * @author Thomas Freese
 */
public class BooleanClipboardConverter extends AbstractClipboardConverter {
    @Override
    public Object fromClipboard(final String value) {
        if (value == null || value.isBlank()) {
            return Boolean.FALSE;
        }

        final String temp = value.strip();

        if ("true".equalsIgnoreCase(temp)
                || "1".equalsIgnoreCase(temp)
                || "yes".equalsIgnoreCase(temp)
                || "ja".equalsIgnoreCase(temp)
                || "on".equalsIgnoreCase(temp)
                || "an".equalsIgnoreCase(temp)
                || "active".equalsIgnoreCase(temp)
                || "aktiv".equalsIgnoreCase(temp)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
