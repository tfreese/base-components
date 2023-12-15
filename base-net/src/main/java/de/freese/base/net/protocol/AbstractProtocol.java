package de.freese.base.net.protocol;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisklasse für Netzwerkprotokolle.
 *
 * @author Thomas Freese
 */
public abstract class AbstractProtocol {
    private final HexFormat hexFormat = HexFormat.of().withUpperCase();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public abstract void close();

    /**
     * Gets the APOP message digest. From RFC 1939: The 'digest' parameter is calculated by applying the MD5 algorithm [RFC1321] to a string consisting of the
     * timestamp (including angle-brackets) followed by a shared secret. The 'digest' parameter itself is a 16-octet value which is sent in hexadecimal format,
     * using lower-case ASCII characters.
     *
     * @param password The APOP password
     *
     * @return The APOP digest or an empty string if an error occurs.
     */
    protected String getDigest(final String password) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));

            return hexFormat.formatHex(digest);
        }
        catch (NoSuchAlgorithmException ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        return null;
    }

    protected Logger getLogger() {
        return this.logger;
    }
}
