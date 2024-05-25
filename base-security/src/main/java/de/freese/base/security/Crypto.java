// Created: 25 Mai 2024
package de.freese.base.security;

/**
 * @author Thomas Freese
 */
public interface Crypto {
    String decrypt(final String encrypted) throws Exception;

    String encrypt(final String message) throws Exception;
}
