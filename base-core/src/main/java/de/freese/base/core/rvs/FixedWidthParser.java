package de.freese.base.core.rvs;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public final class FixedWidthParser {

    private FixedWidthParser() {
        super();
    }

    public static RvsRecord parse(final String line, final RvsLayout layout) {
        if (line.length() < layout.getMinLineLength()) {
            throw new IllegalArgumentException("Zeilenlänge %d, erwartet %d".formatted(line.length(), layout.getMinLineLength()));
        }

        final Map<String, Object> values = new LinkedHashMap<>();
        final Map<String, String> raw = new LinkedHashMap<>();

        for (final RvsField rvsField : layout.getFields()) {
            final String slice = line.substring(rvsField.getStartInclusive(), rvsField.getEndExclusive());
            final String rawValue = slice.strip();

            raw.put(rvsField.getName(), rawValue);

            try {
                final Object valueConverted = rvsField.getConverter().apply(rawValue);
                values.put(rvsField.getName(), valueConverted);
            } catch (final RuntimeException ex) {
                throw new IllegalArgumentException("Feld '%s' konnte nicht konvertiert werden. Raw='%s'".formatted(rvsField.getName(), rawValue), ex);
            }
        }

        return new RvsRecord(values, raw);
    }
}
