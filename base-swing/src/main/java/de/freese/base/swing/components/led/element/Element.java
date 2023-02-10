// Created: 28.12.2020
package de.freese.base.swing.components.led.element;

import java.util.List;

import de.freese.base.swing.components.led.token.Token;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Element {
    List<Token<?>> getTokens();
}
