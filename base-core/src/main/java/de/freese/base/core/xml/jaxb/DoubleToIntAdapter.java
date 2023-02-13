package de.freese.base.core.xml.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Thomas Freese
 */
public class DoubleToIntAdapter extends XmlAdapter<String, Integer> {
    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(final Integer arg0) throws Exception {
        return arg0.toString();
    }

    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Integer unmarshal(final String arg0) throws Exception {
        if ((arg0 == null) || (arg0.length() == 0)) {
            return null;
        }

        double value = Double.parseDouble(arg0);

        return (int) value;
    }
}
