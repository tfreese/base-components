// Created: 06.10.2008
package de.freese.base.swing.clipboard.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.swing.clipboard.ClipboardConverter;

/**
 * Basis ClipboardConverter.
 *
 * @author Thomas Freese
 */
public abstract class AbstractClipboardConverter implements ClipboardConverter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @see de.freese.base.swing.clipboard.ClipboardConverter#toClipboard(java.lang.Object)
     */
    @Override
    public String toClipboard(final Object object) {
        if (object != null) {
            return object.toString();
        }

        return "";
    }

    protected Logger getLogger() {
        return this.logger;
    }
}
