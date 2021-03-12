// Created: 06.10.2008
package de.freese.base.swing.clipboard.converter;

import org.apache.commons.lang3.StringUtils;

/**
 * Allgemeiner ClipboardConverter für String.
 *
 * @author Thomas Freese
 */
public class StringClipboardConverter extends AbstractClipboardConverter
{
    /**
     * @see de.freese.base.swing.clipboard.ClipboardConverter#fromClipboard(java.lang.String)
     */
    @Override
    public Object fromClipboard(final String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return null;
        }

        return value.trim();
    }
}
