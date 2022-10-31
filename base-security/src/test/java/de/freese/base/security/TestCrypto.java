// Created: 13.06.2011
package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;

import de.freese.base.security.crypto.Crypto;
import de.freese.base.security.crypto.CryptoAsymetric;
import de.freese.base.security.crypto.CryptoConfig;
import de.freese.base.security.crypto.CryptoConfigSymetric;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestCrypto
{
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String SOURCE = "abcABC123,.;:-_ÖÄÜöäü*'#+`?ß´987/()=?";

    private static final byte[] SOURCE_BYTES = SOURCE.getBytes(CHARSET);

    @Test
    void testAsymetricRsa() throws Exception
    {
        // @formatter:off
         Crypto crypto = CryptoConfig.asymetric()
             .providerCipher("SunJCE")
             .providerKeyGenerator("SunRsaSign")
             .providerSignature("SunRsaSign")
             .algorithmCipher("RSA/ECB/NoPadding")
             .algorithmDigest("SHA-512")
             .algorithmKeyGenerator("RSA")
             .algorithmSignature("SHA512withRSA")
             .keySize(4096)
             .build()
             ;
         // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    @Test
    void testAsymetricRsaBc() throws Exception
    {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
        {
            Security.addProvider(new BouncyCastleProvider());
        }

        // @formatter:off
         Crypto crypto = CryptoConfig.asymetric()
             .providerDefault(BouncyCastleProvider.PROVIDER_NAME)
             .algorithmCipher("RSA/ECB/NoPadding")
             .algorithmKeyGenerator("RSA")
             .algorithmSignature("SHA512withRSA")
             .keySize(4096)
             .build()
             ;
         // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    @Test
    void testSymetricAesCbc() throws Exception
    {
        // @formatter:off
        Crypto crypto = CryptoConfig.symetric()
            //.providerDefault("SunJCE")
            .algorithmDefault("AES")
            .algorithmCipher("AES/CBC/PKCS5Padding") // AES/GCM/NoPadding, "AES/GCM/PKCS5Padding"
            .initVector(Arrays.copyOf(CryptoConfigSymetric.DEFAULT_INIT_VECTOR, 16))
            .keySize(256)
            .build()
            ;
        // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    @Test
    void testSymetricAesCbcBC() throws Exception
    {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
        {
            Security.addProvider(new BouncyCastleProvider());
        }

        // @formatter:off
        Crypto crypto = CryptoConfig.symetric()
            .providerDefault(BouncyCastleProvider.PROVIDER_NAME)
            .algorithmDefault("PBEWITHSHA256AND256BITAES-CBC-BC")
            .algorithmKeyGenerator("AES")
            .initVector(CryptoConfigSymetric.DEFAULT_INIT_VECTOR)
            .keySize(4096)
//            .keyPassword("gehaim")
            .build()
            ;
        // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    @Test
    void testSymetricAesGcm() throws Exception
    {
        // @formatter:off
         Crypto crypto = CryptoConfig.symetric()
             .algorithmDefault("AES")
             .algorithmCipher("AES/GCM/NoPadding") // "AES/GCM/NoPadding", "AES/GCM/PKCS5Padding"
             .initVector(CryptoConfigSymetric.DEFAULT_INIT_VECTOR)
             .keySize(256)
             .build()
             ;
         // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    @Test
    void testSymetricAesGcmPlain() throws Exception
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

    @Test
    void testSymetricBlowfish() throws Exception
    {
        // @formatter:off
        Crypto crypto = CryptoConfig.symetric()
            .algorithmDefault("Blowfish")
            .algorithmCipher("Blowfish/CBC/PKCS5Padding")
            .initVector(Arrays.copyOf(CryptoConfigSymetric.DEFAULT_INIT_VECTOR, 8))
            .keySize(448)
            .build()
            ;
        // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    @Test
    void testSymetricDes() throws Exception
    {
        // @formatter:off
        Crypto crypto = CryptoConfig.symetric()
            .algorithmDefault("DES")
            .algorithmCipher("DES/CBC/PKCS5Padding")
            .initVector(Arrays.copyOf(CryptoConfigSymetric.DEFAULT_INIT_VECTOR, 8))
            .keySize(56)
            .build()
            ;
        // @formatter:on

        testCodec(crypto);
        testSignAndVerify(crypto);
    }

    private void testCodec(final Crypto crypto) throws Exception
    {
        byte[] encrypted = crypto.encrypt(SOURCE_BYTES);
        byte[] decrypted = crypto.decrypt(encrypted);

        if (crypto instanceof CryptoAsymetric)
        {
            // Blockgrösse berücksichtigen.
            decrypted = Arrays.copyOfRange(decrypted, decrypted.length - SOURCE_BYTES.length, decrypted.length);
        }

        assertArrayEquals(SOURCE_BYTES, decrypted);

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

        if (crypto instanceof CryptoAsymetric)
        {
            // Blockgrösse berücksichtigen.
            decrypted = Arrays.copyOfRange(decrypted, decrypted.length - SOURCE_BYTES.length, decrypted.length);
        }

        assertArrayEquals(SOURCE_BYTES, decrypted);
    }

    private void testSignAndVerify(final Crypto crypto) throws Exception
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

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(sig))
            {
                boolean verified = crypto.verify(bais, inputStream);
                assertTrue(verified, "Wrong Signature");
            }
        }
    }
}
