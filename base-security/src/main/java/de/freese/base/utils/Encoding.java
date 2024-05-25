// Created: 07.03.24
package de.freese.base.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;

/**
 * @author Thomas Freese
 */
public enum Encoding {
    BASE64,
    HEX;

    public byte[] decode(final String value) {
        if (this.equals(BASE64)) {
            return Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8));
        }

        return HexFormat.of().parseHex(value);
    }

    public String encode(final byte[] data) {
        if (this.equals(BASE64)) {
            return new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8);
        }

        return HexFormat.of().withUpperCase().formatHex(data);
    }
}
