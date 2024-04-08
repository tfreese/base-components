package de.freese.base.swing.fontchange;

import java.awt.Font;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface FontChangeHandler {
    void fontChanged(Font newFont, Object object);
}
