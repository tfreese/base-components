package de.freese.base.core.comparator;

import java.io.Serializable;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisklasse fuer konfigurierbare Comparatoren.
 * 
 * @see GenericMethodComparator
 * @author Thomas Freese
 * @param <T> Konkreter Objekttyp
 */
public abstract class AbstractKonfigurableComparator<T> implements Comparator<T>, Serializable
{
	/**
	 * 
	 */
	public static final int ASC = +1;

	/**
	 * 
	 */
	public static final int DESC = -1;

	/**
	 *
	 */
	private static final long serialVersionUID = 5157965624533246532L;

	/**
	 * 
	 */
	private final Object[] attributes;

	/**
	 * 
	 */
	private final int[] directions;

	/**
	 *
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Creates a new {@link AbstractKonfigurableComparator} object.
	 * 
	 * @param attribute Object
	 */
	public AbstractKonfigurableComparator(final Object attribute)
	{
		this(new Object[]
		{
			attribute
		}, new int[]
		{
			ASC
		});
	}

	/**
	 * Creates a new {@link AbstractKonfigurableComparator} object.
	 * 
	 * @param attribute1 Object
	 * @param attribute2 Object
	 */
	public AbstractKonfigurableComparator(final Object attribute1, final Object attribute2)
	{
		this(new Object[]
		{
				attribute1, attribute2
		}, new int[]
		{
				ASC, ASC
		});
	}

	/**
	 * Creates a new {@link AbstractKonfigurableComparator} object.
	 * 
	 * @param attributes Object[]
	 * @param directions int[], aus Sortungen; +1 = Asc; -1 = Desc
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	public AbstractKonfigurableComparator(final Object[] attributes, final int[] directions)
	{
		super();

		if (attributes.length != directions.length)
		{
			throw new IllegalArgumentException("Arrays haben unterschiedliche Laengen !");
		}

		this.attributes = attributes;
		this.directions = directions;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public int compare(final Object object1, final Object object2)
	{
		int comp = 0;

		for (int i = 0; i < this.attributes.length; i++)
		{
			Object attribute = this.attributes[i];
			int direction = this.directions[i];

			Object value1 = getValue(object1, attribute);
			Object value2 = getValue(object2, attribute);

			validateComparable(value1);
			validateComparable(value2);

			if ((value1 instanceof Comparable) && (value2 instanceof Comparable))
			{
				comp = ((Comparable<Object>) value1).compareTo(value2);

				// Sortierung beruecksuchtigen
				comp *= direction;
			}
			else if ((value1 == null) && (value2 != null))
			{
				comp = -1;
			}
			else if ((value1 != null) && (value2 == null))
			{
				comp = +1;
			}
			else
			{
				// NOOP
			}

			// Ungleichheit = Abbruch
			if (comp != 0)
			{
				break;
			}
		}

		return comp;
	}

	/**
	 * Liefert den Logger.
	 * 
	 * @return {@link Logger}
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * Liefert mit Hilfe eines Attributes den Wert eines bestimmten Objektes, um damit die Sortierung zu bestimmen.
	 * 
	 * @param object Object
	 * @param attribute Object
	 * @return Object
	 */
	protected abstract Object getValue(Object object, Object attribute);

	/**
	 * Logmeldung wenn Value nicht instanceof Comparable und nicht NULL ist.
	 * 
	 * @param value Object
	 */
	private void validateComparable(final Object value)
	{
		if ((value != null) && !(value instanceof Comparable<?>))
		{
			getLogger().warn("Value " + value + " ist nicht instanceof Comparable !");
		}
	}
}
