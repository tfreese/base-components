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
 * Basis-Implementierung des {@link Crypto}-Interfaces.
 *
 * @author Thomas Freese
 */
abstract class AbstractCrypto implements Crypto
{
    /**
     *
     */
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    /**
     *
     */
    private final CryptoConfig<?> config;
    /**
     *
     */
    private final SecureRandom secureRandom;

    /**
     * Erstellt ein neues {@link AbstractCrypto} Object.
     *
     * @param cryptoConfig {@link CryptoConfig}
     *
     * @throws Exception Falls was schiefgeht.
     */
    AbstractCrypto(final CryptoConfig<?> cryptoConfig) throws Exception
    {
        super();

        this.config = Objects.requireNonNull(cryptoConfig, "cryptoConfig required");

        // SecureRandom ist ThreadSafe.
        this.secureRandom = SecureRandom.getInstance(this.config.getAlgorythmSecureRandom(), this.config.getProviderSecureRandom());
    }

    /**
     * @see de.freese.base.security.crypto.Crypto#decrypt(byte[])
     */
    @Override
    public byte[] decrypt(final byte[] bytes) throws Exception
    {
        Cipher cipher = createCipherDecrypt();

        return decrypt(cipher, bytes);
    }

    /**
     * @see de.freese.base.security.crypto.Crypto#decrypt(java.io.InputStream, java.io.OutputStream)
     */
    @Override
    public void decrypt(final InputStream in, final OutputStream out) throws Exception
    {
        Cipher cipher = createCipherDecrypt();

        decrypt(cipher, in, out);
    }

    /**
     * @see de.freese.base.security.crypto.Crypto#digest(byte[])
     */
    @Override
    public byte[] digest(final byte[] bytes) throws Exception
    {
        MessageDigest messageDigest = createMessageDigest();

        messageDigest.update(bytes);

        byte[] digest = messageDigest.digest();
        messageDigest.reset();

        return digest;
    }

    /**
     * @see de.freese.base.security.crypto.Crypto#digest(java.io.InputStream)
     */
    @Override
    public byte[] digest(final InputStream in) throws Exception
    {
        MessageDigest messageDigest = createMessageDigest();

        return digest(messageDigest, in);
    }

    /**
     * @param messageDigest {@link MessageDigest}
     * @param in {@link InputStream}
     *
     * @return byte[]
     *
     * @throws Exception Falls was schiefgeht.
     */
    public byte[] digest(final MessageDigest messageDigest, final InputStream in) throws Exception
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0)
        {
            messageDigest.update(buffer, 0, numRead);
        }

        return messageDigest.digest();
    }

    /**
     * @see de.freese.base.security.crypto.Crypto#encrypt(byte[])
     */
    @Override
    public byte[] encrypt(final byte[] bytes) throws Exception
    {
        Cipher cipher = createCipherEncrypt();

        return encrypt(cipher, bytes);
    }

    /**
     * @see de.freese.base.security.crypto.Crypto#encrypt(java.io.InputStream, java.io.OutputStream)
     */
    @Override
    public void encrypt(final InputStream in, final OutputStream out) throws Exception
    {
        Cipher cipher = createCipherEncrypt();

        encrypt(cipher, in, out);
    }

    /**
     * {@link Cipher} ist nicht ThreadSafe.
     *
     * @return {@link Cipher}
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected abstract Cipher createCipherDecrypt() throws Exception;

    /**
     * {@link Cipher} ist nicht ThreadSafe.
     *
     * @return {@link Cipher}
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected abstract Cipher createCipherEncrypt() throws Exception;

    /**
     * {@link MessageDigest} ist nicht ThreadSafe.
     *
     * @return {@link MessageDigest}
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected MessageDigest createMessageDigest() throws Exception
    {
        return MessageDigest.getInstance(getConfig().getAlgorythmDigest(), getConfig().getProviderDigest());
    }

    /**
     * @param cipher {@link Cipher}
     * @param bytes byte[]
     *
     * @return byte[]
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected byte[] decrypt(final Cipher cipher, final byte[] bytes) throws Exception
    {
        return cipher.doFinal(bytes);
    }

    /**
     * @param cipher {@link Cipher}
     * @param in {@link InputStream}
     * @param out {@link OutputStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected void decrypt(final Cipher cipher, final InputStream in, final OutputStream out) throws Exception
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        try (CipherInputStream cipherInputStream = new CipherInputStream(in, cipher))
        {
            int numRead = 0;

            while ((numRead = cipherInputStream.read(buffer)) >= 0)
            {
                out.write(buffer, 0, numRead);
            }
        }

        out.flush();
    }

    /**
     * @param cipher cipher {@link Cipher}
     * @param bytes byte[]
     *
     * @return byte[]
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected byte[] encrypt(final Cipher cipher, final byte[] bytes) throws Exception
    {
        return cipher.doFinal(bytes);
    }

    /**
     * @param cipher {@link Cipher}
     * @param in {@link InputStream}
     * @param out {@link OutputStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected void encrypt(final Cipher cipher, final InputStream in, final OutputStream out) throws Exception
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(out, cipher))
        {
            int numRead = 0;

            while ((numRead = in.read(buffer)) >= 0)
            {
                cipherOutputStream.write(buffer, 0, numRead);
            }
        }

        out.flush();
    }

    /**
     * @return {@link CryptoConfig}<?>
     */
    protected CryptoConfig<?> getConfig()
    {
        return this.config;
    }

    /**
     * @return {@link SecureRandom}
     */
    protected SecureRandom getSecureRandom()
    {
        return this.secureRandom;
    }

    /**
     * Signiert den InputStream.<br>
     *
     * @param signature {@link Signature}
     * @param in {@link InputStream}, Verschlüsselt
     * @param out {@link OutputStream};
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected void sign(final Signature signature, final InputStream in, final OutputStream out) throws Exception
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0)
        {
            signature.update(buffer, 0, numRead);
        }

        // Bei einer RSA Keysize von 4096 wird die Blocksize 512 betragen (4096/8).
        byte[] sig = signature.sign();
        out.write(sig);

        out.flush();
    }

    /**
     * Verifiziert den InputStream.<br>
     *
     * @param signature {@link Signature}
     * @param in {@link InputStream}; Verschlüsselt
     * @param signIn {@link InputStream}
     *
     * @return boolean
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected boolean verify(final Signature signature, final InputStream in, final InputStream signIn) throws Exception
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0)
        {
            signature.update(buffer, 0, numRead);
        }

        // Bei einer RSA Keysize von 4096 wird die Blocksize 512 betragen (4096/8).
        byte[] sig = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            while ((numRead = signIn.read(buffer)) >= 0)
            {
                baos.write(buffer, 0, numRead);
            }

            sig = baos.toByteArray();
        }

        return signature.verify(sig);
    }
}
