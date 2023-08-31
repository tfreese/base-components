package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public class DoubleResourceConverter extends AbstractNumberResourceConverter<Double> {
    @Override
    protected Double convertString(final String value, final int radix) throws NumberFormatException {
        return Double.valueOf(value.replace(",", "."));
    }
}
