package de.freese.base.core.xml.jaxb;

import java.awt.Color;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB Adapter für ein {@link Color} Objekt.
 *
 * @author Thomas Freese
 */
public class ColorAdapter extends XmlAdapter<String, Color>
{
    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(final Color arg0) throws Exception
    {
        // zB. ff00ff00 -> Grün
        String hex = "#" + Integer.toHexString(arg0.getRGB());

        // Die ersten beiden FFs abschneiden, ist der Alphawert
        hex = hex.replaceFirst("#ff", "#");

        return hex;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Color unmarshal(final String arg0) throws Exception
    {
        return Color.decode(arg0);
    }
}
