package de.freese.base.swing.fontchange;

import java.awt.Font;

/**
 * Interface für eine Klasse zum Ändern eines Fonts eines Objektes.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface FontChangeHandler
{
    void fontChanged(Font newFont, Object object);
}
