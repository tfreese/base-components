// Created: 06.10.2008
package de.freese.base.swing.clipboard.converter;

import org.apache.commons.lang3.StringUtils;

/**
 * Allgemeiner ClipboardConverter für Boolean.
 *
 * @author Thomas Freese
 */
public class BooleanClipboardConverter extends AbstractClipboardConverter
{
    /**
     * @see de.freese.base.swing.clipboard.ClipboardConverter#fromClipboard(java.lang.String)
     */
    @Override
    public Object fromClipboard(final String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return Boolean.FALSE;
        }

        String temp = value.trim();

        if ("true".equalsIgnoreCase(temp) || "1".equalsIgnoreCase(temp) || "yes".equalsIgnoreCase(temp) || "ja".equalsIgnoreCase(temp)
                || "on".equalsIgnoreCase(temp) || "an".equalsIgnoreCase(temp) || "active".equalsIgnoreCase(temp) || "aktiv".equalsIgnoreCase(temp))
        {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
