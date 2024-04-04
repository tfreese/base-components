// Created: 21.11.2013
package de.freese.base.security.bouncycastle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.util.encoders.Hex;

/**
 * Links:<br>
 * <a href="http://www.lockboxlabs.org/content/downloads">lockboxlabs.org/</a><br>
 * <a href="http://sloanseaman.com/wordpress/2012/05/13/revisited-pgp-encryptiondecryption-in-java">revisited-pgp-encryptiondecryption-in-java</a><br>
 * <a href="http://blog.sealyu.com/2012/08/23/revisited-pgp-encryptiondecryption-in-java-waiting-for-wit">revisited-pgp-encryptiondecryption-in-java-waiting-for-wit</a><br>
 * <a href="http://www.torsten-horn.de/techdocs/java-crypto.htm#CryptoOpenPgpBouncyCastle">torsten-horn</a><br>
 * <a href="http://oreilly.com/catalog/javacrypt/chapter/ch06.html">oreilly</a><br>
 * <br>
 *
 * @author Thomas Freese
 */
class PgpCryptoBc {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int KEY_FLAGS = 27;
    private static final int[] MASTER_KEY_CERTIFICATION_TYPES =
            {PGPSignature.POSITIVE_CERTIFICATION, PGPSignature.CASUAL_CERTIFICATION, PGPSignature.NO_CERTIFICATION, PGPSignature.DEFAULT_CERTIFICATION};

    public static String getAlgorithm(final int algorithm) {
        return switch (algorithm) {
            case PublicKeyAlgorithmTags.RSA_GENERAL -> "RSA_GENERAL";
            case PublicKeyAlgorithmTags.RSA_ENCRYPT -> "RSA_ENCRYPT";
            case PublicKeyAlgorithmTags.RSA_SIGN -> "RSA_SIGN";
            case PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT -> "ELGAMAL_ENCRYPT";
            case PublicKeyAlgorithmTags.DSA -> "DSA";
            case PublicKeyAlgorithmTags.ECDH -> "ECDH";
            case PublicKeyAlgorithmTags.ECDSA -> "ECDSA";
            case PublicKeyAlgorithmTags.ELGAMAL_GENERAL -> "ELGAMAL_GENERAL";
            case PublicKeyAlgorithmTags.DIFFIE_HELLMAN -> "DIFFIE_HELLMAN";

            default -> "unknown";
        };
    }

    static void pubRingDump(final String file) throws Exception {
        try (InputStream inputStream = new FileInputStream(file);
             InputStream decoderInputStream = PGPUtil.getDecoderStream(inputStream)) {
            final PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(decoderInputStream, new BcKeyFingerprintCalculator());

            final Iterator<PGPPublicKeyRing> rIt = pubRings.getKeyRings();

            while (rIt.hasNext()) {
                final PGPPublicKeyRing pgpPub = rIt.next();

                try {
                    pgpPub.getPublicKey();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }

                final Iterator<PGPPublicKey> it = pgpPub.getPublicKeys();
                boolean first = true;

                while (it.hasNext()) {
                    final PGPPublicKey pgpKey = it.next();

                    if (first) {
                        System.out.printf("Key ID: %d, HEX: %s%n", pgpKey.getKeyID(), Long.toHexString(pgpKey.getKeyID()).toUpperCase());
                        first = false;
                    }
                    else {
                        System.out.printf("Subkey ID: %d, HEX: %s%n", pgpKey.getKeyID(), Long.toHexString(pgpKey.getKeyID()).toUpperCase());
                    }

                    System.out.printf("\tAlgorithm: %s%n", getAlgorithm(pgpKey.getAlgorithm()));
                    System.out.printf("\tFingerprint: %s%n", new String(Hex.encode(pgpKey.getFingerprint()), StandardCharsets.UTF_8).toUpperCase());

                    final Iterator<String> userIDs = pgpKey.getUserIDs();

                    while (userIDs.hasNext()) {
                        System.out.printf("\tUserID: %s%n", userIDs.next());
                    }
                }
            }
        }
    }

    // @Override
    // public void setKeyPair(final KeyPair keyPair)
    // {
    // setPublicKey(keyPair.getPublic());
    // setPrivateKey(keyPair.getPrivate());
    // }

    PgpCryptoBc() {
        super();

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // @Override
    // public byte[] decrypt(final byte[] bytes) throws GeneralSecurityException
    // {
    // final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    // final ByteArrayOutputStream out = new ByteArrayOutputStream();
    //
    // try
    // {
    // decrypt(in, out);
    // }
    // catch (IOException ex)
    // {
    // throw new GeneralSecurityException(ex);
    // }
    //
    // return out.toByteArray();
    // }

    /**
     * @param in {@link InputStream}, verschlüsselt
     * @param out {@link OutputStream}, unverschlüsselt
     * @param keyIn {@link InputStream}, PrivateKey
     */
    public void decryptFile(final InputStream in, final OutputStream out, final InputStream keyIn, final char[] password) throws Exception {
        PGPObjectFactory objectFactory = new PGPObjectFactory(PGPUtil.getDecoderStream(in), new BcKeyFingerprintCalculator());
        final Object object = objectFactory.nextObject();
        PGPEncryptedDataList encryptedDataList = null;

        //
        // the first object might be a PGP marker packet.
        //
        if (object instanceof PGPEncryptedDataList obj) {
            encryptedDataList = obj;
        }
        else {
            encryptedDataList = (PGPEncryptedDataList) objectFactory.nextObject();
        }

        //
        // find the secret key
        //
        final Iterator<PGPEncryptedData> it = encryptedDataList.getEncryptedDataObjects();
        PGPPublicKeyEncryptedData encryptedData = null;
        PGPPrivateKey privateKey = null;

        while (it.hasNext()) {
            encryptedData = (PGPPublicKeyEncryptedData) it.next();
            privateKey = findPrivateKey(keyIn, encryptedData.getKeyID(), password);

            if (privateKey != null) {
                break;
            }
        }

        if (privateKey == null || encryptedData == null) {
            throw new IllegalArgumentException("Private key for message not found.");
        }

        final InputStream decryptedInputStream = encryptedData.getDataStream(new BcPublicKeyDataDecryptorFactory(privateKey));
        objectFactory = new PGPObjectFactory(decryptedInputStream, new BcKeyFingerprintCalculator());
        Object message = objectFactory.nextObject();

        if (message instanceof PGPCompressedData compressedData) {
            objectFactory = new PGPObjectFactory(compressedData.getDataStream(), new BcKeyFingerprintCalculator());
            message = objectFactory.nextObject();
        }

        if (message instanceof PGPLiteralData literalData) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            try (InputStream inputStream = literalData.getInputStream()) {
                int numRead = 0;

                while ((numRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, numRead);
                }
            }

            buffer = null;
        }
        else if (message instanceof PGPOnePassSignatureList) {
            throw new PGPException("Encrypted message contains a signed message - not literal data.");
        }
        else {
            throw new PGPException("Message is not a simple encrypted file - type unknown.");
        }

        if (encryptedData.isIntegrityProtected()) {
            if (!encryptedData.verify()) {
                throw new PGPException("Message failed integrity check");
            }
        }
    }

    /**
     * @param encryptedFile String, verschlüsselt
     * @param rawFile String, unverschlüsselt
     */
    public void encryptFile(final String encryptedFile, final String rawFile, final PGPPublicKey publicKey, final boolean armored, final boolean integrityCheck) throws Exception {
        try (OutputStream outputStream = armored ? new ArmoredOutputStream(new FileOutputStream(encryptedFile)) : new FileOutputStream(encryptedFile)) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);

            try (OutputStream dataOutputStream = compressedDataGenerator.open(baos)) {
                PGPUtil.writeFileToLiteralData(dataOutputStream, PGPLiteralData.BINARY, new File(rawFile));
            }

            compressedDataGenerator.close();

            final BcPGPDataEncryptorBuilder encryptorBuilder = new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256);
            encryptorBuilder.setWithIntegrityPacket(integrityCheck);
            encryptorBuilder.setSecureRandom(new SecureRandom());

            final PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(encryptorBuilder);
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));

            final byte[] bytes = baos.toByteArray();

            try (OutputStream cOut = encryptedDataGenerator.open(outputStream, bytes.length)) {
                cOut.write(bytes);
            }
        }
    }

    /**
     * Load a secret key ring collection from keyIn and find the private key corresponding to keyID if it exists.
     */
    public PGPPrivateKey findPrivateKey(final InputStream keyIn, final long keyID, final char[] pass) throws Exception {
        try (InputStream decoderInputStream = PGPUtil.getDecoderStream(keyIn)) {
            final PGPSecretKeyRingCollection keyRingCollection = new PGPSecretKeyRingCollection(decoderInputStream, new BcKeyFingerprintCalculator());

            return findPrivateKey(keyRingCollection.getSecretKey(keyID), pass);
        }
    }

    /**
     * Load a secret key and find the private key in it.
     */
    public PGPPrivateKey findPrivateKey(final PGPSecretKey secretKey, final char[] pass) throws Exception {
        if (secretKey == null) {
            return null;
            // throw new IllegalArgumentException("Can't find private key in the key ring.");
        }

        // // Validate secret key
        // if (!secretKey.isSigningKey())
        // {
        // throw new IllegalArgumentException("Private key does not allow signing.");
        // }
        //
        // if (secretKey.getPublicKey().isRevoked())
        // {
        // throw new IllegalArgumentException("Private key has been revoked.");
        // }
        //
        // if (!hasKeyFlags(secretKey.getPublicKey(), KeyFlags.SIGN_DATA))
        // {
        // throw new IllegalArgumentException("Key cannot be used for signing.");
        // }

        final PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass);

        return secretKey.extractPrivateKey(decryptor);
    }

    public PGPPublicKey findPublicKey(final String keyIn, final String hexCode) throws Exception {
        PGPPublicKey publicKey = null;

        try (InputStream inputStream = new FileInputStream(keyIn);
             InputStream decoderInputStream = PGPUtil.getDecoderStream(inputStream)) {
            final PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(decoderInputStream, new BcKeyFingerprintCalculator());

            final Iterator<PGPPublicKeyRing> iteratorKeyring = pubRings.getKeyRings();

            while (iteratorKeyring.hasNext()) {
                final PGPPublicKeyRing pgpPub = iteratorKeyring.next();

                final Iterator<PGPPublicKey> iteratorPublicKeys = pgpPub.getPublicKeys();

                while (iteratorPublicKeys.hasNext()) {
                    final PGPPublicKey pubKey = iteratorPublicKeys.next();

                    if (!pubKey.isEncryptionKey()) {
                        continue;
                    }

                    final String keyHexCode = Long.toHexString(pubKey.getKeyID()).toUpperCase();

                    if (keyHexCode.endsWith(hexCode)) {
                        publicKey = pubKey;
                        break;
                    }
                }

                if (publicKey != null) {
                    break;
                }
            }
        }

        if (publicKey != null && !isForEncryption(publicKey)) {
            throw new IllegalArgumentException("KeyID " + publicKey.getKeyID() + " not flagged for encryption.");
        }

        return publicKey;
    }

    /**
     * From LockBox Lobs PGP Encryption tools.<br>
     * I didn't think it was worth having to import a 4meg lib for three methods.
     */
    public boolean isForEncryption(final PGPPublicKey key) {
        if (key.getAlgorithm() == PublicKeyAlgorithmTags.RSA_SIGN
                || key.getAlgorithm() == PublicKeyAlgorithmTags.DSA
                || key.getAlgorithm() == PublicKeyAlgorithmTags.ECDH
                || key.getAlgorithm() == PublicKeyAlgorithmTags.ECDSA) {
            return false;
        }

        return hasKeyFlags(key, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);
    }

    public PGPSecretKey readSecretKey(final String keyIn) throws Exception {
        PGPSecretKey secretKey = null;

        try (InputStream inputStream = new FileInputStream(keyIn);
             InputStream decoderInputStream = PGPUtil.getDecoderStream(inputStream)) {
            final PGPSecretKeyRingCollection keyRingCollection = new PGPSecretKeyRingCollection(decoderInputStream, new BcKeyFingerprintCalculator());

            // We just loop through the collection till we find a key suitable for signing.
            // In the real world you would probably want to be a bit smarter about this.
            final Iterator<PGPSecretKeyRing> rIt = keyRingCollection.getKeyRings();

            while (secretKey == null && rIt.hasNext()) {
                final PGPSecretKeyRing keyRing = rIt.next();
                final Iterator<PGPSecretKey> kIt = keyRing.getSecretKeys();

                while (secretKey == null && kIt.hasNext()) {
                    final PGPSecretKey key = kIt.next();

                    if (key.isSigningKey()) {
                        secretKey = key;
                    }
                }
            }
        }

        // Validate secret key
        if (secretKey == null) {
            throw new IllegalArgumentException("Can't find private key in the key ring.");
        }

        if (!secretKey.isSigningKey()) {
            throw new IllegalArgumentException("Private key does not allow signing.");
        }

        if (secretKey.getPublicKey().hasRevocation()) {
            throw new IllegalArgumentException("Private key has been revoked.");
        }

        if (!hasKeyFlags(secretKey.getPublicKey(), KeyFlags.SIGN_DATA)) {
            throw new IllegalArgumentException("Key cannot be used for signing.");
        }

        return secretKey;
    }

    public void signEncryptFile(final String encryptedFile, final String fileName, final PGPPublicKey publicKey, final PGPSecretKey secretKey, final char[] password,
                                final boolean armored, final boolean withIntegrityCheck) throws Exception {
        try (OutputStream outputStream = armored ? new ArmoredOutputStream(new FileOutputStream(encryptedFile)) : new FileOutputStream(encryptedFile)) {
            final BcPGPDataEncryptorBuilder dataEncryptor = new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256);
            dataEncryptor.setWithIntegrityPacket(withIntegrityCheck);
            dataEncryptor.setSecureRandom(new SecureRandom());

            final PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));

            final OutputStream encryptedOut = encryptedDataGenerator.open(outputStream, new byte[DEFAULT_BUFFER_SIZE]);

            // Initialize compressed data generator
            final PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
            final OutputStream compressedOut = compressedDataGenerator.open(encryptedOut, new byte[DEFAULT_BUFFER_SIZE]);

            // Initialize signature generator
            final PGPPrivateKey privateKey = findPrivateKey(secretKey, password);

            final PGPContentSignerBuilder signerBuilder = new BcPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1);

            final PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(signerBuilder);
            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);

            boolean firstTime = true;
            final Iterator<String> it = secretKey.getPublicKey().getUserIDs();

            while (it.hasNext() && firstTime) {
                final PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
                spGen.addSignerUserID(false, it.next());
                signatureGenerator.setHashedSubpackets(spGen.generate());
                // Exit the loop after the first iteration
                firstTime = false;
            }

            signatureGenerator.generateOnePassVersion(false).encode(compressedOut);

            // Initialize literal data generator
            final PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
            final OutputStream literalOut = literalDataGenerator.open(compressedOut, PGPLiteralData.BINARY, fileName, new Date(), new byte[DEFAULT_BUFFER_SIZE]);

            // Main loop - read the "in" stream, compress, encrypt and write to the "out" stream
            try (FileInputStream in = new FileInputStream(fileName)) {
                final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
                int len;

                while ((len = in.read(buf)) > 0) {
                    literalOut.write(buf, 0, len);
                    signatureGenerator.update(buf, 0, len);
                }
            }

            literalDataGenerator.close();
            // Generate the signature, compress, encrypt and write to the "out" stream
            signatureGenerator.generate().encode(compressedOut);
            compressedDataGenerator.close();
            encryptedDataGenerator.close();
        }
    }

    @SuppressWarnings("checkstyle:ParameterAssignment")
    public boolean verifyFile(final InputStream in, final InputStream keyIn, final String extractContentFile) throws Exception {
        final InputStream decoderInputStream = PGPUtil.getDecoderStream(in);

        PGPObjectFactory pgpFact = new PGPObjectFactory(decoderInputStream, new BcKeyFingerprintCalculator());
        final PGPCompressedData c1 = (PGPCompressedData) pgpFact.nextObject();

        pgpFact = new PGPObjectFactory(c1.getDataStream(), new BcKeyFingerprintCalculator());
        final PGPOnePassSignatureList p1 = (PGPOnePassSignatureList) pgpFact.nextObject();
        final PGPOnePassSignature ops = p1.get(0);

        final PGPLiteralData p2 = (PGPLiteralData) pgpFact.nextObject();

        final InputStream dIn = p2.getInputStream();

        Files.copy(dIn, new File(extractContentFile).toPath());

        int ch;
        final PGPPublicKeyRingCollection pgpRing = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new BcKeyFingerprintCalculator());

        final PGPPublicKey key = pgpRing.getPublicKey(ops.getKeyID());

        try (FileOutputStream out = new FileOutputStream(p2.getFileName())) {
            ops.init(new BcPGPContentVerifierBuilderProvider(), key);

            while ((ch = dIn.read()) >= 0) {
                ops.update((byte) ch);
                out.write(ch);
            }
        }

        final PGPSignatureList p3 = (PGPSignatureList) pgpFact.nextObject();

        return ops.verify(p3.get(0));
    }

    /**
     * From LockBox Lobs PGP Encryption tools.<br>
     * I didn't think it was worth having to import a 4meg lib for three methods.
     */
    private boolean hasKeyFlags(final PGPPublicKey encKey, final int keyUsage) {
        if (encKey.isMasterKey()) {
            for (int certType : MASTER_KEY_CERTIFICATION_TYPES) {
                for (final Iterator<PGPSignature> iterator = encKey.getSignaturesOfType(certType); iterator.hasNext(); ) {
                    final PGPSignature sig = iterator.next();

                    if (!isMatchingUsage(sig, keyUsage)) {
                        return false;
                    }
                }
            }
        }
        else {
            for (final Iterator<PGPSignature> iterator = encKey.getSignaturesOfType(PGPSignature.SUBKEY_BINDING); iterator.hasNext(); ) {
                final PGPSignature sig = iterator.next();

                if (!isMatchingUsage(sig, keyUsage)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * From LockBox Lobs PGP Encryption tools.<br>
     * I didn't think it was worth having to import a 4meg lib for three methods.
     */
    private boolean isMatchingUsage(final PGPSignature sig, final int keyUsage) {
        if (sig.hasSubpackets()) {
            final PGPSignatureSubpacketVector sv = sig.getHashedSubPackets();

            if (sv.hasSubpacket(KEY_FLAGS)) {
                // code fix suggested by kzt (see comments)
                return sv.getKeyFlags() != 0 || keyUsage != 0;
            }
        }

        return true;
    }

    // @Override
    // public byte[] encrypt(final byte[] bytes) throws GeneralSecurityException {
    // final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    // final ByteArrayOutputStream out = new ByteArrayOutputStream();
    //
    // try {
    // encrypt(in, out);
    // }
    // catch (IOException ex) {
    // throw new GeneralSecurityException(ex);
    // }
    //
    // return out.toByteArray();
    // }
}
