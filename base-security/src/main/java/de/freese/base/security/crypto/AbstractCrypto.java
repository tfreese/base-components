// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

/**
 * Basis-Implementierung des {@link Crypto}-Interfaces.<br/>
 * {@link Cipher} ist nicht ThreadSafe.<br/>
 * {@link MessageDigest} ist nicht ThreadSafe.
 *
 * @author Thomas Freese
 */
abstract class AbstractCrypto implements Crypto {
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private final CryptoConfig<?> config;

    private final SecureRandom secureRandom;

    AbstractCrypto(final CryptoConfig<?> cryptoConfig) throws Exception {
        super();

        this.config = Objects.requireNonNull(cryptoConfig, "cryptoConfig required");

        // SecureRandom ist ThreadSafe.
        this.secureRandom = SecureRandom.getInstance(this.config.getAlgorithmSecureRandom(), this.config.getProviderSecureRandom());
    }

    @Override
    public byte[] decrypt(final byte[] bytes) throws Exception {
        Cipher cipher = createCipherDecrypt();

        return decrypt(cipher, bytes);
    }

    @Override
    public void decrypt(final InputStream in, final OutputStream out) throws Exception {
        Cipher cipher = createCipherDecrypt();

        decrypt(cipher, in, out);
    }

    @Override
    public byte[] digest(final byte[] bytes) throws Exception {
        MessageDigest messageDigest = createMessageDigest();

        messageDigest.update(bytes);

        byte[] digest = messageDigest.digest();
        messageDigest.reset();

        return digest;
    }

    @Override
    public byte[] digest(final InputStream in) throws Exception {
        MessageDigest messageDigest = createMessageDigest();

        return digest(messageDigest, in);
    }

    public byte[] digest(final MessageDigest messageDigest, final InputStream in) throws Exception {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0) {
            messageDigest.update(buffer, 0, numRead);
        }

        return messageDigest.digest();
    }

    @Override
    public byte[] encrypt(final byte[] bytes) throws Exception {
        Cipher cipher = createCipherEncrypt();

        return encrypt(cipher, bytes);
    }

    @Override
    public void encrypt(final InputStream in, final OutputStream out) throws Exception {
        Cipher cipher = createCipherEncrypt();

        encrypt(cipher, in, out);
    }

    protected abstract Cipher createCipherDecrypt() throws Exception;

    protected abstract Cipher createCipherEncrypt() throws Exception;

    protected MessageDigest createMessageDigest() throws Exception {
        return MessageDigest.getInstance(getConfig().getAlgorithmDigest(), getConfig().getProviderDigest());
    }

    protected byte[] decrypt(final Cipher cipher, final byte[] bytes) throws Exception {
        return cipher.doFinal(bytes);
    }

    protected void decrypt(final Cipher cipher, final InputStream in, final OutputStream out) throws Exception {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        try (CipherInputStream cipherInputStream = new CipherInputStream(in, cipher)) {
            int numRead = 0;

            while ((numRead = cipherInputStream.read(buffer)) >= 0) {
                out.write(buffer, 0, numRead);
            }
        }

        out.flush();
    }

    protected byte[] encrypt(final Cipher cipher, final byte[] bytes) throws Exception {
        return cipher.doFinal(bytes);
    }

    protected void encrypt(final Cipher cipher, final InputStream in, final OutputStream out) throws Exception {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(out, cipher)) {
            int numRead = 0;

            while ((numRead = in.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, numRead);
            }
        }

        out.flush();
    }

    protected CryptoConfig<?> getConfig() {
        return this.config;
    }

    protected SecureRandom getSecureRandom() {
        return this.secureRandom;
    }

    /**
     * @param in {@link InputStream}, Verschlüsselt
     */
    protected void sign(final Signature signature, final InputStream in, final OutputStream out) throws Exception {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0) {
            signature.update(buffer, 0, numRead);
        }

        // Bei einer RSA KeySize von 4096 wird die BlockSize 512 betragen (4096/8).
        byte[] sig = signature.sign();
        out.write(sig);

        out.flush();
    }

    /**
     * @param in {@link InputStream}; Verschlüsselt
     */
    protected boolean verify(final Signature signature, final InputStream in, final InputStream signIn) throws Exception {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0) {
            signature.update(buffer, 0, numRead);
        }

        // Bei einer RSA KeySize von 4096 wird die BlockSize 512 betragen (4096/8).
        byte[] sig = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            while ((numRead = signIn.read(buffer)) >= 0) {
                baos.write(buffer, 0, numRead);
            }

            sig = baos.toByteArray();
        }

        return signature.verify(sig);
    }
}
