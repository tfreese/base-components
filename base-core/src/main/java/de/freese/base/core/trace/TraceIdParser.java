package de.freese.base.core.trace;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Thomas Freese
 * @since 05.08.26
 */
public final class TraceIdParser {
    private static final Pattern VERSION_00_PATTERN = Pattern.compile("^00-([0-9a-f]{32})-([0-9a-f]{16})-([0-9a-f]{2})$");

    private static final String ZERO_TRACE_ID = "00000000000000000000000000000000";
    private static final String ZERO_PARENT_ID = "0000000000000000";

    public static Optional<Trace> parse(final String value) {
        if (value == null || value.length() != 55) {
            return Optional.empty();
        }

        final Matcher matcher = VERSION_00_PATTERN.matcher(value);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        final String traceId = matcher.group(1);
        String parentId = matcher.group(2);
        final String traceFlags = matcher.group(3);

        if (ZERO_TRACE_ID.equals(traceId)) {
            return Optional.empty();
        }

        if (ZERO_PARENT_ID.equals(parentId)) {
            parentId = null;
        }

        return Optional.of(new Trace(traceId, parentId, traceFlags));
    }

    private TraceIdParser() {
        super();
    }
}
