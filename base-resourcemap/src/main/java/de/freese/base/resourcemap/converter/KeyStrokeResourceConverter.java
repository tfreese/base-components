package de.freese.base.resourcemap.converter;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 * @author Thomas Freese
 */
public class KeyStrokeResourceConverter extends AbstractResourceConverter<KeyStroke> {
    @Override
    public KeyStroke convert(final String key, final String value) {
        String v = value;

        if (v.contains("shortcut")) {
            final int k = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            v = v.replace("shortcut", (k == KeyEvent.VK_META) ? "meta" : "control");
        }

        return KeyStroke.getKeyStroke(v);
    }
}
