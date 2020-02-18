package de.freese.base.swing.components.tree;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TreeCellRenderer, dem ein Methodennamen uebergeben werden, und der seine Objekte ueber Reflection
 * zur Anzeige bringt.
 * 
 * @author Thomas Freese
 */
public class GenericTreeCellRenderer extends DefaultTreeCellRenderer
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericTreeCellRenderer.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -775516117685402073L;

	/**
	 * 
	 */
	private final Map<Class<?>, Icon> classIconMap = new HashMap<>();

	/**
	 * 
	 */
	private final Map<Class<?>, String> classMethodMap = new HashMap<>();

	/**
	 * Creates a new {@link GenericTreeCellRenderer} object.
	 */
	public GenericTreeCellRenderer()
	{
		super();
	}

	/**
	 * Hinzufuegen eines Mappings eines Icons zur Klasse.
	 * 
	 * @param clazz Class
	 * @param icon {@link Icon}
	 */
	public void addIconMapping(final Class<?> clazz, final Icon icon)
	{
		getClassIconMap().put(clazz, icon);
	}

	/**
	 * Hinzufuegen eines Mappings einer Methode zur Klasse.
	 * 
	 * @param clazz Class
	 * @param method String
	 */
	public void addMethodMapping(final Class<?> clazz, final String method)
	{
		getClassMethodMap().put(clazz, method);
	}

	/**
	 * Map der Class->Icon Verknuepfung.
	 * 
	 * @return Map<Class<?>, Icon>
	 */
	protected Map<Class<?>, Icon> getClassIconMap()
	{
		return this.classIconMap;
	}

	/**
	 * Map der Class->Method Verknuepfung.
	 * 
	 * @return Map<Class<?>, String>
	 */
	protected Map<Class<?>, String> getClassMethodMap()
	{
		return this.classMethodMap;
	}

	/**
	 * Liefert das Icon zur Klasse.
	 * 
	 * @param clazz Class
	 * @return {@link Icon}
	 */
	protected Icon getIcon(final Class<?> clazz)
	{
		Icon icon = getClassIconMap().get(clazz);

		return icon;
	}

	/**
	 * Liefert den Methodennamen zur Klasse, oder 'toString', wenn nicht vorhanden.
	 * 
	 * @param clazz Class
	 * @return String
	 */
	protected String getMethod(final Class<?> clazz)
	{
		String method = getClassMethodMap().get(clazz);

		if (method == null)
		{
			method = "toString";
		}

		return method;
	}

	/**
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value,
													final boolean sel, final boolean expanded,
													final boolean leaf, final int row,
													final boolean hasFocus)
	{
		JLabel label =
				(JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
						hasFocus);

		if (value != null)
		{
			Class<?> clazz = value.getClass();

			// Text
			try
			{
				Method method = clazz.getMethod(getMethod(clazz), (Class[]) null);

				if (method != null)
				{
					Object retVal = method.invoke(value, (Object[]) null);

					label.setText("" + retVal);
				}
			}
			catch (Exception ex)
			{
				LOGGER.warn(null, ex);
			}

			// Icon
			Icon icon = getIcon(clazz);

			if (icon != null)
			{
				label.setIcon(icon);
			}
		}
		else
		{
			label.setText(" ");
		}

		return label;
	}

	/**
	 * Entfernt das Mapping eines Icons zur Klasse.
	 * 
	 * @param clazz Class
	 */
	public void removeIconMapping(final Class<?> clazz)
	{
		getClassIconMap().remove(clazz);
	}

	/**
	 * Entfernt das Mapping einer Methode zur Klasse.
	 * 
	 * @param clazz Class
	 */
	public void removeMethodMapping(final Class<?> clazz)
	{
		getClassMethodMap().remove(clazz);
	}
}
