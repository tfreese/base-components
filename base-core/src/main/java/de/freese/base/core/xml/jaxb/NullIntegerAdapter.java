package de.freese.base.core.xml.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Thomas Freese
 */
public class NullIntegerAdapter extends XmlAdapter<String, Integer> {
    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(final Integer arg0) throws Exception {
        return arg0 != null ? arg0.toString() : "null";
    }

    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Integer unmarshal(final String arg0) throws Exception {
        return arg0 != null ? Integer.valueOf(arg0) : null;
    }
}
