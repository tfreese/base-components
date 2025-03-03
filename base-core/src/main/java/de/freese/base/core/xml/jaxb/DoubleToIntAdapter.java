package de.freese.base.core.xml.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Thomas Freese
 */
public class DoubleToIntAdapter extends XmlAdapter<String, Integer> {
    @Override
    public String marshal(final Integer arg0) {
        return arg0.toString();
    }

    @Override
    public Integer unmarshal(final String arg0) {
        if (arg0 == null || arg0.isEmpty()) {
            return null;
        }

        final double value = Double.parseDouble(arg0);

        return (int) value;
    }
}
