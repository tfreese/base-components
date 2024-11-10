package de.freese.base.core.xml.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Thomas Freese
 */
public class NullIntegerAdapter extends XmlAdapter<String, Integer> {
    @Override
    public String marshal(final Integer arg0) {
        return arg0 != null ? arg0.toString() : "null";
    }

    @Override
    public Integer unmarshal(final String arg0) {
        return arg0 != null ? Integer.valueOf(arg0) : null;
    }
}
