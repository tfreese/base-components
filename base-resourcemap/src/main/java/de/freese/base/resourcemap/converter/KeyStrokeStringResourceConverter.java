package de.freese.base.resourcemap.converter;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

/**
 * IResourceConverter fuer KeyStrokes.
 *
 * @author Thomas Freese
 */
public class KeyStrokeStringResourceConverter extends AbstractResourceConverter<KeyStroke>
{
    /**
     * Erstellt ein neues {@link KeyStrokeStringResourceConverter} Object.
     */
    public KeyStrokeStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public KeyStroke convert(final String key, final String value)
    {
        String m_key = value;

        if (m_key.contains("shortcut"))
        {
            int k = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            m_key = m_key.replaceAll("shortcut", (k == KeyEvent.VK_META) ? "meta" : "control");
        }

        return KeyStroke.getKeyStroke(m_key);
    }
}
