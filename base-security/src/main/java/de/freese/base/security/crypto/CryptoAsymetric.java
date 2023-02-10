// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;

/**
 * Implementierung für asymetrische Verschlüsselungen von {@link Crypto}.
 *
 * @author Thomas Freese
 */
public class CryptoAsymetric extends AbstractCrypto {
    private KeyPair keyPair;

    CryptoAsymetric(final CryptoConfig<?> cryptoConfig) throws Exception {
        super(cryptoConfig);
    }

    /**
     * @see de.freese.base.security.crypto.Crypto#sign(java.io.InputStream, java.io.OutputStream)
     */
    @Override
    public void sign(final InputStream in, final OutputStream out) throws Exception {
        Signature signature = createSignatureSign();

        sign(signature, in, out);
    }

    /**
     * Symetrische Verschlüsselung kann nicht mit {@link Signature} arbeiten, weil dafür {@link PublicKey} und {@link PrivateKey} benötigt werden.
     *
     * @see de.freese.base.security.crypto.Crypto#verify(java.io.InputStream, java.io.InputStream)
     */
    @Override
    public boolean verify(final InputStream in, final InputStream signIn) throws Exception {
        Signature signature = createSignatureVerify();

        return verify(signature, in, signIn);
    }

    void setKeyPair(final KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    /**
     * @see de.freese.base.security.crypto.AbstractCrypto#createCipherDecrypt()
     */
    @Override
    protected Cipher createCipherDecrypt() throws Exception {
        Cipher cipherDecrypt = Cipher.getInstance(getConfig().getAlgorithmCipher(), getConfig().getProviderCipher());
        cipherDecrypt.init(Cipher.DECRYPT_MODE, getKeyPair().getPrivate(), getSecureRandom());

        return cipherDecrypt;
    }

    /**
     * @see de.freese.base.security.crypto.AbstractCrypto#createCipherEncrypt()
     */
    @Override
    protected Cipher createCipherEncrypt() throws Exception {
        Cipher cipherEncrypt = Cipher.getInstance(getConfig().getAlgorithmCipher(), getConfig().getProviderCipher());
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, getKeyPair().getPublic(), getSecureRandom());

        return cipherEncrypt;
    }

    protected Signature createSignatureSign() throws Exception {
        Signature signatureSign = Signature.getInstance(getConfig().getAlgorithmSignature(), getConfig().getProviderSignature());
        signatureSign.initSign(getKeyPair().getPrivate(), getSecureRandom());

        return signatureSign;
    }

    protected Signature createSignatureVerify() throws Exception {
        Signature signatureVerify = Signature.getInstance(getConfig().getAlgorithmSignature(), getConfig().getProviderSignature());
        signatureVerify.initVerify(getKeyPair().getPublic());

        return signatureVerify;
    }

    /**
     * @see de.freese.base.security.crypto.AbstractCrypto#getConfig()
     */
    @Override
    protected CryptoConfigAsymetric getConfig() {
        return (CryptoConfigAsymetric) super.getConfig();
    }

    protected KeyPair getKeyPair() {
        return this.keyPair;
    }
}
