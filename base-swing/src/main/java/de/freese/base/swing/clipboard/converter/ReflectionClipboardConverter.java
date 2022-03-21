// Created: 06.10.2008
package de.freese.base.swing.clipboard.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * ClipboardConverter für Objekterzeugung über Konstruktorparameter mit Reflection.
 *
 * @author Thomas Freese
 */
public class ReflectionClipboardConverter extends AbstractClipboardConverter
{
    /**
     *
     */
    private final Class<?> clazz;

    /**
     * Creates a new {@link ReflectionClipboardConverter} object.
     * <p/>
     *
     * @param clazz {@link Class}
     */
    public ReflectionClipboardConverter(final Class<?> clazz)
    {
        super();

        this.clazz = Objects.requireNonNull(clazz, "clazz required");
    }

    /**
     * @see de.freese.base.swing.clipboard.ClipboardConverter#fromClipboard(java.lang.String)
     */
    @Override
    public Object fromClipboard(final String value)
    {
        try
        {
            Constructor<?> con = this.clazz.getConstructor(String.class);

            return con.newInstance(value);
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex)
        {
            getLogger().error(null, ex);

            return null;
        }
    }
}
