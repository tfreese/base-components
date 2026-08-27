package de.freese.base.resourcemap.converter;

import java.awt.Toolkit;
import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

/**
 * @author Thomas Freese
 */
public class KeyStrokeResourceConverter extends AbstractResourceConverter<KeyStroke> {
    private static final int KEY = InputEvent.SHIFT_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;

    @Override
    public KeyStroke convert(final String key, final String value) {
        String v = value;

        if (v.contains("shortcut")) {
            final int k = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            v = v.replace("shortcut", (k == KEY) ? "meta" : "control");
            // v = v.replace("shortcut", (k == KeyEvent.VK_META) ? "meta" : "control");
        }

        return KeyStroke.getKeyStroke(v);
    }
}
