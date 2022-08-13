// Created: 15.04.2008
package de.freese.base.reports.layout;

import javax.swing.SwingConstants;

/**
 * Implementierung eines LayoutElementes für Linien.
 *
 * @author Thomas Freese
 */
public class LineLayoutElement extends AbstractLayoutElement
{
    /**
     *
     */
    private int orientation = -1;
    /**
     *
     */
    private int thickness = 1;

    /**
     * Creates a new LineLayoutElement object.
     */
    public LineLayoutElement()
    {
        this(SwingConstants.HORIZONTAL);
    }

    /**
     * Creates a new LineLayoutElement object.
     *
     * @param orientation <code>SwingConstants</code>: <code>VERTICAL</code>, oder <code>HORIZONTAL</code>
     */
    public LineLayoutElement(final int orientation)
    {
        super();

        this.orientation = orientation;
    }

    /**
     * @see de.freese.base.reports.layout.AbstractLayoutElement#getHeight()
     */
    @Override
    public float getHeight()
    {
        return switch (getOrientation())
                {
                    case SwingConstants.HORIZONTAL -> getThickness();
                    case SwingConstants.VERTICAL -> super.getHeight();
                    default -> -1;
                };
    }

    /**
     * Ausrichtung der Linie.
     *
     * @return int; <code>SwingConstants</code>: <code>VERTICAL</code>, oder <code>HORIZONTAL</code>
     */
    public int getOrientation()
    {
        return this.orientation;
    }

    /**
     * Dicke der Linie.
     *
     * @return int
     */
    public int getThickness()
    {
        return this.thickness;
    }

    /**
     * @see de.freese.base.reports.layout.AbstractLayoutElement#getWidth()
     */
    @Override
    public float getWidth()
    {
        return switch (getOrientation())
                {
                    case SwingConstants.HORIZONTAL -> super.getWidth();
                    case SwingConstants.VERTICAL -> getThickness();
                    default -> -1;
                };
    }

    /**
     * Dicke der Linie.
     *
     * @param thickness int
     */
    public void setThickness(final int thickness)
    {
        this.thickness = thickness;
    }
}
