package de.freese.base.swing.components.list.renderer;

import java.awt.Component;
import java.io.Serial;
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
public abstract class AbstractGenericListCellRenderer extends DefaultListCellRenderer {
    @Serial
    private static final long serialVersionUID = 335775306955315738L;

    private final String attribute;
    private final transient Logger logger = LoggerFactory.getLogger(getClass());
    private final String nullText;

    protected AbstractGenericListCellRenderer(final String attribute) {
        this(attribute, " ");
    }

    protected AbstractGenericListCellRenderer(final String attribute, final String nullText) {
        super();

        this.attribute = attribute;
        this.nullText = nullText;
    }

    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null) {
            try {
                label.setText(getString(value, attribute));
            }
            catch (Exception ex) {
                getLogger().warn(null, ex);

                try {
                    label.setText("" + invokeMethod(value, "toString"));
                }
                catch (Exception ex2) {
                    getLogger().error(ex2.getMessage(), ex2);

                    label.setText("Unknown Attribute: " + attribute);
                }
            }
        }
        else {
            label.setText(nullText);
        }

        return label;
    }

    protected final Logger getLogger() {
        return logger;
    }

    protected abstract String getString(Object object, String attribute) throws Exception;

    protected Object invokeField(final Object value, final String fieldName) throws Exception {
        final Field field = value.getClass().getField(fieldName);

        return field.get(value);
    }

    protected Object invokeMethod(final Object value, final String methodName) throws Exception {
        final Method method = value.getClass().getMethod(methodName, (Class<?>[]) null);

        return method.invoke(value, (Object[]) null);
    }
}
