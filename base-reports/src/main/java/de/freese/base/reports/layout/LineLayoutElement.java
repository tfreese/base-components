/**
 * 15.04.2008
 */
package de.freese.base.reports.layout;

import javax.swing.SwingConstants;

/**
 * Implementierung eines LayoutElementes fuer Linien.
 * 
 * @author Thomas Freese
 */
public class LineLayoutElement extends AbstractLayoutElement
{
	/**
	 * !
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
	 * @param orientation <code>SwingConstants</code>: <code>VERTICAL</code>, oder
	 *            <code>HORIZONTAL</code>
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
		float height = -1;

		switch (getOrientation())
		{
			case SwingConstants.HORIZONTAL:
				height = getThickness();

				break;

			case SwingConstants.VERTICAL:
				height = super.getHeight();

				break;

			default:
				break;
		}

		return height;
	}

	/**
	 * Ausrichting der Linie.
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
	 * @param thickness int
	 */
	public void setThickness(final int thickness)
	{
		this.thickness = thickness;
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
		float width = -1;

		switch (getOrientation())
		{
			case SwingConstants.HORIZONTAL:
				width = super.getWidth();

				break;

			case SwingConstants.VERTICAL:
				width = getThickness();

				break;

			default:
				break;
		}

		return width;
	}
}
