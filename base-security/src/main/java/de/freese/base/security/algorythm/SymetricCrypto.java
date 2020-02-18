/**
 * Created: 14.05.2019
 */

package de.freese.base.security.algorythm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;

/**
 * Implementierung für symetrische Verschlüsselungen von {@link Crypto}.
 *
 * @author Thomas Freese
 */
public class SymetricCrypto extends AbstractCrypto
{
    /**
    *
    */
    private Key key = null;

    /**
     * Erstellt ein neues {@link SymetricCrypto} Object.
     */
    SymetricCrypto()
    {
        super();
    }

    /**
     * @return {@link Key}
     */
    protected Key getKey()
    {
        return this.key;
    }

    /**
     * @param key {@link Key}
     */
    void setKey(final Key key)
    {
        this.key = key;
    }

    /**
     * Symetrische Verschlüsselung kann nicht mit {@link Signature} arbeiten, weil dafür {@link PublicKey} und {@link PrivateKey} benötigt werden.
     *
     * @see de.freese.base.security.algorythm.Crypto#sign(java.io.InputStream, java.io.OutputStream)
     */
    @Override
    public void sign(final InputStream in, final OutputStream out) throws GeneralSecurityException, IOException
    {
        byte[] digest = digest(in);
        out.write(digest);
    }

    /**
     * Symetrische Verschlüsselung kann nicht mit {@link Signature} arbeiten, weil dafür {@link PublicKey} und {@link PrivateKey} benötigt werden.
     *
     * @see de.freese.base.security.algorythm.Crypto#verify(java.io.InputStream, java.io.InputStream)
     */
    @Override
    public boolean verify(final InputStream in, final InputStream signIn) throws GeneralSecurityException, IOException
    {
        byte[] digest = digest(in);
        byte[] sig = IOUtils.toByteArray(signIn);

        return Arrays.equals(digest, sig);
    }
}
