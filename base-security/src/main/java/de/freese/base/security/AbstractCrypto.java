// Created: 07.03.24
package de.freese.base.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jcajce.io.CipherOutputStream;

import de.freese.base.utils.CryptoUtils;
import de.freese.base.utils.Encoding;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
abstract class AbstractCrypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Cipher decodeCipher;
    private final Cipher encodeCipher;

    /**
     * @param publicKey {@link PublicKey}; required for encryption
     * @param privateKey {@link PrivateKey; required for decryption
     */
    protected AbstractCrypto(final Key publicKey, final Key privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        super();

        if (publicKey != null) {
            encodeCipher = Cipher.getInstance(publicKey.getAlgorithm());
            encodeCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        }
        else {
            encodeCipher = null;
        }

        if (privateKey != null) {
            decodeCipher = Cipher.getInstance(privateKey.getAlgorithm());
            decodeCipher.init(Cipher.DECRYPT_MODE, privateKey);
        }
        else {
            decodeCipher = null;
        }
    }

    protected AbstractCrypto(final Cipher encodeCipher, final Cipher decodeCipher) {
        super();

        this.encodeCipher = Objects.requireNonNull(encodeCipher, "encodeCipher required");
        this.decodeCipher = Objects.requireNonNull(decodeCipher, "decodeCipher required");
    }

    public InputStream decorateInputStream(final InputStream inputStream) {
        return new CipherInputStream(inputStream, getDecodeCipher());
    }

    public OutputStream decorateOutputStream(final OutputStream outputStream) {
        return new CipherOutputStream(outputStream, getEncodeCipher());
    }

    public String decrypt(final String value, final Encoding encoding) throws IllegalBlockSizeException, BadPaddingException {
        return decrypt(CryptoUtils.decode(encoding, value));
    }

    public String decrypt(final byte[] decoded) throws IllegalBlockSizeException, BadPaddingException {
        final byte[] decypted = getDecodeCipher().doFinal(decoded);

        return new String(decypted, CHARSET);
    }

    public String encrypt(final String value, final Encoding encoding) throws IllegalBlockSizeException, BadPaddingException {
        final byte[] encrypted = encrypt(value);

        return CryptoUtils.encode(encoding, encrypted);
    }

    public byte[] encrypt(final String value) throws IllegalBlockSizeException, BadPaddingException {
        return getEncodeCipher().doFinal(value.getBytes(CHARSET));
    }

    private Cipher getDecodeCipher() {
        Objects.requireNonNull(decodeCipher, "decodeCipher required");

        return decodeCipher;
    }

    private Cipher getEncodeCipher() {
        Objects.requireNonNull(encodeCipher, "encodeCipher required");

        return encodeCipher;
    }
}
