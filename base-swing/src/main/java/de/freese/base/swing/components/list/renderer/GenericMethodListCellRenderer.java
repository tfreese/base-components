package de.freese.base.swing.components.list.renderer;

import java.io.Serial;

/**
 * ListCellRenderer, dem ein Methodenname übergeben wird, und der seine Objekte über Reflection zur Anzeige bringt.
 *
 * @author Thomas Freese
 */
public class GenericMethodListCellRenderer extends AbstractGenericListCellRenderer {
    @Serial
    private static final long serialVersionUID = -4533421995241634353L;

    public GenericMethodListCellRenderer(final String methodName) {
        super(methodName);
    }

    public GenericMethodListCellRenderer(final String methodName, final String nullText) {
        super(methodName, nullText);
    }

    @Override
    protected String getString(final Object object, final String attribute) throws Exception {
        return "" + invokeMethod(object, attribute);
    }
}
