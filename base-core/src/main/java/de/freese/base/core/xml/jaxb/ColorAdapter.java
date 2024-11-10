package de.freese.base.core.xml.jaxb;

import java.awt.Color;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Thomas Freese
 */
public class ColorAdapter extends XmlAdapter<String, Color> {
    @Override
    public String marshal(final Color arg0) {
        // z.B. ff00ff00 -> Grün
        String hex = "#" + Integer.toHexString(arg0.getRGB());

        // Die ersten beiden FFs abschneiden, ist der Alphawert
        hex = hex.replaceFirst("#ff", "#");

        return hex;
    }

    @Override
    public Color unmarshal(final String arg0) {
        return Color.decode(arg0);
    }
}
