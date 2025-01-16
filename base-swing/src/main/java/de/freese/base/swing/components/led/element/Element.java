// Created: 28.12.2020
package de.freese.base.swing.components.led.element;

import java.util.List;
import java.util.function.Supplier;

import de.freese.base.swing.components.led.token.Token;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Element extends Supplier<List<Token>> {
    @Override
    default List<Token> get() {
        return getTokens();
    }

    List<Token> getTokens();
}
