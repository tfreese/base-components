// Created: 27.08.2014

package de.freese.base.security.util;

import java.io.Serial;

import javax.crypto.spec.SecretKeySpec;

/**
 * Defaultimplementierung.
 *
 * @author Thomas Freese
 */
public class DefaultSecretKey extends SecretKeySpec
{
    public static final byte[] DEFAULT_KEY =
            {
                    0, 1, 0, 1, 0, 1, 0, 1
            };

    @Serial
    private static final long serialVersionUID = -218843118467501326L;

    public DefaultSecretKey(final byte[] key, final String algorithm)
    {
        super(key, algorithm);
    }

    public DefaultSecretKey(final String algorithm)
    {
        super(DEFAULT_KEY, algorithm);
    }
}
