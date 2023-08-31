package de.freese.base.swing.clipboard.converter;

/**
 * @author Thomas Freese
 */
public class LongClipBoardConverter extends AbstractNumberClipBoardConverter {
    @Override
    public Object fromClipboard(final String value) {
        if (value == null) {
            return null;
        }

        String temp = normalizeNonFraction(value);

        return Long.valueOf(temp);
    }
}
