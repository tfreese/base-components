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
public class Crypter {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Cipher decryptCipher;
    private final Cipher encryptCipher;

    /**
     * @param publicKey {@link PublicKey}; required for encryption
     * @param privateKey {@link PrivateKey; required for decryption
     */
    public Crypter(final Key publicKey, final Key privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        super();

        if (publicKey != null) {
            encryptCipher = Cipher.getInstance(publicKey.getAlgorithm());
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        }
        else {
            encryptCipher = null;
        }

        if (privateKey != null) {
            decryptCipher = Cipher.getInstance(privateKey.getAlgorithm());
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        }
        else {
            decryptCipher = null;
        }
    }

    public Crypter(final Cipher encryptCipher, final Cipher decryptCipher) {
        super();

        this.encryptCipher = Objects.requireNonNull(encryptCipher, "encryptCipher required");
        this.decryptCipher = Objects.requireNonNull(decryptCipher, "decryptCipher required");
    }

    public InputStream decorateInputStream(final InputStream inputStream) {
        return new CipherInputStream(inputStream, getDecryptCipher());
    }

    public OutputStream decorateOutputStream(final OutputStream outputStream) {
        return new CipherOutputStream(outputStream, getEncryptCipher());
    }

    public String decrypt(final String value, final Encoding encoding) throws IllegalBlockSizeException, BadPaddingException {
        return decrypt(CryptoUtils.decode(encoding, value));
    }

    public String decrypt(final byte[] decoded) throws IllegalBlockSizeException, BadPaddingException {
        final byte[] decypted = getDecryptCipher().doFinal(decoded);

        return new String(decypted, CHARSET);
    }

    public String encrypt(final String value, final Encoding encoding) throws IllegalBlockSizeException, BadPaddingException {
        final byte[] encrypted = encrypt(value);

        return CryptoUtils.encode(encoding, encrypted);
    }

    public byte[] encrypt(final String value) throws IllegalBlockSizeException, BadPaddingException {
        return getEncryptCipher().doFinal(value.getBytes(CHARSET));
    }

    private Cipher getDecryptCipher() {
        Objects.requireNonNull(decryptCipher, "decryptCipher required");

        return decryptCipher;
    }

    private Cipher getEncryptCipher() {
        Objects.requireNonNull(encryptCipher, "encryptCipher required");

        return encryptCipher;
    }
}
