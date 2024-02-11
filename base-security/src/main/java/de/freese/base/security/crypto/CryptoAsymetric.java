// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.Signature;

import javax.crypto.Cipher;

/**
 * @author Thomas Freese
 */
public class CryptoAsymetric extends AbstractCrypto {
    private KeyPair keyPair;

    CryptoAsymetric(final CryptoConfig<?> cryptoConfig) throws Exception {
        super(cryptoConfig);
    }

    @Override
    public void sign(final InputStream in, final OutputStream out) throws Exception {
        final Signature signature = createSignatureSign();

        sign(signature, in, out);
    }

    @Override
    public boolean verify(final InputStream in, final InputStream signIn) throws Exception {
        final Signature signature = createSignatureVerify();

        return verify(signature, in, signIn);
    }

    void setKeyPair(final KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @Override
    protected Cipher createCipherDecrypt() throws Exception {
        final Cipher cipherDecrypt = Cipher.getInstance(getConfig().getAlgorithmCipher(), getConfig().getProviderCipher());
        cipherDecrypt.init(Cipher.DECRYPT_MODE, getKeyPair().getPrivate(), getSecureRandom());

        return cipherDecrypt;
    }

    @Override
    protected Cipher createCipherEncrypt() throws Exception {
        final Cipher cipherEncrypt = Cipher.getInstance(getConfig().getAlgorithmCipher(), getConfig().getProviderCipher());
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, getKeyPair().getPublic(), getSecureRandom());

        return cipherEncrypt;
    }

    protected Signature createSignatureSign() throws Exception {
        final Signature signatureSign = Signature.getInstance(getConfig().getAlgorithmSignature(), getConfig().getProviderSignature());
        signatureSign.initSign(getKeyPair().getPrivate(), getSecureRandom());

        return signatureSign;
    }

    protected Signature createSignatureVerify() throws Exception {
        final Signature signatureVerify = Signature.getInstance(getConfig().getAlgorithmSignature(), getConfig().getProviderSignature());
        signatureVerify.initVerify(getKeyPair().getPublic());

        return signatureVerify;
    }

    @Override
    protected CryptoConfigAsymetric getConfig() {
        return (CryptoConfigAsymetric) super.getConfig();
    }

    protected KeyPair getKeyPair() {
        return this.keyPair;
    }
}
