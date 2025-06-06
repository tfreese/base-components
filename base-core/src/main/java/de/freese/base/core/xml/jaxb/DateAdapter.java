package de.freese.base.core.xml.jaxb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Thomas Freese
 */
public class DateAdapter extends XmlAdapter<String, Date> {
    private final DateFormat formatter;

    /**
     * Example:<br/>
     * yyyy-MM-dd<br/>
     * yyyy-MM-dd HH:mm:ss<br/>
     */
    public DateAdapter(final String pattern) {
        super();

        formatter = new SimpleDateFormat(pattern);
    }

    @Override
    public String marshal(final Date date) {
        return formatter.format(date);
    }

    @Override
    public Date unmarshal(final String date) throws Exception {
        return formatter.parse(date);
    }
}
