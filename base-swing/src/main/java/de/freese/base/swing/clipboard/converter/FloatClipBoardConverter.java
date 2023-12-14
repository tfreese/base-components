package de.freese.base.swing.clipboard.converter;

/**
 * @author Thomas Freese
 */
public class FloatClipBoardConverter extends AbstractNumberClipBoardConverter {
    @Override
    public Object fromClipboard(final String value) {
        if (value == null) {
            return null;
        }

        final String temp = normalizeFraction(value);

        return Float.valueOf(temp);
    }
}
