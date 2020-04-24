package de.freese.base.resourcemap.converter;

import java.awt.Image;

import de.freese.base.resourcemap.ResourceMap;

/**
 * Laedt aus einem String (Path) ein Image.
 * 
 * @author Thomas Freese
 */
public class ImageStringResourceConverter extends AbstractResourceConverter<Image>
{
	/**
	 * Erstellt ein neues {@link ImageStringResourceConverter} Object.
	 */
	public ImageStringResourceConverter()
	{
		super();
	}

	/**
	 * @see de.freese.base.resourcemap.converter.ResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.ResourceMap)
	 */
	@Override
	public Image parseString(final String key, final ResourceMap resourceMap)
		throws ResourceConverterException
	{
		return loadImageIcon(key, resourceMap.getClassLoader()).getImage();
	}
}
