/**
 * Created: 14.05.2019
 */

package de.freese.base.security.algorythm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.function.Supplier;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

/**
 * Basis-Implementierung von {@link Crypto}.
 *
 * @author Thomas Freese
 */
abstract class AbstractCrypto implements Crypto
{
    /**
    *
    */
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    /**
    *
    */
    private Supplier<Cipher> cipherDecryptSupplier = null;

    /**
     *
     */
    private Supplier<Cipher> cipherEncryptSupplier = null;

    /**
     *
     */
    private MessageDigest messageDigest = null;

    /**
     *
     */
    private SecureRandom secureRandom = null;

    /**
     * Erstellt ein neues {@link AbstractCrypto} Object.
     */
    AbstractCrypto()
    {
        super();
    }

    /**
     * @see de.freese.base.security.algorythm.Crypto#decrypt(byte[])
     */
    @Override
    public byte[] decrypt(final byte[] bytes) throws GeneralSecurityException
    {
        Cipher cipher = getCipherDecrypt();

        byte[] decypted = decrypt(cipher, bytes);

        return decypted;
    }

    /**
     * @param cipher {@link Cipher}
     * @param bytes byte[]
     * @return byte[]
     * @throws GeneralSecurityException Falls was schief geht.
     */
    protected byte[] decrypt(final Cipher cipher, final byte[] bytes) throws GeneralSecurityException
    {
        byte[] decypted = cipher.doFinal(bytes);

        return decypted;
    }

    /**
     * @param cipher {@link Cipher}
     * @param in {@link InputStream}
     * @param out {@link OutputStream}
     * @throws IOException Falls was schief geht.
     */
    protected void decrypt(final Cipher cipher, final InputStream in, final OutputStream out) throws IOException
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
        buffer = null;
    }

    /**
     * @see de.freese.base.security.algorythm.Crypto#decrypt(java.io.InputStream, java.io.OutputStream)
     */
    @Override
    public void decrypt(final InputStream in, final OutputStream out) throws GeneralSecurityException, IOException
    {
        Cipher cipher = getCipherDecrypt();

        decrypt(cipher, in, out);
    }

    /**
     * @see de.freese.base.security.algorythm.Crypto#digest(byte[])
     */
    @Override
    public byte[] digest(final byte[] bytes) throws GeneralSecurityException
    {
        MessageDigest messageDigest = getMessageDigest();

        messageDigest.update(bytes);

        byte[] digest = messageDigest.digest();
        messageDigest.reset();

        return digest;
    }

    /**
     * @see de.freese.base.security.algorythm.Crypto#digest(java.io.InputStream)
     */
    @Override
    public byte[] digest(final InputStream in) throws GeneralSecurityException, IOException
    {
        MessageDigest messageDigest = getMessageDigest();

        byte[] digest = digest(messageDigest, in);

        return digest;
    }

    /**
     * @param messageDigest {@link MessageDigest}
     * @param in {@link InputStream}
     * @return byte[]
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public byte[] digest(final MessageDigest messageDigest, final InputStream in) throws GeneralSecurityException, IOException
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0)
        {
            messageDigest.update(buffer, 0, numRead);
        }

        byte[] digest = messageDigest.digest();
        messageDigest.reset();
        buffer = null;

        return digest;
    }

    /**
     * @see de.freese.base.security.algorythm.Crypto#encrypt(byte[])
     */
    @Override
    public byte[] encrypt(final byte[] bytes) throws GeneralSecurityException
    {
        Cipher cipher = getCipherEncrypt();

        byte[] encrypted = encrypt(cipher, bytes);

        return encrypted;
    }

    /**
     * @param cipher cipher {@link Cipher}
     * @param bytes byte[]
     * @return byte[]
     * @throws GeneralSecurityException Falls was schief geht.
     */
    protected byte[] encrypt(final Cipher cipher, final byte[] bytes) throws GeneralSecurityException
    {
        byte[] encrypted = cipher.doFinal(bytes);

        return encrypted;
    }

    /**
     * @param cipher {@link Cipher}
     * @param in {@link InputStream}
     * @param out {@link OutputStream}
     * @throws IOException Falls was schief geht.
     */
    protected void encrypt(final Cipher cipher, final InputStream in, final OutputStream out) throws IOException
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
        buffer = null;
    }

    /**
     * @see de.freese.base.security.algorythm.Crypto#encrypt(java.io.InputStream, java.io.OutputStream)
     */
    @Override
    public void encrypt(final InputStream in, final OutputStream out) throws GeneralSecurityException, IOException
    {
        Cipher cipher = getCipherEncrypt();

        encrypt(cipher, in, out);
    }

    /**
     * @return {@link Cipher}
     */
    protected Cipher getCipherDecrypt()
    {
        return this.cipherDecryptSupplier.get();
    }

    /**
     * @return {@link Cipher}
     */
    protected Cipher getCipherEncrypt()
    {
        return this.cipherEncryptSupplier.get();
    }

    /**
     * @return {@link MessageDigest}
     */
    protected MessageDigest getMessageDigest()
    {
        return this.messageDigest;
    }

    /**
     * @return {@link SecureRandom}
     */
    protected SecureRandom getSecureRandom()
    {
        return this.secureRandom;
    }

    /**
     * @param cipherDecryptSupplier {@link Supplier}<Cipher>
     */
    void setCipherDecryptSupplier(final Supplier<Cipher> cipherDecryptSupplier)
    {
        this.cipherDecryptSupplier = cipherDecryptSupplier;
    }

    /**
     * @param cipherEncryptSupplier {@link Supplier}<Cipher>
     */
    void setCipherEncryptSupplier(final Supplier<Cipher> cipherEncryptSupplier)
    {
        this.cipherEncryptSupplier = cipherEncryptSupplier;
    }

    /**
     * @param messageDigest {@link MessageDigest}
     */
    void setMessageDigest(final MessageDigest messageDigest)
    {
        this.messageDigest = messageDigest;
    }

    /**
     * @param secureRandom {@link SecureRandom}
     */
    void setSecureRandom(final SecureRandom secureRandom)
    {
        this.secureRandom = secureRandom;
    }

    /**
     * Signiert den InputStream.<br>
     *
     * @param signature {@link Signature}
     * @param in {@link InputStream}, Verschlüsselt
     * @param out {@link OutputStream};
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    protected void sign(final Signature signature, final InputStream in, final OutputStream out) throws GeneralSecurityException, IOException
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
        buffer = null;
    }

    /**
     * Verifiziert den InputStream.<br>
     *
     * @param signature {@link Signature}
     * @param in {@link InputStream}; Verschlüsselt
     * @param signIn {@link InputStream}
     * @return boolean
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    protected boolean verify(final Signature signature, final InputStream in, final InputStream signIn) throws GeneralSecurityException, IOException
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0)
        {
            signature.update(buffer, 0, numRead);
        }

        // Bei einer RSA Keysize von 4096 wird die Blocksize 512 betragen (4096/8).
        byte[] sig = null;// IOUtils.toByteArray(signIn);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            while ((numRead = signIn.read(buffer)) >= 0)
            {
                baos.write(buffer, 0, numRead);
            }

            sig = baos.toByteArray();
        }

        boolean verified = signature.verify(sig);

        in.close();
        signIn.close();

        return verified;
    }
}
