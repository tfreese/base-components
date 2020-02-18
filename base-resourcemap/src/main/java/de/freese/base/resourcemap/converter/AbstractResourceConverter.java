package de.freese.base.resourcemap.converter;

import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

/**
 * Basisklasse eines {@link IResourceConverter}s.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter konvertierter Typ
 */
public abstract class AbstractResourceConverter<T> implements IResourceConverter<T>
{
	/**
	 *
	 */
	private final Set<Class<?>> supportedTypes = new HashSet<>();

	/**
	 * Erstellt ein neues {@link AbstractResourceConverter} Object.
	 */
	public AbstractResourceConverter()
	{
		super();

		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

		addType((Class<?>) parameterizedType.getActualTypeArguments()[0]);
	}

	/**
	 * Hinzufügen eines zusätzlichen unterstützen Typs.
	 * 
	 * @param type {@link Class}
	 */
	protected void addType(final Class<?> type)
	{
		this.supportedTypes.add(type);
	}

	/**
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#getSupportedTypes()
	 */
	@Override
	public Set<Class<?>> getSupportedTypes()
	{
		return Collections.unmodifiableSet(this.supportedTypes);
	}

	/**
	 * Laden eines Bildes. Wird kein Bild gefunden gehts im Pfad weiter nach oben.
	 * 
	 * @param imagePath String
	 * @param classLoader {@link ClassLoader}
	 * @return {@link ImageIcon}
	 * @throws ResourceConverterException Falls was schief geht.
	 */
	protected ImageIcon loadImageIcon(final String imagePath, final ClassLoader classLoader)
		throws ResourceConverterException
	{
		if (imagePath == null)
		{
			String msg = String.format("invalid image/icon path \"%s\"", imagePath);

			throw new ResourceConverterException(msg, null);
		}

		URL url = classLoader.getResource(imagePath);

		if (url == null)
		{
			url = Thread.currentThread().getContextClassLoader().getResource(imagePath);
		}

		if (url != null)
		{
			return new ImageIcon(url);
		}

		String msg = String.format("couldn't find Icon resource \"%s\"", imagePath);

		throw new ResourceConverterException(msg, imagePath);
	}

	/**
	 * String key is assumed to contain n number substrings separated by commas. Return a list of
	 * those integers or null if there are too many, too few, or if a substring can't be parsed. The
	 * format of the numbers is specified by Double.valueOf().
	 * 
	 * @param key String
	 * @param n int
	 * @param errorMsg String
	 * @return {@link List}
	 * @throws ResourceConverterException Falls was schief geht.
	 */
	protected List<Double> parseDoubles(final String key, final int n, final String errorMsg)
		throws ResourceConverterException
	{
		String[] splits = key.split(",", n + 1);

		if (splits.length != n)
		{
			throw new ResourceConverterException(errorMsg, key);
		}

		List<Double> doubles = new ArrayList<>(n);

		for (String doubleString : splits)
		{
			try
			{
				doubles.add(Double.valueOf(doubleString));
			}
			catch (NumberFormatException ex)
			{
				throw new ResourceConverterException(errorMsg, key, ex);
			}
		}

		return doubles;
	}

	/**
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#supportsType(java.lang.Class)
	 */
	@Override
	public boolean supportsType(final Class<?> type)
	{
		return this.supportedTypes.contains(type);
	}
}
