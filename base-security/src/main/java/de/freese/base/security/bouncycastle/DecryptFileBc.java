package de.freese.base.security.bouncycastle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entschlüsselt Verzeichnisse/Dateien mit einem {@link X509Certificate} des BouncyCastleProviders.
 *
 * @author Svetlana Margolina (eck*cellent)
 * @author Thomas Freese
 */
public class DecryptFileBc {
    private static final Logger LOGGER = LoggerFactory.getLogger(DecryptFileBc.class);

    public DecryptFileBc() {
        super();

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Entschlüsselt die Datei mit dem {@link PrivateKey} eines {@link X509Certificate}.
     */
    public void decryptX509File(final String encryptedFile, final String decryptedFile, final PrivateKey privateKey) throws Exception {
        final File file = new File(encryptedFile);

        if (file.isDirectory()) {
            LOGGER.warn("Skipping Folder: {}", encryptedFile);
            return;
        }

        if (!file.canRead()) {
            final String msg = String.format("unable to read file %s", encryptedFile);
            throw new IOException(msg);
        }

        LOGGER.info("Decrypt File \"{}\" to \"{}\" with \"{}\"", encryptedFile, encryptedFile, privateKey.getAlgorithm() + "/" + privateKey.getFormat());

        // get encrypted input stream
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            // create BC base instance
            final CMSEnvelopedData envelopedData = new CMSEnvelopedData(inputStream);

            // get recipients information from the encrypted data package
            final RecipientInformationStore recipientsInfos = envelopedData.getRecipientInfos();

            final Collection<?> colRecipients = recipientsInfos.getRecipients();
            LOGGER.debug("Recipients [{}]", colRecipients.size());

            final Iterator<?> itRecipients = colRecipients.iterator();

            if (itRecipients.hasNext()) {
                final RecipientInformation recipient = (RecipientInformation) itRecipients.next();

                // decrypt data with BouncyCastle
                final JceKeyTransEnvelopedRecipient jkter = new JceKeyTransEnvelopedRecipient(privateKey);
                jkter.setProvider(BouncyCastleProvider.PROVIDER_NAME);
                final byte[] decryptedData = recipient.getContent(jkter);

                // write decrypted data to file.
                try (OutputStream os = Files.newOutputStream(new File(decryptedFile).toPath())) {
                    os.write(decryptedData);
                }
            }
        }
    }

    /**
     * Entschlüsselt die Datei mit dem {@link PrivateKey} eines {@link X509Certificate}.
     */
    public void decryptX509File(final String encryptedFile, final String decryptedFile, final String zertifikatFile, final char[] password) throws Exception {
        final PrivateKey privateKey = getPrivateKey(zertifikatFile, password);

        decryptX509File(encryptedFile, decryptedFile, privateKey);
    }

    /**
     * Entschlüsselt die Datei mit einem {@link X509Certificate}.
     */
    public void decryptX509File(final String encryptedFile, final String decryptedFile, final String keystoreFile, final char[] keyStorePassword, final String alias, final char[] aliasPassword) throws Exception {
        final PrivateKey privateKey = getPrivateKey(keystoreFile, keyStorePassword, alias, aliasPassword);

        decryptX509File(encryptedFile, decryptedFile, privateKey);
    }

    /**
     * Entschlüsselt alle Dateien innerhalb des Verzeichnisses mit dem {@link PrivateKey} eines {@link X509Certificate} OHNE Unterverzeichnisse.
     */
    public void decryptX509Folder(final String inputFolder, final String outputFolder, final PrivateKey privateKey) throws Exception {
        final File folder = new File(inputFolder);
        final String[] files = folder.list();

        for (String fileName : files) {
            final String encryptedFile = inputFolder + File.separator + fileName;
            final String decryptedFile = outputFolder + File.separator + "Decrypted_" + fileName;

            decryptX509File(encryptedFile, decryptedFile, privateKey);
        }
    }

    /**
     * Entschlüsselt alle Dateien innerhalb des Verzeichnisses mit dem {@link PrivateKey} eines {@link X509Certificate} OHNE Unterverzeichnisse.
     */
    public void decryptX509Folder(final String inputFolder, final String outputFolder, final String zertifikatFile, final char[] password) throws Exception {
        final PrivateKey privateKey = getPrivateKey(zertifikatFile, password);

        decryptX509Folder(inputFolder, outputFolder, privateKey);
    }

    /**
     * Entschlüsselt alle Dateien innerhalb des Verzeichnisses mit dem {@link PrivateKey} eines {@link X509Certificate} OHNE Unterverzeichnisse.
     */
    public void decryptX509Folder(final String inputFolder, final String outputFolder, final String keystoreFile, final char[] keyStorePassword, final String alias, final char[] aliasPassword) throws Exception {
        final PrivateKey privateKey = getPrivateKey(keystoreFile, keyStorePassword, alias, aliasPassword);

        decryptX509Folder(inputFolder, outputFolder, privateKey);
    }

    protected void printKeyStoreInfo(final KeyStore ks, final char[] password) throws Exception {
        LOGGER.info("Start printKeyStoreInfo.");

        if (ks == null) {
            LOGGER.warn("Keystore is null.");
            return;
        }

        LOGGER.info("Key-Store-Type is: {}", ks.getType());
        LOGGER.info("Key-Store-Provider is: {}", ks.getProvider().getName());

        // Alias ermitteln
        LOGGER.info("Getting keystore aliases.");
        final Enumeration<String> aliases = ks.aliases();
        String ksAlias = "";

        LOGGER.info("Before while loop.");

        while (aliases.hasMoreElements()) {
            ksAlias = aliases.nextElement();
            LOGGER.info("Key-Store-Alias is: {}", ksAlias);
        }

        // Now we shall iterate through the entries of the KeyStore
        // and display the contents.
        LOGGER.info("Before for loop.");

        for (Enumeration<String> e = ks.aliases(); e.hasMoreElements(); ) {
            final String alias = e.nextElement();
            LOGGER.info("--- Entry Alias: \"{}\" ---", alias);

            if (ks.isKeyEntry(alias)) {
                LOGGER.info("Key Entry:");

                // To retrieve a key, the correct encryption password is
                // required. In this case, the password is again "password".
                // However, it is possible that this password is different
                // to the password required to load the KeyStore.
                final PrivateKey key = (PrivateKey) ks.getKey(alias, password);

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Key Algorithm: {}", key.getAlgorithm());
                    LOGGER.info("Key Encoded: {}", new String(key.getEncoded(), StandardCharsets.UTF_8));
                }

                // Now retrieve the certificate chain for this key.
                // The first element is the certificate for this key.
                final Certificate[] certs = ks.getCertificateChain(alias);
                LOGGER.info("Cert Chain: length = {}", certs.length);

                for (Certificate cert2 : certs) {
                    final X509Certificate cert = (X509Certificate) cert2;
                    final X500Principal subject = cert.getSubjectX500Principal();
                    LOGGER.info("Subject: {}", subject);
                }
            }
            else if (ks.isCertificateEntry(alias)) {
                final Certificate cert = ks.getCertificate(alias);
                LOGGER.info("Trusted Certificate Entry: {}", cert);
            }
        }

        LOGGER.info("End printKeyStoreInfo.");
    }

    /**
     * Laden des {@link PrivateKey}s.
     */
    private PrivateKey getPrivateKey(final String zertifikatFile, final char[] password) throws Exception {
        final KeyStore ks = KeyStore.getInstance("PKCS12");// , BouncyCastleProvider.PROVIDER_NAME);

        try (InputStream inputStream = new FileInputStream(zertifikatFile)) {
            ks.load(inputStream, password);
        }
        // printKeyStoreInfo(ks, password);

        Key privateKey = null;
        final Enumeration<String> aliases = ks.aliases();

        while (aliases.hasMoreElements()) {
            final String name = aliases.nextElement();
            privateKey = ks.getKey(name, password);
            final X509Certificate c = (X509Certificate) ks.getCertificate(name);
            final List<String> keyExtensions = c.getExtendedKeyUsage();
            LOGGER.debug("Key Extension= {}", keyExtensions);

            if ((keyExtensions != null) && keyExtensions.contains("1.3.6.1.4.1.311.10.3.4")) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(keyExtensions.toString());
                }

                break;
            }
        }

        return (PrivateKey) privateKey;
    }

    /**
     * Laden des {@link PrivateKey}s.
     */
    private PrivateKey getPrivateKey(final String keystoreFile, final char[] keyStorePassword, final String alias, final char[] aliasPassword) throws Exception {
        final KeyStore ks = KeyStore.getInstance("PKCS12");// , BouncyCastleProvider.PROVIDER_NAME);

        try (InputStream inputStream = new FileInputStream(keystoreFile)) {
            ks.load(inputStream, keyStorePassword);
        }

        final Key privateKey = ks.getKey(alias, aliasPassword);

        return (PrivateKey) privateKey;
    }
}
