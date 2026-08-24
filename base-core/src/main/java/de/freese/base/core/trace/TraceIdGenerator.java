package de.freese.base.core.trace;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Random;

/**
 * W3C Trace Context
 * Der standardisierte Header: traceparent
 * <p/>
 * Format:
 * version-trace-id-parent-id-trace-flags
 * 00-<32 lowercase hex>-<16 lowercase hex>-<2 lowercase hex>
 * <p/>
 * Beispiel:
 * <p/>
 * HTTP traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
 * <p/>
 * oder wenn traceparent nicht vorhanden:
 * HTTP X-Trace-Id: 4bf92f3577b34da6a3ce929d0e0e4736
 *
 * @author Thomas Freese
 * @since 05.08.26
 */
public final class TraceIdGenerator {
    private static final Random RANDOM = new SecureRandom();
    private static final HexFormat HEX_FORMAT = HexFormat.of().withLowerCase();

    public static String newParentId() {
        return newNonZeroIdentifier(8);
    }

    public static Trace newTrace() {
        return new Trace(newTraceId(), newParentId(), "00");
    }

    public static String newTraceId() {
        return newNonZeroIdentifier(16);
    }

    public static Trace newTraceSampled() {
        return new Trace(newTraceId(), newParentId(), "01");
    }

    private static boolean isAllZero(final byte[] bytes) {
        for (final byte value : bytes) {
            if (value != 0) {
                return false;
            }
        }

        return true;
    }

    private static String newNonZeroIdentifier(final int length) {
        final byte[] bytes = new byte[length];

        do {
            RANDOM.nextBytes(bytes);
        }
        while (isAllZero(bytes));

        return HEX_FORMAT.formatHex(bytes);
    }

    private TraceIdGenerator() {
        super();
    }
}
