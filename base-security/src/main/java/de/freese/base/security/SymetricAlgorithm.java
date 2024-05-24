// Created: 24.05.2024
package de.freese.base.security;

/**
 * @author Thomas Freese
 */
public enum SymetricAlgorithm {
    PBE_WITH_HMAC_SHA512_AND_AES256("PBEWithHmacSHA512AndAES_256"),
    PBE_WITH_HMAC_SHA256_AND_AES128("PBEWithHmacSHA256AndAES_128"),
    /**
     * Needs BouncyCastleProvider<br>
     * <pre>{@code
     * if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
     *      Security.addProvider(new BouncyCastleProvider());
     * }
     * }</pre>
     */
    PBE_WITH_SHA256_AND_256BIT_AES_CBC_BC("PBEWITHSHA256AND256BITAES-CBC-BC"),
    PBE_WITH_MD5_AND_TRIPLEDES("PBEWithMD5AndTripleDES"),
    PBE_WITH_MD5_AND_DES("PBEWithMD5AndDES");

    private final String algorithmName;

    SymetricAlgorithm(final String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }
}
