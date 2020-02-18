package de.freese.base.resourcemap.converter;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.freese.base.resourcemap.IResourceMap;

/**
 * Laedt aus einem String (Path) ein Icon.
 * 
 * @author Thomas Freese
 */
public class IconStringResourceConverter extends AbstractResourceConverter<Icon>
{
	/**
	 * Erstellt ein neues {@link IconStringResourceConverter} Object.
	 */
	public IconStringResourceConverter()
	{
		super();

		addType(ImageIcon.class);
	}

	/**
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.IResourceMap)
	 */
	@Override
	public Icon parseString(final String key, final IResourceMap resourceMap)
		throws ResourceConverterException
	{
		return loadImageIcon(key, resourceMap.getClassLoader());
	}
}
