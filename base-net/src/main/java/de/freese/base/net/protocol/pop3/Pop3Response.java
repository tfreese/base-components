// Created: 12.03.2021
package de.freese.base.net.protocol.pop3;

import java.io.InputStream;

/**
 * Enthaelt das Ergebniss des Requests.
 *
 * @author Thomas Freese
 */
final class Pop3Response
{
    /**
     * all the bytes from a multi-line response
     */
    InputStream bytes;

    /**
     * rest of line after "+OK" or "-ERR"
     */
    String data;

    /**
     * true if "+OK"
     */
    boolean ok;
}
