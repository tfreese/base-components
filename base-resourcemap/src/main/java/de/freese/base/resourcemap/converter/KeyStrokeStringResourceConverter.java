package de.freese.base.resourcemap.converter;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import de.freese.base.resourcemap.ResourceMap;

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
     * @see de.freese.base.resourcemap.converter.ResourceConverter#parseString(java.lang.String, de.freese.base.resourcemap.ResourceMap)
     */
    @Override
    public KeyStroke parseString(final String key, final ResourceMap resourceMap) throws ResourceConverterException
    {
        String m_key = key;

        if (m_key.contains("shortcut"))
        {
            int k = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            m_key = m_key.replaceAll("shortcut", (k == KeyEvent.VK_META) ? "meta" : "control");
        }

        return KeyStroke.getKeyStroke(m_key);
    }
}
