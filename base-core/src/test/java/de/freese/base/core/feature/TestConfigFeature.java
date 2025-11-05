// Created: 05 Nov. 2025
package de.freese.base.core.feature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestConfigFeature {
    private enum TestFeature implements ConfigFeature {
        DEFAULT_ENABLED(true),
        DEFAULT_DISABLED(false),
        EXAMPLE(false);

        /**
         * Method that calculates bit set (flags) of all features that are enabled by default.
         */
        public static int getDefaults() {
            return ConfigFeature.getDefaults(TestFeature.class);
        }

        private final boolean enabledByDefault;
        private final int mask;

        TestFeature(final boolean enabledByDefault) {
            this.enabledByDefault = enabledByDefault;

            mask = 1 << ordinal();
        }

        @Override
        public int getMask() {
            return mask;
        }

        @Override
        public boolean isEnabledByDefault() {
            return enabledByDefault;
        }
    }

    @Test
    void testDefaults() {
        final int flags = TestFeature.getDefaults();

        assertEquals(flags, ConfigFeature.getDefaults(TestFeature.class));

        assertTrue(TestFeature.DEFAULT_ENABLED.isEnabled(flags));
        assertFalse(TestFeature.DEFAULT_ENABLED.isDisabled(flags));

        assertFalse(TestFeature.DEFAULT_DISABLED.isEnabled(flags));
        assertTrue(TestFeature.DEFAULT_DISABLED.isDisabled(flags));
    }

    @Test
    void testDisable() {
        final int flags = TestFeature.EXAMPLE.disable(TestFeature.EXAMPLE.getMask());

        assertFalse(TestFeature.EXAMPLE.isEnabled(flags));
        assertTrue(TestFeature.EXAMPLE.isDisabled(flags));
    }

    @Test
    void testDisableAlreadyDisabled() {
        final int flags = TestFeature.getDefaults();

        assertEquals(flags, TestFeature.DEFAULT_DISABLED.disable(flags));
    }

    @Test
    void testEnable() {
        final int flags = TestFeature.EXAMPLE.enable(0);

        assertTrue(TestFeature.EXAMPLE.isEnabled(flags));
        assertFalse(TestFeature.EXAMPLE.isDisabled(flags));
    }

    @Test
    void testEnableAlreadyEnabled() {
        final int flags = TestFeature.getDefaults();

        assertEquals(flags, TestFeature.DEFAULT_ENABLED.enable(flags));
    }

    @Test
    void testNoConfig() {
        final int flags = 0;

        assertFalse(TestFeature.DEFAULT_ENABLED.isEnabled(flags));
        assertTrue(TestFeature.DEFAULT_ENABLED.isDisabled(flags));

        assertFalse(TestFeature.DEFAULT_DISABLED.isEnabled(flags));
        assertTrue(TestFeature.DEFAULT_DISABLED.isDisabled(flags));
    }
}
