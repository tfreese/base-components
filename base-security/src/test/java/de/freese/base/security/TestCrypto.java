/**
 * Created: 13.06.2011
 */

package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.base.security.algorythm.AlgorythmConfigBuilder;
import de.freese.base.security.algorythm.AsymetricCrypto;
import de.freese.base.security.algorythm.Crypto;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestCrypto
{
    /**
     *
     */
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
    *
    */
    private static final String SOURCE = "abcABC123,.;:-_ÖÄÜöäü*'#+`?ß´987/()=?";

    /**
     *
     */
    private static final byte[] SOURCE_BYTES = SOURCE.getBytes(CHARSET);

    /**
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    @Test
    void asymetricRsa() throws GeneralSecurityException, IOException
    {
        // @formatter:off
         Crypto crypto = AlgorythmConfigBuilder.asymetric()
             .providerCipher("SunJCE")
             .providerKeyGenerator("SunRsaSign")
             .providerSignature("SunRsaSign")
             .algorythmCipher("RSA/ECB/NoPadding")
             .algorythmKeyGenerator("RSA")
             .algorythmSignature("SHA512withRSA")
             .keySize(2048)
             .build()
             ;
         // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    /**
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    @Test
    void asymetricRsaBc() throws GeneralSecurityException, IOException
    {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
        {
            Security.addProvider(new BouncyCastleProvider());
        }

        // @formatter:off
         Crypto crypto = AlgorythmConfigBuilder.asymetric()
             .provider(BouncyCastleProvider.PROVIDER_NAME)
             .algorythmCipher("RSA/ECB/NoPadding")
             .algorythmKeyGenerator("RSA")
             .algorythmSignature("SHA512withRSA")
             .keySize(1024)
             .build()
             ;
         // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    /**
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    @Test
    void symetricAesCbc() throws GeneralSecurityException, IOException
    {
        // @formatter:off
        Crypto crypto = AlgorythmConfigBuilder.symetric()
            //.provider("SunJCE")
            .algorythm("AES")
            .algorythmCipher("AES/CBC/PKCS5Padding") // AES/GCM/NoPadding, "AES/GCM/PKCS5Padding"
            .initVector(Arrays.copyOf(AlgorythmConfigBuilder.DEFAULT_INIT_VECTOR, 16))
            .keySize(256)
            .build()
            ;
        // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    /**
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    @Test
    void symetricAesCbcBC() throws GeneralSecurityException, IOException
    {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
        {
            Security.addProvider(new BouncyCastleProvider());
        }

        // @formatter:off
        Crypto crypto = AlgorythmConfigBuilder.symetric()
            .provider(BouncyCastleProvider.PROVIDER_NAME)
            .algorythm("PBEWITHSHA256AND256BITAES-CBC-BC")
            .algorythmKeyGenerator("AES")
            .initVector(AlgorythmConfigBuilder.DEFAULT_INIT_VECTOR)
            .keySize(512)
//            .keyPassword("gehaim")
            .build()
            ;
        // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    /**
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    @Test
    void symetricAesGcm() throws GeneralSecurityException, IOException
    {
        // @formatter:off
         Crypto crypto = AlgorythmConfigBuilder.symetric()
             .algorythm("AES")
             .algorythmCipher("AES/GCM/NoPadding") // "AES/GCM/NoPadding", "AES/GCM/PKCS5Padding"
             .initVector(AlgorythmConfigBuilder.DEFAULT_INIT_VECTOR)
             .keySize(256)
             .build()
             ;
         // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    /**
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    @Test
    void symetricAesGcmPlain() throws GeneralSecurityException, IOException
    {
        // "AES/GCM/NoPadding", "AES/GCM/PKCS5Padding"
        String cipherTransformation = "AES/GCM/NoPadding";

        byte[] initVector = new byte[512];

        // Password/Key erstellen
        // SecureRandom random = SecureRandom.getInstanceStrong();
        // SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG", "SUN");
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, secureRandom);

        secureRandom.nextBytes(initVector);
        Key key = keyGen.generateKey();

        // Verschlüsseln
        Cipher encodeCipher = Cipher.getInstance(cipherTransformation);
        encodeCipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, initVector), secureRandom);

        byte[] encrypted = encodeCipher.doFinal(SOURCE_BYTES);
        String encryptedString = Base64.getEncoder().encodeToString(encrypted);
        System.out.println(encryptedString);

        // Entschlüsseln
        Cipher decodeCipher = Cipher.getInstance(cipherTransformation);
        decodeCipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, initVector), secureRandom);

        byte[] decrypted = decodeCipher.doFinal(encrypted);
        String decryptedString = new String(decrypted, CHARSET);
        System.out.println(decryptedString);

        assertEquals(SOURCE, decryptedString);
    }

    /**
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    @Test
    void symetricBlowfish() throws GeneralSecurityException, IOException
    {
        // @formatter:off
        Crypto crypto = AlgorythmConfigBuilder.symetric()
            .algorythm("Blowfish")
            .algorythmCipher("Blowfish/CBC/PKCS5Padding")
            .initVector(Arrays.copyOf(AlgorythmConfigBuilder.DEFAULT_INIT_VECTOR, 8))
            .keySize(448)
            .build()
            ;
        // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    /**
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    @Test
    void symetricDes() throws GeneralSecurityException, IOException
    {
        // @formatter:off
        Crypto crypto = AlgorythmConfigBuilder.symetric()
            .algorythm("DES")
            .algorythmCipher("DES/CBC/PKCS5Padding")
            .initVector(Arrays.copyOf(AlgorythmConfigBuilder.DEFAULT_INIT_VECTOR, 8))
            .keySize(56)
            .build()
            ;
        // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    /**
     * @param crypto {@link Crypto}
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    private void testCodec(final Crypto crypto) throws GeneralSecurityException, IOException
    {
        byte[] encrypted = crypto.encrypt(SOURCE_BYTES);
        byte[] decrypted = crypto.decrypt(encrypted);

        if (crypto instanceof AsymetricCrypto)
        {
            // Blockgrösse berücksichtigen.
            decrypted = Arrays.copyOfRange(decrypted, decrypted.length - SOURCE_BYTES.length, decrypted.length);
        }

        assertTrue(Arrays.equals(SOURCE_BYTES, decrypted));

        // Streams
        try (ByteArrayInputStream bais = new ByteArrayInputStream(SOURCE_BYTES);
             ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            crypto.encrypt(bais, baos);
            encrypted = baos.toByteArray();
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(encrypted);
             ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            crypto.decrypt(bais, baos);
            decrypted = baos.toByteArray();
        }

        if (crypto instanceof AsymetricCrypto)
        {
            // Blockgrösse berücksichtigen.
            decrypted = Arrays.copyOfRange(decrypted, decrypted.length - SOURCE_BYTES.length, decrypted.length);
        }

        assertTrue(Arrays.equals(SOURCE_BYTES, decrypted));
    }

    /**
     * @param crypto {@link Crypto}
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    private void testSignAndVerify(final Crypto crypto) throws GeneralSecurityException, IOException
    {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(SOURCE_BYTES))
        {
            byte[] sig = null;

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
            {
                crypto.sign(bais, baos);
                sig = baos.toByteArray();
            }

            bais.reset();

            try (ByteArrayInputStream baisSig = new ByteArrayInputStream(sig))
            {
                boolean verified = crypto.verify(bais, baisSig);
                assertTrue(verified, "Wrong Signature");
            }
        }
    }
}