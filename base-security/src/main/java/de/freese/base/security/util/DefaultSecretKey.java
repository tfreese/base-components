/**
 * Created: 27.08.2014
 */

package de.freese.base.security.util;

import javax.crypto.spec.SecretKeySpec;

/**
 * Defaultimplementierung.
 *
 * @author Thomas Freese
 */
public class DefaultSecretKey extends SecretKeySpec
{
    /**
     * 
     */
    public static final byte[] DEFAULT_KEY = new byte[]
    {
            0, 1, 0, 1, 0, 1, 0, 1
    };

    /**
     *
     */
    private static final long serialVersionUID = -218843118467501326L;

    /**
     * Erstellt ein neues {@link DefaultSecretKey} Object.
     * 
     * @param key byte[]
     * @param algorithm String
     */
    public DefaultSecretKey(final byte[] key, final String algorithm)
    {
        super(key, algorithm);
    }

    /**
     * Erstellt ein neues {@link DefaultSecretKey} Object.
     *
     * @param algorithm String
     */
    public DefaultSecretKey(final String algorithm)
    {
        super(DEFAULT_KEY, algorithm);
    }
}
