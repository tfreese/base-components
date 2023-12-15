// Created: 12.03.2021
package de.freese.base.net.protocol.pop3;

import java.io.InputStream;

/**
 * Enthält das Ergebnis des Requests.
 *
 * @author Thomas Freese
 */
final class Pop3Response {
    /**
     * all the bytes from a multi-line response
     */
    private InputStream bytes;
    /**
     * rest of line after "+OK" or "-ERR"
     */
    private String data;
    /**
     * true if "+OK"
     */
    private boolean ok;

    InputStream getBytes() {
        return bytes;
    }

    String getData() {
        return data;
    }

    boolean isOk() {
        return ok;
    }

    void setBytes(final InputStream bytes) {
        this.bytes = bytes;
    }

    void setData(final String data) {
        this.data = data;
    }

    void setOk(final boolean ok) {
        this.ok = ok;
    }
}
