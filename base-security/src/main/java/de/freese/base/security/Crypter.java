// Created: 07.03.24
package de.freese.base.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;
import java.util.function.Supplier;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

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

    private final Supplier<Cipher> decryptCipherSupplier;
    private final Supplier<Cipher> encryptCipherSupplier;

    /**
     * @param publicKey {@link PublicKey}; required for encryption
     * @param privateKey {@link PrivateKey}; required for decryption
     */
    public Crypter(final Key publicKey, final Key privateKey) throws GeneralSecurityException {
        super();

        if (publicKey != null) {
            final Cipher encryptCipher = Cipher.getInstance(publicKey.getAlgorithm());
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            encryptCipherSupplier = () -> encryptCipher;
        }
        else {
            encryptCipherSupplier = () -> null;
        }

        if (privateKey != null) {
            final Cipher decryptCipher = Cipher.getInstance(privateKey.getAlgorithm());
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

            decryptCipherSupplier = () -> decryptCipher;
        }
        else {
            decryptCipherSupplier = () -> null;
        }
    }

    public Crypter(final Cipher encryptCipher, final Cipher decryptCipher) {
        this(() -> encryptCipher, () -> decryptCipher);

    }

    public Crypter(final Supplier<Cipher> encryptCipherSupplier, final Supplier<Cipher> decryptCipherSupplier) {
        super();

        this.encryptCipherSupplier = encryptCipherSupplier;
        this.decryptCipherSupplier = decryptCipherSupplier;
    }

    public InputStream decorateInputStream(final InputStream inputStream) {
        return new CipherInputStream(inputStream, getDecryptCipher());
    }

    public OutputStream decorateOutputStream(final OutputStream outputStream) {
        return new CipherOutputStream(outputStream, getEncryptCipher());
    }

    public byte[] decrypt(final byte[] encrypted) throws GeneralSecurityException {
        return getDecryptCipher().doFinal(encrypted);

        // getDecryptCipher().update(encrypted);
        // return getDecryptCipher().doFinal();
    }

    public String decrypt(final String encodedEncrypted, final Encoding encoding) throws GeneralSecurityException {
        final byte[] encrypted = CryptoUtils.decode(encoding, encodedEncrypted);

        final byte[] decrypted = decrypt(encrypted);

        return new String(decrypted, CHARSET);
    }

    public byte[] encrypt(final byte[] data) throws GeneralSecurityException {
        return getEncryptCipher().doFinal(data);

        // getEncryptCipher().update(data);
        // return getEncryptCipher().doFinal();
    }

    public String encrypt(final String value, final Encoding encoding) throws GeneralSecurityException {
        final byte[] encrypted = encrypt(value.getBytes(CHARSET));

        return CryptoUtils.encode(encoding, encrypted);
    }

    private Cipher getDecryptCipher() {
        Objects.requireNonNull(decryptCipherSupplier, "decryptCipherSupplier required");

        return Objects.requireNonNull(decryptCipherSupplier.get(), "decryptCipher required");
    }

    private Cipher getEncryptCipher() {
        Objects.requireNonNull(encryptCipherSupplier, "encryptCipherSupplier required");

        return Objects.requireNonNull(encryptCipherSupplier.get(), "encryptCipher required");
    }
}
