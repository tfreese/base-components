/**
 *
 */
package de.freese.base.reports.jfreechart;

import java.text.NumberFormat;
import org.jfree.chart.axis.NumberTickUnit;

/**
 * {@link NumberTickUnit} die den Text ausblenden kann, je nach angegebenen Excludes ( {@link #setExcludes(String...)}).
 *
 * @author Thomas Freese
 */
public class ExtNumberTickUnit extends NumberTickUnit
{
    /**
     * 
     */
    private static final long serialVersionUID = 8151941607328082952L;

    /**
     * 
     */
    private String[] excludes;

    /**
     * Erstellt ein neues {@link ExtNumberTickUnit} Objekt.
     * 
     * @param size double
     */
    public ExtNumberTickUnit(final double size)
    {
        super(size);
    }

    /**
     * Erstellt ein neues {@link ExtNumberTickUnit} Objekt.
     * 
     * @param size double
     * @param formatter {@link NumberFormat}
     */
    public ExtNumberTickUnit(final double size, final NumberFormat formatter)
    {
        super(size, formatter);
    }

    /**
     * Erstellt ein neues {@link ExtNumberTickUnit} Objekt.
     * 
     * @param size double
     * @param formatter {@link NumberFormat}
     * @param minorTickCount int
     */
    public ExtNumberTickUnit(final double size, final NumberFormat formatter, final int minorTickCount)
    {
        super(size, formatter, minorTickCount);
    }

    /**
     * @param excludes String[]
     */
    public void setExcludes(final String...excludes)
    {
        this.excludes = excludes;
    }

    /**
     * @see org.jfree.chart.axis.NumberTickUnit#valueToString(double)
     */
    @Override
    public String valueToString(final double value)
    {
        String strValue = super.valueToString(value);

        for (String exclude : this.excludes)
        {
            if (exclude.equals(strValue))
            {
                strValue = "";
                break;
            }
        }

        return strValue;
    }
}
