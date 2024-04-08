package de.freese.base.swing.clipboard.converter;

/**
 * @author Thomas Freese
 */
public class IntegerClipBoardConverter extends AbstractNumberClipBoardConverter {
    @Override
    public Object fromClipboard(final String value) {
        if (value == null) {
            return null;
        }

        final String temp = normalizeNonFraction(value);

        return Integer.valueOf(temp);
    }
}
