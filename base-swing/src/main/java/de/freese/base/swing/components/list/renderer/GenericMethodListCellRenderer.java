package de.freese.base.swing.components.list.renderer;

import java.io.Serial;

/**
 * ListCellRenderer, dem ein Methodenname übergeben wird, und der seine Objekte über Reflection zur Anzeige bringt.
 *
 * @author Thomas Freese
 */
public class GenericMethodListCellRenderer extends AbstractGenericListCellRenderer
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4533421995241634353L;

    /**
     * Creates a new {@link GenericMethodListCellRenderer} object.
     *
     * @param methodName String
     */
    public GenericMethodListCellRenderer(final String methodName)
    {
        super(methodName);
    }

    /**
     * Creates a new {@link GenericMethodListCellRenderer} object.
     *
     * @param methodName String
     * @param nullText String
     */
    public GenericMethodListCellRenderer(final String methodName, final String nullText)
    {
        super(methodName, nullText);
    }

    /**
     * @see de.freese.base.swing.components.list.renderer.AbstractGenericListCellRenderer#getString(java.lang.Object, java.lang.String)
     */
    @Override
    protected String getString(final Object object, final String attribute) throws Exception
    {
        return "" + invokeMethod(object, attribute);
    }
}
