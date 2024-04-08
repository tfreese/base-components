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
public class ReflectionClipboardConverter extends AbstractClipboardConverter {
    private final Class<?> clazz;

    public ReflectionClipboardConverter(final Class<?> clazz) {
        super();

        this.clazz = Objects.requireNonNull(clazz, "clazz required");
    }

    @Override
    public Object fromClipboard(final String value) {
        try {
            final Constructor<?> con = this.clazz.getConstructor(String.class);

            return con.newInstance(value);
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            getLogger().error(ex.getMessage(), ex);

            return null;
        }
    }
}
