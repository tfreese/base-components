package de.freese.base.swing.components.list.renderer;

import java.io.Serial;

/**
 * ListCellRenderer, dem ein FieldName übergeben wird, und der seine Objekte über Reflection zur Anzeige bringt.
 *
 * @author Thomas Freese
 */
public class GenericFieldListCellRenderer extends AbstractGenericListCellRenderer {
    @Serial
    private static final long serialVersionUID = -4533421995241634353L;

    public GenericFieldListCellRenderer(final String fieldName) {
        super(fieldName);
    }

    public GenericFieldListCellRenderer(final String fieldName, final String nullText) {
        super(fieldName, nullText);
    }

    @Override
    protected String getString(final Object object, final String attribute) throws Exception {
        return "" + invokeField(object, attribute);
    }
}
