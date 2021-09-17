/**
 * Created: 21.11.2013
 */

package de.freese.base.security.bouncycastle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
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
 * http://sloanseaman.com/wordpress/2012/05/13/revisited-pgp-encryptiondecryption-in-java/<br>
 * http://blog.sealyu.com/2012/08/23/revisited-pgp-encryptiondecryption-in-java-waiting-for-wit/<br>
 * http://www.torsten-horn.de/techdocs/java-crypto.htm#CryptoOpenPgpBouncyCastle<br>
 * http://oreilly.com/catalog/javacrypt/chapter/ch06.html<br>
 * <br>
 *
 * @author Thomas Freese
 */
class PGPCryptoBC
{
    /**
    *
    */
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    /**
     *
     */
    private static final int KEY_FLAGS = 27;
    /**
     *
     */
    private static final int[] MASTER_KEY_CERTIFICATION_TYPES =
    {
            PGPSignature.POSITIVE_CERTIFICATION, PGPSignature.CASUAL_CERTIFICATION, PGPSignature.NO_CERTIFICATION, PGPSignature.DEFAULT_CERTIFICATION
    };

    /**
     * @param algorithm int
     *
     * @return String
     */
    public static String getAlgorithm(final int algorithm)
    {
        return switch (algorithm)
        {
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

    /**
     * @param args String[]
     *
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // KeyID = -3266217749052483355
        System.out.println(Long.decode("0x09DFF54A96322AD9"));
        System.out.println(new BigInteger("09DFF54A96322AD9", 16).longValue());
        System.out.println(new BigInteger("D2AC10432CA698E5", 16).longValue());

        // pubRingDump("/home/tommy/.gnupg/pubring.gpg");

        PGPCryptoBC codec = new PGPCryptoBC();

        System.out.println("Encrypt");
        PGPPublicKey publicKey = codec.findPublicKey("/home/tommy/.gnupg/pubring.gpg", "96322AD9");
        // PGPSecretKey secretKey = codec.readSecretKey("/home/tommy/.gnupg/secring.gpg");
        // codec.esignEncryptFile("/tmp/conkyrc.gpg", "/home/tommy/.conkyrc", publicKey, secretKey, args[0].toCharArray(), false, true);
        codec.encryptFile("/tmp/conkyrc.gpg", "/home/tommy/.conkyrc", publicKey, false, true);

        // System.out.println("Verify");
        // codec.verifyFile(new FileInputStream("/tmp/conkyrc.gpg"), new FileInputStream("/home/tommy/.gnupg/pubring.gpg"), "/tmp/extraContent.txt");

        System.out.println("Decrypt");
        // InputStream in = new FileInputStream("/home/tommy/.password-store/arbeit/webmail.auel.de.gpg");

        try (InputStream in = new FileInputStream("/tmp/conkyrc.gpg");
             OutputStream out = new FileOutputStream("/tmp/test.txt");
             InputStream keyIn = new FileInputStream("/home/tommy/.gnupg/secring.gpg"))
        {
            codec.decryptFile(in, out, keyIn, args[0].toCharArray());
        }
    }

    /**
     * @param file String
     *
     * @throws Exception Falls was schief geht.
     */
    static void pubRingDump(final String file) throws Exception
    {
        try (InputStream inputStream = new FileInputStream(file);
             InputStream decoderInputStream = PGPUtil.getDecoderStream(inputStream))
        {
            PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(decoderInputStream, new BcKeyFingerprintCalculator());

            Iterator<PGPPublicKeyRing> rIt = pubRings.getKeyRings();

            while (rIt.hasNext())
            {
                PGPPublicKeyRing pgpPub = rIt.next();

                try
                {
                    pgpPub.getPublicKey();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    continue;
                }

                Iterator<PGPPublicKey> it = pgpPub.getPublicKeys();
                boolean first = true;

                while (it.hasNext())
                {
                    PGPPublicKey pgpKey = it.next();

                    if (first)
                    {
                        System.out.printf("Key ID: %d, HEX: %s%n", pgpKey.getKeyID(), Long.toHexString(pgpKey.getKeyID()).toUpperCase());
                        first = false;
                    }
                    else
                    {
                        System.out.printf("Subkey ID: %d, HEX: %s%n", pgpKey.getKeyID(), Long.toHexString(pgpKey.getKeyID()).toUpperCase());
                    }

                    System.out.printf("\tAlgorithm: %s%n", getAlgorithm(pgpKey.getAlgorithm()));
                    System.out.printf("\tFingerprint: %s%n", new String(Hex.encode(pgpKey.getFingerprint()), StandardCharsets.UTF_8).toUpperCase());

                    Iterator<String> userIDs = pgpKey.getUserIDs();

                    while (userIDs.hasNext())
                    {
                        System.out.printf("\tUserID: %s%n", userIDs.next());
                    }
                }
            }
        }
    }

    // /**
    // * @see de.freese.base.security.codec.keypair.ICryptoCodecKeyPair#setKeyPair(java.security.KeyPair)
    // */
    // @Override
    // public void setKeyPair(final KeyPair keyPair)
    // {
    // setPublicKey(keyPair.getPublic());
    // setPrivateKey(keyPair.getPrivate());
    // }

    /**
     * Erstellt ein neues {@link PGPCryptoBC} Object.
     */
    public PGPCryptoBC()
    {
        super();

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
        {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // /**
    // * @see de.freese.base.security.codec.ICryptoCodec#decrypt(byte[])
    // */
    // @Override
    // public byte[] decrypt(final byte[] bytes) throws GeneralSecurityException
    // {
    // ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    // ByteArrayOutputStream out = new ByteArrayOutputStream();
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
     * @param password char[]
     *
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings("resource")
    private void decryptFile(final InputStream in, final OutputStream out, final InputStream keyIn, final char[] password) throws Exception
    {
        PGPObjectFactory objectFactory = new PGPObjectFactory(PGPUtil.getDecoderStream(in), new BcKeyFingerprintCalculator());
        Object object = objectFactory.nextObject();
        PGPEncryptedDataList encryptedDataList = null;

        //
        // the first object might be a PGP marker packet.
        //
        if (object instanceof PGPEncryptedDataList)
        {
            encryptedDataList = (PGPEncryptedDataList) object;
        }
        else
        {
            encryptedDataList = (PGPEncryptedDataList) objectFactory.nextObject();
        }

        //
        // find the secret key
        //
        Iterator<PGPEncryptedData> it = encryptedDataList.getEncryptedDataObjects();
        PGPPublicKeyEncryptedData encryptedData = null;
        PGPPrivateKey privateKey = null;

        while (it.hasNext())
        {
            encryptedData = (PGPPublicKeyEncryptedData) it.next();
            privateKey = findPrivateKey(keyIn, encryptedData.getKeyID(), password);

            if (privateKey != null)
            {
                break;
            }
        }

        if ((privateKey == null) || (encryptedData == null))
        {
            throw new IllegalArgumentException("Private key for message not found.");
        }

        InputStream decryptedInputStream = encryptedData.getDataStream(new BcPublicKeyDataDecryptorFactory(privateKey));
        objectFactory = new PGPObjectFactory(decryptedInputStream, new BcKeyFingerprintCalculator());
        Object message = objectFactory.nextObject();

        if (message instanceof PGPCompressedData compressedData)
        {
            objectFactory = new PGPObjectFactory(compressedData.getDataStream(), new BcKeyFingerprintCalculator());
            message = objectFactory.nextObject();
        }

        if (message instanceof PGPLiteralData literalData)
        {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            try (InputStream inputStream = literalData.getInputStream())
            {
                int numRead = 0;

                while ((numRead = inputStream.read(buffer)) >= 0)
                {
                    out.write(buffer, 0, numRead);
                }
            }

            buffer = null;
        }
        else if (message instanceof PGPOnePassSignatureList)
        {
            throw new PGPException("Encrypted message contains a signed message - not literal data.");
        }
        else
        {
            throw new PGPException("Message is not a simple encrypted file - type unknown.");
        }

        if (encryptedData.isIntegrityProtected())
        {
            if (!encryptedData.verify())
            {
                throw new PGPException("Message failed integrity check");
            }
        }
    }

    /**
     * @param encryptedFile String, verschlüsselt
     * @param rawFile String, unverschlüsselt
     * @param publicKey {@link PGPPublicKey}
     * @param armored boolean
     * @param integrityCheck boolean
     *
     * @throws Exception Falls was schief geht.
     */
    private void encryptFile(final String encryptedFile, final String rawFile, final PGPPublicKey publicKey, final boolean armored,
                             final boolean integrityCheck)
        throws Exception
    {
        try (OutputStream outputStream = armored ? new ArmoredOutputStream(new FileOutputStream(encryptedFile)) : new FileOutputStream(encryptedFile))
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);

            try (OutputStream dataOutputStream = compressedDataGenerator.open(baos))
            {
                PGPUtil.writeFileToLiteralData(dataOutputStream, PGPLiteralData.BINARY, new File(rawFile));
            }

            compressedDataGenerator.close();

            BcPGPDataEncryptorBuilder encryptorBuilder = new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256);
            encryptorBuilder.setWithIntegrityPacket(integrityCheck);
            encryptorBuilder.setSecureRandom(new SecureRandom());

            PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(encryptorBuilder);
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));

            byte[] bytes = baos.toByteArray();

            try (OutputStream cOut = encryptedDataGenerator.open(outputStream, bytes.length))
            {
                cOut.write(bytes);
            }
        }
    }

    /**
     * @param encryptedFile String
     * @param fileName String
     * @param publicKey {@link PGPPublicKey}
     * @param secretKey {@link PGPSecretKey}
     * @param password char[]
     * @param armored boolean
     * @param withIntegrityCheck boolean
     *
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public void esignEncryptFile(final String encryptedFile, final String fileName, final PGPPublicKey publicKey, final PGPSecretKey secretKey,
                                 final char[] password, final boolean armored, final boolean withIntegrityCheck)
        throws Exception
    {
        try (OutputStream outputStream = armored ? new ArmoredOutputStream(new FileOutputStream(encryptedFile)) : new FileOutputStream(encryptedFile))
        {
            BcPGPDataEncryptorBuilder dataEncryptor = new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256);
            dataEncryptor.setWithIntegrityPacket(withIntegrityCheck);
            dataEncryptor.setSecureRandom(new SecureRandom());

            PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));

            OutputStream encryptedOut = encryptedDataGenerator.open(outputStream, new byte[DEFAULT_BUFFER_SIZE]);

            // Initialize compressed data generator
            PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
            OutputStream compressedOut = compressedDataGenerator.open(encryptedOut, new byte[DEFAULT_BUFFER_SIZE]);

            // Initialize signature generator
            PGPPrivateKey privateKey = findPrivateKey(secretKey, password);

            PGPContentSignerBuilder signerBuilder = new BcPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1);

            PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(signerBuilder);
            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);

            boolean firstTime = true;
            Iterator<String> it = secretKey.getPublicKey().getUserIDs();

            while (it.hasNext() && firstTime)
            {
                PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
                spGen.addSignerUserID(false, it.next());
                signatureGenerator.setHashedSubpackets(spGen.generate());
                // Exit the loop after the first iteration
                firstTime = false;
            }

            signatureGenerator.generateOnePassVersion(false).encode(compressedOut);

            // Initialize literal data generator
            PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
            OutputStream literalOut = literalDataGenerator.open(compressedOut, PGPLiteralData.BINARY, fileName, new Date(), new byte[DEFAULT_BUFFER_SIZE]);

            // Main loop - read the "in" stream, compress, encrypt and write to the "out" stream
            try (FileInputStream in = new FileInputStream(fileName))
            {
                byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
                int len;

                while ((len = in.read(buf)) > 0)
                {
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

    /**
     * Load a secret key ring collection from keyIn and find the private key corresponding to keyID if it exists.
     *
     * @param keyIn input stream representing a key ring collection.
     * @param keyID keyID we want.
     * @param pass passphrase to decrypt secret key with.
     *
     * @return {@link PGPPrivateKey}
     *
     * @throws Exception Falls was schief geht.
     */
    public PGPPrivateKey findPrivateKey(final InputStream keyIn, final long keyID, final char[] pass) throws Exception
    {
        try (InputStream decoderInputStream = PGPUtil.getDecoderStream(keyIn))
        {
            PGPSecretKeyRingCollection keyRingCollection = new PGPSecretKeyRingCollection(decoderInputStream, new BcKeyFingerprintCalculator());

            return findPrivateKey(keyRingCollection.getSecretKey(keyID), pass);
        }
    }

    /**
     * Load a secret key and find the private key in it
     *
     * @param secretKey {@link PGPSecretKey}
     * @param pass passphrase to decrypt secret key with
     *
     * @return {@link PGPPrivateKey}
     *
     * @throws Exception Falls was schief geht.
     */
    public PGPPrivateKey findPrivateKey(final PGPSecretKey secretKey, final char[] pass) throws Exception
    {
        if (secretKey == null)
        {
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

        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass);

        return secretKey.extractPrivateKey(decryptor);
    }

    /**
     * @param keyIn String
     * @param hexCode String
     *
     * @return {@link PGPPublicKey}
     *
     * @throws Exception Falls was schief geht.
     */
    private PGPPublicKey findPublicKey(final String keyIn, final String hexCode) throws Exception
    {
        PGPPublicKey publicKey = null;

        try (InputStream inputStream = new FileInputStream(keyIn);
             InputStream decoderInputStream = PGPUtil.getDecoderStream(inputStream))
        {
            PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(decoderInputStream, new BcKeyFingerprintCalculator());

            Iterator<PGPPublicKeyRing> iteratorKeyrings = pubRings.getKeyRings();

            while (iteratorKeyrings.hasNext())
            {
                PGPPublicKeyRing pgpPub = iteratorKeyrings.next();

                Iterator<PGPPublicKey> iteratorPublicKeys = pgpPub.getPublicKeys();

                while (iteratorPublicKeys.hasNext())
                {
                    PGPPublicKey pubKey = iteratorPublicKeys.next();

                    if (!pubKey.isEncryptionKey())
                    {
                        continue;
                    }

                    String keyHexCode = Long.toHexString(pubKey.getKeyID()).toUpperCase();

                    if (keyHexCode.endsWith(hexCode))
                    {
                        publicKey = pubKey;
                        break;
                    }
                }

                if (publicKey != null)
                {
                    break;
                }
            }
        }

        if ((publicKey != null) && !isForEncryption(publicKey))
        {
            throw new IllegalArgumentException("KeyID " + publicKey.getKeyID() + " not flagged for encryption.");
        }

        return publicKey;
    }

    /**
     * From LockBox Lobs PGP Encryption tools.<br>
     * http://www.lockboxlabs.org/content/downloads<br>
     * I didn't think it was worth having to import a 4meg lib for three methods.
     *
     * @param encKey {@link PGPPublicKey}
     * @param keyUsage int
     *
     * @return boolean
     */
    private boolean hasKeyFlags(final PGPPublicKey encKey, final int keyUsage)
    {
        if (encKey.isMasterKey())
        {
            for (int certType : MASTER_KEY_CERTIFICATION_TYPES)
            {
                for (Iterator<PGPSignature> iterator = encKey.getSignaturesOfType(certType); iterator.hasNext();)
                {
                    PGPSignature sig = iterator.next();

                    if (!isMatchingUsage(sig, keyUsage))
                    {
                        return false;
                    }
                }
            }
        }
        else
        {
            for (Iterator<PGPSignature> iterator = encKey.getSignaturesOfType(PGPSignature.SUBKEY_BINDING); iterator.hasNext();)
            {
                PGPSignature sig = iterator.next();

                if (!isMatchingUsage(sig, keyUsage))
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * From LockBox Lobs PGP Encryption tools.<br>
     * http://www.lockboxlabs.org/content/downloads<br>
     * I didn't think it was worth having to import a 4meg lib for three methods.
     *
     * @param key {@link PGPPublicKey}
     *
     * @return boolean
     */
    public boolean isForEncryption(final PGPPublicKey key)
    {
        if ((key.getAlgorithm() == PublicKeyAlgorithmTags.RSA_SIGN) || (key.getAlgorithm() == PublicKeyAlgorithmTags.DSA)
                || (key.getAlgorithm() == PublicKeyAlgorithmTags.ECDH) || (key.getAlgorithm() == PublicKeyAlgorithmTags.ECDSA))
        {
            return false;
        }

        return hasKeyFlags(key, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);
    }

    /**
     * From LockBox Lobs PGP Encryption tools.<br>
     * http://www.lockboxlabs.org/content/downloads<br>
     * I didn't think it was worth having to import a 4meg lib for three methods.
     *
     * @param sig {@link PGPSignature}
     * @param keyUsage int
     *
     * @return boolean
     */
    private boolean isMatchingUsage(final PGPSignature sig, final int keyUsage)
    {
        if (sig.hasSubpackets())
        {
            PGPSignatureSubpacketVector sv = sig.getHashedSubPackets();

            if (sv.hasSubpacket(KEY_FLAGS))
            {
                // code fix suggested by kzt (see comments)
                if ((sv.getKeyFlags() == 0) && (keyUsage == 0))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param keyIn String
     *
     * @return {@link PGPSecretKey}
     *
     * @throws Exception Falls was schief geht.
     */
    public PGPSecretKey readSecretKey(final String keyIn) throws Exception
    {
        PGPSecretKey secretKey = null;

        try (InputStream inputStream = new FileInputStream(keyIn);
             InputStream decoderInputStream = PGPUtil.getDecoderStream(inputStream))
        {
            PGPSecretKeyRingCollection keyRingCollection = new PGPSecretKeyRingCollection(decoderInputStream, new BcKeyFingerprintCalculator());

            // We just loop through the collection till we find a key suitable for signing.
            // In the real world you would probably want to be a bit smarter about this.
            Iterator<PGPSecretKeyRing> rIt = keyRingCollection.getKeyRings();

            while ((secretKey == null) && rIt.hasNext())
            {
                PGPSecretKeyRing keyRing = rIt.next();
                Iterator<PGPSecretKey> kIt = keyRing.getSecretKeys();

                while ((secretKey == null) && kIt.hasNext())
                {
                    PGPSecretKey key = kIt.next();

                    if (key.isSigningKey())
                    {
                        secretKey = key;
                    }
                }
            }
        }

        // Validate secret key
        if (secretKey == null)
        {
            throw new IllegalArgumentException("Can't find private key in the key ring.");
        }

        if (!secretKey.isSigningKey())
        {
            throw new IllegalArgumentException("Private key does not allow signing.");
        }

        if (secretKey.getPublicKey().hasRevocation())
        {
            throw new IllegalArgumentException("Private key has been revoked.");
        }

        if (!hasKeyFlags(secretKey.getPublicKey(), KeyFlags.SIGN_DATA))
        {
            throw new IllegalArgumentException("Key cannot be used for signing.");
        }

        return secretKey;
    }

    /**
     * @param in {@link InputStream}
     * @param keyIn {@link InputStream}
     * @param extractContentFile String
     *
     * @return boolean
     *
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public boolean verifyFile(InputStream in, final InputStream keyIn, final String extractContentFile) throws Exception
    {
        in = PGPUtil.getDecoderStream(in);

        PGPObjectFactory pgpFact = new PGPObjectFactory(in, new BcKeyFingerprintCalculator());
        PGPCompressedData c1 = (PGPCompressedData) pgpFact.nextObject();

        pgpFact = new PGPObjectFactory(c1.getDataStream(), new BcKeyFingerprintCalculator());
        PGPOnePassSignatureList p1 = (PGPOnePassSignatureList) pgpFact.nextObject();
        PGPOnePassSignature ops = p1.get(0);

        PGPLiteralData p2 = (PGPLiteralData) pgpFact.nextObject();

        InputStream dIn = p2.getInputStream();

        Files.copy(dIn, new File(extractContentFile).toPath());

        int ch;
        PGPPublicKeyRingCollection pgpRing = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new BcKeyFingerprintCalculator());

        PGPPublicKey key = pgpRing.getPublicKey(ops.getKeyID());

        try (FileOutputStream out = new FileOutputStream(p2.getFileName()))
        {
            ops.init(new BcPGPContentVerifierBuilderProvider(), key);

            while ((ch = dIn.read()) >= 0)
            {
                ops.update((byte) ch);
                out.write(ch);
            }
        }

        PGPSignatureList p3 = (PGPSignatureList) pgpFact.nextObject();

        return ops.verify(p3.get(0));
    }

    // /**
    // * @see de.freese.base.security.codec.ICryptoCodec#encrypt(byte[])
    // */
    // @Override
    // public byte[] encrypt(final byte[] bytes) throws GeneralSecurityException
    // {
    // ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    // ByteArrayOutputStream out = new ByteArrayOutputStream();
    //
    // try
    // {
    // encrypt(in, out);
    // }
    // catch (IOException ex)
    // {
    // throw new GeneralSecurityException(ex);
    // }
    //
    // return out.toByteArray();
    // }
}
