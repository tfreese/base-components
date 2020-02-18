package de.freese.base.core.model.wrapper;

import de.freese.base.core.model.IdentifierProvider;
import de.freese.base.core.model.NameProvider;

/**
 * Interface eines Wrappers fuer ein Objekt.
 * 
 * @author Thomas Freese
 */
public interface ObjectWrapper extends NameProvider, IdentifierProvider
{
	/**
	 * Liefert das gewrappte Objekt.
	 * 
	 * @param <T> Konkreter Typ des Objekts.
	 * @return Object
	 */
	public <T> T getObject();

	/**
	 * Liefert die Klasse des gewrappten Objekts.
	 * 
	 * @return Class
	 */
	public Class<?> getObjectClass();
}
