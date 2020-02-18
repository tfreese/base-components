package de.freese.base.core.xml.dom;

import de.freese.base.core.comparator.AbstractKonfigurableComparator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Comparator, der DOM {@link Node}s ueber ein oder mehrere Attribute sortiert.
 * 
 * @author Thomas Freese
 */
public class NodeAttributeComparator extends AbstractKonfigurableComparator<Object>
{
	/**
	 *
	 */
	private static final long serialVersionUID = 5289713048464916421L;

	/**
	 * Creates a new {@link NodeAttributeComparator} object.
	 * 
	 * @param attributeName String
	 */
	public NodeAttributeComparator(final String attributeName)
	{
		super(attributeName);
	}

	/**
	 * Creates a new {@link NodeAttributeComparator} object.
	 * 
	 * @param attributeName1 String
	 * @param attributeName2 String
	 */
	public NodeAttributeComparator(final String attributeName1, final String attributeName2)
	{
		super(attributeName1, attributeName2);
	}

	/**
	 * Creates a new {@link NodeAttributeComparator} object.
	 * 
	 * @param attributeNamen String[]
	 * @param directions int[]
	 */
	public NodeAttributeComparator(final String[] attributeNamen, final int[] directions)
	{
		super(attributeNamen, directions);
	}

	/**
	 * @see de.freese.base.core.comparator.AbstractKonfigurableComparator#getValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Object getValue(final Object object, final Object attribute)
	{
		Element element = (Element) object;

		return element.getAttribute(attribute.toString());
	}
}
