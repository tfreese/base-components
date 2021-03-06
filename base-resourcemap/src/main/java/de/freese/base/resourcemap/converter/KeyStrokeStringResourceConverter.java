package de.freese.base.resourcemap.converter;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

/**
 * {@link ResourceConverter} für {@link KeyStroke}.
 *
 * @author Thomas Freese
 */
public class KeyStrokeStringResourceConverter extends AbstractResourceConverter<KeyStroke>
{
    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public KeyStroke convert(final String key, final String value)
    {
        String v = value;

        if (v.contains("shortcut"))
        {
            int k = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            v = v.replace("shortcut", (k == KeyEvent.VK_META) ? "meta" : "control");
        }

        return KeyStroke.getKeyStroke(v);
    }
}
