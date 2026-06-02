package de.freese.base.core.rvs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Faustregel: POSITION(start:end) in SQL*Loader entspricht substring(start - 1, end) in Java.<br>
 * Oracle Charset WE8MSWIN1252 = Java Charset.forName("windows-1252")<br>
 * WE8MSWIN1252 ist Single-Byte-Encoding = Byteposition = Zeichenposition = Zeichenweises mit Java möglich.
 *
 * @author Thomas Freese
 */
public final class RvsLayouts {
    private static final DateTimeFormatter DATE_TIME_FORMATTER_DEFAULT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private RvsLayouts() {
        super();
    }

    public static RvsLayout defaultLayout() {
        return new RvsLayout(List.of(
                new RvsField("a", 0, 1),
                new RvsField("b", 2, 5, raw -> LocalDate.parse(raw, DATE_TIME_FORMATTER_DEFAULT)),
                new RvsField("c", 5, 10, BigDecimal::new)
        )
        );
    }
}
