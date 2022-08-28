package de.freese.base.security.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;

/**
 * Klasse zum Ver- und Entschlüsseln.
 *
 * @author Thomas Freese
 */
public class SymetricCrypt
{
    /**
     *
     */
    private static final String AES_ALGORYTHM = "AES/CBC/PKCS5Padding";
    // /**
    // * 32bit entspricht AES256.
    // */
    // private static final int AES_KEY_SIZE = 32;
    /**
     *
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * 16bit<br>
     * AES Initialisierungsvektor, muss dem Empfänger bekannt sein !
     */
    private static final byte[] INIT_VECTOR =
            {
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
            };
    /**
     *
     */
    private static final SymetricCrypt INSTANCE = new SymetricCrypt(StandardCharsets.UTF_8);

    /**
     * @return {@link SymetricCrypt} mit UTF-8 {@link Charset}.
     */
    public static SymetricCrypt getUTF8Instance()
    {
        return INSTANCE;
    }

    /**
     * Liefert den {@link Key}.
     *
     * @return {@link Key}
     */
    private static Key createDefaultKey()
    {
        // byte[] key = new byte[AES_KEY_SIZE];
        // SecureRandom secureRandom = new SecureRandom();
        // secureRandom.nextBytes(key);
        //
        // return new SecretKeySpec(key, "AES");
        return new DefaultSecretKey("AES");
    }

    /**
     *
     */
    private final Charset charset;

    /**
     *
     */
    private final Key key;

    /**
     * Erzeugt eine neue Instanz von {@link SymetricCrypt}
     *
     * @param charset {@link Charset}; wird nur für die Strings benötigt, nicht für die Streams
     */
    public SymetricCrypt(final Charset charset)
    {
        this(charset, createDefaultKey());
    }

    /**
     * Erzeugt eine neue Instanz von {@link SymetricCrypt}
     *
     * @param charset {@link Charset}; wird nur für die Strings benötigt, nicht für die Streams
     * @param key {@link Key}
     */
    public SymetricCrypt(final Charset charset, final Key key)
    {
        super();

        this.charset = Objects.requireNonNull(charset, "charset required");
        this.key = Objects.requireNonNull(key, "key required");
    }

    /**
     * @param input Verschlüsselter {@link InputStream}, dieser wird geschlossen.
     *
     * @return Entschlüsselter {@link InputStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public InputStream decrypt(final InputStream input) throws Exception
    {
        Path file = Files.createTempFile("pim", ".tmp");
        file.toFile().deleteOnExit();

        Cipher decodeCipher = Cipher.getInstance(AES_ALGORYTHM);
        decodeCipher.init(Cipher.DECRYPT_MODE, getKey(), new IvParameterSpec(INIT_VECTOR));

        try (OutputStream fileOS = new BufferedOutputStream(Files.newOutputStream(file));
             OutputStream cipherOS = new CipherOutputStream(fileOS, decodeCipher))
        {
            // IOUtils.copy(input, cipherOS);
            byte[] buffer = new byte[BUFFER_SIZE];
            int numRead = 0;

            while ((numRead = input.read(buffer)) >= 0)
            {
                cipherOS.write(buffer, 0, numRead);
                // byte[] output = decodeCipher.doFinal(buffer, 0, numRead);
                // os.write(output);
            }

            cipherOS.flush();
            fileOS.flush();
        }

        return new BufferedInputStream(Files.newInputStream(file));
    }

    /**
     * @param input Verschlüsselter String
     *
     * @return Klartext
     *
     * @throws Exception Falls was schiefgeht.
     */
    public String decrypt(final String input) throws Exception
    {
        if ((input == null) || input.isBlank())
        {
            return null;
        }

        Cipher decodeCipher = Cipher.getInstance(AES_ALGORYTHM);
        decodeCipher.init(Cipher.DECRYPT_MODE, getKey(), new IvParameterSpec(INIT_VECTOR));

        // byte[] decrypted = decodeCipher.doFinal(Base64.decodeBase64(input));
        byte[] decrypted = decodeCipher.doFinal(Base64.getDecoder().decode(input));

        return new String(decrypted, getCharset());
    }

    /**
     * @param input Der {@link InputStream} wird geschlossen.
     *
     * @return Entschlüsselter {@link InputStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public InputStream encrypt(final InputStream input) throws Exception
    {
        Path file = Files.createTempFile("pim", ".tmp");
        file.toFile().deleteOnExit();

        try (OutputStream fileOS = new BufferedOutputStream(Files.newOutputStream(file));
             OutputStream cipherOS = getCipherOutputStream(fileOS))
        {
            // IOUtils.copy(input, cipherOS);
            byte[] buffer = new byte[BUFFER_SIZE];
            int numRead = 0;

            while ((numRead = input.read(buffer)) >= 0)
            {
                cipherOS.write(buffer, 0, numRead);
                // byte[] output = encodeCipher.doFinal(buffer, 0, numRead);
                // cipherOS.write(output);
            }

            cipherOS.flush();
            fileOS.flush();
        }

        return new BufferedInputStream(Files.newInputStream(file));
    }

    /**
     * @param input Klartext
     *
     * @return Verschlüsselter String
     *
     * @throws Exception Falls was schiefgeht.
     */
    public String encrypt(final String input) throws Exception
    {
        if ((input == null) || input.isBlank())
        {
            return null;
        }

        Cipher encodeCipher = Cipher.getInstance(AES_ALGORYTHM);
        encodeCipher.init(Cipher.ENCRYPT_MODE, getKey(), new IvParameterSpec(INIT_VECTOR));

        byte[] encrypted = encodeCipher.doFinal(input.getBytes(getCharset()));

        // return Base64.encodeBase64String(encrypted);
        return new String(Base64.getEncoder().encode(encrypted), getCharset());
    }

    /**
     * Liefert den {@link OutputStream} zum Verschlüsseln.
     *
     * @param output {@link OutputStream}
     *
     * @return {@link OutputStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public OutputStream getCipherOutputStream(final OutputStream output) throws Exception
    {
        Cipher encodeCipher = Cipher.getInstance(AES_ALGORYTHM);
        encodeCipher.init(Cipher.ENCRYPT_MODE, getKey(), new IvParameterSpec(INIT_VECTOR));

        return new CipherOutputStream(output, encodeCipher);
    }

    /**
     * @return {@link Charset}
     */
    protected Charset getCharset()
    {
        return this.charset;
    }

    /**
     * @return {@link Key}
     */
    protected Key getKey()
    {
        return this.key;
    }
}
