// Created: 05 Nov. 2025
package de.freese.base.core.feature;

/**
 * Inspired by tools.jackson.databind.cfg.ConfigFeature.<br>
 * A ConfigFeature enum may contain a maximum of 31 Entries to avoid an Integer-Range overflow.
 *
 * @author Thomas Freese
 */
public interface ConfigFeature {
    /**
     * Method that calculates bit set (flags) of all features that are enabled by default.
     */
    static <F extends Enum<F> & ConfigFeature> int getDefaults(final Class<F> enumClass) {
        final F[] allFeatures = enumClass.getEnumConstants();

        if (allFeatures.length > 31) {
            final String desc = enumClass.getName();

            throw new IllegalArgumentException("Can not use type '%s': too many entries (%d > 31)".formatted(desc, allFeatures.length));
        }

        int flags = 0;

        for (F value : allFeatures) {
            if (value.isEnabledByDefault()) {
                flags = value.enable(flags);
            }
        }

        return flags;
    }

    /**
     * Returns changed bit mask with disabled feature.
     */
    default int disable(final int flags) {
        return flags & ~getMask();
    }

    /**
     * Returns changed bit mask with enabled feature.
     */
    default int enable(final int flags) {
        return flags | getMask();
    }

    /**
     * Returns bit mask for this feature instance.
     */
    int getMask();

    /**
     * Checking whether feature is disabled in given bitmask.
     */
    default boolean isDisabled(final int flags) {
        return (flags & getMask()) == 0;
    }

    /**
     * Checking whether feature is enabled in given bitmask.
     */
    default boolean isEnabled(final int flags) {
        return (flags & getMask()) != 0;
    }

    /**
     * Checking whether this feature is enabled by default.
     */
    boolean isEnabledByDefault();
}
