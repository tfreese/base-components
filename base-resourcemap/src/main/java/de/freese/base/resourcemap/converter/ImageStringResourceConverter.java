package de.freese.base.resourcemap.converter;

import java.awt.Image;

import de.freese.base.resourcemap.IResourceMap;

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
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.IResourceMap)
	 */
	@Override
	public Image parseString(final String key, final IResourceMap resourceMap)
		throws ResourceConverterException
	{
		return loadImageIcon(key, resourceMap.getClassLoader()).getImage();
	}
}
