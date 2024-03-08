// Created: 13.06.2011
package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import de.freese.base.security.crypto.Crypto;
import de.freese.base.security.crypto.CryptoAsymetric;
import de.freese.base.security.crypto.CryptoConfig;
import de.freese.base.security.crypto.CryptoConfigSymetric;
import de.freese.base.utils.CryptoUtils;
import de.freese.base.utils.Encoding;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestCrypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String SOURCE = "abcABC123,.;:-_ÖÄÜöäü*'#+`?ß´987/()=?";
    private static final byte[] SOURCE_BYTES = SOURCE.getBytes(CHARSET);

    @Test
    void testAsymetricRsa() throws Exception {
        // @formatter:off
        final Crypto crypto = CryptoConfig.asymetric()
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
    void testAsymetricRsaBc() throws Exception {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        // @formatter:off
        final Crypto crypto = CryptoConfig.asymetric()
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
    void testSymetricAesCbc() throws Exception {
        testCrypter(SymetricCrypto.createAesCbc("password"));
    }

    /**
     * AES-GCM Cipher can not be reused !
     */
    @Test
    void testSymetricAesGcmPlain() throws Exception {
        // "AES/GCM/NoPadding", "AES/GCM/PKCS5Padding"
        final String algorithm = "AES/GCM/NoPadding";

        // final SecureRandom random = SecureRandom.getInstanceStrong();
        // final SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        final SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG", "SUN");

        final byte[] initVector = new byte[512];
        secureRandom.nextBytes(initVector);

        final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, secureRandom);

        final Key key = keyGenerator.generateKey();

        final AlgorithmParameterSpec parameterSpec = new GCMParameterSpec(128, initVector);

        final Cipher encryptCipher = Cipher.getInstance(algorithm);
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec, secureRandom);

        final Cipher decryptCipher = Cipher.getInstance(algorithm);
        decryptCipher.init(Cipher.DECRYPT_MODE, key, parameterSpec, secureRandom);

        // AES-GCM Cipher can not be reused !
        // testCrypter(new Crypter(encryptCipher, decryptCipher));

        final byte[] encrypted = encryptCipher.doFinal(SOURCE_BYTES);

        for (Encoding encoding : Encoding.values()) {
            System.out.printf("%6s: %s%n", encoding, CryptoUtils.encode(encoding, encrypted));
        }

        final byte[] decrypted = decryptCipher.doFinal(encrypted);
        assertEquals(SOURCE, new String(decrypted, CHARSET));
    }

    @Test
    void testSymetricBlowfish() throws Exception {
        // @formatter:off
        final Crypto crypto = CryptoConfig.symetric()
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
    void testSymetricDes() throws Exception {
        // @formatter:off
        final Crypto crypto = CryptoConfig.symetric()
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

    @Test
    void textAsymetricCrypto() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512, SecureRandom.getInstanceStrong());

        final KeyPair keyPair = keyPairGenerator.generateKeyPair();

        final AsymetricCrypto crypto = new AsymetricCrypto(keyPair.getPublic(), keyPair.getPrivate());

        for (Encoding encoding : Encoding.values()) {
            final String encrypted = crypto.encrypt(SOURCE, encoding);
            System.out.println(encrypted);

            assertEquals(SOURCE, crypto.decrypt(encrypted, encoding));
        }
    }

    @Test
    void textSymetricCrypto() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException,
            InvalidAlgorithmParameterException {

        // Required by PBEWITHSHA256AND256BITAES-CBC-BC
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        for (SymetricCrypto.Algorithm algorithm : SymetricCrypto.Algorithm.values()) {
            System.out.println(algorithm);

            testCrypter(SymetricCrypto.create("password", algorithm));
        }
    }

    private void testCodec(final Crypto crypto) throws Exception {
        byte[] encrypted = crypto.encrypt(SOURCE_BYTES);
        byte[] decrypted = crypto.decrypt(encrypted);

        if (crypto instanceof CryptoAsymetric) {
            // Blockgrösse berücksichtigen.
            decrypted = Arrays.copyOfRange(decrypted, decrypted.length - SOURCE_BYTES.length, decrypted.length);
        }

        assertArrayEquals(SOURCE_BYTES, decrypted);

        // Streams
        try (ByteArrayInputStream bais = new ByteArrayInputStream(SOURCE_BYTES);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            crypto.encrypt(bais, baos);
            encrypted = baos.toByteArray();
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(encrypted);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            crypto.decrypt(bais, baos);
            decrypted = baos.toByteArray();
        }

        if (crypto instanceof CryptoAsymetric) {
            // Blockgrösse berücksichtigen.
            decrypted = Arrays.copyOfRange(decrypted, decrypted.length - SOURCE_BYTES.length, decrypted.length);
        }

        assertArrayEquals(SOURCE_BYTES, decrypted);
    }

    private void testCrypter(final Crypter crypter) throws IllegalBlockSizeException, BadPaddingException {
        for (Encoding encoding : Encoding.values()) {
            final String encrypted = crypter.encrypt(SOURCE, encoding);
            System.out.printf("%6s: %s%n", encoding, encrypted);

            assertEquals(SOURCE, crypter.decrypt(encrypted, encoding));
        }
    }

    private void testSignAndVerify(final Crypto crypto) throws Exception {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(SOURCE_BYTES)) {
            byte[] sig = null;

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                crypto.sign(bais, baos);
                sig = baos.toByteArray();
            }

            bais.reset();

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(sig)) {
                final boolean verified = crypto.verify(bais, inputStream);
                assertTrue(verified, "Wrong Signature");
            }
        }
    }
}
