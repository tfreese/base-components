package de.freese.base.core.trace;

import org.jspecify.annotations.NonNull;

/**
 * @author Thomas Freese
 * @since 05.08.26
 */
public record Trace(
        String traceId,
        String parentId,
        String traceFlags) {
    /**
     * true = Hinweis für nachgelagerte Systeme das dieser Trace aufgezeichnet/geloggt werden sollte.
     */
    public boolean isSampled() {
        final int flags = Integer.parseInt(traceFlags, 16);

        return (flags & 0x01) != 0;
    }

    @Override
    public @NonNull String toString() {
        return "00-" + traceId + '-' + parentId + '-' + traceFlags;
    }
}
