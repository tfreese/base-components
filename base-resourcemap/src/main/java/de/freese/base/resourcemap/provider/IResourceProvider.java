package de.freese.base.resourcemap.provider;

import java.util.Locale;
import java.util.Map;

/**
 * Ein {@link IResourceProvider} ist zuständig für das Laden der Resources.
 * 
 * @author Thomas Freese
 */
public interface IResourceProvider
{
	/**
	 * Laden der Daten von Resourcen für die Internationalisierung.
	 * 
	 * @param baseName String
	 * @param locale {@link Locale}
	 * @param classLoader {@link ClassLoader}
	 * @return {@link Map}
	 */
	public Map<String, String> getResources(String baseName, Locale locale, ClassLoader classLoader);
}
