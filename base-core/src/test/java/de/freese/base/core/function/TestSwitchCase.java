// Created: 01.02.2018
package de.freese.base.core.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.freese.base.core.function.usecase.Switch;
import de.freese.base.core.function.usecase.Switch.Case;

/**
 * @author Thomas Freese
 */
class TestSwitchCase
{
    /**
     *
     */
    @Test
    void testUseCase()
    {
        for (int j = 0; j < 5; j++)
        {
            final int i = j;

            //@formatter:off
            Optional<String> result = Switch.match(
                    Case.matchDefault(() -> "unknown value"),
                    Case.matchCase(() -> i==0, () -> "Value = 0"),
                    Case.matchCase(() -> i==1, () -> "Value = 1"),
                    Case.matchCase(() -> i==2, () -> "Value = 2")
                    );
            //@formatter:on

            assertNotNull(result.get());

            if (j < 3)
            {
                assertEquals("Value = " + j, result.get());
            }
            else
            {
                assertEquals("unknown value", result.get());
            }
        }
    }
}
