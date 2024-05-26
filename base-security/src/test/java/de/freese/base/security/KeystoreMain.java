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
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Collections;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Beispiele für JCE-API.
 *
 * @author Thomas Freese see base-security/keystore.txt
 */
public final class KeystoreMain {
    public static void main(final String[] args) throws Exception {
        final String provider = "SunJCE"; // "SUN";

        final char[] keystorePSW = "gehaim".toCharArray();

        // KeyStore.getDefaultType();
        final Path keystorePath = Paths.get(System.getProperty("java.io.tmpdir"), "keystore.p12");
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");

        if (Files.exists(keystorePath)) {
            try (InputStream in = Files.newInputStream(keystorePath)) {
                keyStore.load(in, keystorePSW);
            }
        }
        else {
            keyStore.load(null, keystorePSW);
        }

        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        Key key = null;

        // Keys erzeugen, wenn nicht vorhanden.
        final String alias = "test-AES-256";
        final char[] aliasPSW = alias.toCharArray();

        if (!keyStore.containsAlias(alias)) {
            final KeyGenerator kg = KeyGenerator.getInstance("AES", provider);
            kg.init(256, secureRandom);
            key = kg.generateKey();

            keyStore.setKeyEntry(alias, key, aliasPSW, null);

            try (OutputStream outputStream = Files.newOutputStream(keystorePath)) {
                keyStore.store(outputStream, keystorePSW);
            }
        }

        Collections.list(keyStore.aliases()).forEach(a -> System.out.println("Alias: " + a));

        key = keyStore.getKey(alias, aliasPSW);

        Cipher encryptCipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
        Cipher decryptCipher = Cipher.getInstance("AES/GCM/NoPadding", provider);

        // byte iv[] = new byte[16];
        // secureRandom.nextBytes(iv);
        final byte[] iv = secureRandom.generateSeed(16);

        encryptCipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv)); // IvParameterSpec(iv)
        decryptCipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        testCrypt(encryptCipher, decryptCipher);

        final Certificate cert = keyStore.getCertificate(alias);
        final PublicKey publicKey = cert.getPublicKey();
        final PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, aliasPSW);

        // "RSA/ECB/PKCS1Padding"
        encryptCipher = Cipher.getInstance("RSA/ECB/NoPadding", provider);
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey, secureRandom);

        decryptCipher = Cipher.getInstance("RSA/ECB/NoPadding", provider);
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey, secureRandom);

        testCrypt(encryptCipher, decryptCipher);

        // Stream
        try (InputStream in = new FileInputStream("pom.xml");
             CipherOutputStream cipherOutputStream = new CipherOutputStream(new FileOutputStream("/tmp/pom-crypt.dat"), encryptCipher)) {
            // OutputStream out = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
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
        final String message = "abcABC123";
        System.out.println("Message: " + message);

        final byte[] encryptedBytes = encryptCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encrypted Bytes: " + new String(encryptedBytes, StandardCharsets.UTF_8));
        System.out.println("Encrypted Base64: " + Base64.getEncoder().encodeToString(encryptedBytes));

        final byte[] decryptedBytes = decryptCipher.doFinal(encryptedBytes);
        System.out.println("Decrypted Message: " + new String(decryptedBytes, StandardCharsets.UTF_8));

        System.out.println();
    }

    private KeystoreMain() {
        super();
    }
}
