package de.freese.base.core.rvs;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Thomas Freese
 */
class TestRvs {
    private static final DateTimeFormatter DATE_TIME_FORMATTER_DEFAULT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final List<RvsField> RVS_FIELDS = List.of(
            new RvsField("a A", 0, 1),
            new RvsField("b.B", 1, 9, raw -> LocalDate.parse(raw, DATE_TIME_FORMATTER_DEFAULT)),
            new RvsField("c", 9, 14, raw -> new BigDecimal(raw.replace(',', '.')))
    );

    @Test
    void testRvs() {
        final String line = "x2026052813,75";

        final RvsLayout rvsLayout = new RvsLayout(RVS_FIELDS);
        assertEquals(14, rvsLayout.getMinLineLength());

        final RvsRecord rvsRecord = FixedWidthParser.parse(line, rvsLayout);

        assertNotNull(rvsRecord);
        assertEquals("x", rvsRecord.get(RVS_FIELDS.getFirst().getName(), String.class));
        assertEquals(LocalDate.of(2026, 5, 28), rvsRecord.get(RVS_FIELDS.get(1).getName(), LocalDate.class));
        assertEquals(new BigDecimal("13.75"), rvsRecord.get(RVS_FIELDS.getLast().getName(), BigDecimal.class));
    }

    @Test
    void testRvsInsertSql() {
        final RvsLayout rvsLayout = new RvsLayout(RVS_FIELDS);
        assertEquals(14, rvsLayout.getMinLineLength());

        final String sql = RvsJdbcUtils.createInsertSql("test", rvsLayout);
        assertNotNull(sql);
        assertEquals("insert into TEST (A_A, B_B, C) values (?, ?, ?)", sql);
    }
}
