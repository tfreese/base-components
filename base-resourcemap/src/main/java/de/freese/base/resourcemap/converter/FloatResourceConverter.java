package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public class FloatResourceConverter extends AbstractNumberResourceConverter<Float> {
    @Override
    protected Float convertString(final String value, final int radix) throws NumberFormatException {
        return Float.valueOf(value.replace(",", "."));
    }
}
