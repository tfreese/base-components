// Created: 03.04.2012
package de.freese.base.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Beispiele für JCE-API.
 *
 * @author Thomas Freese see base-security/keystore.txt
 */
public final class KeystoreMain {
    public static void main(final String[] args) throws Exception {
        String provider = "SunJCE";// "SUN";

        char[] keystorePSW = "gehaim".toCharArray();

        // KeyStore.getDefaultType();
        Path keystorePath = Paths.get(System.getProperty("java.io.tmpdir"), "keystore.p12");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        if (Files.exists(keystorePath)) {
            try (InputStream in = Files.newInputStream(keystorePath)) {
                keyStore.load(in, keystorePSW);
            }
        }
        else {
            keyStore.load(null, keystorePSW);
        }

        SecureRandom secureRandom = new SecureRandom();
        SecretKey secretKey = null;

        // Keys erzeugen, wenn nicht vorhanden.
        String alias = "test-AES-256";
        char[] aliasPSW = alias.toCharArray();

        if (!keyStore.containsAlias(alias)) {
            KeyGenerator kg = KeyGenerator.getInstance("AES", provider);
            kg.init(256, secureRandom);
            secretKey = kg.generateKey();

            keyStore.setKeyEntry(alias, secretKey, aliasPSW, null);

            try (OutputStream outputStream = Files.newOutputStream(keystorePath)) {
                keyStore.store(outputStream, keystorePSW);
            }
        }

        Collections.list(keyStore.aliases()).forEach(a -> System.out.println("Alias: " + a));

        secretKey = (SecretKey) keyStore.getKey(alias, aliasPSW);

        Cipher encryptCipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
        Cipher decryptCipher = Cipher.getInstance("AES/GCM/NoPadding", provider);

        // byte iv[] = new byte[16];
        // secureRandom.nextBytes(iv);
        byte[] iv = secureRandom.generateSeed(16);

        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv)); // IvParameterSpec(iv)
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        testCrypt(encryptCipher, decryptCipher);

        // Certificate cert = keyStore.getCertificate(alias);
        // PublicKey publicKey = cert.getPublicKey();
        // PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, aliasPSW);
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
        kpg.initialize(1024, secureRandom);
        KeyPair keyPair = kpg.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // "RSA/ECB/PKCS1Padding"
        encryptCipher = Cipher.getInstance("RSA/ECB/NoPadding", provider);
        decryptCipher = Cipher.getInstance("RSA/ECB/NoPadding", provider);

        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey, secureRandom);
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey, secureRandom);
        testCrypt(encryptCipher, decryptCipher);

        // Stream
        try (InputStream in = new FileInputStream("pom.xml"); CipherOutputStream cipherOutputStream = new CipherOutputStream(new FileOutputStream("/tmp/pom-crypt.dat"), encryptCipher)) {
            // OutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int numRead = 0;

            while ((numRead = in.read(buffer)) >= 0) {
                // byte[] updates = encodeCipher.update(buffer, 0, numRead);
                // System.out.println(Arrays.toString(updates));

                cipherOutputStream.write(buffer, 0, numRead);
            }

            cipherOutputStream.flush();
        }
    }

    private static void testCrypt(final Cipher encryptCipher, final Cipher decryptCipher) throws Exception {
        String message = "abcABC123";
        System.out.println("Message: " + message);

        byte[] encryptedBytes = encryptCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encrypted Bytes: " + new String(encryptedBytes, StandardCharsets.UTF_8));
        System.out.println("Encrypted Base64: " + Base64.getEncoder().encodeToString(encryptedBytes));

        byte[] decryptedBytes = decryptCipher.doFinal(encryptedBytes);
        System.out.println("Decrypted Message: " + new String(decryptedBytes, StandardCharsets.UTF_8));

        System.out.println();
    }

    private KeystoreMain() {
        super();
    }
}
