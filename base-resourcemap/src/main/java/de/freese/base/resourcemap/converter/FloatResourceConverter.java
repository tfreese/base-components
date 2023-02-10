package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public class FloatResourceConverter extends AbstractNumberResourceConverter<Float> {
    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#convertString(java.lang.String, int)
     */
    @Override
    protected Float convertString(final String value, final int radix) throws NumberFormatException {
        return Float.valueOf(value.replace(",", "."));
    }
}
