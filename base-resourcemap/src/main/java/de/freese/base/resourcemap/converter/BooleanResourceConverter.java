package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public class BooleanResourceConverter extends AbstractResourceConverter<Boolean> {
    private final String[] trueStrings;

    /**
     * @param trueStrings String, zb. true, on, yes, 1
     */
    @SuppressWarnings("varargs")
    public BooleanResourceConverter(final String... trueStrings) {
        super();

        this.trueStrings = trueStrings;
    }

    @Override
    public Boolean convert(final String key, final String value) {
        final String v = value.strip();

        for (String trueString : trueStrings) {
            if (v.equalsIgnoreCase(trueString)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }
}
