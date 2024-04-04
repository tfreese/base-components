package de.freese.base.security.bouncycastle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verschlüsselt Verzeichnisse/Dateien mit einem {@link X509Certificate} des BouncyCastleProviders.
 *
 * @author Svetlana Margolina (eck*cellent)
 * @author Thomas Freese
 */
public class EncryptFileBc {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptFileBc.class);

    public EncryptFileBc() {
        super();

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Verschlüsselt die Datei mit einem {@link X509Certificate}.
     */
    public void encryptX509File(final String decryptedFile, final String encryptedFile, final String zertifikatFile) throws Exception {
        final X509Certificate cert = getCertificate(zertifikatFile);

        encryptX509File(decryptedFile, encryptedFile, cert);
    }

    /**
     * Verschlüsselt die Datei mit einem {@link X509Certificate}.
     */
    public void encryptX509File(final String decryptedFile, final String encryptedFile, final String keystoreFile, final char[] keyStorePassword, final String alias)
            throws Exception {
        final X509Certificate cert = getCertificate(keystoreFile, keyStorePassword, alias);

        encryptX509File(decryptedFile, encryptedFile, cert);
    }

    /**
     * Verschlüsselt die Datei mit einem {@link X509Certificate}.
     */
    public void encryptX509File(final String decryptedFile, final String encryptedFile, final X509Certificate cert) throws Exception {
        final File file = new File(decryptedFile);

        if (file.isDirectory()) {
            LOGGER.warn("Skipping Folder: {}", decryptedFile);
            return;
        }

        if (!file.canRead()) {
            final String msg = String.format("unable to read file %s", decryptedFile);
            throw new IOException(msg);
        }

        LOGGER.info("Encrypt File \"{}\" to \"{}\" with \"{}\"", decryptedFile, encryptedFile, cert.getSubjectX500Principal());

        final byte[] data = Files.readAllBytes(file.toPath());

        final CMSTypedData msg = new CMSProcessableByteArray(data);
        final CMSEnvelopedDataGenerator edGen = new CMSEnvelopedDataGenerator();
        edGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(cert));

        final CMSEnvelopedData ed = edGen.generate(msg, new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC).setProvider(BouncyCastleProvider.PROVIDER_NAME).build());

        try (OutputStream os = Files.newOutputStream(new File(encryptedFile).toPath())) {
            os.write(ed.getEncoded());
        }
    }

    /**
     * Verschlüsselt alle Dateien innerhalb des Verzeichnisses mit einem {@link X509Certificate}<br>
     * OHNE Unterverzeichnisse.
     */
    public void encryptX509Folder(final String inputFolder, final String outputFolder, final String zertifikatFile) throws Exception {
        final X509Certificate cert = getCertificate(zertifikatFile);

        encryptX509Folder(inputFolder, outputFolder, cert);
    }

    /**
     * Verschlüsselt alle Dateien innerhalb des Verzeichnisses mit einem {@link X509Certificate}<br>
     * OHNE Unterverzeichnisse.
     */
    public void encryptX509Folder(final String inputFolder, final String outputFolder, final String keystoreFile, final char[] keyStorePassword, final String alias)
            throws Exception {
        final X509Certificate cert = getCertificate(keystoreFile, keyStorePassword, alias);

        encryptX509Folder(inputFolder, outputFolder, cert);
    }

    /**
     * Verschlüsselt alle Dateien innerhalb des Verzeichnisses mit einem {@link X509Certificate}<br>
     * OHNE Unterverzeichnisse.
     */
    public void encryptX509Folder(final String inputFolder, final String outputFolder, final X509Certificate cert) throws Exception {
        final File folder = new File(inputFolder);

        if (!folder.canRead()) {
            final String msg = String.format("unable to read folder %s", inputFolder);
            throw new IOException(msg);
        }

        final String[] files = folder.list();

        for (String name : files) {
            final String inputFile = inputFolder + File.separator + name;
            final String encryptedFile = outputFolder + File.separator + "Encrypted_" + name;

            encryptX509File(inputFile, encryptedFile, cert);
        }
    }

    /**
     * Laden des {@link X509Certificate}s.
     */
    private X509Certificate getCertificate(final String zertifikatFile) throws Exception {
        Certificate cert = null;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zertifikatFile))) {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");

            while (bis.available() > 0) {
                cert = cf.generateCertificate(bis);
            }
        }

        return (X509Certificate) cert;
    }

    /**
     * Laden des {@link X509Certificate}s.
     */
    private X509Certificate getCertificate(final String keystoreFile, final char[] keyStorePassword, final String alias) throws Exception {
        final KeyStore ks = KeyStore.getInstance("PKCS12"); // , BouncyCastleProvider.PROVIDER_NAME);

        try (FileInputStream fis = new FileInputStream(keystoreFile)) {
            ks.load(fis, keyStorePassword);
        }

        final Certificate cert = ks.getCertificate(alias);

        return (X509Certificate) cert;
    }
}
