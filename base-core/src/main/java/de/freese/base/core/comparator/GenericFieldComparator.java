package de.freese.base.core.comparator;

import java.lang.reflect.Field;

/**
 * Comparator, der ueber Reflection die Felder eines Objects vergleicht.
 * 
 * @author Thomas Freese
 */
public class GenericFieldComparator extends AbstractKonfigurableComparator<Object>
{
	/**
	 *
	 */
	private static final long serialVersionUID = 8754475799347636063L;

	/**
	 * Creates a new {@link GenericFieldComparator} object.
	 * 
	 * @param fieldName String
	 */
	public GenericFieldComparator(final String fieldName)
	{
		super(fieldName);
	}

	/**
	 * Creates a new {@link GenericFieldComparator} object.
	 * 
	 * @param fieldName1 String
	 * @param fieldName2 String
	 */
	public GenericFieldComparator(final String fieldName1, final String fieldName2)
	{
		super(fieldName1, fieldName2);
	}

	/**
	 * Creates a new {@link GenericFieldComparator} object.
	 * 
	 * @param fieldNames String[]
	 * @param directions int[], aus Sortungen; +1 = Asc; -1 = Desc
	 */
	public GenericFieldComparator(final String[] fieldNames, final int[] directions)
	{
		super(fieldNames, directions);
	}

	/**
	 * @see de.freese.base.core.comparator.AbstractKonfigurableComparator#getValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Object getValue(final Object object, final Object fieldName)
	{
		Object value = null;

		if (object == null)
		{
			return value;
		}

		try
		{
			Field field = object.getClass().getField(fieldName.toString());

			if (field != null)
			{
				value = field.get(object);
			}
		}
		catch (Exception ex)
		{
			getLogger().error(null, ex);
		}

		return value;
	}
}
