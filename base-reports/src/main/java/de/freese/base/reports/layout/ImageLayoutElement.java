/**
 * 15.04.2008
 */
package de.freese.base.reports.layout;

import java.awt.Image;

/**
 * Implementierung eines LayoutElementes fuer Bilder.
 * 
 * @author Thomas Freese
 */
public class ImageLayoutElement extends AbstractLayoutElement
{
	/**
	 *
	 */
	private Image image = null;

	/**
	 * Creates a new {@link ImageLayoutElement} object.
	 */
	public ImageLayoutElement()
	{
		super();
	}

	/**
	 * Creates a new {@link ImageLayoutElement} object.
	 * 
	 * @param name String
	 */
	public ImageLayoutElement(final String name)
	{
		super(name);
	}

	/**
	 * Bild des LayoutElements.
	 * 
	 * @param image {@link Image}
	 */
	public void setImage(final Image image)
	{
		this.image = image;
	}

	/**
	 * Bild des LayoutElements.
	 * 
	 * @return {@link Image}
	 */
	public Image getImage()
	{
		return this.image;
	}
}
