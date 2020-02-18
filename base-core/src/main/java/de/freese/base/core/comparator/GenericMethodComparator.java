package de.freese.base.core.comparator;

import java.lang.reflect.Method;

/**
 * Comparator, der ueber Reflection die return Types der angegebenen Methode vergleicht.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Objekttyp
 */
public class GenericMethodComparator<T> extends AbstractKonfigurableComparator<T>
{
	/**
	 *
	 */
	private static final long serialVersionUID = 7931006224032889689L;

	/**
	 * Creates a new {@link GenericMethodComparator} object.
	 * 
	 * @param methodName String
	 */
	public GenericMethodComparator(final String methodName)
	{
		super(methodName);
	}

	/**
	 * Creates a new {@link GenericMethodComparator} object.
	 * 
	 * @param methodName1 String
	 * @param methodName2 String
	 */
	public GenericMethodComparator(final String methodName1, final String methodName2)
	{
		super(methodName1, methodName2);
	}

	/**
	 * Creates a new {@link GenericMethodComparator} object.
	 * 
	 * @param methodNames String[]
	 * @param directions int[], aus Sortungen; +1 = Asc; -1 = Desc
	 */
	public GenericMethodComparator(final String[] methodNames, final int[] directions)
	{
		super(methodNames, directions);
	}

	/**
	 * @see de.freese.base.core.comparator.AbstractKonfigurableComparator#getValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Object getValue(final Object object, final Object methodName)
	{
		Object value = null;

		if (object == null)
		{
			return value;
		}

		try
		{
			Method method = object.getClass().getMethod(methodName.toString());

			if (method != null)
			{
				value = method.invoke(object);
			}
		}
		catch (Exception ex)
		{
			getLogger().error(null, ex);
		}

		return value;
	}
}
