// Created: 20.07.2010
package de.freese.base.persistence;

import org.hibernate.engine.jdbc.internal.FormatStyle;

/**
 * Formatiert SQL Strings mithilfe des Hibernate Formatters.
 *
 * @author Thomas Freese
 */
public class SQLFormatter
{
    /**
     *
     */
    private FormatStyle formatStyle = FormatStyle.NONE;

    /**
     * Formatiert den SQL String in ein lesbares Format.
     *
     * @param sql String
     *
     * @return String
     */
    public String format(final String sql)
    {
        return this.formatStyle.getFormatter().format(sql);
    }

    /**
     * @return boolean
     */
    public boolean isFormatSQL()
    {
        return (this.formatStyle != FormatStyle.NONE);
    }

    /**
     * @param formatSQL boolean
     */
    public void setFormatSQL(final boolean formatSQL)
    {
        if (formatSQL)
        {
            this.formatStyle = FormatStyle.BASIC;
        }
        else
        {
            this.formatStyle = FormatStyle.NONE;
        }
    }
}
