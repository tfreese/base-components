package de.freese.base.swing.clipboard.converter;

/**
 * @author Thomas Freese
 */
public class DoubleClipBoardConverter extends AbstractNumberClipBoardConverter {
    @Override
    public Object fromClipboard(final String value) {
        if (value == null) {
            return null;
        }

        String temp = normalizeFraction(value);

        return Double.valueOf(temp);
    }
}
