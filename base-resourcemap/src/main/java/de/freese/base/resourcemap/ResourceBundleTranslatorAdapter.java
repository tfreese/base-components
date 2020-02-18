/**
 * Created: 01.07.2011
 */

package de.freese.base.resourcemap;

import java.util.Locale;
import java.util.ResourceBundle;

import de.freese.base.core.i18n.Translator;

/**
 * Implementierung, die ein {@link ResourceBundle} verwendet.
 * 
 * @author Thomas Freese
 */
public class ResourceBundleTranslatorAdapter implements Translator
{
	/**
	 * 
	 */
	private final ResourceBundle resourceBundle;

	/**
	 * Erstellt ein neues {@link ResourceBundleTranslatorAdapter} Object.
	 * 
	 * @param resourceBundle {@link ResourceBundle}
	 */
	public ResourceBundleTranslatorAdapter(final ResourceBundle resourceBundle)
	{
		super();

		this.resourceBundle = resourceBundle;
	}

	/**
	 * Erstellt ein neues {@link ResourceBundleTranslatorAdapter} Object.
	 * 
	 * @param baseName String
	 */
	public ResourceBundleTranslatorAdapter(final String baseName)
	{
		this(ResourceBundle.getBundle(baseName));
	}

	/**
	 * Erstellt ein neues {@link ResourceBundleTranslatorAdapter} Object.
	 * 
	 * @param baseName String
	 * @param locale {@link Locale}
	 */
	public ResourceBundleTranslatorAdapter(final String baseName, final Locale locale)
	{
		this(ResourceBundle.getBundle(baseName, locale));
	}

	/**
	 * @see de.freese.base.core.i18n.Translator#translate(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public String translate(final String key, final Object...args)
	{
		String text = this.resourceBundle.getString(key);

		return String.format(text, args);
	}
}
