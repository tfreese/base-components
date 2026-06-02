package de.freese.base.core.rvs;

import java.util.Map;

/**
 * @author Thomas Freese
 */
public record RvsRecord(Map<String, Object> values, Map<String, String> raw) {
    public RvsRecord {
        values = values == null ? Map.of() : Map.copyOf(values);
        raw = raw == null ? Map.of() : Map.copyOf(raw);
    }

    public <T> T get(final RvsField rvsField, final Class<T> type) {
        return get(rvsField.getName(), type);
    }


    @SuppressWarnings("unchecked")
    public <T> T get(final String name, final Class<T> type) {
        final Object value = values.get(name);

        if (value == null) {
            return null;
        }

        if (!type.isInstance(value)) {
            throw new IllegalStateException("Feld '%s' ist %s, erwartet %s".formatted(name, value.getClass().getName(), type.getName()));
        }

        return (T) value;
    }

    public String getRaw(final String name) {
        return raw.get(name);
    }

    public Object getValue(final String name) {
        return values.get(name);
    }
}
