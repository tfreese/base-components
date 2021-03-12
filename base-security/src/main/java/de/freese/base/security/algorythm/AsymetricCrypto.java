/**
 * Created: 14.05.2019
 */

package de.freese.base.security.algorythm;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.Signature;
import java.util.function.Supplier;

/**
 * Implementierung für asymetrische Verschlüsselungen von {@link Crypto}.
 *
 * @author Thomas Freese
 */
public class AsymetricCrypto extends AbstractCrypto
{
    /**
    *
    */
    private KeyPair keyPair;

    /**
    *
    */
    private Supplier<Signature> signatureSignSupplier;

    /**
    *
    */
    private Supplier<Signature> signatureVerifySupplier;

    /**
     * Erstellt ein neues {@link AsymetricCrypto} Object.
     */
    AsymetricCrypto()
    {
        super();
    }

    /**
     * @return {@link KeyPair}
     */
    protected KeyPair getKeyPair()
    {
        return this.keyPair;
    }

    /**
     * @return {@link Signature}
     */
    protected Signature getSignatureSign()
    {
        return this.signatureSignSupplier.get();
    }

    /**
     * @return {@link Signature}
     */
    protected Signature getSignatureVerify()
    {
        return this.signatureVerifySupplier.get();
    }

    /**
     * @param keyPair {@link KeyPair}
     */
    void setKeyPair(final KeyPair keyPair)
    {
        this.keyPair = keyPair;
    }

    /**
     * @param signatureSignSupplier {@link Supplier}<Signature>
     */
    void setSignatureSignSupplier(final Supplier<Signature> signatureSignSupplier)
    {
        this.signatureSignSupplier = signatureSignSupplier;
    }

    /**
     * @param signatureVerifySupplier {@link Supplier}<Signature>
     */
    void setSignatureVerifySupplier(final Supplier<Signature> signatureVerifySupplier)
    {
        this.signatureVerifySupplier = signatureVerifySupplier;
    }

    /**
     * @see de.freese.base.security.algorythm.Crypto#sign(java.io.InputStream, java.io.OutputStream)
     */
    @Override
    public void sign(final InputStream in, final OutputStream out) throws Exception
    {
        Signature signature = getSignatureSign();

        sign(signature, in, out);
    }

    /**
     * @see de.freese.base.security.algorythm.Crypto#verify(java.io.InputStream, java.io.InputStream)
     */
    @Override
    public boolean verify(final InputStream in, final InputStream signIn) throws Exception
    {
        Signature signature = getSignatureVerify();

        return verify(signature, in, signIn);
    }
}
