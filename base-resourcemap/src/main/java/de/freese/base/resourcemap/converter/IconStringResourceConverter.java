package de.freese.base.resourcemap.converter;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.freese.base.resourcemap.ResourceMap;

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
	 * @see de.freese.base.resourcemap.converter.ResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.ResourceMap)
	 */
	@Override
	public Icon parseString(final String key, final ResourceMap resourceMap)
		throws ResourceConverterException
	{
		return loadImageIcon(key, resourceMap.getClassLoader());
	}
}
