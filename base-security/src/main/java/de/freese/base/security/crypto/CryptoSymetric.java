// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author Thomas Freese
 */
public class CryptoSymetric extends AbstractCrypto {
    private Key key;

    CryptoSymetric(final CryptoConfig<?> cryptoConfig) throws Exception {
        super(cryptoConfig);
    }

    /**
     * Symmetric encryption can not use {@link Signature}, because we need a {@link PublicKey} and {@link PrivateKey} for that.
     */
    @Override
    public void sign(final InputStream in, final OutputStream out) throws Exception {
        final byte[] digest = digest(in);

        out.write(digest);
    }

    /**
     * Symmetric encryption can not use {@link Signature}, because we need a {@link PublicKey} and {@link PrivateKey} for that.
     */
    @Override
    public boolean verify(final InputStream in, final InputStream signIn) throws Exception {
        final byte[] digest = digest(in);
        final byte[] sig = signIn.readAllBytes();

        return Arrays.equals(digest, sig);
    }

    void setKey(final Key key) {
        this.key = key;
    }

    @Override
    protected Cipher createCipherDecrypt() throws Exception {
        if (getConfig().getAlgorithmCipher().contains("/GCM/")) {
            // GCM braucht speziellen ParameterSpec.
            final AlgorithmParameterSpec parameterSpec = new GCMParameterSpec(128, getConfig().getInitVector());

            final Cipher cipherDecrypt = Cipher.getInstance(getConfig().getAlgorithmCipher(), getConfig().getProviderCipher());
            cipherDecrypt.init(Cipher.DECRYPT_MODE, getKey(), parameterSpec, getSecureRandom());

            return cipherDecrypt;
        }

        final AlgorithmParameterSpec parameterSpec;

        if (BouncyCastleProvider.PROVIDER_NAME.equals(getConfig().getProviderCipher())) {
            parameterSpec = new PBEParameterSpec(getConfig().getInitVector(), 4096);
        }
        else {
            parameterSpec = new IvParameterSpec(getConfig().getInitVector());
        }

        final Cipher cipherDecrypt = Cipher.getInstance(getConfig().getAlgorithmCipher(), getConfig().getProviderCipher());
        cipherDecrypt.init(Cipher.DECRYPT_MODE, getKey(), parameterSpec, getSecureRandom());

        return cipherDecrypt;
    }

    @Override
    protected Cipher createCipherEncrypt() throws Exception {
        if (getConfig().getAlgorithmCipher().contains("/GCM/")) {
            // GCM braucht speziellen ParameterSpec.
            final AlgorithmParameterSpec parameterSpec = new GCMParameterSpec(128, getConfig().getInitVector());

            final Cipher cipherEncrypt = Cipher.getInstance(getConfig().getAlgorithmCipher(), getConfig().getProviderCipher());
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, getKey(), parameterSpec, getSecureRandom());

            return cipherEncrypt;
        }

        final AlgorithmParameterSpec parameterSpec;

        if (BouncyCastleProvider.PROVIDER_NAME.equals(getConfig().getProviderCipher())) {
            parameterSpec = new PBEParameterSpec(getConfig().getInitVector(), 4096);
        }
        else {
            parameterSpec = new IvParameterSpec(getConfig().getInitVector());
        }

        final Cipher cipherEncrypt = Cipher.getInstance(getConfig().getAlgorithmCipher(), getConfig().getProviderCipher());
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, getKey(), parameterSpec, getSecureRandom());

        return cipherEncrypt;
    }

    @Override
    protected CryptoConfigSymetric getConfig() {
        return (CryptoConfigSymetric) super.getConfig();
    }

    protected Key getKey() {
        return this.key;
    }
}
