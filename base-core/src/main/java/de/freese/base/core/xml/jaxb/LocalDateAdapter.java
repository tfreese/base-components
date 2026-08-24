package de.freese.base.core.xml.jaxb;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Thomas Freese
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String marshal(final LocalDate date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    @Override
    public LocalDate unmarshal(final String date) throws Exception {
        return DATE_TIME_FORMATTER.parse(date, LocalDate::from);
    }
}
