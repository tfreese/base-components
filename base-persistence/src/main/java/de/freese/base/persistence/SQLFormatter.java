// Created: 20.07.2010
package de.freese.base.persistence;

import org.hibernate.engine.jdbc.internal.FormatStyle;

/**
 * Formattiert SQL Strings mit Hilfe des Hibernate Formatters.
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
     * Formattiert den SQL String in ein lesbares Format.
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
