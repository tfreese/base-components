// Created: 10.03.24
package de.freese.base.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import de.freese.base.utils.Encoding;

/**
 * @author Thomas Freese
 */
public final class Signer {
    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    public enum Algorithm {
        SHA1_WITH_RSA("SHA1withRSA"),
        SHA512_WITH_RSA("SHA512withRSA"),
        SHA3_512_WITH_RSA("SHA3-512withRSA");

        private final String algorithmName;

        Algorithm(final String algorithmName) {
            this.algorithmName = algorithmName;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }
    }

    public static byte[] sign(final byte[] message, final PrivateKey privateKey, final Algorithm algorithm) throws GeneralSecurityException {
        final Signature signature = Signature.getInstance(algorithm.getAlgorithmName());
        signature.initSign(privateKey);

        signature.update(message);

        return signature.sign();
    }

    public static String sign(final String encoded, final PrivateKey privateKey, final Algorithm algorithm, final Encoding encoding) throws GeneralSecurityException {
        final byte[] messageBytes = encoding.decode(encoded);

        final byte[] signature = sign(messageBytes, privateKey, algorithm);

        return encoding.encode(signature);
    }

    /**
     * @param in {@link InputStream}; Signed
     */
    public static void sign(final InputStream in, final OutputStream out, final PrivateKey privateKey, final Algorithm algorithm) throws GeneralSecurityException, IOException {
        final Signature signature = Signature.getInstance(algorithm.getAlgorithmName());
        signature.initSign(privateKey);

        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0) {
            signature.update(buffer, 0, numRead);
        }

        // For a RSA KeySize with 4096 the BlockSize is 512 (4096/8).
        final byte[] sig = signature.sign();
        out.write(sig);

        out.flush();
    }

    public static boolean verify(final byte[] message, final byte[] signedMessage, final PublicKey publicKey, final Algorithm algorithm) throws GeneralSecurityException {
        final Signature signature = Signature.getInstance(algorithm.getAlgorithmName());
        signature.initVerify(publicKey);

        signature.update(message);

        return signature.verify(signedMessage);
    }

    public static boolean verify(final String encoded, final String signedMessage, final PublicKey publicKey, final Algorithm algorithm, final Encoding encoding)
            throws GeneralSecurityException {
        final byte[] messageBytes = encoding.decode(encoded);

        final byte[] signedMessageBytes = encoding.decode(signedMessage);

        return verify(messageBytes, signedMessageBytes, publicKey, algorithm);
    }

    /**
     * @param in {@link InputStream}; Signed
     */
    public static boolean verify(final InputStream in, final InputStream signIn, final PublicKey publicKey, final Algorithm algorithm)
            throws GeneralSecurityException, IOException {
        final Signature signature = Signature.getInstance(algorithm.getAlgorithmName());
        signature.initVerify(publicKey);

        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int numRead = 0;

        while ((numRead = in.read(buffer)) >= 0) {
            signature.update(buffer, 0, numRead);
        }

        // For a RSA KeySize with 4096 the BlockSize is 512 (4096/8).
        byte[] sig = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            while ((numRead = signIn.read(buffer)) >= 0) {
                baos.write(buffer, 0, numRead);
            }

            sig = baos.toByteArray();
        }

        return signature.verify(sig);
    }

    private Signer() {
        super();
    }
}
