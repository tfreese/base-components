package de.freese.base.swing.components.list.renderer;

import java.awt.Component;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BasisListCellRenderer, der seine Objekte über Reflection zur Anzeige bringt.
 *
 * @author Thomas Freese
 */
public abstract class AbstractGenericListCellRenderer extends DefaultListCellRenderer
{
    /**
     *
     */
    private static final long serialVersionUID = 335775306955315738L;
    /**
     *
     */
    private final String attribute;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private final String nullText;

    /**
     * Creates a new {@link AbstractGenericListCellRenderer} object.
     *
     * @param attribute String
     */
    protected AbstractGenericListCellRenderer(final String attribute)
    {
        this(attribute, " ");
    }

    /**
     * Creates a new {@link AbstractGenericListCellRenderer} object.
     *
     * @param attribute String
     * @param nullText String, falls Object null ist, anderen Text rendern als Leerzeichen
     */
    protected AbstractGenericListCellRenderer(final String attribute, final String nullText)
    {
        super();

        this.attribute = attribute;
        this.nullText = nullText;
    }

    /**
     * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected,
                                                  final boolean cellHasFocus)
    {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null)
        {
            try
            {
                label.setText(getString(value, this.attribute));
            }
            catch (Exception ex)
            {
                getLogger().warn(null, ex);

                try
                {
                    label.setText("" + invokeMethod(value, "toString"));
                }
                catch (Exception ex2)
                {
                    getLogger().error(null, ex2);

                    label.setText("Unknown Attribute: " + this.attribute);
                }
            }
        }
        else
        {
            label.setText(this.nullText);
        }

        return label;
    }

    /**
     * Liefert den Logger.
     *
     * @return {@link Logger}
     */
    protected final Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert mithilfe eines Attributes den Wert eines bestimmten Objektes.
     *
     * @param object Object
     * @param attribute String
     *
     * @return String
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract String getString(Object object, String attribute) throws Exception;

    /**
     * Aufrufen des Methoden Names des Values.
     *
     * @param value Object
     * @param fieldName String
     *
     * @return Object
     *
     * @throws Exception Falls was schief geht.
     */
    protected Object invokeField(final Object value, final String fieldName) throws Exception
    {
        Field field = value.getClass().getField(fieldName);

        return field.get(value);
    }

    /**
     * Aufrufen des Methoden Names des Values.
     *
     * @param value Object
     * @param methodName String
     *
     * @return Object
     *
     * @throws Exception Falls was schief geht.
     */
    protected Object invokeMethod(final Object value, final String methodName) throws Exception
    {
        Method method = value.getClass().getMethod(methodName, (Class[]) null);

        return method.invoke(value, (Object[]) null);
    }
}
