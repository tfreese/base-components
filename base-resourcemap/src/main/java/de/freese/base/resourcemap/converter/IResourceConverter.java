package de.freese.base.resourcemap.converter;

import java.util.Set;

import de.freese.base.resourcemap.IResourceMap;

/**
 * Konvertiert aus einen String das konkrete Object.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter konvertierter Typ
 */
public interface IResourceConverter<T>
{
	/**
	 * @return {@link Set}
	 */
	public Set<Class<?>> getSupportedTypes();

	/**
	 * Liefert für einen Key das entsprechende Object.
	 * 
	 * @param key {@link String}
	 * @param resourceMap {@link IResourceMap}
	 * @return Object
	 * @throws ResourceConverterException Falls was schief geht.
	 */
	public T parseString(String key, IResourceMap resourceMap) throws ResourceConverterException;

	/**
	 * Liefert true wenn der Konverter diesen Typ unterstützt.
	 * 
	 * @param type Class
	 * @return boolean
	 */
	public boolean supportsType(final Class<?> type);
}
